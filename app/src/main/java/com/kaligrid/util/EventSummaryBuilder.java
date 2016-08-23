package com.kaligrid.util;

import com.kaligrid.model.Event;

import java.util.List;

public class EventSummaryBuilder {

    public static String build(Event event) {
        switch(event.getType()) {
            case EVENT:
                return buildEvent(event);
            case FYI:
                return buildFyi(event);
            case REMINDER:
                return buildReminder(event);
            default:
                return buildEvent(event);
        }
    }

    private static String buildEvent(Event event) {
        if (isMyEvent(event)) {
            List<String> recipients = event.getRecipients();
            int numOfRecipients = recipients.size();

            if ((numOfRecipients == 0)) {
                return event.getTitle();
            } else if (numOfRecipients == 1) {
                return String.format("%s with %s", event.getTitle(), recipients.get(0));
            } else {
                return String.format("%s with %s and %d more", event.getTitle(), recipients.get(0), numOfRecipients - 1);
            }
        } else {
            return String.format("From %s: %s", event.getUser(), event.getTitle());
        }
    }

    private static String buildFyi(Event event) {
        if (isMyEvent(event)) {
            List<String> recipients = event.getRecipients();
            int numOfRecipients = recipients.size();

            if (numOfRecipients == 0) {
                return event.getTitle();
            } else if (numOfRecipients == 1) {
                return String.format("FYI to %s: %s", recipients.get(0), event.getTitle());
            } else if (numOfRecipients == 2) {
                return String.format("FYI to %s and %s: %s", recipients.get(0), recipients.get(1), event.getTitle());
            } else {
                return String.format("FYI to %s and %d more: %s", recipients.get(0), numOfRecipients - 1, event.getTitle());
            }
        } else {
            return String.format("FYI from %s: %s", event.getUser(), event.getTitle());
        }
    }

    private static String buildReminder(Event event) {
        if (isMyEvent(event)) {
            List<String> recipients = event.getRecipients();
            int numOfRecipients = recipients.size();

            if (numOfRecipients == 0) {
                return event.getTitle();
            } else if (numOfRecipients == 1) {
                if (event.isSelfIncluded()) {
                    return String.format("Reminder for you and %s: %s", recipients.get(0), event.getTitle());
                } else {
                    return String.format("Reminder to %s: %s", recipients.get(0), event.getTitle());
                }
            } else if (numOfRecipients == 2) {
                if (event.isSelfIncluded()) {
                    return String.format("Reminder for you and 2 more: %s", event.getTitle());
                } else {
                    return String.format("Reminder to %s and %s: %s", recipients.get(0), recipients.get(1), event.getTitle());
                }
            } else {
                if (event.isSelfIncluded()) {
                    return String.format("Reminder for you and %d more: %s", numOfRecipients, event.getTitle());
                } else {
                    return String.format("Reminder to %s and %d more: %s", recipients.get(0), numOfRecipients - 1, event.getTitle());
                }
            }
        } else {
            return String.format("Reminder from %s: %s", event.getUser(), event.getTitle());
        }
    }

    private static boolean isMyEvent(Event event) {
        return event.getUser().equalsIgnoreCase("Me");
    }
}
