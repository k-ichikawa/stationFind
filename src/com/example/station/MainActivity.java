package com.example.station;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.CycleInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends Activity {

	private LocationManager locationManager = null;	
	private ImageView imageTrain = null;
	private TextView touch = null;
	Looper myLooper = Looper.myLooper();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		imageTrain = (ImageView)findViewById(R.id.image_traincircle);
		Animation anim = AnimationUtils.loadAnimation(this, R.anim.rotate);
		anim.setInterpolator(new LinearInterpolator());
		imageTrain.startAnimation(anim);
		startMeasure();
	}
	
	private Handler mHandler = new Handler();
	private ScheduledExecutorService mScheduledExecutor;
	
	private void startMeasure() {
		touch = (TextView)findViewById(R.id.textView_touch);
	    mScheduledExecutor = Executors.newScheduledThreadPool(2);

	    mScheduledExecutor.scheduleWithFixedDelay(new Runnable() {
	        @Override
	        public void run() {
	            mHandler.post(new Runnable() {
	                @Override
	                public void run() {
	                    touch.setVisibility(View.VISIBLE);

	                    // HONEYCOMBより前のAndroid SDKがProperty Animation非対応のため
	                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
	                        animateAlpha();
	                    }
	                }
	            });
	        }

	        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
	        private void animateAlpha() {

	            // 実行するAnimatorのリスト
	            List<Animator> animatorList = new ArrayList<Animator>();

	            // alpha値を0から1へ1000ミリ秒かけて変化させる。
	            ObjectAnimator animeFadeIn = ObjectAnimator.ofFloat(touch, "alpha", 0f, 1f);
	            animeFadeIn.setDuration(1000);

	            // alpha値を1から0へ600ミリ秒かけて変化させる。
	            ObjectAnimator animeFadeOut = ObjectAnimator.ofFloat(touch, "alpha", 1f, 0f);
	            animeFadeOut.setDuration(600);

	            // 実行対象Animatorリストに追加。
	            animatorList.add(animeFadeIn);
	            animatorList.add(animeFadeOut);

	            final AnimatorSet animatorSet = new AnimatorSet();

	            // リストの順番に実行
	            animatorSet.playSequentially(animatorList);

	            animatorSet.start();
	        }
	    }, 0, 1700, TimeUnit.MILLISECONDS);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		setContentView(R.layout.activity_main2);
		imageTrain = (ImageView)findViewById(R.id.image_traincircle);
		Animation anim = AnimationUtils.loadAnimation(this, R.anim.rotate);
		anim.setInterpolator(new LinearInterpolator());
		imageTrain.startAnimation(anim);
		Button buttonCertain = (Button)findViewById(R.id.button_certain);
		buttonCertain.setOnClickListener(mButton1Listener);
		return true;
	}
	


	private OnClickListener mButton1Listener = new OnClickListener() {
		public void onClick(View v) {
	        if (locationManager != null) {
	        	locationManager.removeUpdates(mLocationListener);
	        	locationManager = null;
	        }
        	locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

            if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            	//locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100000, 10,mLocationListener);
            	locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER,mLocationListener,myLooper);
            }
            if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
            	//locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 100000, 10,mLocationListener);
            	locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER,mLocationListener,myLooper);
            }
        }
	};

	private  LocationListener mLocationListener  = new LocationListener() {
        public void onStatusChanged(String provider, int status,
                Bundle extras) {
        }
        public void onProviderEnabled(String provider) {
        }
        public void onProviderDisabled(String provider) {
        }
        public void onLocationChanged(Location location) {
        	String latitude = Double.toString(location.getLatitude());
        	String longitude = Double.toString(location.getLongitude());
        	Intent intent = new Intent(MainActivity.this,Second_Activity.class);
        	intent.putExtra("Lat",latitude);
        	intent.putExtra("Lon",longitude);
        	startActivity(intent);
        	finish();
        }
    };
    

}