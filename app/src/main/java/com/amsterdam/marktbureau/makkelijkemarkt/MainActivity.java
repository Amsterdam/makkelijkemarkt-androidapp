package com.amsterdam.marktbureau.makkelijkemarkt;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * MainActivity of the application that presents the home and loginscreen
 */
public class MainActivity extends AppCompatActivity implements MainFragment.Callback {

    // use classname when logging
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    // bind layout elements
    @Bind(R.id.toolbar) Toolbar mToolbar;

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
            TextView titleView = (TextView) findViewById(R.id.toolbar_title);
            titleView.setText(R.string.login);

            // set the statusbar sransparency
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                Window w = getWindow();
                w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                w.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            }

            // hide the toolbar
            mToolbar.setVisibility(View.GONE);
        }

        // if we did not save data to the activity state, add the mainfragment
        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.container, new MainFragment());
            transaction.commit();
        }
    }

    /**
     * Replace the container framelayout with given fragment
     * @param fragment the fragment to place in the container
     */
    public void replaceFragment(Fragment fragment)
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
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment, null);

        // add the fragment name to the backstack to support the back-button
        transaction.addToBackStack(fragment.getClass().getSimpleName());

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
            startActivity(new Intent(this, AboutActivity.class));
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
}
