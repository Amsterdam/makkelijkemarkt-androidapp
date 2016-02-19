/*
 * Copyright (C) 2016 Gemeente Amsterdam, Marktbureau
 */
package com.amsterdam.marktbureau.makkelijkemarkt;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author marcolangebeeke
 */
public class Utility {

    /**
     * Show a toast for a short while
     * @param text String
     */
    public static Toast showToast(Context context, Toast toast, String text) {

        // if the toast is already active, cancel it
        if (toast != null) {
            toast.cancel();
        }

        // set the toast text and show it for a short moment
        toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        toast.show();

        return toast;
    }

    /**
     * Check if the network is available
     * @return boolean
     */
    public static boolean isNetworkAvailable(Context context) {

        // get the connectivity manager service
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        // get info about the network
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        // return true if we have networkinfo and are connected
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
            return true;
        }  else {
            return false;
        }
    }

    /**
     * Hide the softkeyboard from an activity
     * @param activity Activity
     */
    public static void hideKeyboard(Activity activity) {

        // get the inputmanager from the activity
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);

        // find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();

        // if no view currently has focus, create a new one, just so we can grab a window token from it
        if(view == null) {
            view = new View(activity);
        }

        // and hide the keyboard
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /**
     * Hide the softkeyboard from a context (usually fragment)
     * @param context Context
     * @param view View
     */
    public static void hideKeyboard(Context context, View view) {

        // get the inputmanager from the given context
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);

        // and hide the keyboard
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /**
     * Log helper to show the package name, component name, and message
     * @param context a context needed for retrieving the app name from the strings resource
     * @param logTag the name of the component
     * @param message the message we want to log
     */
    public static void log(Context context, String logTag, String message) {
        Log.d(context.getString(R.string.log_tag_package), logTag + " " + context.getString(R.string.log_tag_separator) + " " + message);
    }

    /**
     * Helper to join a list of string items separating them using given separator
     * @param list list containing the strings
     * @param separator the string to separate the listitems
     * @return string containing <separator> separated items
     */
    public static String listToCsv(List<String> list, String separator) {
        List<String> rolesCopy = new ArrayList<String>(list);

        StringBuilder builder = new StringBuilder();

        if (rolesCopy.size() > 0) {
            builder.append(rolesCopy.remove(0));
            for (String role : rolesCopy) {
                builder.append(separator);
                builder.append(role);
            }
        }

        return builder.toString();
    }

    /**
     * Map a string resource id to a color resource id based on name
     * @param context the context to get the resources from
     * @param sollicitatieStatus the sollicitatie status string
     * @return the mapped color resource id
     */
    public static int getSollicitatieStatusColor(Context context, String sollicitatieStatus) {
        int sollicitatieStatusColor = R.color.sollicitatie_status_undefined;

        if (sollicitatieStatus.equals(context.getString(R.string.sollicitatie_status_lot))) {
            sollicitatieStatusColor = R.color.sollicitatie_status_lot;
        } else if (sollicitatieStatus.equals(context.getString(R.string.sollicitatie_status_soll))) {
            sollicitatieStatusColor = R.color.sollicitatie_status_soll;
        } else if (sollicitatieStatus.equals(context.getString(R.string.sollicitatie_status_vkk))) {
            sollicitatieStatusColor = R.color.sollicitatie_status_vkk;
        } else if (sollicitatieStatus.equals(context.getString(R.string.sollicitatie_status_vpl))) {
            sollicitatieStatusColor = R.color.sollicitatie_status_vpl;
        }

        return sollicitatieStatusColor;
    }

    /**
     * Open/close meldingen view
     * @param view view that needs to collapse
     * @param collapse collapse or open
     */
    public static void collapseView(View view, boolean collapse) {
        ViewGroup.LayoutParams lp = view.getLayoutParams();

        if (collapse) {
            lp.height = 0;
        } else {
            lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        }

        view.setLayoutParams(lp);
    }

    /**
     * Get a resource id from a resource name and type
     * @param name The name of the resource as a string
     * @param type the type of resource (R.id.class, etc.)
     * @return a unique id as an int
     */
    public static int getResId(String name, Class<?> type) {
        try {
            Field idField = type.getDeclaredField(name);
            return idField.getInt(idField);
        } catch (Exception e) {
            return -1;
        }
    }
}