package com.kaligrid.calendar.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.LinearLayout;

public class CalendarLayoutView extends LinearLayout {

    public CalendarLayoutView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private boolean isDownPressed = false;
    private float initialX = 0;
    private float initialY = 0;
    private static final float MOVE_IGNORED_CHANGE_IN_X = 50f;
    private static final float MOVE_IGNORED_CHANGE_IN_Y = 20f;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            isDownPressed = true;
            initialX = event.getX();
            initialY = event.getY();
            Log.d("TEST", String.format("onInterceptTouchEvent: Down (%f, %f)", initialX, initialY));
            return false;
        }

        if (isDownPressed && (event.getAction() == MotionEvent.ACTION_UP)) {
            Log.d("TEST", "onInterceptTouchEvent: Up");
            isDownPressed = false;
            return false;
        }

        if (isDownPressed && (event.getAction() == MotionEvent.ACTION_MOVE)) {
            Log.d("TEST", "onInterceptTouchEvent: Move " + event.getX() + " " + event.getY());

            float changeInX = event.getX() - initialX;
            float changeInY = event.getY() - initialY;
            boolean isXChanged = Math.abs(changeInX) > MOVE_IGNORED_CHANGE_IN_X;
            boolean isYChanged = Math.abs(changeInY) > MOVE_IGNORED_CHANGE_IN_Y;

            if (!isXChanged && !isYChanged) {
                Log.d("TEST", String.format("onInterceptTouchEvent: Move (ignore change; %f, %f)", changeInX, changeInY));
            } else if (!isXChanged) {
                if (changeInY > 0) {
                    Log.d("TEST", String.format("onInterceptTouchEvent: Swipe Down (%f, %f)", changeInX, changeInY));
                } else {
                    Log.d("TEST", String.format("onInterceptTouchEvent: Swipe Up (%f, %f)", changeInX, changeInY));
                }
                return true;
            } else if (!isYChanged) {
                if (changeInX > 0) {
                    Log.d("TEST", String.format("onInterceptTouchEvent: Swipe Right (%f, %f)", changeInX, changeInY));
                } else {
                    Log.d("TEST", String.format("onInterceptTouchEvent: Swipe Left (%f, %f)", changeInX, changeInY));
                }
                return true;
            }

            return false;
        }

        return false;

        // Down -> Up, return false to call select date
        // Down -> Move, analyze swipe coordinates and return true
    }
}
