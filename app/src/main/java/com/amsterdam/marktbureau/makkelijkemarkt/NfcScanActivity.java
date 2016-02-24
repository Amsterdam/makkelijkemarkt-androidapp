/*
 * Copyright (C) 2016 Gemeente Amsterdam, Marktbureau
 */
package com.amsterdam.marktbureau.makkelijkemarkt;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.widget.Button;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 *
 * @author marcolangebeeke
 */
public class NfcScanActivity extends Activity {

    // use classname when logging
    private static final String LOG_TAG = NfcScanActivity.class.getSimpleName();

    // nfc scan components
    private NfcAdapter mAdapter;
    private PendingIntent mPendingIntent;
    private IntentFilter[] mFilters;

    // bind layout elements
    @Bind(R.id.scan_cancel) Button mCancelButton;

    /**
     * Instantiate the view and nfc scan components
     * @param savedInstanceState saved state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set the nfc scan layout
        setContentView(R.layout.nfcscan_activity);

        // bind the elements to the view
        ButterKnife.bind(this);

        // create an adapter for processing nfc tags
        mAdapter = NfcAdapter.getDefaultAdapter(this);

        // create a pending intent that will be attached to the nfc adapter and used to transfer the
        // nfc tag details to the activity when once the adapter detected a nfc tag near the device
        mPendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        // filter for tag discovery only (this is the broadest way of filtering because the
        // mifare classic tag we use does not support reading/writing memory, but only can be
        // detected being there and we can read the UID)
        mFilters = new IntentFilter[] { new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED) };

        // disable upper-casing the wizard menu buttons
        mCancelButton.setTransformationMethod(null);
    }

    /**
     * When we receive an intent from the nfc adapter try to read the nfc tag and send the uid back
     * to the caller
     * @param intent intent from the nfc adapter
     */
    @Override
    public void onNewIntent(Intent intent) {

        // get the tag from the received intent
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        if (tag != null) {
            Intent returnIntent = new Intent();

            // if we found a tag and uid add it to the return intent to send it back to the caller
            byte[] uid = tag.getId();
            if (uid != null) {
                returnIntent.putExtra(getString(R.string.scan_nfc_result_uid), Utility.bin2hex(uid));
            }

            // add the result and exit the activity
            setResult(Activity.RESULT_OK, returnIntent);
            finish();
        }
    }

    /**
     * When running in the foreground enable this activity's nfc adapter to receive intents for a
     * given set of intent-filters. Tag discovered intent-filter in our case.
     */
    @Override
    public void onResume() {
        super.onResume();
        if (mAdapter != null) {
            mAdapter.enableForegroundDispatch(this, mPendingIntent, mFilters, null);
        }
    }

    /**
     * When in paused mode disable receiving nfc intents
     */
    @Override
    public void onPause() {
        super.onPause();
        if (mAdapter != null) {
            mAdapter.disableForegroundDispatch(this);
        }
    }

    @OnClick(R.id.scan_cancel)
    public void onCancelScan() {
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, returnIntent);
        finish();
    }
}