package com.amsterdam.marktbureau.makkelijkemarkt;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements MainFragment.Callback {

    // use classname when logging
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    /**
     * Set the activity layout and add a fragment to the container
     * @param savedInstanceState activity state bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set the used layout
        setContentView(R.layout.main_activity);

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
        // replace the fragment using the fragmentmanager
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);

        // add the fragment name to the backstack to support the back-button
        transaction.addToBackStack(fragment.getClass().getSimpleName());

        // execute the transaction
        transaction.commit();
    }
}
