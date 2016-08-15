/**
 * Copyright (C) 2016 X Gemeente
 *                    X Amsterdam
 *                    X Onderzoek, Informatie en Statistiek
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/
 */
package com.amsterdam.marktbureau.makkelijkemarkt;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextPaint;
import android.widget.Button;

/**
 * DialogFragment to inform the user that an app update is available and open the play store app
 * @author marcolangebeeke
 */
public class UpdateAvailableDialogFragment extends DialogFragment {

    // use classname when logging
    private static final String LOG_TAG = UpdateAvailableDialogFragment.class.getSimpleName();

    // name for bundle packagename variable
    private static final String PACKAGE_NAME = "packageName";

    /**
     * Instantiate a new instance of the dialog fragment and pass a bundle with arguments
     * @param packageName string name of the app package we want to open in the play store
     * @return fragment
     */
    public static UpdateAvailableDialogFragment newInstance(String packageName) {

        // instantiate the dialog fragment
        UpdateAvailableDialogFragment fragment = new UpdateAvailableDialogFragment();

        // pass a bundle with the given package name
        Bundle args = new Bundle();
        args.putString(PACKAGE_NAME, packageName);
        fragment.setArguments(args);

        return fragment;
    }

    /**
     * Create the dialog at set its properties
     * @param savedInstanceState bundle containing arguments
     * @return the created alertdialog
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // get app package name from the passed bundle
        final String packageName = getArguments().getString(PACKAGE_NAME);

        // create the dialog
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        dialogBuilder.setIcon(R.drawable.mm_orange);
        dialogBuilder.setTitle(getString(R.string.dialog_app_update_title));
        dialogBuilder.setMessage(getString(R.string.dialog_app_update_message));
        dialogBuilder.setPositiveButton(getString(R.string.dialog_app_update_button_label), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                // onclick launch the play store app with the given app package name
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + packageName)));
                }
            }
        });

        // create the dialog, and onshow add a google play icon left drawable to the positive button
        final AlertDialog dialog = dialogBuilder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.google_play, 0, 0, 0);
                centerImageAndTextInButton(button);
            }
        });

        return dialog;
    }

    /**
     * Center the left drawable and text in a button
     * @param button the button
     */
    public static void centerImageAndTextInButton(Button button) {
        Rect textBounds = new Rect();

        // get text bounds
        CharSequence text = button.getText();
        if (text != null && text.length() > 0) {
            TextPaint textPaint = button.getPaint();
            textPaint.getTextBounds(text.toString(), 0, text.length(), textBounds);
        }

        // set left drawable bounds
        Drawable leftDrawable = button.getCompoundDrawables()[0];
        if (leftDrawable != null) {
            Rect leftBounds = leftDrawable.copyBounds();
            int width = button.getWidth() - (button.getPaddingLeft() + button.getPaddingRight());
            int leftOffset = (width - (textBounds.width() + leftBounds.width()) - button.getCompoundDrawablePadding()) / 2 - button.getCompoundDrawablePadding();
            leftBounds.offset(leftOffset, 0);
            leftDrawable.setBounds(leftBounds);
        }
    }
}