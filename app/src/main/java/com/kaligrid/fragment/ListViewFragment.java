package com.kaligrid.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.NestedScrollView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kaligrid.R;
import com.kaligrid.animation.HeightResizeAnimation;
import com.kaligrid.fragment.calendar.CalendarFragment;
import com.kaligrid.model.ContentViewType;
import com.kaligrid.model.Event;
import com.kaligrid.model.EventType;
import com.kaligrid.util.ViewHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import butterknife.Bind;
import butterknife.ButterKnife;
import hirondelle.date4j.DateTime;

public class ListViewFragment extends TypedBaseFragment {

    private static final int RESIZE_ANIMATION_DURATION = 200;

    @Bind(R.id.calendar) FrameLayout calendarFrameLayout;
    @Bind(R.id.event_list_scroll_view) NestedScrollView eventListView;
    @Bind(R.id.event_list_layout) LinearLayout eventListLayout;

    private Context context;
    private CalendarFragment calendarFragment;
    private boolean isMonthView = true;
    private float initialYTouchPoint;

    public static ListViewFragment newInstance(Context context) {
        ListViewFragment fragment = new ListViewFragment();
        fragment.setContext(context);
        return fragment;
    }

    public void setContext(Context context) {
        this.context = context;
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

        initializeCalendar(view);
        initializeEventList();

        return view;
    }

    @Override
    public ContentViewType getType() {
        return ContentViewType.LIST;
    }

    private void initializeCalendar(View view) {
        calendarFragment = new CalendarFragment();
        FragmentTransaction transaction = ((FragmentActivity) context).getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.calendar, calendarFragment);
        transaction.commit();

        initializeCalendarExpandingTouchListener(view);
        initializeCalendarCollapsingTouchListener();
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

    private Map<Long, List<Event>> loadEvents() {
        Calendar today = Calendar.getInstance();
        Map<Long, List<Event>> events = new HashMap<>();

        List<Event> todayEvents = new ArrayList<>();
        todayEvents.add(new Event("Test event", EventType.EVENT, today.getTimeInMillis()));
        todayEvents.add(new Event("Test FYI", EventType.FYI, today.getTimeInMillis()));
        todayEvents.add(new Event("Test reminder", EventType.REMINDER, today.getTimeInMillis()));
        events.put(today.getTimeInMillis(), todayEvents);

        today.add(Calendar.DATE, 1);
        List<Event> tomorrowEvents = new ArrayList<>();
        tomorrowEvents.add(new Event("Test event", EventType.EVENT, today.getTimeInMillis()));
        tomorrowEvents.add(new Event("Test FYI", EventType.FYI, today.getTimeInMillis()));
        tomorrowEvents.add(new Event("Test reminder", EventType.REMINDER, today.getTimeInMillis()));
        events.put(today.getTimeInMillis(), tomorrowEvents);

        today.add(Calendar.DATE, 1);
        List<Event> tomorrowEvents2 = new ArrayList<>();
        tomorrowEvents2.add(new Event("Test event", EventType.EVENT, today.getTimeInMillis()));
        tomorrowEvents2.add(new Event("Test FYI", EventType.FYI, today.getTimeInMillis()));
        tomorrowEvents2.add(new Event("Test reminder", EventType.REMINDER, today.getTimeInMillis()));
        events.put(today.getTimeInMillis(), tomorrowEvents2);

        today.add(Calendar.DATE, 1);
        List<Event> tomorrowEvents3 = new ArrayList<>();
        tomorrowEvents3.add(new Event("Test event", EventType.EVENT, today.getTimeInMillis()));
        tomorrowEvents3.add(new Event("Test FYI", EventType.FYI, today.getTimeInMillis()));
        tomorrowEvents3.add(new Event("Test reminder", EventType.REMINDER, today.getTimeInMillis()));
        events.put(today.getTimeInMillis(), tomorrowEvents3);

        today.add(Calendar.DATE, 1);
        List<Event> tomorrowEvents4 = new ArrayList<>();
        tomorrowEvents4.add(new Event("Test event", EventType.EVENT, today.getTimeInMillis()));
        tomorrowEvents4.add(new Event("Test FYI", EventType.FYI, today.getTimeInMillis()));
        tomorrowEvents4.add(new Event("Test reminder", EventType.REMINDER, today.getTimeInMillis()));
        events.put(today.getTimeInMillis(), tomorrowEvents4);

        today.add(Calendar.DATE, 1);
        List<Event> tomorrowEvents5 = new ArrayList<>();
        tomorrowEvents5.add(new Event("Test event", EventType.EVENT, today.getTimeInMillis()));
        tomorrowEvents5.add(new Event("Test FYI", EventType.FYI, today.getTimeInMillis()));
        tomorrowEvents5.add(new Event("Test reminder", EventType.REMINDER, today.getTimeInMillis()));
        events.put(today.getTimeInMillis(), tomorrowEvents5);

        return events;
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

    private void initializeEventList() {
        Map<Long, List<Event>> events = loadEvents();

        for(Map.Entry<Long, List<Event>> eventsForDay : events.entrySet()) {
            eventListLayout.addView(buildDateTextView(eventsForDay.getKey()));

            for(Event event : eventsForDay.getValue()) {
                eventListLayout.addView(buildEventSummaryView(event));
            }
        }
    }

    private TextView buildDateTextView(Long key) {
        TextView dateText = new TextView(context);
        dateText.setText(key.toString());
        ViewHelper.setTextAppearance(context, dateText, R.style.EventListDateText);
        return dateText;
    }

    private TextView buildEventSummaryView(Event event) {
        TextView eventSummaryView = new TextView(context);
        eventSummaryView.setText(event.getType() + "/" + event.getTitle());
        ViewHelper.setTextAppearance(context, eventSummaryView, R.style.EventListBodyText);
        return eventSummaryView;
    }
}
