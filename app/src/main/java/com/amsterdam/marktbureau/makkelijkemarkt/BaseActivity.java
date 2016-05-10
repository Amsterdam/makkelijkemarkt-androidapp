/*
 * Copyright (C) 2016 Gemeente Amsterdam, Marktbureau
 */
package com.amsterdam.marktbureau.makkelijkemarkt;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.amsterdam.marktbureau.makkelijkemarkt.api.ApiCall;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Date;

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
    @Bind(R.id.drawer_layout) DrawerLayout mDrawerLayout;

    // reference to the drawerfragment containing the menu
    public DrawerFragment mDrawerFragment;

    // drawertoggle component to control the drawer icon state
    protected ActionBarDrawerToggle mDrawerToggle;

    // common toast object
    protected Toast mToast;

    /**
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set the base activity layout containing the toolbar
        setContentView(R.layout.base_activity);

        // bind the elements to the view
        ButterKnife.bind(this);

        // get a reference to the drawerfragment containing the menu so we can set the active option
        // in the extending activities
        mDrawerFragment = (DrawerFragment) getSupportFragmentManager().findFragmentById(R.id.drawer);

        // link the drawer with the toolbar so we can control the drawer icon state
        mDrawerToggle = new ActionBarDrawerToggle(this,
                mDrawerLayout,
                mToolbar,
                R.string.drawer_open,
                R.string.drawer_close);

        // set the drawer toggle as the drawerlistener
        mDrawerLayout.addDrawerListener(mDrawerToggle);

        // set the toolbar as supportactionbar, with default title disabled and homebutton enabled
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
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

        // handle clicks on the drawer icon
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        // log the user out
        if (id == R.id.action_logout) {
            Utility.logout(this, true);
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

    /**
     * Sync the toggle state after onRestoreInstanceState has occurred
     * @param savedInstanceState
     */
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    /**
     * Handle configuration changes
     * @param newConfig
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    /**
     * Handle unauthorised event received from the api call unauthorised interceptor
     * @param event the received event
     */
    @Subscribe
    public void onUnauthorizedEvent(ApiCall.OnUnauthorizedEvent event) {

        // if we received an event with an error http code show an error toast
        if (event.mCode == 403 || event.mCode == 412) {
            mToast = Utility.showToast(this, mToast, event.mMessage);
            Utility.logout(this, false);
        }
    }

    /**
     * On start of the activity log activity timestamp and register eventbus handlers
     */
    @Override
    public void onStart() {
        super.onStart();

        // keep track of app activity
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = settings.edit();
        editor.putLong(
                getString(R.string.sharedpreferences_key_app_activity_timestamp),
                new Date().getTime());
        editor.apply();

        // register eventbus handlers
        EventBus.getDefault().register(this);
    }

    /**
     * On stop unregister eventbus handlers
     */
    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }
}