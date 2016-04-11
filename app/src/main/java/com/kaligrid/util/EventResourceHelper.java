package com.kaligrid.util;

import com.kaligrid.R;
import com.kaligrid.model.EventType;

public class EventResourceHelper {

    public static int getEventTypeIcon(EventType eventType) {
        switch (eventType) {
            case EVENT:
                return R.drawable.icon_front_event;
            case FYI:
                return R.drawable.icon_front_fyi;
            case REMINDER:
                return R.drawable.icon_front_reminder;
            default:
                return R.drawable.icon_front_event;
        }
    }
}
