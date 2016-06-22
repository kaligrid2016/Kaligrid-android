package com.kaligrid.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import com.kaligrid.activity.NewEventMenuActivity;
import com.kaligrid.adapter.EventListItemAdapter;
import com.kaligrid.animation.HeightResizeAnimation;
import com.kaligrid.app.App;
import com.kaligrid.calendar.CaldroidFragment;
import com.kaligrid.calendar.CaldroidListener;
import com.kaligrid.model.ContentViewType;
import com.kaligrid.model.Event;
import com.kaligrid.model.EventListDateHeaderItem;
import com.kaligrid.model.EventListEventItem;
import com.kaligrid.model.EventListItem;
import com.kaligrid.service.EventService;
import com.kaligrid.util.ViewHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hirondelle.date4j.DateTime;

public class ListViewFragment extends TypedBaseViewFragment {

    private static final int RESIZE_ANIMATION_DURATION = 200;
    private static final int SWIPE_MARGIN = 120;

    private static int CALENDAR_HEIGHT_WEEK_VIEW;
    private static int CALENDAR_HEIGHT_MONTH_VIEW;
    private static DateTime TODAY;

    @Bind(R.id.calendar) FrameLayout calendarFrameLayout;
    @Bind(R.id.event_list) ListView eventList;

    @Inject EventService eventService;

    private Context context;
    private CaldroidFragment calendarFragment;
    private boolean isMonthView = true;
    private List<EventListItem> eventListItems;
    boolean isEventListListenersInitialized = false;

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

        selectedDate = TODAY;

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isEventListListenersInitialized) {
            initializeEventListTouchListener();
            isEventListListenersInitialized = true;
        }
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

    @OnClick({R.id.new_button})
    public void onNewButtonClick(View v) {
        Intent intent = new Intent(getActivity(), NewEventMenuActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

    private void initializeConstants() {
        CALENDAR_HEIGHT_WEEK_VIEW = getResources().getDimensionPixelSize(R.dimen.calendar_height_week_view);
        CALENDAR_HEIGHT_MONTH_VIEW = getResources().getDimensionPixelSize(R.dimen.calendar_height_month_view);
        TODAY = DateTime.today(TimeZone.getDefault());
    }

    private void initializeCalendar() {
        calendarFragment = new CaldroidFragment();

        calendarFragment.setCaldroidListener(new CaldroidListener() {
            @Override
            public void onSelectDate(Date date, View view) {
                Log.d("TEST", "onSelectDate: " + date.toString());
            }
        });

        FragmentTransaction transaction = ((FragmentActivity) context).getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.calendar, calendarFragment);
        transaction.commit();
    }

    public void showMonthView() {
        HeightResizeAnimation animation = new HeightResizeAnimation(calendarFrameLayout,
                CALENDAR_HEIGHT_WEEK_VIEW,
                CALENDAR_HEIGHT_MONTH_VIEW,
                new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        calendarFragment.showMonthView(selectedDate);
//                        ViewHelper.setHeight(calendarSwipeArea, CALENDAR_HEIGHT_MONTH_VIEW);
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

    public void showWeekView() {
        HeightResizeAnimation animation = new HeightResizeAnimation(calendarFrameLayout,
                CALENDAR_HEIGHT_MONTH_VIEW,
                CALENDAR_HEIGHT_WEEK_VIEW,
                new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) { }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        calendarFragment.showWeekView(selectedDate);
//                        ViewHelper.setHeight(calendarSwipeArea, CALENDAR_HEIGHT_WEEK_VIEW);
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

    public boolean isMonthViewShown() {
        return isMonthView;
    }

    private void initializeEventList() {
        List<Event> events = loadEvents();
        Map<DateTime, EventListSourceItem> eventListSource = buildEventListSource(events);
        eventListItems = new ArrayList<>();
        int firstItemIndex = -1;

        for (Map.Entry<DateTime, EventListSourceItem> entry : eventListSource.entrySet()) {
            DateTime date = entry.getKey();

            // First event that's today or later should be the first event shown.
            if ((firstItemIndex < 0) && date.gteq(TODAY)) {
                firstItemIndex = eventListItems.size();
            }

            eventListItems.add(new EventListDateHeaderItem(date, context));
            addAllDayEventListItems(eventListItems, entry.getValue().allDayEvents);
            addTimedEventListItems(eventListItems, entry.getValue().timedEvents);
        }

        eventList.setAdapter(new EventListItemAdapter(context, eventListItems));
        eventList.setSelection(firstItemIndex);
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

    private DateTime selectedDate = null;

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
                if (visibleItemCount == 0) {
                    return;
                }

                EventListItem item = eventListItems.get(firstVisibleItem);
                DateTime newSelectedDate = item.getDate();
                if (!newSelectedDate.isSameDayAs(selectedDate)) {
                    selectedDate = newSelectedDate.truncate(DateTime.Unit.DAY);
                    if (isMonthView) {
                        calendarFragment.showMonthView(selectedDate);
                    } else {
                        calendarFragment.showWeekView(selectedDate);
                    }

                }
            }
        });
    }

    public void nextMonth() {
        calendarFragment.nextMonth();
    }

    public void prevMonth() {
        calendarFragment.nextMonth();
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
