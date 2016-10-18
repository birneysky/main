/*
 * Copyright 2013, Edmodo, Inc. 
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this work except in compliance with the License.
 * You may obtain a copy of the License in the LICENSE file, or at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" 
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language 
 * governing permissions and limitations under the License. 
 */

package com.yingwei.view.rangebar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.TypedValue;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents the underlying gray bar in the RangeBar (without the
 * thumbs).
 */
class Bar {

    // Member Variables ////////////////////////////////////////////////////////

    private final Paint mPaint;

    // Left-coordinate of the horizontal bar.
    private final float mLeftX;
    private final float mRightX;
    private final float mY;

    private int mNumSegments;
    private float mTickDistance;
    private final float mTickHeight;
    private final float mTickStartY;
    private final float mTickEndY;

    /******* add by birneysky *****/
//    private String[] bottomText = {"¥ 0","¥ 150","¥ 300","¥ 500","¥ 700","不限"};//
    private List<String> bottomText = new ArrayList<>();
    /*******************************/

    // Constructor /////////////////////////////////////////////////////////////

    Bar(Context ctx,
        float x,
        float y,
        float length,
        int tickCount,
        float tickHeightDP,
        float BarWeight,
        int BarColor) {

        mLeftX = x;
        mRightX = x + length;
        mY = y;

        mNumSegments = tickCount - 1;
        mTickDistance = length / mNumSegments;
        mTickHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                                                tickHeightDP,
                                                ctx.getResources().getDisplayMetrics());
        mTickStartY = mY - mTickHeight / 2f;
        mTickEndY = mY + mTickHeight / 2f;

        // Initialize the paint.
        mPaint = new Paint();
        mPaint.setColor(BarColor);
        mPaint.setStrokeWidth(BarWeight);
        mPaint.setAntiAlias(true);

        bottomText.add("￥ 0");
        bottomText.add("￥ 150");
        bottomText.add("￥ 300");
        bottomText.add("￥ 500");
        bottomText.add("￥ 700");
        bottomText.add("不限");
    }

    // Package-Private Methods /////////////////////////////////////////////////

    /**
     * Draws the bar on the given Canvas.
     * 
     * @param canvas Canvas to draw on; should be the Canvas passed into {#link
     *            View#onDraw()}
     */
    void draw(Canvas canvas) {
        mPaint.setColor(0xFFd1d1d1);
        canvas.drawLine(mLeftX, mY, mRightX, mY, mPaint);

        drawTicks(canvas);
    }

    /**
     * Get the x-coordinate of the left edge of the bar.
     * 
     * @return x-coordinate of the left edge of the bar
     */
    float getLeftX() {
        return mLeftX;
    }

    /**
     * Get the x-coordinate of the right edge of the bar.
     * 
     * @return x-coordinate of the right edge of the bar
     */
    float getRightX() {
        return mRightX;
    }

    /**
     * Gets the x-coordinate of the nearest tick to the given x-coordinate.
     * 
     * @param x the x-coordinate to find the nearest tick for
     * @return the x-coordinate of the nearest tick
     */
    float getNearestTickCoordinate(Thumb thumb) {

        final int nearestTickIndex = getNearestTickIndex(thumb);

        final float nearestTickCoordinate = mLeftX + (nearestTickIndex * mTickDistance);

        return nearestTickCoordinate;
    }

    /**
     * Gets the zero-based index of the nearest tick to the given thumb.
     * 
     * @param thumb the Thumb to find the nearest tick for
     * @return the zero-based index of the nearest tick
     */
    int getNearestTickIndex(Thumb thumb) {

        final int nearestTickIndex = (int) ((thumb.getX() - mLeftX + mTickDistance / 2f) / mTickDistance);

        return nearestTickIndex;
    }

    /**
     * Set the number of ticks that will appear in the RangeBar.
     * 
     * @param tickCount the number of ticks
     */
    void setTickCount(int tickCount) {

        final float barLength = mRightX - mLeftX;

        mNumSegments = tickCount - 1;
        mTickDistance = barLength / mNumSegments;
    }

    // Private Methods /////////////////////////////////////////////////////////

    /**
     * Draws the tick marks on the bar.
     * 
     * @param canvas Canvas to draw on; should be the Canvas passed into {#link
     *            View#onDraw()}
     */
    private void drawTicks(Canvas canvas) {

        /******* add by birneysky *****/
        float radius = (mTickEndY - mTickStartY) / 2.0f;
        mPaint.setTextSize(22f);
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setColor(0xFF28CE9D);
        /*******************************/
        // Loop through and draw each tick (except final tick).
        for (int i = 0; i < mNumSegments; i++) {
            final float x = i * mTickDistance + mLeftX;
            //canvas.drawLine(x, mTickStartY, x, mTickEndY, mPaint);

            /******* add by birneysky *****/
            canvas.drawArc(new RectF(x - radius, mTickStartY, x+ radius, mTickEndY), 0, 360, true, mPaint);
            String temp = String.format("$ 胡萝卜:%d",i);

            canvas.drawText(bottomText.get(i),x,mTickEndY + radius + 30,mPaint);
            /*******************************/
        }
        // Draw final tick. We draw the final tick outside the loop to avoid any
        // rounding discrepancies.

        //canvas.drawLine(mRightX, mTickStartY, mRightX, mTickEndY, mPaint);
        /******* add by birneysky *****/
        canvas.drawArc(new RectF((mRightX - radius),mTickStartY,mRightX + radius,mTickEndY),0,360,true,mPaint);
        canvas.drawText(bottomText.get(5),mRightX,mTickEndY + radius + 30,mPaint);
        /*******************************/
    }

    public void setText(List<String> bottomText){
        this.bottomText = bottomText;
    }
}
