/*
 * Copyright (C) 2016 Gemeente Amsterdam, Marktbureau
 */
package com.amsterdam.marktbureau.makkelijkemarkt;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.amsterdam.marktbureau.makkelijkemarkt.adapters.VervangerKoopmannenAdapter;
import com.amsterdam.marktbureau.makkelijkemarkt.data.MakkelijkeMarktProvider;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 *
 * @author marcolangebeeke
 */
public class VervangerDialogActivity extends FragmentActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    // use classname when logging
    private static final String LOG_TAG = VervangerDialogActivity.class.getSimpleName();

    // unique id for the koopmannen loader
    private static final int KOOPMANNEN_LOADER = 9;

    // cursoradapter for populating the koopmannen litsview with koopmannen that the selected vervanger may work for
    private VervangerKoopmannenAdapter mKoopmannenAdapter;

    // id of the vervanger
    private int mVervangerId = -1;

    // bind layout elements
    @Bind(R.id.vervanger_voorletters_achternaam) TextView mVervangerVoorlettersAchternaamText;
    @Bind(R.id.dialog_cancel) Button mCancelButton;
    @Bind(R.id.listview_koopmannen) ListView mKoopmannenListView;

    /**
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set the nfc scan layout
        setContentView(R.layout.vervanger_dialog_activity);

        // bind the elements to the view
        ButterKnife.bind(this);

        // get the vervanger id from the activity intent
        Intent intent = getIntent();
        if ((intent != null) && (intent.hasExtra(DagvergunningFragmentKoopman.VERVANGER_INTENT_EXTRA_VERVANGER_ID))) {
            int vervangerId = intent.getIntExtra(DagvergunningFragmentKoopman.VERVANGER_INTENT_EXTRA_VERVANGER_ID, 0);
            if (vervangerId != 0) {
                mVervangerId = vervangerId;
            }
        }


        // joined query maken om koopman(nen) te zoeken bij geselecteerde vervanger
        // loader icon tonen tijdens het laden van de koopman(nen)
        // dialog netjes opmaken
        // onclick on a listitem in the dialog close it and call selectKoopman with selected koopman id and empty selectionMethod
        // in dagvergunningfragmentkoopman: in onactivityresult de geselecteerde koopman uitlezen
        // in dagvergunningfragmentkoopman: modify selectKoopman so that when empty selectionMethod was given to not set the membervar (because it was already set before that vervanger dialog)



        // create an adapter for the koopmannen listview
        mKoopmannenAdapter = new VervangerKoopmannenAdapter(this);
        mKoopmannenListView.setAdapter(mKoopmannenAdapter);

        // inititate loading the dagvergunningen from the database with the given koopman id
        getSupportLoaderManager().initLoader(KOOPMANNEN_LOADER, null, this);

        // disable upper-casing the wizard menu buttons
        mCancelButton.setTransformationMethod(null);
    }

    /**
     * Onclick on the cancel button close the vervanger dialog
     */
    @OnClick(R.id.dialog_cancel)
    public void onCancelScan() {
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, returnIntent);
        finish();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        CursorLoader loader = new CursorLoader(this);
        loader.setUri(MakkelijkeMarktProvider.mUriKoopman);
        loader.setSelection(
                MakkelijkeMarktProvider.mTableKoopman + "." + MakkelijkeMarktProvider.Koopman.COL_ID + " = ? "
        );
        loader.setSelectionArgs(new String[] {
                String.valueOf(mVervangerId)
        });

        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mKoopmannenAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mKoopmannenAdapter.swapCursor(null);
    }
}