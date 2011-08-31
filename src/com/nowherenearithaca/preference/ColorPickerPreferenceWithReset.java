package com.nowherenearithaca.preference;

import com.nowherenearithaca.preference.core.BasicUtils;
import com.nowherenearithaca.preference.core.ColorPickerDialog;
import com.nowherenearithaca.preference.core.BasicUtils.PreferenceWithResetToDefault;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/*
 * This class is a modification of the one in the Android graphics demo
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

public class ColorPickerPreferenceWithReset extends Preference implements
		PreferenceManager.OnActivityDestroyListener,
		ColorPickerDialog.OnColorChangedListener, PreferenceWithResetToDefault {

	private static final String TAG = "ColorPickerPreferenceWithReset";
	private ColorPickerDialog mColorPickerDialog;

	private int lastColor = 0;

	private Button mySwatch;
	private String sDefaultColorString;
	private Button mResetButton;
	private AlertDialog mAlertDialogToConfirmResetToDefault;

	@Override
	public void mainWidgetClicked() {
		onClick();
	}

	@Override
	protected void onClick() {
		showColorPicker();
	}

	private void showColorPicker() {
		mColorPickerDialog = new ColorPickerDialog(getContext(), this,
				lastColor);
		mColorPickerDialog.setTitle(getTitle());
		mColorPickerDialog.show();
	}

	private void setUponConstruct(Context ctxt, AttributeSet attrs) {
		setWidgetLayoutResource(R.layout.colorpreference_swatch);
	}

	public ColorPickerPreferenceWithReset(Context ctxt) {
		this(ctxt, null);
	}

	public ColorPickerPreferenceWithReset(Context ctxt, AttributeSet attrs) {
		this(ctxt, attrs, 0);
	}

	public ColorPickerPreferenceWithReset(Context ctxt, AttributeSet attrs,
			int defStyle) {
		super(ctxt, attrs, defStyle);
		setUponConstruct(ctxt, attrs);
	}

	@Override
	protected View onCreateView(ViewGroup parent) {
		View mViewForPreference = super.onCreateView(parent);

		BasicUtils.setGradientForPreferenceView(mViewForPreference);

		TextView textView = (TextView) mViewForPreference
				.findViewById(android.R.id.title);
		if (textView != null) {
			textView.setSingleLine(false);
		}

		int indexOfMainWidget = -1;
		mResetButton = BasicUtils.setUpPreferenceViewToHaveResetButton(
				getContext(), mViewForPreference, indexOfMainWidget, this);

		// //things are a little different for this one
		mySwatch = (Button) mViewForPreference
				.findViewById(R.id.colorPrefSwatch);
		mySwatch.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showColorPicker();
			}
		});

		updateSwatchColor();
		updateResetButtonEnabled();

		return mViewForPreference;
	}

	public void resetButtonClicked() {

		Context context = getContext();
		String confirmResetText = context
				.getString(R.string.resetSingleToDefaultMessage);
		String sOk = context.getString(R.string.OK);
		String sCancel = context.getString(R.string.Cancel);
		String title = context.getString(R.string.resetSingleToDefault);

		mAlertDialogToConfirmResetToDefault = BasicUtils
				.getConfirmResetToDefaultDialog(context, confirmResetText,
						title, this, sOk, sCancel);

		mAlertDialogToConfirmResetToDefault.show();
	}

	@Override
	protected void onBindView(View v) {
		super.onBindView(v);
		updateSwatchColor();
		updateResetButtonEnabled();
	}

	void updateSwatchColor() {
		if (mySwatch != null) {
			Drawable d = mySwatch.getBackground(); // findViewById(R.id.button_name).getBackground();
			PorterDuffColorFilter filter = new PorterDuffColorFilter(lastColor,
					android.graphics.PorterDuff.Mode.SRC_ATOP);
			d.setColorFilter(filter);
		}
	}

	@Override
	protected Object onGetDefaultValue(TypedArray a, int index) {
		sDefaultColorString = a.getString(index);
		return sDefaultColorString;
	}

	private void persistToHexString(int value) {
		// delete it if it's there
		// this will throw an error if there's an "int" in there
		// ugh - it checks to see if it's null first
		String sARGB = BasicUtils.getARGBString(value);
		try {
			persistString(sARGB);
		} catch (ClassCastException cce) {
			// ok - let's delete the thing and try again
			BasicUtils
					.deleteKeyFromPreferencesIfThere(getKey(),
							PreferenceManager
									.getDefaultSharedPreferences(getContext()));

			persistString(sARGB);
		}
	}

	private int decodePersistedValue(int defaultValue) {

		int theValue;
		try {
			String s = getPersistedString(String.valueOf(defaultValue));
			theValue = Color.parseColor(s);
		} catch (ClassCastException e) {
			// can happen if an int got in there
			theValue = getPersistedInt(defaultValue);
		}
		return theValue;

	}

	private void updateResetButtonEnabled() {
		if (mResetButton == null) {
			return;
		}
		if (sDefaultColorString == null) {
			mResetButton.setEnabled(false);
		}
		try {
			int sCurrentColor = Color.parseColor(sDefaultColorString);
			if (sCurrentColor == lastColor) {
				mResetButton.setEnabled(false);
				mResetButton.setVisibility(View.INVISIBLE);
			} else {
				mResetButton.setEnabled(true);
				mResetButton.setVisibility(View.VISIBLE);
			}
		} catch (IllegalArgumentException e) {
			// nothing to do - nill
		}
	}

	public void resetToDefault() {
		if (sDefaultColorString != null) {
			try {
				lastColor = Color.parseColor(sDefaultColorString);
				persistToHexString(lastColor);
				updateSwatchColor();
				updateResetButtonEnabled();
				notifyChanged();
			} catch (IllegalArgumentException e) {
				// whatever
				// just get out of here
				Log.e(TAG, "IllegalArgumentException ('" + e.toString()
						+ "') resettingToDefault, default string is '"
						+ sDefaultColorString + "'");
			}
		}
	}

	@Override
	protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {

		if (restoreValue) {
			lastColor = decodePersistedValue(lastColor);
		} else {
			if (defaultValue instanceof String) {
				lastColor = Color.parseColor((String) defaultValue);
			} else {
				lastColor = (Integer) defaultValue;
			}
			persistToHexString(lastColor);

		}

		updateSwatchColor();
		updateResetButtonEnabled();
		notifyChanged();
	}

	@Override
	public void colorChanged(int color) {
		if (callChangeListener(color)) {
			lastColor = color;
			persistToHexString(lastColor);
			updateSwatchColor();
			updateResetButtonEnabled();
		}

	}

	@Override
	public void onActivityDestroy() {
		// /get rid of any dialogs showing
		if ((mColorPickerDialog != null) && (mColorPickerDialog.isShowing())) {
			mColorPickerDialog.dismiss();
		}
		if ((mAlertDialogToConfirmResetToDefault != null)
				&& (mAlertDialogToConfirmResetToDefault.isShowing())) {
			mAlertDialogToConfirmResetToDefault.dismiss();
		}

	}
}
