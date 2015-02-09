package com.example.station;

import java.net.HttpURLConnection;

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
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

public class Second_Activity extends Activity {
	private TextView area, station = null;
	Button air, map, send, top = null;
	String name, line, town;
	SharedPreferences p;
	String latitude, longitude = null;
	String requestURL, loadURL, cURL;
	WebView mWebview;
	String param_edittext;
	Task task = new Task();
	
	
	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
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
		task.execute(requestURL);
		
		p = PreferenceManager
				.getDefaultSharedPreferences(this);

		mWebview = (WebView) findViewById(R.id.webView);
		WebSettings settings = mWebview.getSettings();
		settings.setJavaScriptEnabled(true);
		mWebview.setWebViewClient(new WebViewClient());
		mWebview.loadUrl(loadURL);

		air.setOnClickListener(mButtonListener);
		map.setOnClickListener(mButtonListener2);
		send.setOnClickListener(mButtonListener3);
		top.setOnClickListener(mButtonListener4);
	}

	private OnClickListener mButtonListener = new OnClickListener() {
		public void onClick(View v) {
			cURL = cURL + "&t=k";
			mWebview.loadUrl(cURL);
			cURL = cURL.substring(0, cURL.length() - 4);
		}
	};

	private OnClickListener mButtonListener2 = new OnClickListener() {
		public void onClick(View v) {
			cURL = cURL + "&t=m";
			mWebview.loadUrl(cURL);
			cURL = cURL.substring(0, cURL.length() - 4);
		}
	};

	private OnClickListener mButtonListener3 = new OnClickListener() {
		public void onClick(View v) {
			
			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_SEND);
			intent.putExtra(Intent.EXTRA_EMAIL, p.getString("edittext", "Unselected"));
			intent.putExtra(Intent.EXTRA_SUBJECT, "最寄駅探索");
			intent.putExtra(Intent.EXTRA_TEXT, "あなたの現在地は「" + town + "」、最寄駅は「"
					+ name + "」、主な路線は「" + line + "」");
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
	protected void onDestroy() {
		super.onDestroy();
		task.cancel(true);

		mWebview.stopLoading();
		ViewGroup webParent = (ViewGroup) mWebview.getParent();
		if (webParent != null) {
			webParent.removeView(mWebview);
		}
		mWebview.destroy();
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

	protected class Task extends AsyncTask<String, String, String> {
		@Override
		protected String doInBackground(String... params) {
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

			}
			return rtn;

		}

		@Override
		protected void onPostExecute(String result) {
			try {
				String jsonBase = "{\"root\":" + result + "}";
				JSONObject json = new JSONObject(jsonBase);
				JSONObject obj = json.getJSONArray("root").getJSONObject(0);
				name = obj.getString("name");
				line = obj.getString("line");
				town = obj.getString("city");
				area.setText(town);
				station.setText(name + ":" + line);
				loadURL = "https://maps.google.com/maps?saddr=" + latitude
						+ "," + longitude + "&daddr=" + name + "&dirflg=w";
				cURL = loadURL;
			} catch (JSONException e) {
				area.setText("Json Error!!!" + e.getMessage());
				e.printStackTrace();
			}
		}

	}

}