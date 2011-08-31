package com.nowherenearithaca.preference;

/* The is based on what was written by Matthew Wiggins - I modified so that 
 *  - it can handle a min
 *  - it can handle styled attributes 
 *  - it can show a "Reset to Default" button
 * 
 * I figured out the "style" stuff from a few snippets from here
 * http://www.bryandenny.com/index.php/2010/05/25/what-i-learned-from-writing-my-first-android-application/
 * 
 * 
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

import com.nowherenearithaca.preference.core.BasicUtils;
import com.nowherenearithaca.preference.core.BasicUtils.PreferenceWithResetToDefault;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.preference.DialogPreference;
import android.preference.PreferenceManager;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.LinearLayout;

public class SeekBarPreferenceWithReset extends DialogPreference implements
		SeekBar.OnSeekBarChangeListener,
		PreferenceManager.OnActivityDestroyListener,
		PreferenceWithResetToDefault {

	private static final String TAG = "SeekBarPreferenceWithReset";

	private SeekBar mSeekBar;
	private TextView mSplashText, mValueText;

	private String mDialogMessage;
	private String mSuffix; // ="";
	private String mSummaryPrefix;
	private int mDefault;// based on how things work, this might be overwriting
							// what is done in the constructor = 52;
	private int mMax = 100;
	private int mMin = 0;
	private int mValue = 51;

	private Button mResetButton;

	private AlertDialog mAlertDialogToConfirmResetToDefault;

	public SeekBarPreferenceWithReset(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		initializeOnConstruct(context, attrs);
	}

	public SeekBarPreferenceWithReset(Context context, AttributeSet attrs) {// ,
																			// int
																			// defStyle)
																			// {

		super(context, attrs); // this will eventually call onGetDefaultValue
		initializeOnConstruct(context, attrs);
	}

	void initializeOnConstruct(Context context, AttributeSet attrs) {

		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.SeekBarPreference);
		mMin = a.getInt(R.styleable.SeekBarPreference_min, 0);
		mMax = a.getInt(R.styleable.SeekBarPreference_max, 100);

		mDialogMessage = a
				.getString(R.styleable.SeekBarPreference_dialogMessage);
		mSuffix = a.getString(R.styleable.SeekBarPreference_text);
		mSummaryPrefix = a
				.getString(R.styleable.SeekBarPreference_summaryPrefix);

		a.recycle();
	}

	public void resetButtonClicked() {

		Context context = getContext();
		String sOk = context.getString(R.string.OK);
		String sCancel = context.getString(R.string.Cancel);
		String title = context.getString(R.string.resetSingleToDefault);

		final String confirmResetText = String
				.format(context
						.getString(R.string.resetSingleToDefaultMessageFormatWithDefaultValueShown),
						getTitle(), String.valueOf(mDefault));

		mAlertDialogToConfirmResetToDefault = BasicUtils
				.getConfirmResetToDefaultDialog(context, confirmResetText,
						title, this, sOk, sCancel);

		mAlertDialogToConfirmResetToDefault.show();
	}

	public void resetToDefault() {
		setValue(mDefault);
	}

	@Override
	protected View onCreateView(ViewGroup parent) {
		View mViewForPreference = super.onCreateView(parent);

		int indexOfMainWidget = 0;
		mResetButton = BasicUtils.setUpPreferenceViewToHaveResetButton(
				getContext(), mViewForPreference, indexOfMainWidget, this);

		updateBasedOnValueChanged(); // to make sure things get set ok

		return mViewForPreference;
	}

	public void mainWidgetClicked() {
		super.onClick();
	}

	@Override
	protected View onCreateDialogView() {

		LinearLayout.LayoutParams params;
		LinearLayout layout = new LinearLayout(getContext());
		layout.setOrientation(LinearLayout.VERTICAL);
		layout.setPadding(6, 6, 6, 6);

		mSplashText = new TextView(getContext());
		if (mDialogMessage != null)
			mSplashText.setText(mDialogMessage);
		layout.addView(mSplashText);

		mValueText = new TextView(getContext());
		mValueText.setGravity(Gravity.CENTER_HORIZONTAL);
		mValueText.setTextSize(32);
		params = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		layout.addView(mValueText, params);

		updateDialogValueText();

		mSeekBar = new SeekBar(getContext());
		mSeekBar.setOnSeekBarChangeListener(this);
		layout.addView(mSeekBar, new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT));

		// if (shouldPersist()) {
		// mValue = getPersistedValueFromString(mDefault);
		// Log.d(TAG,"onCreateDialogView, mValue I just read is " + mValue);
		// }
		// Log.d(TAG,"onCreateDialogView, about to set max");
		mSeekBar.setMax(mMax - mMin);
		// Log.d(TAG,"onCreateDialogView, about to set Progress");
		mSeekBar.setProgress(getNormalizedValue(mValue));
		// Log.d(TAG,"onCreateDialogView, returning the layout");
		return layout;
	}

	@Override
	protected void onBindDialogView(View v) {
		// Log.d(TAG,"onBindDialogView");

		super.onBindDialogView(v);
		// Log.d(TAG,"onBindDialogView - done with call to super");
		mSeekBar.setMax(mMax - mMin);
		mSeekBar.setProgress(getNormalizedValue(mValue));
		updateBasedOnValueChanged();
	}

	@Override
	protected Object onGetDefaultValue(TypedArray a, int index) {
		String sPossibleDefaultValue = a.getString(index);
		try {
			mDefault = Integer.parseInt(sPossibleDefaultValue);
		} catch (NumberFormatException nfe) {
			Log.e(TAG, "Error reading default value '" + sPossibleDefaultValue
					+ "'; ignoring");
		}

		updateResetButtonEnabled();
		return new Integer(mDefault);

	}

	private void updateResetButtonEnabled() {
		if (mResetButton == null) {
			return;
		}
		if (mValue == mDefault) {
			mResetButton.setEnabled(false);
			mResetButton.setVisibility(View.INVISIBLE);
		} else {
			// Log.d(TAG,"updateResetButtonEnabled - is NOT default - will enable button");
			mResetButton.setVisibility(View.VISIBLE);
			mResetButton.setEnabled(true);
		}
		mResetButton.invalidate();
	}

	@Override
	protected void onSetInitialValue(boolean restore, Object defaultValue) {
		super.onSetInitialValue(restore, defaultValue);
		if (restore) {
			mValue = shouldPersist() ? getPersistedValueFromString(mDefault)
					: 0;
		} else {
			persistValueToString((Integer) defaultValue);
			mValue = (Integer) defaultValue;
		}
		updateBasedOnValueChanged();
		notifyChanged();
	}

	private void updateBasedOnValueChanged() {
		if (mSummaryPrefix == null) {
			// nill
			// Log.d(TAG,"updateSummary - mSummaryPrefix is null");
		} else {
			String s = mSummaryPrefix.concat(String.valueOf(mValue));
			if (mSuffix != null) {
				this.setSummary(s.concat(mSuffix));
			} else {
				// Log.d(TAG,"updateSummary - summary is now '" + s+
				// "' (mSuffix is '" + mSuffix + "')");
				this.setSummary(s);
			}
		}
		updateResetButtonEnabled();
	}

	// these methods will likely be moved to a utility class
	// I am wanting to store everything but booleans in a string in the
	// preferences
	// thing so as to not have to deal with weirdness there that fries the app
	private void persistValueToString(int value) {
		String s = String.valueOf(value);
		persistString(s);
	}

	private int getPersistedValueFromString(int defaultValue) {
		String s = getPersistedString(getKey());
		try {
			return Integer.parseInt(s);
		} catch (NumberFormatException nfe) {
			Log.e(TAG,
					"NumberFormatException in getPersistedValueFromString; will return default value of '"
							+ defaultValue + "'");
			return defaultValue;
		}
	}

	private int getNormalizedValue(int unNormalizedValue) {
		return unNormalizedValue - mMin; // doh
	}

	private int getValueFromNormalizedValue(int normalizedValue) {
		return mMin + normalizedValue;
	}

	@Override
	public void onProgressChanged(SeekBar seek, int value, boolean fromTouch) {
		int unNormalizedValue = getValueFromNormalizedValue(value);

		mValue = unNormalizedValue;

		updateDialogValueText();

	}

	private void updateDialogValueText() {
		String t = String.valueOf(mValue);
		mValueText.setText(mSuffix == null ? t : t.concat(mSuffix));
	}

	@Override
	public void onStartTrackingTouch(SeekBar seek) {
	}

	@Override
	public void onStopTrackingTouch(SeekBar seek) {
	}

	public void setMax(int max) {
		mMax = max;
	}

	public int getMax() {
		return mMax;
	}

	public void setMin(int min) {
		mMin = min;
	}

	public int getMin() {
		return mMin;
	}

	@Override
	protected void onDialogClosed(boolean positiveResult) {
		if (positiveResult) {
			setValue(mValue);
		} else {
			// nothing to do
		}
	}

	public void setValue(int theValue) {
		mValue = theValue;
		if (shouldPersist()) {
			if (!callChangeListener(new Integer(mValue))) {
				return;
			}
			persistValueToString(mValue);
		}
		updateBasedOnValueChanged();
		notifyChanged();
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
