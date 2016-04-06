package com.kaligrid.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.NestedScrollView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.kaligrid.R;
import com.kaligrid.animation.HeightResizeAnimation;
import com.kaligrid.fragment.calendar.CalendarFragment;

import java.util.TimeZone;

import butterknife.Bind;
import butterknife.ButterKnife;
import hirondelle.date4j.DateTime;

public class ListViewFragment extends TypedBaseFragment {

    private static final int RESIZE_ANIMATION_DURATION = 200;

    @Bind(R.id.event_list_view) NestedScrollView eventListView;
    @Bind(R.id.calendar) FrameLayout calendarFrameLayout;

    private Context context;
    private CalendarFragment calendarFragment;
    private boolean isMonthView = true;
    private float initialYTouchPoint;

    public static ListViewFragment newInstance(Context context) {
        ListViewFragment fragment = new ListViewFragment();
        fragment.setContext(context);
        return fragment;
    }

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_view, container, false);
        ButterKnife.bind(this, view);

        initializeCalendar();
        initializeCalendarExpandingTouchListener(view);
        initializeCalendarCollapsingTouchListener();

        return view;
    }

    private void initializeCalendarExpandingTouchListener(View view) {
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!isMonthView) {
                    switch(event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            if (event.getY() <= calendarFrameLayout.getHeight()) {
                                initialYTouchPoint = event.getY();
                            }
                            break;
                        case MotionEvent.ACTION_UP:
                            if (event.getY() > initialYTouchPoint) {
                                showMonthView();
                            }
                            break;
                    }
                    return true;
                }
                return false;
            }
        });
    }

    private void initializeCalendarCollapsingTouchListener() {
        eventListView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (isMonthView) {
                    showWeekView();
                }
            }
        });
    }

    @Override
    public ContentViewType getType() {
        return ContentViewType.LIST;
    }

    private void initializeCalendar() {
        calendarFragment = new CalendarFragment();
        FragmentTransaction transaction = ((FragmentActivity) context).getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.calendar, calendarFragment);
        transaction.commit();
    }

    private void showMonthView() {
        HeightResizeAnimation animation = new HeightResizeAnimation(calendarFrameLayout,
                getResources().getDimensionPixelSize(R.dimen.calendar_height_week_view),
                getResources().getDimensionPixelSize(R.dimen.calendar_height_month_view),
                new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        calendarFragment.showMonthView();
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) { }

                    @Override
                    public void onAnimationRepeat(Animation animation) { }
                }
        );
        animation.setDuration(RESIZE_ANIMATION_DURATION);
        calendarFrameLayout.startAnimation(animation);

        isMonthView = true;
    }

    private void showWeekView() {
        HeightResizeAnimation animation = new HeightResizeAnimation(calendarFrameLayout,
                getResources().getDimensionPixelSize(R.dimen.calendar_height_month_view),
                getResources().getDimensionPixelSize(R.dimen.calendar_height_week_view),
                new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) { }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        calendarFragment.showWeekView(DateTime.today(TimeZone.getDefault()));
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) { }
                }
        );
        animation.setDuration(RESIZE_ANIMATION_DURATION);
        calendarFrameLayout.startAnimation(animation);

        isMonthView = false;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
