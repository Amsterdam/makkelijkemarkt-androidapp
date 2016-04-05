/*
 * Copyright (C) 2016 Gemeente Amsterdam, Marktbureau
 */
package com.amsterdam.marktbureau.makkelijkemarkt;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.amsterdam.marktbureau.makkelijkemarkt.api.MakkelijkeMarktApiService;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * MainActivity of the application that presents the home and loginscreen
 * @author marcolangebeeke
 */
public class MainActivity extends AppCompatActivity implements MainFragment.Callback {

    // use classname when logging
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    // the main and login fragments
    private MainFragment mMainFragment;
    private LoginFragment mLoginFragment;

    // create unique fragment instance tags
    private static final String MAIN_FRAGMENT_TAG = LOG_TAG + MainFragment.class.getSimpleName() + "_TAG";
    private static final String LOGIN_FRAGMENT_TAG = LOG_TAG + LoginFragment.class.getSimpleName() + "_TAG";

    // bind layout elements
    @Bind(R.id.toolbar) Toolbar mToolbar;
    @Bind(R.id.toolbar_title) TextView mToolbarTitle;

    /**
     * Set the activity layout and add a fragment to the container
     * @param savedInstanceState activity state bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set the used layout
        setContentView(R.layout.main_activity);

        // bind the elements to the view
        ButterKnife.bind(this);

        // setup a toolbar as supportactionbar
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            // set the title texview
            mToolbarTitle.setText(R.string.login);

            // set the statusbar sransparency
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                Window w = getWindow();
                w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                w.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            }

            // hide the toolbar
            mToolbar.setVisibility(View.GONE);
        }

        // load accounts and add mainfragment, not on rotate
        if (savedInstanceState == null) {

            // create a mainfragment and add it to the framelayout container
            mMainFragment = new MainFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.container, mMainFragment, MAIN_FRAGMENT_TAG);
            transaction.commit();
        } else {

            // re-use existing instance of main fragment
            mMainFragment = (MainFragment) getSupportFragmentManager().findFragmentByTag(MAIN_FRAGMENT_TAG);
        }
    }

    /**
     * Replace the container framelayout with the login fragment
     */
    public void replaceLoginFragment()
    {
        if (getSupportActionBar() != null) {

            // clear the statusbar transparency
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                Window w = getWindow();
                w.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                w.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            }

            // show the toolbar
            mToolbar.setVisibility(View.VISIBLE);
        }

        // replace the fragment using the fragmentmanager
        mLoginFragment = new LoginFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, mLoginFragment, LOGIN_FRAGMENT_TAG);

        // add the fragment name to the backstack to support the back-button
        transaction.addToBackStack(LoginFragment.class.getSimpleName());

        // execute the transaction
        transaction.commit();
    }

    /**
     * Add the actions menu to the actionbar
     * @param menu the menu object to create the options in
     * @return always true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // inflate the menu options as defined in the public actions menu xml
        getMenuInflater().inflate(R.menu.public_actions_menu, menu);

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
        if (id == R.id.action_about) {
            startActivity(new Intent(this, AboutPublicActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Enable the toolbar back-button to support navigating our fragments
     * @return true
     */
    @Override
    public boolean onSupportNavigateUp() {

        // call our backpressed override to support hiding the status- and toolbar
        onBackPressed();

        return true;
    }

    /**
     * Act on back-button pressed
     */
    @Override
    public void onBackPressed() {

        // if the backstack has only one item, it is the homescreen, so we hide the toolbar
        if (getSupportFragmentManager().getBackStackEntryCount() < 2) {
            if (getSupportActionBar() != null) {

                // set the statusbar sransparency
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    Window w = getWindow();
                    w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                    w.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
                }

                // hide the toolbar
                mToolbar.setVisibility(View.GONE);
            }
        }

        super.onBackPressed();
    }

    /**
     * On destroy stop the api service
     */
    @Override
    protected void onDestroy() {

        // stop the api service when the app is closed
        Intent apiServiceIntent = new Intent(this, MakkelijkeMarktApiService.class);
        stopService(apiServiceIntent);

        super.onDestroy();
    }
}