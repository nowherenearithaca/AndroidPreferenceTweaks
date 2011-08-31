package com.nowherenearithaca.preference.core;

//Based on the api demo package com.example.android.apis.graphics;
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

import com.nowherenearithaca.preference.R;

import android.os.Bundle;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class ColorPickerDialog extends Dialog {

	private static final String TAG = "ColorPickerDialog";

	public interface OnColorChangedListener {
		void colorChanged(int color);
	}

	private OnColorChangedListener mListener;
	private int mInitialColor;
	private ColorPickerView mColorPickerView;

	public ColorPickerDialog(Context context, OnColorChangedListener listener,
			int initialColor) {
		super(context);

		mListener = listener;
		mInitialColor = initialColor;
	}

	protected void saveColorButtonClicked() {
		if (mColorPickerView != null) {
			int color = mColorPickerView.getCurrentColor();
			if (mListener != null) {
				mListener.colorChanged(color);
			}
		}
		dismiss();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		OnColorChangedListener listenerColorChange = new OnColorChangedListener() {
			public void colorChanged(int color) {
				if (mListener != null) {
					mListener.colorChanged(color);
				}
				dismiss();
			}
		};

		ViewGroup vg = (ViewGroup) findViewById(android.R.id.content);
		LayoutInflater inflater = LayoutInflater.from(getContext());
		View vShouldBeLayout = inflater.inflate(R.layout.colorpicker_view, vg,
				false);
		setContentView(vShouldBeLayout);

		// just do it for the whole thing here:
		BasicUtils.setGradientForPreferenceView(vShouldBeLayout);

		mColorPickerView = (ColorPickerView) vShouldBeLayout
				.findViewById(R.id.colorPickerView);
		mColorPickerView.setColorChangeListener(listenerColorChange);
		mColorPickerView.setInitialColor(mInitialColor);

		Button cancelButton = (Button) vShouldBeLayout
				.findViewById(R.id.cancelColorPick);
		if (cancelButton != null) {
			cancelButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					dismiss();
				}
			});
		}

		Button saveButton = (Button) vShouldBeLayout
				.findViewById(R.id.saveColorPick);
		if (saveButton != null) {
			saveButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					saveColorButtonClicked();
				}
			});
		}
	}
}
