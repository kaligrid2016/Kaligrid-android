package com.kaligrid.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import butterknife.Bind;
import butterknife.ButterKnife;
import hirondelle.date4j.DateTime;

public class ListViewFragment extends TypedBaseFragment {

    private static final int RESIZE_ANIMATION_DURATION = 200;
    private static final int SWIPE_MARGIN = 120;
    private static final String DATE_FORMAT = "WWW, MMM D";

    private static int CALENDAR_HEIGHT_WEEK_VIEW;
    private static int CALENDAR_HEIGHT_WEEK_VIEW_SWIPE_AREA;
    private static int CALENDAR_HEIGHT_MONTH_VIEW;
    private static int CALENDAR_HEIGHT_MONTH_VIEW_SWIPE_AREA;
    private static int EVENT_LIST_DATE_BORDER_HEIGHT;
    private static int EVENT_LIST_HORIZONTAL_PADDING;

    @Bind(R.id.calendar_wrapper) FrameLayout calendarFrameLayout;
    @Bind(R.id.calendar_swipe_area) View calendarSwipeArea;
    @Bind(R.id.event_list_scroll_view) NestedScrollView eventListView;
    @Bind(R.id.event_list_layout) LinearLayout eventListLayout;

    private Context context;
    private CalendarFragment calendarFragment;
    private boolean isMonthView = true;
    private float initialXTouchPoint;
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
        CALENDAR_HEIGHT_WEEK_VIEW_SWIPE_AREA = getResources().getDimensionPixelSize(R.dimen.calendar_height_week_view_swipe_area);
        CALENDAR_HEIGHT_MONTH_VIEW = getResources().getDimensionPixelSize(R.dimen.calendar_height_month_view);
        CALENDAR_HEIGHT_MONTH_VIEW_SWIPE_AREA = getResources().getDimensionPixelSize(R.dimen.calendar_height_month_view_swipe_area);
        EVENT_LIST_DATE_BORDER_HEIGHT = getResources().getDimensionPixelSize(R.dimen.event_list_date_border_height);
        EVENT_LIST_HORIZONTAL_PADDING = getResources().getDimensionPixelSize(R.dimen.event_list_horizontal_padding);
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
                            calendarFragment.prevMonth();
                        } else if (event.getX() > initialXTouchPoint) {
                            // Swipe right
                            calendarFragment.nextMonth();
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
                        ViewHelper.setHeight(calendarSwipeArea, CALENDAR_HEIGHT_MONTH_VIEW_SWIPE_AREA);
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
                        ViewHelper.setHeight(calendarSwipeArea, CALENDAR_HEIGHT_WEEK_VIEW_SWIPE_AREA);
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
        Map<Long, List<Event>> events = loadEvents();

        for(Map.Entry<Long, List<Event>> eventsForDay : events.entrySet()) {
            addDateTextView(eventListLayout, eventsForDay.getKey());

            for(Event event : eventsForDay.getValue()) {
                eventListLayout.addView(buildEventSummaryView(event));
            }
        }
    }

    private void initializeEventListTouchListener() {
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
        DateTime today = DateTime.today(TimeZone.getDefault());
        Map<Long, List<Event>> events = new LinkedHashMap<>();

        for (int i = 0; i < 10; i++) {
            today = today.plusDays(i);
            long todayInMillis = today.getMilliseconds(TimeZone.getDefault());
            List<Event> todayEvents = new ArrayList<>();
            todayEvents.add(new Event("Test event " + i, EventType.EVENT, todayInMillis));
            todayEvents.add(new Event("Test FYI " + i, EventType.FYI, todayInMillis));
            todayEvents.add(new Event("Test reminder " + i, EventType.REMINDER, todayInMillis));
            events.put(today.getMilliseconds(TimeZone.getDefault()), todayEvents);
        }

        return events;
    }

    private void addDateTextView(LinearLayout layout, Long date) {
        layout.addView(buildDateTextBorderView());
        layout.addView(buildDateTextView(date));
        layout.addView(buildDateTextBorderView());
    }

    private View buildDateTextView(Long date) {
        TextView view = new TextView(context);
        view.setPadding(EVENT_LIST_HORIZONTAL_PADDING, 0, EVENT_LIST_HORIZONTAL_PADDING, 0);
        view.setText(getDateString(date));

        ViewHelper.setTextAppearance(context, view, R.style.EventListDateText);

        return view;
    }

    private View buildDateTextBorderView() {
        ViewGroup.MarginLayoutParams layoutParams = new ViewGroup.MarginLayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                EVENT_LIST_DATE_BORDER_HEIGHT);

        View view = new View(context);
        view.setLayoutParams(layoutParams);
        view.setBackground(ContextCompat.getDrawable(context, R.drawable.line_dotted));
        view.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        return view;
    }

    private TextView buildEventSummaryView(Event event) {
        TextView view = new TextView(context);
        view.setPadding(EVENT_LIST_HORIZONTAL_PADDING, 0, EVENT_LIST_HORIZONTAL_PADDING, 0);
        view.setText(event.getType() + "/" + event.getTitle());
        ViewHelper.setTextAppearance(context, view, R.style.EventListBodyText);
        return view;
    }

    private String getDateString(Long date) {
        DateTime dateTime = DateTime.forInstant(date, TimeZone.getDefault());
        DateTime today = DateTime.today(TimeZone.getDefault());
        StringBuilder dateString = new StringBuilder(dateTime.format(DATE_FORMAT, Locale.getDefault()));

        if (dateTime.isSameDayAs(DateTime.today(TimeZone.getDefault()))) {
            dateString.append(" (").append(getResources().getString(R.string.date_today)).append(")");
        } else if (dateTime.isSameDayAs(today.plusDays(1))) {
            dateString.append(" (").append(getResources().getString(R.string.date_tomorrow)).append(")");
        }

        return dateString.toString();
    }
}
