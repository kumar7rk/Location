package com.geeky7.rohit.location;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Rohit on 9/08/2016.
 */
public class Main {

    public static String getMethodName(final int depth)
    {
        final StackTraceElement[] ste = Thread.currentThread().getStackTrace();
        return ste[1+depth].getMethodName();
    }
    public static void showToast(Context context, String text) {
        Toast.makeText(context, text, Toast.LENGTH_LONG).show();
    }

}
