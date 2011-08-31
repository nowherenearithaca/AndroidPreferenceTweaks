package com.nowherenearithaca.preference.core;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceGroup;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;

import com.nowherenearithaca.preference.R;
import com.nowherenearithaca.preference.ResetAllPreferencesPreference;
import com.nowherenearithaca.preference.core.BasicUtils.PreferenceWithResetToDefault;

/*
 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

public class PreferencesApi4 extends PreferenceActivity implements
		ResetAllPreferencesPreference.ResetAllPreferencesListener {

	private static final String TAG = "PreferencesApi4";

	@Override
	protected void onDestroy() {
		closeAnyDialogs();
		super.onDestroy();
	}

	private void closeAnyDialogs() {
		// Log.d(TAG,"closeAnyDialogs");
		PreferenceScreen ps = getPreferenceScreen();
		for (int i = 0; i < ps.getPreferenceCount(); i++) {
			Preference p = ps.getPreference(i);
			// Log.d(TAG,"Checking preference " + p.getTitle());
			if (p instanceof PreferenceGroup) {
				PreferenceGroup pGroup = (PreferenceGroup) p;
				for (int j = 0; j < pGroup.getPreferenceCount(); j++) {
					Preference pBelow = pGroup.getPreference(j);

					if (PreferenceWithResetToDefault.class.isInstance(pBelow)) {
						PreferenceWithResetToDefault preferenceWithResetToDefault = (PreferenceWithResetToDefault) pBelow;
						preferenceWithResetToDefault.onActivityDestroy();
					}
				}
			}
			if (PreferenceWithResetToDefault.class.isInstance(p)) {
				PreferenceWithResetToDefault preferenceWithResetToDefault = (PreferenceWithResetToDefault) p;
				preferenceWithResetToDefault.onActivityDestroy();
			}
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		closeAnyDialogs();
	}

	public void reloadDefaults() {

		boolean readAgain = true;
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(this);

		Editor editor = sharedPreferences.edit();
		editor.clear().commit();

		//sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

		// Log.d(TAG,"reloadDefaults - about to call setDefaultValues");
		PreferenceManager.setDefaultValues(this, R.xml.preferences, readAgain);
		// Log.d(TAG,"reloadDefaults - DONE call setDefaultValues");

		// read somewhere (and even Diane Hackborn suggested) to do this to get
		// it
		// to reload)
		// Log.d(TAG,"reloadDefaults - about to start new activity");
		startActivity(getIntent());
		// Log.d(TAG,"reloadDefaults - about to call 'finish'");
		finish();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Log.d(TAG,"onCreate - will call addPreferencesFromResource");

		addPreferencesFromResource(R.xml.preferences);
		// see if I have a ResetAll button
		ResetAllPreferencesPreference r = (ResetAllPreferencesPreference) findPreference(getString(R.string.pref_resetAllPreferencesPreference_key));

		if (r != null) {
			r.setResetAllPreferencesListener(this);
		}
	}

	@Override
	public void doResetAllPreferences() {
		reloadDefaults();
	}
}