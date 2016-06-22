package com.kaligrid.calendar.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.LinearLayout;

import com.kaligrid.calendar.CalendarGestureListener;

public class CalendarLayoutView extends LinearLayout {

    private static final float MOVE_IGNORED_CHANGE_IN_X = 50f;
    private static final float MOVE_IGNORED_CHANGE_IN_Y = 20f;

    private CalendarGestureListener calendarGestureListener;
    private boolean isDownPressed;
    private float initialX;
    private float initialY;

    public CalendarLayoutView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Down -> Up, return false to call select date
     * Down -> Move, analyze swipe coordinates and return true
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            Log.d("TEST", String.format("onInterceptTouchEvent: Down (%f, %f)", initialX, initialY));
            startCapturingMotionEvent(event);
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            Log.d("TEST", "onInterceptTouchEvent: Up");
            stopCapturingMotionEvent();
        } else if (isDownPressed && (event.getAction() == MotionEvent.ACTION_MOVE)) {
            Log.d("TEST", "onInterceptTouchEvent: Move " + event.getX() + " " + event.getY());

            float changeInX = event.getX() - initialX;
            float changeInY = event.getY() - initialY;
            boolean isXChanged = Math.abs(changeInX) > MOVE_IGNORED_CHANGE_IN_X;
            boolean isYChanged = Math.abs(changeInY) > MOVE_IGNORED_CHANGE_IN_Y;

            if (!isXChanged && isYChanged) {
                if (changeInY > 0) {
                    Log.d("TEST", String.format("onInterceptTouchEvent: Swipe Down (%f, %f)", changeInX, changeInY));
                    calendarGestureListener.onExpandCalendar();
                } else {
                    Log.d("TEST", String.format("onInterceptTouchEvent: Swipe Up (%f, %f)", changeInX, changeInY));
                    calendarGestureListener.onCollapseCalendar();
                }
                return true;
            } else if (isXChanged && !isYChanged) {
                if (changeInX > 0) {
                    Log.d("TEST", String.format("onInterceptTouchEvent: Swipe Right (%f, %f)", changeInX, changeInY));
                    calendarGestureListener.onShowPrevMonth();
                } else {
                    Log.d("TEST", String.format("onInterceptTouchEvent: Swipe Left (%f, %f)", changeInX, changeInY));
                    calendarGestureListener.onShowNextMonth();
                }
                return true;
            }
        }

        return false;
    }

    private void startCapturingMotionEvent(MotionEvent event) {
        isDownPressed = true;
        initialX = event.getX();
        initialY = event.getY();
    }

    private void stopCapturingMotionEvent() {
        isDownPressed = false;
        initialX = 0;
        initialY = 0;
    }

    public void setCalendarGestureListener(CalendarGestureListener calendarGestureListener) {
        this.calendarGestureListener = calendarGestureListener;
    }
}
