/*
 * Copyright (C) 2016 Gemeente Amsterdam, Marktbureau
 */
package com.amsterdam.marktbureau.makkelijkemarkt;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

/**
 *
 * @author marcolangebeeke
 */
public class DagvergunningActivity extends BaseActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        DagvergunningFragmentKoopman.Callback,
        DagvergunningFragmentProduct.Callback,
        DagvergunningFragmentOverzicht.Callback {

    // use classname when logging
    private static final String LOG_TAG = DagvergunningActivity.class.getSimpleName();

    // create unique dagvergunningfragent instance tag
    private static final String DAGVERGUNNING_FRAGMENT_TAG = LOG_TAG + DagvergunningFragment.class.getSimpleName() + "_TAG";

    // the dagvergunningfragment
    private DagvergunningFragment mDagvergunningFragment;

    // google play services api client
    private GoogleApiClient mGoogleApiClient;

    /**
     * Get markt naam from the shared prefs, set the title and subtile, and instantiate the
     * dagvergunning fragment
     * @param savedInstanceState the saved activity statew
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // get selected markt naam from sharedpreferences
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        String marktNaam = settings.getString(getString(R.string.sharedpreferences_key_markt_naam), "");

        // set the title and subtitle in the toolbar
        setToolbarTitle(getString(R.string.dagvergunning));
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

        // TODO: Implement functionality that disables the location scanning when app in background (to save battery life)

        // build the google play services client
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    /**
     * Connect to the google play services client
     */
    @Override
    public void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    /**
     * Disconnect to the google play services client
     */
    @Override
    public void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    /**
     * When we are connected to the google play services api client start retrieving the locations
     * and set them in the dagvergunning fragment
     * @param bundle
     */
    @Override
    public void onConnected(Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            Utility.log(this, LOG_TAG, "Geen toestemming!");

//            // check permissions
//            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.ACCESS_FINE_LOCATION }, REQUEST_LOCATION);

        } else {

//            Utility.log(this, LOG_TAG, "Wel toestemming!");

            // get the last location
            Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (lastLocation != null) {
                mDagvergunningFragment.setRegistratieGeoLocatie(lastLocation);
            }

            // create a location request that will run on interval every 10 seconds
            LocationRequest locationRequest = new LocationRequest();
            locationRequest.setInterval(10000);
            locationRequest.setFastestInterval(5000);
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

            // TODO: implement functionality to request permission

//            // request permission
//            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
//            PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());

            // subscribe to start receiving location updates
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, this);
        }
    }

    /**
     * When we receive a new location from the googleapiclient sent it to the dagvergunning fragment
     * @param location the received location
     */
    @Override
    public void onLocationChanged(Location location) {
        Utility.log(this, LOG_TAG, "GoogleApiClient onLocationChanged");

        if (location != null) {
            mDagvergunningFragment.setRegistratieGeoLocatie(location);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Utility.log(this, LOG_TAG, "GoogleApiClient onConnectionSuspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Utility.log(this, LOG_TAG, "GoogleApiClient onConnectionFailed");
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
}