package com.kaligrid.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.kaligrid.R;
import com.kaligrid.activity.NewEventActivity;
import com.kaligrid.activity.NewEventBaseActivity;
import com.kaligrid.activity.NewEventMenuActivity;
import com.kaligrid.activity.NewFyiActivity;
import com.kaligrid.activity.NewReminderActivity;
import com.kaligrid.adapter.EventListItemAdapter;
import com.kaligrid.animation.HeightResizeAnimation;
import com.kaligrid.app.App;
import com.kaligrid.calendar.CaldroidFragment;
import com.kaligrid.calendar.CaldroidListener;
import com.kaligrid.calendar.CalendarGestureListener;
import com.kaligrid.model.ContentViewType;
import com.kaligrid.model.Event;
import com.kaligrid.model.EventType;
import com.kaligrid.model.converter.EventsToEventListViewSourceConverter;
import com.kaligrid.model.eventlist.EventListEventItem;
import com.kaligrid.model.eventlist.EventListItem;
import com.kaligrid.model.eventlist.EventListViewSource;
import com.kaligrid.service.EventService;
import com.kaligrid.util.DateTimeUtil;
import com.kaligrid.util.ViewHelper;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hirondelle.date4j.DateTime;

public class ListViewFragment extends TypedBaseViewFragment {

    private static final int RESIZE_ANIMATION_DURATION = 200;

    private static int CALENDAR_HEIGHT_WEEK_VIEW;
    private static int CALENDAR_HEIGHT_MONTH_VIEW;

    @Bind(R.id.calendar) FrameLayout calendarFrameLayout;
    @Bind(R.id.event_list) ListView eventList;

    @Inject EventService eventService;

    private Context context;
    private CaldroidFragment calendarFragment;
    private EventListViewSource eventListViewSource;
    private boolean isEventListListenersInitialized;

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

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        initializeEventList();

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
    }

    private void initializeCalendar() {
        calendarFragment = new CaldroidFragment();
        calendarFragment.setSelectedDate(DateTimeUtil.today());
        calendarFragment.setCalendarGestureListener(new CalendarGestureListener() {
            @Override
            public void onExpandCalendar() {
                showMonthView();
            }

            @Override
            public void onCollapseCalendar() {
                showWeekView();
            }

            @Override
            public void onShowPrevMonth() {
                if (calendarFragment.getCurrentViewMode() == CaldroidFragment.ViewMode.MONTH_VIEW) {
                    calendarFragment.nextMonth();
                }
            }

            @Override
            public void onShowNextMonth() {
                if (calendarFragment.getCurrentViewMode() == CaldroidFragment.ViewMode.MONTH_VIEW) {
                    calendarFragment.nextMonth();
                }
            }
        });

        calendarFragment.setCaldroidListener(new CaldroidListener() {
            @Override
            public void onSelectDate(Date date, View view) {
                DateTime key = DateTimeUtil.dateOnly(date.getTime());
                Integer selectedDateItemIndex = eventListViewSource.getPositionByDate(key);
                if (selectedDateItemIndex != null) {
                    eventList.setSelection(selectedDateItemIndex);
                }
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
                        calendarFragment.refreshView(CaldroidFragment.ViewMode.MONTH_VIEW, calendarFragment.getSelectedDate());
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
                        calendarFragment.refreshView(CaldroidFragment.ViewMode.WEEK_VIEW, calendarFragment.getSelectedDate());
                        ViewHelper.setHeight(calendarFrameLayout, CALENDAR_HEIGHT_WEEK_VIEW);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) { }
                }
        );
        animation.setDuration(RESIZE_ANIMATION_DURATION);
        calendarFrameLayout.startAnimation(animation);
    }

    private void initializeEventList() {
        List<Event> events = eventService.getEvents();
        eventListViewSource = EventsToEventListViewSourceConverter.convert(events, context);
        eventList.setAdapter(new EventListItemAdapter(context, eventListViewSource.getAll()));
        eventList.setSelection(eventListViewSource.getFirstVisibleItem());
    }

    private void initializeEventListTouchListener() {
        eventList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (calendarFragment.getCurrentViewMode() == CaldroidFragment.ViewMode.MONTH_VIEW) {
                    showWeekView();
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (visibleItemCount == 0) {
                    return;
                }

                EventListItem item = eventListViewSource.get(firstVisibleItem);
                DateTime selectedDate = calendarFragment.getSelectedDate();
                DateTime newSelectedDate = item.getDate();

                if (!newSelectedDate.isSameDayAs(selectedDate)) {
                    selectedDate = newSelectedDate.truncate(DateTime.Unit.DAY);
                    calendarFragment.refreshView(calendarFragment.getCurrentViewMode(), selectedDate);
                }
            }
        });

        eventList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object selected = eventList.getItemAtPosition(position);

                if (selected instanceof EventListEventItem) {
                    long selectedEventId = ((EventListEventItem) selected).getEventId();
                    Intent intent = new Intent(context, getEventActivityClass(((EventListEventItem) selected).getEventType()));
                    intent.putExtra(NewEventBaseActivity.EXTRA_KEY_EVENT_ID, selectedEventId);
                    startActivity(intent);
                }
            }

            private Class<?> getEventActivityClass(EventType eventType) {
                switch (eventType) {
                    case FYI:
                        return NewFyiActivity.class;
                    case REMINDER:
                        return NewReminderActivity.class;
                    default:
                        return NewEventActivity.class;
                }
            }
        });
    }
}
