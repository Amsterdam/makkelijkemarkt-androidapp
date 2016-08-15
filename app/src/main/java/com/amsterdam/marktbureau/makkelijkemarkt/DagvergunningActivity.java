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

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.amsterdam.marktbureau.makkelijkemarkt.data.MakkelijkeMarktProvider;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

/**
 * Dagvergunning Activity to add/edit/delete dagvergunningen, using a tablayout and fragments
 * in a fragmentpageradapter, get location updates, and manage the location settings of the device
 * and app.
 * @author marcolangebeeke
 */
public class DagvergunningActivity extends BaseActivity implements
        DagvergunningFragmentKoopman.Callback,
        DagvergunningFragmentProduct.Callback,
        DagvergunningFragmentOverzicht.Callback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    // use classname when logging
    private static final String LOG_TAG = DagvergunningActivity.class.getSimpleName();

    // create unique dagvergunningfragent instance tag
    private static final String DAGVERGUNNING_FRAGMENT_TAG = LOG_TAG + DagvergunningFragment.class.getSimpleName() + "_TAG";

    // the dagvergunningfragment
    private DagvergunningFragment mDagvergunningFragment;

    // common toast object
    private Toast mToast;

    // location components
    private GoogleApiClient mGoogleApiClient;
    private Location mCurrentLocation;
    private LocationRequest mLocationRequest;
    private boolean mRequestingLocationUpdates = false;

    // location config
    private static final int DEVICE_LOCATION_SETTING = 98;
    private static final int APP_LOCATION_SETTING = 99;
    private static final int LOCATION_UPDATES_INTERVAL = 1000 * 3;
    private static final int FASTEST_LOCATION_UPDATES_INTERVAL = 1000 * 2;

    /**
     * Get markt naam from the shared prefs, set the title and subtitle, and instantiate the
     * dagvergunning fragment
     * @param savedInstanceState the saved activity state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set the title in the toolbar
        Intent intent = getIntent();
        if ((intent != null) && (intent.hasExtra(MakkelijkeMarktProvider.mTableDagvergunning +
                MakkelijkeMarktProvider.Dagvergunning.COL_ID))) {
            setToolbarTitle(getString(R.string.dagvergunning_edit));
        } else {
            setToolbarTitle(getString(R.string.dagvergunning_add));
        }

        // get selected markt naam from sharedpreferences and set the subtitle in the toolbar
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        String marktNaam = settings.getString(getString(R.string.sharedpreferences_key_markt_naam), "");
        setToolbarSubtitle(marktNaam);

        // create new or get existing instance of dagvergunningfragment
        if (savedInstanceState == null) {
            mDagvergunningFragment = new DagvergunningFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.container, mDagvergunningFragment, DAGVERGUNNING_FRAGMENT_TAG);
            transaction.commit();
        } else {
            mDagvergunningFragment = (DagvergunningFragment) getSupportFragmentManager().findFragmentByTag(
                    DAGVERGUNNING_FRAGMENT_TAG);
        }

        // set the active drawer menu option
        if (mDrawerFragment.isAdded()) {
            mDrawerFragment.checkItem(mDrawerFragment.DRAWER_POSITION_DAGVERGUNNINGEN);
        }

        // replace the drawer hamburger with the back-arrow
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            mDrawerToggle.setDrawerIndicatorEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // connect to the google api client to get the location
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        // create a location request
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(LOCATION_UPDATES_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_LOCATION_UPDATES_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        // check if we have location request permissions
        checkLocationPermissions();
    }

    /**
     * DagvergunningFragmentKoopman callback to inform the dagvergunningfragment that it's ready
     */
    public void onKoopmanFragmentReady() {
        mDagvergunningFragment.koopmanFragmentReady();
    }

    /**
     * DagvergunningFragmentKoopman callback to retrieve the changed koopman fragment data and
     * populate it again based on the new data
     */
    public void onKoopmanFragmentUpdated() {
        mDagvergunningFragment.getAndSetKoopmanFragmentValues();
    }

    /**
     * DagvergunningFragmentKoopman callback to update meldingen based on loaded koopman
     */
    public void onMeldingenUpdated() {
        mDagvergunningFragment.populateMeldingen();
    }

    /**
     * DagvergunningFragmentProduct callback to inform the dagvergunningfragment that it's ready
     */
    public void onProductFragmentReady() {
        mDagvergunningFragment.productFragmentReady();
    }

    /**
     * DagvergunningFragmentOverzicht callback to inform the dagvergunningfragment that it's ready
     */
    public void onOverzichtFragmentReady() {
        mDagvergunningFragment.overzichtFragmentReady();
    }

    /**
     * Set the visibility of the progressbar in the dagvergunningfragment
     * @param visibility the visibility as View.VISIBLE | View.GONE | View.INVISIBLE
     */
    public void setProgressbarVisibility(int visibility){
        mDagvergunningFragment.setProgressbarVisibility(visibility);
    }

    /**
     * Check the device- and app location permissions and show location settings dialogs if needed
     */
    private void checkLocationPermissions() {

        // create a settingsrequest to get the device location setting
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);

        // disable to show 'NEVER' option in case we need to request device location permission using a dialog
        builder.setAlwaysShow(true);

        // fire the request to get the setting and act on the result
        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
                final Status status = locationSettingsResult.getStatus();
                switch (status.getStatusCode()) {

                    // device settings are not satisfied, but this can be fixed by showing the user a settings dialog
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            status.startResolutionForResult(DagvergunningActivity.this, DEVICE_LOCATION_SETTING);
                        } catch (IntentSender.SendIntentException e) {}
                        break;

                    // device location settings are satisfied, so we check the app permission
                    case LocationSettingsStatusCodes.SUCCESS:

                        // check if the app itself has location permission
                        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                            // if we refused app permission to the location before we show a dialog
                            // with an explanation first
                            if (ActivityCompat.shouldShowRequestPermissionRationale(DagvergunningActivity.this,
                                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                                // show a alert dialog containing an explanation, and on close click
                                // ask for the app location permission
                                new AlertDialog.Builder(DagvergunningActivity.this)
                                        .setIcon(R.drawable.mm_orange)
                                        .setTitle(getString(R.string.title_dagvergunning_app_location_refused))
                                        .setMessage(getString(R.string.notice_dagvergunning_app_location_refused))
                                        .setCancelable(false)
                                        .setPositiveButton(getString(R.string.notice_dagvergunning_app_location_refused_ok), new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {

                                                // ask for app location permission
                                                ActivityCompat.requestPermissions(DagvergunningActivity.this,
                                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                                        APP_LOCATION_SETTING);
                                            }})
                                        .show();
                            } else {

                                // ask for app location permission
                                ActivityCompat.requestPermissions(DagvergunningActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        APP_LOCATION_SETTING);
                            }
                        }
                        break;

                    // device settings are not satisfied. however, we have no way to fix the settings so we won't show the dialog
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        break;
                }
            }
        });
    }

    /**
     * Try to get the last known location from the Google Api Client location api
     */
    private void getLastKnownLocation() {
        if (mGoogleApiClient.isConnected()) {
            try {
                mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                if (mCurrentLocation != null) {
                    setLocation();
                }
            } catch (SecurityException e) {
                Utility.log(getApplicationContext(), LOG_TAG, "getLastLocation SecurityException: " + e.getMessage());
            }
        }
    }

    /**
     * Request the location api of the Google Api Client to start sending location update events
     */
    protected void startLocationUpdates() {
        if (mGoogleApiClient.isConnected() && !mRequestingLocationUpdates) {
            try {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
                mRequestingLocationUpdates = true;
            } catch (SecurityException e) {
                Utility.log(getApplicationContext(), LOG_TAG, "requestLocationUpdates SecurityException: " + e.getMessage());
            }
        }
    }

    /**
     * Tell the location api of the Google Api Client to stop sending location update events
     */
    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        mRequestingLocationUpdates = false;
    }

    /**
     * Set the retrieved location coordinates in the dagvergunning fragment
     */
    private void setLocation() {
        mDagvergunningFragment.setDagvergunningLocation(mCurrentLocation);
    }

    /**
     * Receive the result of the device location setting dialog
     * @param requestCode the request code that was given when launching the settings dialog
     * @param resultCode the result of the users' choice
     * @param data an optionally sent intent
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == DEVICE_LOCATION_SETTING) {

            // if the permission was not accpeted, inform the user that the device location setting is mandatory
            if (resultCode != Activity.RESULT_OK) {
                mToast = Utility.showToast(getApplicationContext(), mToast, getString(R.string.notice_dagvergunning_device_location_refused));
            }

            // check the location settings again, to force the user to accept it if he did not, and
            // to trigger the app permissions check when the device permissions were accepted
            checkLocationPermissions();
        }
    }

    /**
     * On connect of the Google Api Client get the last known location and start sending location update events
     * @param bundle
     */
    @Override
    public void onConnected(@Nullable Bundle bundle) {

        // get last known location
        getLastKnownLocation();

        // start receiving location updates
        startLocationUpdates();
    }

    /**
     * Log an error when the connection with the Google Api Client was suspended
     */
    @Override
    public void onConnectionSuspended(int i) {
        Utility.log(getApplicationContext(), LOG_TAG, "GoogleApiClient onConnectionSuspended: " + i);
    }

    /**
     * Log an error when the connection with the Google Api Client failed
     * @param connectionResult Parcelable containing a error details
     */
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Utility.log(getApplicationContext(), LOG_TAG, "GoogleApiClient onConnectionFailed: " + connectionResult.getErrorMessage());
    }

    /**
     * Update the location in the dagvergunning fragment when we receive an update
     * @param location the updated location
     */
    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        setLocation();
    }

    /**
     * Connect with the Google Api Client on start of the activity
     */
    @Override
    public void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    /**
     * Disconnect from the Google Api Client on stop of the activity
     */
    @Override
    public void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    /**
     * Stop sending location updates when the activity is paused
     */
    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    /**
     * Start sending location updates when the activity is resumed
     */
    @Override
    protected void onResume() {
        super.onResume();
        startLocationUpdates();
    }
}