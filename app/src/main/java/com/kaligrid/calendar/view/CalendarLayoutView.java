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
            startCapturingMotionEvent(event);
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            stopCapturingMotionEvent();
        } else if (isDownPressed && (event.getAction() == MotionEvent.ACTION_MOVE)) {
            float changeInX = event.getX() - initialX;
            float changeInY = event.getY() - initialY;
            boolean isXChanged = Math.abs(changeInX) > MOVE_IGNORED_CHANGE_IN_X;
            boolean isYChanged = Math.abs(changeInY) > MOVE_IGNORED_CHANGE_IN_Y;

            if (!isXChanged && isYChanged) {
                if (changeInY > 0) {
                    calendarGestureListener.onExpandCalendar();
                } else {
                    calendarGestureListener.onCollapseCalendar();
                }
                return true;
            } else if (isXChanged && !isYChanged) {
                if (changeInX > 0) {
                    calendarGestureListener.onShowPrevMonth();
                } else {
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
