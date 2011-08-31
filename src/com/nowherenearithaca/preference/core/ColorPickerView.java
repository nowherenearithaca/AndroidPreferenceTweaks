package com.nowherenearithaca.preference.core;

//This is an extension of the example in the android demo
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

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.nowherenearithaca.preference.core.ColorPickerDialog.OnColorChangedListener;

import android.view.View;
import android.widget.Button;

//this was static before - 
public class ColorPickerView extends View {

	Button b;
	private static final String TAG = "ColorPickerView";
	private Paint mPaint;
	private Paint mCenterPaint;
	private int[] mColors = new int[] { 0xFF000000, 0xFFFFFFFF, 0xFFFF00FF,
			0xFF0000FF, 0xFF00FFFF, 0xFF00FF00, 0xFFFFFF00, 0xFFFF0000,
			0xFF000000 };

	private OnColorChangedListener mListener;

	private int mCenterX = 150; // this is set in onMeasure
	private int mCenterY = 150; // this is set in onMeasure
	private int mCenterRadius = 32; // set to a fraction of radius below
	private int mOriginalRadius;
	private int mCurrentRadius;
	private int mCurrentDirection = 1;
	private int mMaxDeviationFromCurrentRadiusOnMoveColor = 4;

	// draw some little beads
	private int mBeadRadius = 9;
	private int mMinBeadRadius = 4;
	private int mMaxBeadRadius = 11;
	private int mCurrentBeadRadius = mBeadRadius;
	private int gapBetweenBeads = 3 * mBeadRadius;
	private int mCurrentBeadOffset = 0;
	private int mBeadRadiusDirection = 1;

