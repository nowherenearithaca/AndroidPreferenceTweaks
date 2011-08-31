package com.nowherenearithaca.preference.sample;

import com.nowherenearithaca.preference.R;
import com.nowherenearithaca.preference.core.BasicUtils;
import com.nowherenearithaca.preference.core.PreferencesApi4;
import com.nowherenearithaca.preference.core.PreferencesApi7;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * 
 * This is a sample activity that uses the customized preference classes.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

public class MainActivity extends Activity implements
		OnSharedPreferenceChangeListener {
	private TextView mTvCheckBoxValue;
	private TextView mTvSeekbarValue;
	private TextView mTvTextValue;
	private View mVCurrentColor;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		mTvCheckBoxValue = (TextView) findViewById(R.id.checkboxValue);
		mTvSeekbarValue = (TextView) findViewById(R.id.seekbarValue);
		mTvTextValue = (TextView) findViewById(R.id.textValue);
		mVCurrentColor = findViewById(R.id.colorValue);
		View v = findViewById(R.id.layoutWithColor);
		BasicUtils.setGradientForPreferenceView(v);

		Button btnGoToPreferences = (Button) findViewById(R.id.gotoSettings);
		btnGoToPreferences.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startPreferencesActivity();
			}
		});
		SharedPreferences settings = PreferenceManager
				.getDefaultSharedPreferences(this);
		settings.registerOnSharedPreferenceChangeListener(this);

		loadCurrentPreferences(settings);

	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {

		if (key.equals(getString(R.string.pref_samplecheckbox_key))) {
			boolean bCurrentCheckbox = sharedPreferences
					.getBoolean(
							key,
							Boolean.valueOf(getString(R.string.pref_samplecheckbox_default)));
			mTvCheckBoxValue.setText(String.valueOf(bCurrentCheckbox));
		} else if (key.equals(getString(R.string.pref_samplecolorpicker_key))) {
			String sColor = sharedPreferences.getString(key,
					getString(R.string.pref_samplecolorpicker_default));
			int theColor = Color.parseColor(sColor);
			mVCurrentColor.setBackgroundColor(theColor);
		} else if (key.equals(getString(R.string.pref_sampleseekbar_key))) {
			String seekbarValue = sharedPreferences.getString(key,
					getString(R.string.pref_sampleseekbar_default));
			mTvSeekbarValue.setText(seekbarValue);
		} else if (key.equals(getString(R.string.pref_sampleedittext_key))) {
			String textValue = sharedPreferences.getString(key,
					getString(R.string.pref_sampleedittext_default));
			mTvTextValue.setText(textValue);
		}
	}

	private void loadCurrentPreferences(SharedPreferences sharedPreferences) {
		onSharedPreferenceChanged(sharedPreferences,
				getString(R.string.pref_samplecheckbox_key));
		onSharedPreferenceChanged(sharedPreferences,
				getString(R.string.pref_samplecolorpicker_key));
		onSharedPreferenceChanged(sharedPreferences,
				getString(R.string.pref_sampleseekbar_key));
		onSharedPreferenceChanged(sharedPreferences,
				getString(R.string.pref_sampleedittext_key));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.preferences:
			startPreferencesActivity();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void startPreferencesActivity() {

		// here is where you would decide on which class to send the intent to
		// figure out which version we are at, and then use the right one...
		// Class hierarchy:
		// PreferenceActivity --> Preferences1.6 --> Preferences1.7 -->
		// Preferences 1.8, etc...

		Class c;
		// PreferenceActivity pActivity;
		if (Build.VERSION.SDK_INT >= 7) {
			c = PreferencesApi7.class;
		} else {
			c = PreferencesApi4.class;
		}

		Intent preferencesActivity = new Intent(getBaseContext(), c);
		startActivity(preferencesActivity);
	}

}