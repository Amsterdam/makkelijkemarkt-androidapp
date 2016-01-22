package com.amsterdam.marktbureau.makkelijkemarkt;

import android.content.ContentValues;
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

import com.amsterdam.marktbureau.makkelijkemarkt.api.Account;
import com.amsterdam.marktbureau.makkelijkemarkt.api.MakkelijkeMarktApi;
import com.amsterdam.marktbureau.makkelijkemarkt.data.MakkelijkeMarktProvider;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.GsonConverterFactory;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * MainActivity of the application that presents the home and loginscreen
 */
public class MainActivity extends AppCompatActivity implements
        MainFragment.Callback,
        Callback<List<Account>> {

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

        // load accounts and add mainfragment, not on rotate
        if (savedInstanceState == null) {

            // update the local accounts by reloading them from the api
            getAccounts();

            // add the mainfragment to the framelayout container
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








    // @todo add this functionality that does not require callback to the activity but just updates the database into a separate class (service?)

    private void getAccounts() {

        // create the retrofit builder with a gson converter
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.makkelijkemarkt_api_base_url))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // apply the makkelijkemarkt api interface
        MakkelijkeMarktApi api = retrofit.create(MakkelijkeMarktApi.class);

        // set the api function to call for loading the accounts
        Call<List<Account>> call = api.loadAccounts();

        // call the api asynchronously
        call.enqueue(this);
    }

    /**
     * Response from the loadAccounts method arrives here for updating the database
     * @param response response we received from the api
     */
    @Override
    public void onResponse(Response<List<Account>> response) {

        // check the response and update the database
        if (response != null && response.body() != null && response.body().size() > 0) {
            ContentValues[] ContentValuesArray = new ContentValues[response.body().size()];

            for (int i = 0; i < response.body().size(); i++) {
                Account account = response.body().get(i);

                // copy the values and add the to a contentvalues array that can be used in the
                // contentprovider bulkinsert method
                ContentValues accountValues = new ContentValues();
                accountValues.put(MakkelijkeMarktProvider.Account.COL_ID, account.getId());
                accountValues.put(MakkelijkeMarktProvider.Account.COL_NAAM, account.getNaam());
                accountValues.put(MakkelijkeMarktProvider.Account.COL_EMAIL, account.getEmail());
                accountValues.put(MakkelijkeMarktProvider.Account.COL_USERNAME, account.getUsername());
                accountValues.put(MakkelijkeMarktProvider.Account.COL_ROLE, account.getRolesAsString());
                ContentValuesArray[i] = accountValues;
            }

            // delete existing accounts from db
            int deleted = getContentResolver().delete(MakkelijkeMarktProvider.mUriAccount, null, null);
            Utility.log(this, LOG_TAG, "Accounts deleted: " + deleted);

            // insert downloaded accounts into db
            int inserted = getContentResolver().bulkInsert(MakkelijkeMarktProvider.mUriAccount, ContentValuesArray);
            Utility.log(this, LOG_TAG, "Accounts inserted: " + inserted);
        }
    }

    /**
     * On failure of the loadAccounts method log the error message
     * @param t the thrown exception
     */
    @Override
    public void onFailure(Throwable t) {
        Utility.log(this, LOG_TAG, t.getMessage());
    }
}
