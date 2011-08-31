package com.nowherenearithaca.preference;

import com.nowherenearithaca.preference.core.BasicUtils;
import com.nowherenearithaca.preference.core.BasicUtils.PreferenceWithResetToDefault;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
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

public class CheckboxPreferenceWithReset extends CheckBoxPreference implements
		PreferenceManager.OnActivityDestroyListener,
		PreferenceWithResetToDefault {

	private static final String TAG = "CheckboxPreferenceWithReset";
	private Boolean bDefaultValue;
	private Button mResetButton;
	private AlertDialog mAlertDialogToConfirmResetToDefault;

	public CheckboxPreferenceWithReset(Context context) {
		super(context);
	}

	public CheckboxPreferenceWithReset(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onClick() {
		// Log.d(TAG,"onClick");
		super.onClick();
	}

	public void mainWidgetClicked() {
		super.onClick();
	}

	public void resetButtonClicked() {

		Context context = getContext();
		String sOk = context.getString(R.string.OK);
		String sCancel = context.getString(R.string.Cancel);
		String title = context.getString(R.string.resetSingleToDefault);

		final String confirmResetText;
		if (bDefaultValue != null) {
			confirmResetText = String
					.format(context
							.getString(R.string.resetSingleToDefaultMessageFormatWithDefaultValueShown),
							getTitle(), String.valueOf(bDefaultValue));
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

		if (bDefaultValue != null) {

			if (isChecked() != bDefaultValue.booleanValue()) {
				mResetButton.setEnabled(true);
				mResetButton.setVisibility(View.VISIBLE);
			} else {
				mResetButton.setEnabled(false);
				mResetButton.setVisibility(View.INVISIBLE);
			}
		}

	}

	@Override
	public void setChecked(boolean value) {
		super.setChecked(value);
		updateResetButtonEnabled();
	}

	public void resetToDefault() {
		if (bDefaultValue != null) {
			setChecked(bDefaultValue.booleanValue());
		}
	}


	@Override
	protected Object onGetDefaultValue(TypedArray a, int index) {
		boolean b = a.getBoolean(index, true);
		Boolean bObject = new Boolean(b);
		bDefaultValue = (Boolean) bObject;
		return bObject;
	}

	@Override
	protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {

		// Log.d(TAG,"onSetInitialValue, restoreValue = " + restoreValue);
		super.onSetInitialValue(restoreValue, defaultValue);
		updateResetButtonEnabled();
	}

	@Override
	protected View onCreateView(ViewGroup parent) {
		View mViewForPreference = super.onCreateView(parent);

		int indexOfMainWidget = 0;
		mResetButton = BasicUtils.setUpPreferenceViewToHaveResetButton(
				getContext(), mViewForPreference, indexOfMainWidget, this);

		return mViewForPreference;
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
