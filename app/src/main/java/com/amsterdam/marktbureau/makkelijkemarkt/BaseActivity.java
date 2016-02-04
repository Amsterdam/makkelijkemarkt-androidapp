/*
 * Copyright (C) 2016 Gemeente Amsterdam, Marktbureau
 */
package com.amsterdam.marktbureau.makkelijkemarkt;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Base activity for the main app activities. It will create the toolbar, actions menu, drawer, and
 * other common functionality
 * @author marcolangebeeke
 */
public class BaseActivity extends AppCompatActivity {

    // use classname when logging
    private static final String LOG_TAG = BaseActivity.class.getSimpleName();

    // bind layout elements
    @Bind(R.id.toolbar) Toolbar mToolbar;
    @Bind(R.id.toolbar_title) TextView mTitleView;
    @Bind(R.id.toolbar_subtitle) TextView mSubtitleView;

    /**
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set the base activity layout containing the toolbar (@todo and later the drawer?)
        setContentView(R.layout.base_activity);

        // bind the elements to the view
        ButterKnife.bind(this);

        // set the toolbar as supportactionbar, with default title disabled and homebutton enabled
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * Add the actions menu to the actionbar
     * @param menu the menu object to create the options in
     * @return always true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // inflate the menu options as defined in the private actions menu xml
        getMenuInflater().inflate(R.menu.private_actions_menu, menu);

        return true;
    }

    /**
     * Handle option menu item selection
     * @param item MenuItem
     * @return boolean
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // open the about activity
        if (id == R.id.action_logout) {

            Utility.log(this, LOG_TAG, "Logging out...");

            return true;
        }

        // open the about activity
        if (id == R.id.action_about) {
            startActivity(new Intent(this, AboutPrivateActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Set the title in the textview of the custom toolbar layout
     * @param title string containing the title
     */
    protected void setToolbarTitle(String title) {
        if (getSupportActionBar() != null) {
            mTitleView.setText(title);
        }
    }

    /**
     * Set the subtitle in the textview of the custom toolbar layout
     * @param subTitle string containing the subtitle
     */
    protected void setToolbarSubtitle(String subTitle) {
        if (getSupportActionBar() != null) {
            mSubtitleView.setText(subTitle);

            // also un-hide the subtitle to ensure correct vertical alignment
            mSubtitleView.setVisibility(TextView.VISIBLE);
        }
    }
}