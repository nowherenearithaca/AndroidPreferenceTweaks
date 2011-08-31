package com.nowherenearithaca.preference;

import com.nowherenearithaca.preference.core.BasicUtils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

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

public class ResetAllPreferencesPreference extends
		android.preference.Preference implements
		PreferenceManager.OnActivityDestroyListener {

	public interface ResetAllPreferencesListener {
		public void doResetAllPreferences();
	}

	private ResetAllPreferencesListener mResetAllPreferencesListener;

	private static final String TAG = "ResetAllPreferencesPreference";
	private Button mButton;

	private AlertDialog mAlertDialog;

	public ResetAllPreferencesPreference(Context context) {
		super(context);
		setUpOnConstruct(context);
	}

	public ResetAllPreferencesPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		setUpOnConstruct(context);
	}

	@Override
	protected View onCreateView(ViewGroup parent) {
		View mViewForPreference = super.onCreateView(parent);
		BasicUtils.setGradientForPreferenceView(mViewForPreference);
		return mViewForPreference;
	}

	@Override
	protected void onBindView(View view) {
		super.onBindView(view);
		mButton = (Button) view.findViewById(R.id.resetAllPreferencesToDefault);

		mButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startSetToDefaultProcess();
			}

		});

	}

	protected void startSetToDefaultProcess() {
		Context context = getContext();
		mAlertDialog = new AlertDialog.Builder(context).create();
		mAlertDialog.setTitle(context.getString(R.string.resetAllToDefault));
		mAlertDialog.setMessage(context
				.getString(R.string.resetAllToDefaultMessage));

		mAlertDialog.setButton(AlertDialog.BUTTON_POSITIVE,
				context.getString(R.string.OK),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						doResetToDefault();
					}
				});

		mAlertDialog.setButton(AlertDialog.BUTTON_NEGATIVE,
				context.getString(R.string.Cancel), (Message) null);

		mAlertDialog.show();
	}

	protected void doResetToDefault() {
		if (getResetAllPreferencesListener() != null) {
			getResetAllPreferencesListener().doResetAllPreferences();
		}
	}

	private void setUpOnConstruct(Context context) {
		this.setWidgetLayoutResource(R.layout.bfl_custom_buttonpreference);
	}

	public void setResetAllPreferencesListener(
			ResetAllPreferencesListener mResetAllPreferencesListener) {
		this.mResetAllPreferencesListener = mResetAllPreferencesListener;
	}

	public ResetAllPreferencesListener getResetAllPreferencesListener() {
		return mResetAllPreferencesListener;
	}

	@Override
	public void onActivityDestroy() {
		// /get rid of any dialogs showing
		if ((mAlertDialog != null) && (mAlertDialog.isShowing())) {
			mAlertDialog.dismiss();
		}

	}

}
