package com.kaligrid.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.kaligrid.R;
import com.kaligrid.adapter.EventListItemAdapter;
import com.kaligrid.animation.HeightResizeAnimation;
import com.kaligrid.app.App;
import com.kaligrid.model.ContentViewType;
import com.kaligrid.model.Event;
import com.kaligrid.model.EventListDateHeaderItem;
import com.kaligrid.model.EventListEventItem;
import com.kaligrid.model.EventListItem;
import com.kaligrid.service.EventService;
import com.kaligrid.util.ViewHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import hirondelle.date4j.DateTime;

public class ListViewFragment extends TypedBaseViewFragment {

    private static final int RESIZE_ANIMATION_DURATION = 200;
    private static final int SWIPE_MARGIN = 120;

    private static int CALENDAR_HEIGHT_WEEK_VIEW;
    private static int CALENDAR_HEIGHT_MONTH_VIEW;

    @Bind(R.id.calendar_wrapper) FrameLayout calendarFrameLayout;
    @Bind(R.id.calendar_swipe_area) View calendarSwipeArea;
    @Bind(R.id.event_list) ListView eventList;

    @Inject EventService eventService;

    private Context context;
    private CalendarFragment calendarFragment;
    private boolean isMonthView = true;
    private float initialXTouchPoint;
    private float initialYTouchPoint;
    private List<EventListItem> eventListItems;

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
        App.getObjectGraph().inject(this);

        initializeConstants();
        initializeCalendar();
        initializeEventList();
        initializeCalendarTouchListener();
        initializeEventListTouchListener();

        return view;
    }

    @Override
    public ContentViewType getType() {
        return ContentViewType.LIST;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onAttach(Activity activity) {
        context = activity;
        super.onAttach(activity);
    }

    private void initializeConstants() {
        CALENDAR_HEIGHT_WEEK_VIEW = getResources().getDimensionPixelSize(R.dimen.calendar_height_week_view);
        CALENDAR_HEIGHT_MONTH_VIEW = getResources().getDimensionPixelSize(R.dimen.calendar_height_month_view);
    }

    private void initializeCalendar() {
        calendarFragment = new CalendarFragment();
        FragmentTransaction transaction = ((FragmentActivity) context).getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.calendar, calendarFragment);
        transaction.commit();
    }

    private void initializeCalendarTouchListener() {
        calendarSwipeArea.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    initialXTouchPoint = event.getX();
                    initialYTouchPoint = event.getY();
                    return true;
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    // If X touch point change is within margin, it's either swipe up or down.
                    // If Y touch point change is within margin, it's either swipe left or right.
                    if (Math.abs(event.getX() - initialXTouchPoint) < SWIPE_MARGIN) {
                        if (isMonthView && (event.getY() < initialYTouchPoint)) {
                            // Swipe up
                            showWeekView();
                        } else if (!isMonthView && (event.getY() > initialYTouchPoint)) {
                            // Swipe down
                            showMonthView();
                        }
                    } else if (isMonthView && (Math.abs(event.getY() - initialYTouchPoint) < SWIPE_MARGIN)) {
                        if (event.getX() < initialXTouchPoint) {
                            // Swipe left
                            calendarFragment.nextMonth();
                        } else if (event.getX() > initialXTouchPoint) {
                            // Swipe right
                            calendarFragment.prevMonth();
                        }
                    }
                }

                return false;
            }
        });
    }

    private void showMonthView() {
        HeightResizeAnimation animation = new HeightResizeAnimation(calendarFrameLayout,
                CALENDAR_HEIGHT_WEEK_VIEW,
                CALENDAR_HEIGHT_MONTH_VIEW,
                new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        calendarFragment.showMonthView();
                        ViewHelper.setHeight(calendarSwipeArea, CALENDAR_HEIGHT_MONTH_VIEW);
                        ViewHelper.setHeight(calendarFrameLayout, CALENDAR_HEIGHT_MONTH_VIEW);
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
                CALENDAR_HEIGHT_MONTH_VIEW,
                CALENDAR_HEIGHT_WEEK_VIEW,
                new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) { }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        calendarFragment.showWeekView(DateTime.today(TimeZone.getDefault()));
                        ViewHelper.setHeight(calendarSwipeArea, CALENDAR_HEIGHT_WEEK_VIEW);
                        ViewHelper.setHeight(calendarFrameLayout, CALENDAR_HEIGHT_WEEK_VIEW);
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
        List<Event> events = loadEvents();
        Map<DateTime, EventListSourceItem> eventListSource = buildEventListSource(events);
        eventListItems = new ArrayList<>();

        for (Map.Entry<DateTime, EventListSourceItem> entry : eventListSource.entrySet()) {
            eventListItems.add(new EventListDateHeaderItem(entry.getKey(), context));
            addAllDayEventListItems(eventListItems, entry.getValue().allDayEvents);
            addTimedEventListItems(eventListItems, entry.getValue().timedEvents);
        }

        eventList.setAdapter(new EventListItemAdapter(context, eventListItems));
    }

    private void initializeEventListTouchListener() {
        eventList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (isMonthView) {
                    showWeekView();
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });
    }

    private List<Event> loadEvents() {
        List<Event> events = eventService.getTestEvents();
        Collections.sort(events, new Event.EventStartDateComparator());

        return events;
    }

    private Map<DateTime, EventListSourceItem> buildEventListSource(List<Event> sortedEvents) {
        Map<DateTime, EventListSourceItem> events = new LinkedHashMap<>();

        for(Event event : sortedEvents) {
            DateTime eventDate = DateTime.forInstant(event.getStartDateTime(), TimeZone.getDefault())
                    .truncate(DateTime.Unit.DAY);

            if (!events.containsKey(eventDate)) {
                events.put(eventDate, new EventListSourceItem());
            }
            events.get(eventDate).addEvent(event);
        }

        return events;
    }

    private void addAllDayEventListItems(List<EventListItem> eventListItems, List<Event> events) {
        boolean showAllDayText = true;
        for (Event event : events) {
            eventListItems.add(new EventListEventItem(event, context, showAllDayText));
            showAllDayText = false;
        }
    }

    private void addTimedEventListItems(List<EventListItem> eventListItems, List<Event> events) {
        boolean showEventTimeText = true;
        DateTime oldEventTime = new DateTime(1, 1, 1, 0, 0, 0, 0);

        for (Event event : events) {
            DateTime newEventTime = DateTime.forInstant(event.getStartDateTime(), TimeZone.getDefault());

            // Show event time text is event time is different.
            if (!oldEventTime.getHour().equals(newEventTime.getHour()) ||
                    !oldEventTime.getMinute().equals(newEventTime.getMinute())) {
                showEventTimeText = true;
            }

            eventListItems.add(new EventListEventItem(event, context, showEventTimeText));

            showEventTimeText = false;
            oldEventTime = newEventTime;
        }
    }

    private class EventListSourceItem {

        private List<Event> allDayEvents;
        private List<Event> timedEvents;

        public EventListSourceItem() {
            allDayEvents = new ArrayList<>();
            timedEvents = new ArrayList<>();
        }

        public void addEvent(Event event) {
            if (event.isAllDayEvent()) {
                allDayEvents.add(event);
            } else {
                timedEvents.add(event);
            }
        }
    }
}
