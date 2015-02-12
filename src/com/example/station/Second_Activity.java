package com.example.station;

import java.net.HttpURLConnection;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


public class Second_Activity extends Activity {
	private TextView area, station = null;
	Button air, map, send, top = null;
	String line, town;
	SharedPreferences p;
	String latitude, longitude = null;
	String requestURL,cURL;
	String param_edittext;
	Task task = new Task();
	final GoogleMap gMap=null;
	Geocoder geo;
	LatLng location,dest;
	
	@SuppressLint("SetJavaScriptEnabled")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_second);
		
		Intent intent = getIntent();
		area = (TextView) findViewById(R.id.textArea);
		station = (TextView) findViewById(R.id.textSta);

		air = (Button) findViewById(R.id.button_air);
		map = (Button) findViewById(R.id.button_map);
		send = (Button) findViewById(R.id.button_send);
		top = (Button) findViewById(R.id.button_top);
		latitude = intent.getStringExtra("Lat");
		longitude = intent.getStringExtra("Lon");
		requestURL = "http://map.simpleapi.net/stationapi?x=" + longitude
				+ "&y=" + latitude + "&output=json";
		p = PreferenceManager
				.getDefaultSharedPreferences(this);
		
		task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,requestURL);

		air.setOnClickListener(mButtonListener);
		map.setOnClickListener(mButtonListener2);
		send.setOnClickListener(mButtonListener3);
		top.setOnClickListener(mButtonListener4);
	}

	private OnClickListener mButtonListener = new OnClickListener() {
		public void onClick(View v) {
			MapFragment fragment = ((MapFragment)getFragmentManager().findFragmentById(R.id.map));
			final GoogleMap gMap = fragment.getMap();
			gMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
		}
	};

	private OnClickListener mButtonListener2 = new OnClickListener() {
		public void onClick(View v) {
			MapFragment fragment = ((MapFragment)getFragmentManager().findFragmentById(R.id.map));
			final GoogleMap gMap = fragment.getMap();
			gMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		}
	};

	private OnClickListener mButtonListener3 = new OnClickListener() {
		public void onClick(View v) {
			
			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_SEND);
			intent.putExtra(Intent.EXTRA_EMAIL, p.getString("edittext", "Unselected"));
			intent.putExtra(Intent.EXTRA_SUBJECT, "最寄駅探索");
			intent.putExtra(Intent.EXTRA_TEXT, "あなたの現在地は「" + town + "」、最寄駅は「"
					+ Global.name + "」、主な路線は「" + line + "」");
			intent.setType("text/plain");
			startActivity(intent);
		}
	};

	private OnClickListener mButtonListener4 = new OnClickListener() {
		public void onClick(View v) {
			Intent intent = new Intent(Second_Activity.this, MainActivity.class);
			startActivity(intent);
		}
	};
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		task.cancel(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(Menu.NONE, (Menu.FIRST), Menu.NONE, "設定");
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 1:
			Intent intent = new Intent(Second_Activity.this, Setting.class);
			startActivity(intent);
			return true;

		default:
			break;
		}
		return false;
	}

	public class Task extends AsyncTask<String, String, String> {
		@Override
		public String doInBackground(String... params) {
			HttpClient client = new DefaultHttpClient();
			HttpGet get = new HttpGet(params[0]);
			String rtn = "";
			try {
				HttpResponse response = client.execute(get);
				StatusLine statusLine = response.getStatusLine();
				if (statusLine.getStatusCode() == HttpURLConnection.HTTP_OK) {
					byte[] result = EntityUtils.toByteArray(response
							.getEntity());
					rtn = new String(result, "UTF-8");
					
				}
			} catch (Exception e) {
				System.out.println("doInBackだめ");
			}
			System.out.println("rtn:"+rtn);
			return rtn;
		}

		@Override
		public void onPostExecute (String result) {
			
			try {
				String jsonBase = "{\"root\":" + result + "}";
				JSONObject json = new JSONObject(jsonBase);
				JSONObject obj = json.getJSONArray("root").getJSONObject(0);
				Global.name = obj.getString("name");
				line = obj.getString("line");
				town = obj.getString("city");
				area.setText(town);
				station.setText(Global.name + ":" + line);
				
				
				//Gmapsのインスタンス生成
				MapFragment fragment = ((MapFragment)getFragmentManager().findFragmentById(R.id.map));
				final GoogleMap gMap = fragment.getMap();
				
				
				//初期位置設定
				location = new LatLng(Double.parseDouble(latitude),Double.parseDouble(longitude));
				dest = null;
				gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location,17));
				gMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
				gMap.setMyLocationEnabled(true);
				System.out.println("駅名"+Global.name);
				
				//目的地設定
				geo = new Geocoder(getBaseContext(), Locale.getDefault());
				try{
					List<Address> addressList = geo.getFromLocationName(Global.name, 1);
					Address address = addressList.get(0);
					double latitude2 = address.getLatitude();
			        double longitude2 = address.getLongitude();
			        dest = new LatLng(latitude2,longitude2);
				}catch(Exception e){
					System.out.println("noaddress");
				}
				
				
				MarkerOptions mOptions1 = new MarkerOptions();
				MarkerOptions mOptions2 = new MarkerOptions();		
				mOptions1.title("現在地");
				mOptions2.title(Global.name);
				mOptions1.position(location);
				mOptions2.position(dest);
				gMap.addMarker(mOptions1);
				gMap.addMarker(mOptions2);
			
				
			} catch (JSONException e) {
				area.setText("Json Error!!!" + e.getMessage());
				e.printStackTrace();
			}
		}
	}
}

class Global{
	public static String name;
}


