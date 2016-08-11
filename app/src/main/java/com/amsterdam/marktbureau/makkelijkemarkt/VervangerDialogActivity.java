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
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.amsterdam.marktbureau.makkelijkemarkt.adapters.VervangerKoopmannenAdapter;
import com.amsterdam.marktbureau.makkelijkemarkt.data.MakkelijkeMarktProvider;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;

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
    @Bind(R.id.listview_koopmannen) ListView mKoopmannenListView;
    @Bind(R.id.listview_empty) TextView mListViewEmptyTextView;
    @Bind(R.id.dialog_cancel) Button mCancelButton;

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

    /**
     * On click on a listitem in the koopmannen listview get the koopmanid and give it back as a result
     * @param position the position of the clicked item in the listview
     */
    @OnItemClick(R.id.listview_koopmannen)
    public void onKoopmanClick(int position) {

        // get the koopman id from the adapter based on the selected item position
        Cursor selectedKoopman = (Cursor) mKoopmannenAdapter.getItem(position);
        int koopmanId = selectedKoopman.getInt(selectedKoopman.getColumnIndex(
                MakkelijkeMarktProvider.Koopman.COL_ID));

        // add the koopman to the result intent and exit the activity
        Intent returnIntent = new Intent();
        returnIntent.putExtra(DagvergunningFragmentKoopman.VERVANGER_RETURN_INTENT_EXTRA_KOOPMAN_ID, koopmanId);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    /**
     * Load the koopman(nen) that the selected vervanger can work for
     * @param id
     * @param args
     * @return
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        CursorLoader loader = new CursorLoader(this);
        loader.setUri(MakkelijkeMarktProvider.mUriVervangerJoined);
        loader.setSelection(
                MakkelijkeMarktProvider.mTableVervanger + "." + MakkelijkeMarktProvider.Vervanger.COL_VERVANGER_ID + " = ? "
        );
        loader.setSelectionArgs(new String[] {
                String.valueOf(mVervangerId)
        });

        return loader;
    }

    /**
     * Bind the loaded data
     * @param loader
     * @param data
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        // show the empty notice if there are no koopmannen that selected vervanger can replace on current market
        mListViewEmptyTextView.setVisibility(data.getCount() == 0 ? View.VISIBLE : View.GONE);
        mKoopmannenListView.setVisibility(data.getCount() == 0 ? View.GONE : View.VISIBLE);

        mKoopmannenAdapter.swapCursor(data);
    }

    /**
     * Clear the data
     * @param loader
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mKoopmannenAdapter.swapCursor(null);
    }
}