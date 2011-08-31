package com.nowherenearithaca.preference.core;

import com.nowherenearithaca.preference.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

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

public class BasicUtils {

	private static final String TAG = "BasicUtils";

	public interface PreferenceWithResetToDefault {
		void resetToDefault();

		void mainWidgetClicked();

		void resetButtonClicked();

		void onActivityDestroy();

	}

	public static void deleteKeyFromPreferencesIfThere(String key,
			SharedPreferences sharedPreferences) {
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.remove(key);
		editor.commit();
	}

	public static String getARGBString(int value) {
		return "#" + Integer.toHexString(value);
	}

	public static void setGradientForPreferenceView(View v) {

		int[] colorsForGradient = new int[2];
		colorsForGradient[0] = Color.argb(0, 0, 0, 0);
		colorsForGradient[1] = Color.argb(64, 255, 255, 255);
		GradientDrawable d = new GradientDrawable(
				GradientDrawable.Orientation.LEFT_RIGHT, colorsForGradient);
		d.setGradientType(GradientDrawable.LINEAR_GRADIENT);
		d.setShape(GradientDrawable.RECTANGLE);
		// doesn't help d.setUseLevel(true);
		d.setDither(true);
		v.setBackgroundDrawable(d);
	}

	/**
	 * 
	 * @param context
	 * @param viewForPreference
	 *            The View returned by super.onCreateView in the (custom)
	 *            Preference class
	 * @param indexOfMainWidget
	 *            Set to index in ViewGroup that main widget is; set to -1 if
	 *            this is being handled by caller
	 * @param preferenceWithResetToDefault
	 * @return the Reset button created
	 */
	public static Button setUpPreferenceViewToHaveResetButton(Context context,
			View viewForPreference, int indexOfMainWidget,
			final PreferenceWithResetToDefault preferenceWithResetToDefault) {

		Button vResetButton = null;

		BasicUtils.setGradientForPreferenceView(viewForPreference);

		TextView textView = (TextView) viewForPreference
				.findViewById(android.R.id.title);
		if (textView != null) {
			textView.setSingleLine(false);
		}

		View viewWidgetFrame = viewForPreference
				.findViewById(android.R.id.widget_frame);

		if ((viewWidgetFrame != null) && (viewWidgetFrame instanceof ViewGroup)) {

			ViewGroup vg = (ViewGroup) viewWidgetFrame;

			if (indexOfMainWidget >= 0) { // we need to set this, too
				final View whatsInThere = vg.getChildAt(indexOfMainWidget);
				if (whatsInThere != null) {
					whatsInThere.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							preferenceWithResetToDefault.mainWidgetClicked();
						}
					});
				}
			}

			ViewGroup.LayoutParams layoutParams = vg.getLayoutParams();

			layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;

			vg.setLayoutParams(layoutParams);
			if (vg instanceof LinearLayout) {
				LinearLayout linearLayout = (LinearLayout) vg;
				linearLayout.setOrientation(LinearLayout.HORIZONTAL);// works
			}
			vResetButton = new Button(context);
			vResetButton.setText(context.getString(R.string.reset));
			vResetButton.setLayoutParams(new ViewGroup.LayoutParams(
					ViewGroup.LayoutParams.WRAP_CONTENT,
					ViewGroup.LayoutParams.WRAP_CONTENT));
			vg.addView(vResetButton, 0);

			vResetButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					preferenceWithResetToDefault.resetButtonClicked();
				}
			});
		}

		return vResetButton;

	}

	public static AlertDialog getConfirmResetToDefaultDialog(Context context,
			String confirmResetText, String title,
			final PreferenceWithResetToDefault preferenceWithResetToDefault,
			String sOk, String sCancel) {

		AlertDialog alertDialog = new AlertDialog.Builder(context).create();
		alertDialog.setTitle(title);
		alertDialog.setMessage(confirmResetText);
		alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, sOk,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						preferenceWithResetToDefault.resetToDefault();
					}
				});

		alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, sCancel,
				(Message) null);
		return alertDialog;

	}

}
