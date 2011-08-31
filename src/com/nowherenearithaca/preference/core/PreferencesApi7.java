package com.nowherenearithaca.preference.core;

import android.graphics.PixelFormat;
import android.util.Log;
import android.view.Window;

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

public class PreferencesApi7 extends PreferencesApi4 {

	private static final String TAG = "PreferencesApi7";

	// doing this to see if it helps with banding I am seeing (Eric Burke -
	// http://stuffthathappens.com/blog/2010/06/04/android-color-banding/)
	// and it doesn't work in 1.6 because this method is not there in that
	// case...
	@Override
	public void onAttachedToWindow() {

		super.onAttachedToWindow();
		Window window = getWindow();
		// Eliminates color banding somewhat
		window.setFormat(PixelFormat.RGBA_8888);
	}
}
