package com.example.station;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class Setting extends PreferenceActivity implements
		OnPreferenceChangeListener {
	private EditTextPreference edittext;

	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.pref);
		addPreferencesFromResource(R.xml.setting);

		edittext = (EditTextPreference) findPreference("edittext");

		// リスナーを設定する
		edittext.setOnPreferenceChangeListener(this);

		// 保存されたデータを読み込む
		SharedPreferences p = PreferenceManager
				.getDefaultSharedPreferences(this);

		// 値の取得
		String param_edittext = p.getString("edittext", "Unselected");

		// デフォルト値の設定
		edittext.setDefaultValue(param_edittext);

		// サマリーの設定
		setSummary(edittext, param_edittext);

//		Button button = (Button) findViewById(R.id.button);
//
//		// ボタン押下時の処理
//		button.setOnClickListener(new View.OnClickListener() {
//			public void onClick(View v) {
//				try {
//					Intent intent = new Intent(Setting.this,
//							Second_Activity.class);
//					startActivity(intent);
//					//finish();
//				} catch (Exception e) {
//					Toast.makeText(getApplicationContext(),
//							"ERROR\n" + e.getMessage(), Toast.LENGTH_LONG)
//							.show();
//				}
//			}
//		});

	}

	// リストの値が変更されたときに呼ばれる
	public boolean onPreferenceChange(android.preference.Preference preference,
			Object newValue) {

		if (newValue != null) {

			// newValueの型でサマリーの設定を分ける
			if (newValue instanceof String) {
				// preferenceの型でサマリーの設定を分ける
				if (preference instanceof EditTextPreference) {
					setSummary((EditTextPreference) preference,
							(String) newValue);
				}
			}
			return true;

		}
		return false;
	}

	// Summaryを設定（エディットテキスト）
	private void setSummary(EditTextPreference ep, String param) {

		if (param == null) {
			ep.setSummary("Unselected");
		} else {
			ep.setSummary("登録されているアドレス「" + param + "」");
		}
		param = null;
	}

	// Activity破棄時に実行
	public void onDestroy() {
		super.onDestroy();

		edittext.setOnPreferenceChangeListener(null);
		//edittext = null;
	}

	// Activityの再開時に実行
	public void onRestart() {
		super.onRestart();
		edittext.setEnabled(true);
	}

	// Activityの停止時に実行
	public void onStop() {
		super.onStop();
		edittext.setEnabled(false);
	}

}