	public ColorPickerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initializeOnConstruct(context);
		initializeForInitialColor(0);
	}

	public ColorPickerView(Context c, OnColorChangedListener l, int color) {
		super(c);
		setColorChangeListener(l);
		initializeOnConstruct(c);
		initializeForInitialColor(color);
	}

	public int getCurrentColor() {
		if (mCenterPaint != null) {
			return mCenterPaint.getColor();
		} else {
			Log.d(TAG, "getCurrentColor - mCenterPaint is null");
			return 0;
		}
	}

	private void initializeOnConstruct(Context c) {
	}

	private void initializeForInitialColor(int color) {
		mColors = new int[] { 0xFF000000, 0xFFFFFFFF, 0xFFFF00FF, 0xFF0000FF,
				0xFF00FFFF, 0xFF00FF00, 0xFFFFFF00, 0xFFFF0000, 0xFF000000 };// start
																				// with
																				// black,
																				// end
																				// with
																				// white
		Shader s = new SweepGradient(0, 0, mColors, null);

		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mPaint.setShader(s);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeWidth(32);

		mCenterPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mCenterPaint.setColor(color);
		mCenterPaint.setStrokeWidth(5);
	}

	private boolean mTrackingCenter;
	private boolean mHighlightCenter;
	private Float mLastAngle_radians;

	public static int chooseDimension(int mode, int size) {
		if (mode == MeasureSpec.AT_MOST || mode == MeasureSpec.EXACTLY) {
			return size;
		} else { // (mode == MeasureSpec.UNSPECIFIED)
			return 100;
		}
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		// Log.d(TAG, "onLayout - (left,top), (bottom,right) = " + "(" + left
		// + "," + top + "), (" + bottom + "," + right + ")");
	}

	@Override
	protected void onDraw(Canvas canvas) {

		super.onDraw(canvas);
		// Log.d(TAG,"onDraw - canvas (width, height) = " + canvas.getWidth() +
		// ", " + canvas.getHeight());
		// Log.d(TAG,"onDraw - me: (width, height) = " + getWidth() + ", " +
		// getHeight());

		mCurrentRadius += mCurrentDirection;

		if (mCurrentRadius > mOriginalRadius
				+ mMaxDeviationFromCurrentRadiusOnMoveColor) {
			mCurrentDirection = -1;
		} else if (mCurrentRadius < mOriginalRadius
				- mMaxDeviationFromCurrentRadiusOnMoveColor) {
			mCurrentDirection = +1;
		}

		float r = mCenterX - mPaint.getStrokeWidth() * 0.5f;

		if (mCurrentRadius > r / 2) {
			mCurrentRadius = (int) (r / 2);
			mCurrentDirection = -1;
		}
		if (mCurrentRadius < 2) {
			mCurrentRadius = 2;
			mCurrentDirection = 1;
		}

		canvas.translate(mCenterX, mCenterX);

		canvas.drawOval(new RectF(-r, -r, r, r), mPaint);
		// canvas.drawCircle(0, 0, CENTER_RADIUS, mCenterPaint);
		canvas.drawCircle(0, 0, mCurrentRadius, mCenterPaint);

		if (mLastAngle_radians != null) {
			canvas.drawLine(0f, 0f,
					(float) (r * Math.cos(mLastAngle_radians.floatValue())),
					(float) (r * Math.sin(mLastAngle_radians.floatValue())),
					mCenterPaint);

			int currentBeadCenterRadius = (int) (r - mCurrentBeadOffset);
			int numberBeadsDrawn = 0;

			int currentBeadRadius;

			mCurrentBeadRadius += mBeadRadiusDirection;

			if (mCurrentBeadRadius <= mMinBeadRadius) {
				currentBeadRadius = mMinBeadRadius;
				mBeadRadiusDirection = 1;
			} else if (mCurrentBeadRadius >= mMaxBeadRadius) {
				mBeadRadiusDirection = -1;
			}

			while ((currentBeadCenterRadius > 0)
					&& (currentBeadCenterRadius > mCurrentRadius - mBeadRadius)
					&& (numberBeadsDrawn < 500)) {

				float xCenter = (float) (currentBeadCenterRadius * Math
						.cos(mLastAngle_radians.floatValue()));
				float yCenter = (float) (currentBeadCenterRadius * Math
						.sin(mLastAngle_radians.floatValue()));

				// RectF ovalRect = new RectF(left,top,right,bottom);

				canvas.save();
				canvas.translate(xCenter, yCenter);
				canvas.drawCircle(0, 0, mCurrentBeadRadius, mCenterPaint);
				// canvas.drawOval(oval, paint)drawCircle(0, 0, mBeadRadius,
				// mCenterPaint);
				canvas.restore();

				currentBeadCenterRadius = currentBeadCenterRadius
						- gapBetweenBeads;
				numberBeadsDrawn++;
			}
			mCurrentBeadOffset++;
			if (mCurrentBeadOffset > gapBetweenBeads) {
				mCurrentBeadOffset = 0;
			}

		}

		// set
		if (mTrackingCenter) {
			int c = mCenterPaint.getColor();
			mCenterPaint.setStyle(Paint.Style.STROKE);

			if (mHighlightCenter) {
				mCenterPaint.setAlpha(0xFF);
			} else {
				mCenterPaint.setAlpha(0x80);
			}
			// canvas.drawCircle(0, 0,
			// CENTER_RADIUS + mCenterPaint.getStrokeWidth(),
			// mCenterPaint);

			canvas.drawCircle(0, 0,
					mCurrentRadius + mCenterPaint.getStrokeWidth(),
					mCenterPaint);

			mCenterPaint.setStyle(Paint.Style.FILL);
			mCenterPaint.setColor(c);

		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		// setMeasuredDimension(CENTER_X*2, CENTER_Y*2);

		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);

		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);

		widthSize = chooseDimension(widthMode, widthSize);
		heightSize = chooseDimension(heightMode, heightSize);

		Log.d(TAG, "widthSize, heightSize = " + widthSize + "," + heightSize);

		int iRadius = (int) Math.min(widthSize / 2.0, heightSize / 2.0);

		int centerX = iRadius;
		int centerY = iRadius;

		mCenterX = centerX;
		mCenterY = centerY;
		mCenterRadius = iRadius / 5;
		mOriginalRadius = mCenterRadius;
		mCurrentRadius = mOriginalRadius;
		setMeasuredDimension(2 * iRadius, 2 * iRadius);
	}

	private int ave(int s, int d, float p) {
		return s + java.lang.Math.round(p * (d - s));
	}

	private int interpColor(int colors[], float unit) {
		if (unit <= 0) {
			return colors[0];
		}
		if (unit >= 1) {
			return colors[colors.length - 1];
		}

		float p = unit * (colors.length - 1);
		int i = (int) p;
		p -= i;

		// now p is just the fractional part [0...1) and i is the index
		int c0 = colors[i];
		int c1 = colors[i + 1];
		int a = ave(Color.alpha(c0), Color.alpha(c1), p);
		int r = ave(Color.red(c0), Color.red(c1), p);
		int g = ave(Color.green(c0), Color.green(c1), p);
		int b = ave(Color.blue(c0), Color.blue(c1), p);

		return Color.argb(a, r, g, b);
	}

	private static final float PI = 3.1415926f;

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float x = event.getX() - mCenterX;
		float y = event.getY() - mCenterY;
		boolean inCenter = java.lang.Math.sqrt(x * x + y * y) <= mCenterRadius;

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mTrackingCenter = inCenter;
			if (inCenter) {
				mHighlightCenter = true;
				invalidate();
				break;
			}
		case MotionEvent.ACTION_MOVE:
			if (mTrackingCenter) {
				if (mHighlightCenter != inCenter) {
					mHighlightCenter = inCenter;
					invalidate();
				}
			} else {
				float angle = (float) java.lang.Math.atan2(y, x);
				mLastAngle_radians = angle;
				// need to turn angle [-PI ... PI] into unit [0....1]
				float unit = angle / (2 * PI);
				if (unit < 0) {
					unit += 1;
				}
				mCenterPaint.setColor(interpColor(mColors, unit));
				invalidate();
			}
			break;
		case MotionEvent.ACTION_UP:
			if (mTrackingCenter) {
				if (inCenter) {
					getColorChangeListener().colorChanged(
							mCenterPaint.getColor());
				}
				mTrackingCenter = false; // so we draw w/o halo
				invalidate();
			}
			break;
		}
		return true;
	}

	public void setColorChangeListener(OnColorChangedListener mListener) {
		this.mListener = mListener;
	}

	public OnColorChangedListener getColorChangeListener() {
		return mListener;
	}

	public void setInitialColor(int initialColor) {
		initializeForInitialColor(initialColor);
	}
}
