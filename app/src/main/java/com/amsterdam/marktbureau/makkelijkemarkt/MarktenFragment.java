/*
 * Copyright (C) 2016 Gemeente Amsterdam, Marktbureau
 */
package com.amsterdam.marktbureau.makkelijkemarkt;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.amsterdam.marktbureau.makkelijkemarkt.data.MakkelijkeMarktProvider;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnItemClick;

/**
 *
 * @author marcolangebeeke
 */
public class MarktenFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    // use classname when logging
    private static final String LOG_TAG = MarktenFragment.class.getSimpleName();

    // bind layout elements
    @Bind(R.id.listview_markten) ListView mMarktenListView;

    // unique id for the markten loader
    private static final int MARKTEN_LOADER = 1;

    // cursoradapter for populating the markten litsview with markten from the database
    private SimpleCursorAdapter mMarktenAdapter;

    /**
     * Constructor
     */
    public MarktenFragment() {
    }

    /**
     * Inflate the markten_fragment layout containing the markten listview
     * @param inflater LayoutInflator
     * @param container ViewGroup
     * @param savedInstanceState Bundle
     * @return View
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // get the markten fragment
        View mainView = inflater.inflate(R.layout.markten_fragment, container, false);

        // bind the elements to the view
        ButterKnife.bind(this, mainView);

        // create an adapter for the account spinner
        mMarktenAdapter = new SimpleCursorAdapter(
                getContext(),
                R.layout.markten_list_item,
                null,
                new String[] { MakkelijkeMarktProvider.Markt.COL_NAAM },
                new int[] { R.id.markt_naam },
                0);

        // attach the adapter to the markten listview
        mMarktenListView.setAdapter(mMarktenAdapter);

        // inititate loading the markten from the database
        getLoaderManager().initLoader(MARKTEN_LOADER, null, this);

        return mainView;
    }

    @OnItemClick(R.id.listview_markten)
    public void onItemClick(int position, long id) {
        Utility.log(getContext(), LOG_TAG, "position="+ position +" id="+ id);

        // @todo store the selected markt/id in the shared preferences

        // open the dagvergunningen activity
        Intent intent = new Intent(getActivity(), DagvergunningenActivity.class);
        startActivity(intent);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        // create the loader
        CursorLoader loader = new CursorLoader(getActivity());
        loader.setUri(MakkelijkeMarktProvider.mUriMarkt);
        loader.setProjection(new String[]{
                MakkelijkeMarktProvider.Account.COL_ID,
                MakkelijkeMarktProvider.Account.COL_NAAM
        });
        loader.setSortOrder(
                MakkelijkeMarktProvider.Account.COL_NAAM +" ASC"
        );

        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mMarktenAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mMarktenAdapter.swapCursor(null);
    }
}