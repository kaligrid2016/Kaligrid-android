package com.kaligrid.calendar;

import java.util.Date;

import android.view.MotionEvent;
import android.view.View;

/**
 * CaldroidListener inform when user clicks on a valid date (not within disabled
 * dates, and valid between min/max dates)
 * <p/>
 * The method onChangeMonth is optional, user can always override this to listen
 * to month change event
 *
 * @author thomasdao
 */
public abstract class CaldroidListener {

    /**
     * Inform client user has clicked on a date
     */
    public abstract void onSelectDate(Date date, View view);


    /**
     * Inform client user has long clicked on a date
     */
    public void onLongClickDate(Date date, View view) {
        // Do nothing
    }


    /**
     * Inform client that calendar has changed month
     */
    public void onChangeMonth(int month, int year) {
        // Do nothing
    }

    /**
     * Inform client that CaldroidFragment view has been created and views are
     * no longer null. Useful for customization of button and text views
     */
    public void onCaldroidViewCreated() {
        // Do nothing
    }
}
