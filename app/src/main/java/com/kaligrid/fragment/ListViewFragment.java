package com.kaligrid.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.NestedScrollView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kaligrid.R;
import com.kaligrid.animation.HeightResizeAnimation;
import com.kaligrid.fragment.calendar.CalendarFragment;
import com.kaligrid.model.ContentViewType;
import com.kaligrid.model.Event;
import com.kaligrid.model.EventType;
import com.kaligrid.util.EventResourceHelper;
import com.kaligrid.util.EventSummaryBuilder;
import com.kaligrid.util.ViewHelper;

import java.util.ArrayList;
import java.util.Collections;
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
    private static final String TIME_FORMAT = "h12:mm a";

    private static int CALENDAR_HEIGHT_WEEK_VIEW;
    private static int CALENDAR_HEIGHT_WEEK_VIEW_SWIPE_AREA;
    private static int CALENDAR_HEIGHT_MONTH_VIEW;
    private static int CALENDAR_HEIGHT_MONTH_VIEW_SWIPE_AREA;
    private static int EVENT_LIST_DATE_BORDER_HEIGHT;
    private static int EVENT_LIST_HORIZONTAL_PADDING;
    private static int EVENT_LIST_VERTICAL_PADDING;
    private static int EVENT_LIST_EVENT_SUMMARY_LEFT_PADDING;
    private static int EVENT_LIST_TIME_TEXT_WIDTH;
    private static int EVENT_LIST_TYPE_ICON_SIZE;
    private static int EVENT_LIST_TYPE_ICON_MARGIN_TOP;

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
        EVENT_LIST_VERTICAL_PADDING = getResources().getDimensionPixelSize(R.dimen.event_list_vertical_padding);
        EVENT_LIST_EVENT_SUMMARY_LEFT_PADDING = getResources().getDimensionPixelSize(R.dimen.event_list_event_summary_left_padding);
        EVENT_LIST_TIME_TEXT_WIDTH = getResources().getDimensionPixelSize(R.dimen.event_list_time_text_width);
        EVENT_LIST_TYPE_ICON_SIZE = getResources().getDimensionPixelSize(R.dimen.event_list_type_icon_size);
        EVENT_LIST_TYPE_ICON_MARGIN_TOP = getResources().getDimensionPixelSize(R.dimen.event_list_type_icon_margin_top);
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
        List<Event> events = loadEvents();
        Map<DateTime, EventListSourceItem> eventListSource = buildEventListSource(events);

        for (Map.Entry<DateTime, EventListSourceItem> entry : eventListSource.entrySet()) {
            addDateHeadingTextView(eventListLayout, entry.getKey());
            addAllDayEventViews(eventListLayout, entry.getValue().allDayEvents);
            addTimedEventViews(eventListLayout, entry.getValue().timedEvents);
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

    private List<Event> loadEvents() {
        DateTime today = DateTime.now(TimeZone.getDefault());
        List<Event> events = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            today = today.plusDays(1);
            long todayInMillis = today.getMilliseconds(TimeZone.getDefault());

            events.add(new Event.Builder("Yong", "My long test event/My long test event/My long test event/My long test event/My long test event.",
                    EventType.EVENT, todayInMillis).recipients("daniel", "seula").isSelfIncluded(true).build());

            events.add(new Event.Builder("Seula", "Seula's all day event",
                    EventType.EVENT, todayInMillis).isAllDayEvent(true).isSelfIncluded(true).recipients("Yong").build());

            events.add(new Event.Builder("Yong", "My vacation",
                    EventType.FYI, todayInMillis).isAllDayEvent(true).recipients("Daniel").build());

            events.add(new Event.Builder("Yong", "Out sick",
                    EventType.FYI, todayInMillis).isAllDayEvent(true).recipients("Daniel", "Xingy").build());

            events.add(new Event.Builder("Yong", "Traveling",
                    EventType.FYI, todayInMillis).isAllDayEvent(true).recipients("Daniel", "Seula", "Brad").build());

            events.add(new Event.Builder("Daniel", "Daniel's vacation",
                    EventType.FYI, todayInMillis).isAllDayEvent(true).recipients("Yong", "Seula", "Brad").build());

            events.add(new Event.Builder("Brad", "Brad's workout",
                    EventType.REMINDER, todayInMillis - 10).recipients("yong").build());

            events.add(new Event.Builder("Yong", "My grocery shopping reminder",
                    EventType.REMINDER, todayInMillis - 360000).isSelfIncluded(true).build());

            events.add(new Event.Builder("Yong", "My tax return",
                    EventType.REMINDER, todayInMillis - 360000).recipients("Xingy").isSelfIncluded(true).build());
        }

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

    private void addDateHeadingTextView(LinearLayout layout, DateTime date) {
        layout.addView(buildDateTextBorderView());
        layout.addView(buildDateTextView(date));
        layout.addView(buildDateTextBorderView());
    }

    private void addAllDayEventViews(LinearLayout layout, List<Event> events) {
        boolean showAllDayText = true;

        for (Event event : events) {
            layout.addView(buildEventSummaryView(event, showAllDayText));
            showAllDayText = false;
        }
    }

    private void addTimedEventViews(LinearLayout layout, List<Event> events) {
        boolean showEventTimeText = true;
        DateTime oldEventTime = new DateTime(1, 1, 1, 0, 0, 0, 0);

        for (Event event : events) {
            DateTime newEventTime = DateTime.forInstant(event.getStartDateTime(), TimeZone.getDefault());

            // Show event time text is event time is different.
            if (!oldEventTime.getHour().equals(newEventTime.getHour()) ||
                    !oldEventTime.getMinute().equals(newEventTime.getMinute())) {
                showEventTimeText = true;
            }

            layout.addView(buildEventSummaryView(event, showEventTimeText));

            showEventTimeText = false;
            oldEventTime = newEventTime;
        }
    }

    private View buildDateTextView(DateTime date) {
        TextView view = new TextView(context);
        view.setPadding(EVENT_LIST_HORIZONTAL_PADDING, 0, EVENT_LIST_HORIZONTAL_PADDING, 0);
        view.setText(getDateString(date));

        ViewHelper.setTextAppearance(context, view, R.style.EventListDateText);

        return view;
    }

    private View buildDateTextBorderView() {
        View view = new View(context);
        view.setLayoutParams(new DrawerLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, EVENT_LIST_DATE_BORDER_HEIGHT));
        view.setBackground(ContextCompat.getDrawable(context, R.drawable.line_dotted));
        view.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        return view;
    }

    private View buildEventSummaryView(Event event, boolean showEventTimeText) {
        // Create layout.
        LinearLayout layout = new LinearLayout(context);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layout.setLayoutParams(layoutParams);
        layout.setPadding(EVENT_LIST_HORIZONTAL_PADDING, EVENT_LIST_VERTICAL_PADDING,
                EVENT_LIST_HORIZONTAL_PADDING, EVENT_LIST_VERTICAL_PADDING);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setGravity(Gravity.CENTER_VERTICAL);

        // Add individual layout components.
        layout.addView(buildEventTimeTextView(event, showEventTimeText));
        layout.addView(buildEventTypeImageView(event));
        layout.addView(buildEventSummaryTextView(event));

        return layout;
    }

    private View buildEventTimeTextView(Event event, boolean showEventTimeText) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                EVENT_LIST_TIME_TEXT_WIDTH, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.TOP;

        TextView view = new TextView(context);
        view.setLayoutParams(layoutParams);
        view.setMaxLines(1);

        if (showEventTimeText) {
            if (event.isAllDayEvent()) {
                view.setText(getResources().getText(R.string.event_list_text_all_day));
            } else {
                DateTime eventStartTime = DateTime.forInstant(event.getStartDateTime(), TimeZone.getDefault());
                view.setText(eventStartTime.format(TIME_FORMAT, Locale.getDefault()));
            }
        }

        return view;
    }

    private View buildEventTypeImageView(Event event) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                EVENT_LIST_TYPE_ICON_SIZE, EVENT_LIST_TYPE_ICON_SIZE);
        layoutParams.gravity = Gravity.TOP;
        layoutParams.topMargin = EVENT_LIST_TYPE_ICON_MARGIN_TOP;

        ImageView view = new ImageView(context);
        view.setLayoutParams(layoutParams);
        view.setImageResource(EventResourceHelper.getEventTypeIcon(event.getType()));

        return view;
    }

    private View buildEventSummaryTextView(Event event) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.TOP;

        TextView view = new TextView(context);
        view.setLayoutParams(layoutParams);
        view.setPadding(EVENT_LIST_EVENT_SUMMARY_LEFT_PADDING, 0, 0, 0);
        view.setMaxLines(3);
        view.setText(EventSummaryBuilder.build(event));
        ViewHelper.setTextAppearance(context, view, R.style.EventListBodyText);

        return view;
    }

    private String getDateString(DateTime date) {
        DateTime today = DateTime.today(TimeZone.getDefault());
        StringBuilder dateString = new StringBuilder(date.format(DATE_FORMAT, Locale.getDefault()));

        if (date.isSameDayAs(DateTime.today(TimeZone.getDefault()))) {
            dateString.append(" (").append(getResources().getString(R.string.date_today)).append(")");
        } else if (date.isSameDayAs(today.plusDays(1))) {
            dateString.append(" (").append(getResources().getString(R.string.date_tomorrow)).append(")");
        }

        return dateString.toString();
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
