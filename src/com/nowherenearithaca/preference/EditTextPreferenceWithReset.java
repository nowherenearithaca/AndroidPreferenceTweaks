package com.nowherenearithaca.preference;

import com.nowherenearithaca.preference.core.BasicUtils;
import com.nowherenearithaca.preference.core.BasicUtils.PreferenceWithResetToDefault;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.preference.EditTextPreference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.preference.PreferenceManager;

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

public class EditTextPreferenceWithReset extends EditTextPreference implements
		PreferenceManager.OnActivityDestroyListener,
		PreferenceWithResetToDefault {

	private static final String TAG = "EditTextPreferenceWithReset";
	private String sDefaultValue;
	private Button mResetButton;
	private AlertDialog mAlertDialogToConfirmResetToDefault;
	private String mSummaryPrefix;

	private void initializeOnConstruct(Context context, AttributeSet attrs) {

		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.CustomEditTextPreference);
		mSummaryPrefix = a
				.getString(R.styleable.CustomEditTextPreference_summaryPrefix);
		a.recycle();
	}

	public EditTextPreferenceWithReset(Context context, AttributeSet attrs) {
		super(context, attrs);
		initializeOnConstruct(context, attrs);
	}

	public EditTextPreferenceWithReset(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		initializeOnConstruct(context, attrs);
	}

	public void resetButtonClicked() {

		Context context = getContext();
		String sOk = context.getString(R.string.OK);
		String sCancel = context.getString(R.string.Cancel);
		String title = context.getString(R.string.resetSingleToDefault);

		final String confirmResetText;
		if (sDefaultValue != null) {
			confirmResetText = String
					.format(context
							.getString(R.string.resetSingleToDefaultMessageFormatWithDefaultValueShown),
							getTitle(), String.valueOf(sDefaultValue));
		} else {
			confirmResetText = String.format(context
					.getString(R.string.resetSingleToDefaultMessageFormat),
					getTitle());
		}

		mAlertDialogToConfirmResetToDefault = BasicUtils
				.getConfirmResetToDefaultDialog(context, confirmResetText,
						title, this, sOk, sCancel);

		mAlertDialogToConfirmResetToDefault.show();

	}

	private void updateResetButtonEnabled() {
		if (mResetButton == null) {
			return;
		}
		if (sDefaultValue == null) {
			// Log.d(TAG,"updateResetButtonEnabled - default value is null");
			mResetButton.setEnabled(false);
		}
		if (sDefaultValue.equals(getText())) {
			// Log.d(TAG,"updateResetButtonEnabled - will set enabled to false");
			mResetButton.setEnabled(false);
			mResetButton.setVisibility(View.INVISIBLE);
		} else {
			// Log.d(TAG,"updateResetButtonEnabled - will set enabled to true");
			mResetButton.setEnabled(true);
			mResetButton.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void setText(String s) {
		super.setText(s);
		if (mSummaryPrefix != null) {
			String summary = mSummaryPrefix + "'" + s + "'";
			setSummary(summary);
		}

		updateResetButtonEnabled();
	}

	public void resetToDefault() {
		if (sDefaultValue != null) {
			setText(sDefaultValue);
		} else {
			// Log.d(TAG, "resetToDefault: default value is null");
		}
	}

	@Override
	protected Object onGetDefaultValue(TypedArray a, int index) {
		sDefaultValue = a.getString(index);
		return sDefaultValue;
	}

	@Override
	protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
		// Log.d(TAG,"onSetInitialValue, restoreValue = " + restoreValue);
		super.onSetInitialValue(restoreValue, defaultValue);
		updateResetButtonEnabled();
	}

	@Override
	protected View onCreateView(ViewGroup parent) {
		// Log.d(TAG,"onCreateView");
		View mViewForPreference = super.onCreateView(parent);
		BasicUtils.setGradientForPreferenceView(mViewForPreference);

		int indexOfMainWidget = 0;
		mResetButton = BasicUtils.setUpPreferenceViewToHaveResetButton(
				getContext(), mViewForPreference, indexOfMainWidget, this);

		return mViewForPreference;
	}

	public void mainWidgetClicked() {
		super.onClick();
	}

	@Override
	protected void onBindView(View view) {
		super.onBindView(view);
		updateResetButtonEnabled();
	}

	@Override
	public void onActivityDestroy() {
		// /get rid of any dialogs showing
		if ((mAlertDialogToConfirmResetToDefault != null)
				&& (mAlertDialogToConfirmResetToDefault.isShowing())) {
			mAlertDialogToConfirmResetToDefault.dismiss();
		}
	}
}
