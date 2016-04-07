package com.kaligrid.util;

import android.content.Context;
import android.os.Build;
import android.widget.TextView;

public class ViewHelper {

    public static void setTextAppearance(Context context, TextView textView, int resId) {
        if (Build.VERSION.SDK_INT < 23) {
            textView.setTextAppearance(context, resId);
        } else {
            textView.setTextAppearance(resId);
        }
    }
}
