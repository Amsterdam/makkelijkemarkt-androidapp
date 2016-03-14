/*
 * Copyright (C) 2016 Gemeente Amsterdam, Marktbureau
 */
package com.amsterdam.marktbureau.makkelijkemarkt.data;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.amsterdam.marktbureau.makkelijkemarkt.Utility;

import java.util.HashMap;
import java.util.List;

import de.triplet.simpleprovider.AbstractProvider;
import de.triplet.simpleprovider.Column;
import de.triplet.simpleprovider.Table;

/**
 * Database provider for caching the content from the makkelijke markt api locally in an sqlite db
 * @author marcolangebeeke
 */
public class MakkelijkeMarktProvider extends AbstractProvider {

    // use classname when logging
    private static final String LOG_TAG = MakkelijkeMarktProvider.class.getSimpleName();

    // get package name
    public static final String mPackageName = MakkelijkeMarktProvider.class.getPackage().getName();

    // create a base uri from the package name
    public static final Uri mBaseUri = Uri.parse("content://" + mPackageName);

    // table names
    public static final String mTableAccount = "account";
    public static final String mTableMarkt = "markt";
    public static final String mTableKoopman = "koopman";
    public static final String mTableDagvergunning = "dagvergunning";
    public static final String mTableNotitie = "notitie";
    public static final String mTableSollicitatie = "sollicitatie";

    // uris for the tables
    public static Uri mUriAccount = mBaseUri.buildUpon().appendPath(mTableAccount).build();
    public static Uri mUriMarkt = mBaseUri.buildUpon().appendPath(mTableMarkt).build();
    public static Uri mUriKoopman = mBaseUri.buildUpon().appendPath(mTableKoopman).build();
    public static Uri mUriDagvergunning = mBaseUri.buildUpon().appendPath(mTableDagvergunning).build();
    public static Uri mUriNotitie = mBaseUri.buildUpon().appendPath(mTableNotitie).build();
    public static Uri mUriSollicitatie = mBaseUri.buildUpon().appendPath(mTableSollicitatie).build();

    // other uris
    public static Uri mUriDagvergunningJoined =
            mBaseUri.buildUpon().appendPath(mTableDagvergunning + "joined").build();
    public static Uri mUriKoopmanJoined =
            mBaseUri.buildUpon().appendPath(mTableKoopman + "joined").build();
    public static Uri mUriKoopmanJoinedGroupByErkenningsnummer =
            mBaseUri.buildUpon().appendPath(mTableKoopman + "joinedgroupbyerkenningsnummer").build();
    public static Uri mUriKoopmanJoinedGroupBySollicitatienummer =
            mBaseUri.buildUpon().appendPath(mTableKoopman + "joinedgroupbysollicitatienummer").build();

    /**
     * Get the content provider authority name
     * @return String containing the authority name
     */
    @Override
    protected String getAuthority() {
        return MakkelijkeMarktProvider.class.getPackage().toString();
    }

    /**
     * Get the version number of the table model definition
     * @return int containing the schema version
     */
    @Override
    protected int getSchemaVersion() {
        return 1;
    }

    /**
     * /account - Account table columns definition
     */
    @Table(mTableAccount)
    public class Account {

        @Column(value = Column.FieldType.INTEGER, primaryKey = true)
        public static final String COL_ID = "_id";

        @Column(Column.FieldType.TEXT)
        public static final String COL_NAAM = "naam";

        @Column(Column.FieldType.TEXT)
        public static final String COL_EMAIL = "email";

        @Column(Column.FieldType.TEXT)
        public static final String COL_USERNAME = "username";

        @Column(Column.FieldType.TEXT)
        public static final String COL_ROLE = "role";
    }

    /**
     * /markt - Markt table columns definition
     */
    @Table(mTableMarkt)
    public class Markt {

        @Column(value = Column.FieldType.INTEGER, primaryKey = true)
        public static final String COL_ID = "_id";

        @Column(Column.FieldType.TEXT)
        public static final String COL_NAAM = "naam";

        @Column(Column.FieldType.TEXT)
        public static final String COL_GEO_AREA = "geo_area";

        @Column(Column.FieldType.TEXT)
        public static final String COL_AFKORTING = "afkorting";

        @Column(Column.FieldType.TEXT)
        public static final String COL_SOORT = "soort";

        @Column(Column.FieldType.TEXT)
        public static final String COL_MARKT_DAGEN = "markt_dagen";

        @Column(Column.FieldType.INTEGER)
        public static final String COL_STANDAARD_KRAAM_AFMETING = "standaard_kraam_afmeting";

        @Column(Column.FieldType.INTEGER)
        public static final String COL_EXTRA_METERS_MOGELIJK = "extra_meters_mogelijk";

        @Column(Column.FieldType.TEXT)
        public static final String COL_AANWEZIGE_OPTIES = "aanwezige_opties";
    }

    /**
     * /koopman - Koopman table columns definition
     */
    @Table(mTableKoopman)
    public class Koopman {

        @Column(value = Column.FieldType.INTEGER, primaryKey = true)
        public static final String COL_ID = "_id";

        @Column(Column.FieldType.TEXT)
        public static final String COL_ERKENNINGSNUMMER = "erkenningsnummer";

        @Column(Column.FieldType.TEXT)
        public static final String COL_VOORLETTERS = "voorletters";

        @Column(Column.FieldType.TEXT)
        public static final String COL_ACHTERNAAM = "achternaam";

        @Column(Column.FieldType.TEXT)
        public static final String COL_EMAIL = "email";

        @Column(Column.FieldType.TEXT)
        public static final String COL_TELEFOON = "telefoon";

        @Column(Column.FieldType.TEXT)
        public static final String COL_FOTO_URL = "foto_url";

        @Column(Column.FieldType.TEXT)
        public static final String COL_FOTO_MEDIUM_URL = "foto_medium_url";

        @Column(Column.FieldType.TEXT)
        public static final String COL_STATUS = "status";

        @Column(Column.FieldType.INTEGER)
        public static final String COL_PERFECTVIEWNUMMER = "perfectviewnummer";

        @Column(Column.FieldType.TEXT)
        public static final String COL_PAS_UID = "pas_uid";
    }

    /**
     * /sollicitatie - Sollicitatie table columns definition
     */
    @Table(mTableSollicitatie)
    public class Sollicitatie {

        @Column(value = Column.FieldType.INTEGER, primaryKey = true)
        public static final String COL_ID = "_id";

        @Column(Column.FieldType.INTEGER)
        public static final String COL_MARKT_ID = "markt_id";

        @Column(Column.FieldType.INTEGER)
        public static final String COL_KOOPMAN_ID = "koopman_id";

        @Column(Column.FieldType.INTEGER)
        public static final String COL_SOLLICITATIE_NUMMER = "sollicitatie_nummer";

        @Column(Column.FieldType.TEXT)
        public static final String COL_STATUS = "status";

        @Column(Column.FieldType.INTEGER)
        public static final String COL_INSCHRIJF_DATUM = "inschrijf_datum";

        @Column(Column.FieldType.INTEGER)
        public static final String COL_DOORGEHAALD = "doorgehaald";

        @Column(Column.FieldType.TEXT)
        public static final String COL_DOORGEHAALD_REDEN = "doorgehaald_reden";

        @Column(Column.FieldType.INTEGER)
        public static final String COL_PERFECT_VIEW_NUMMER = "perfect_view_nummer";

        @Column(Column.FieldType.TEXT)
        public static final String COL_VASTE_PLAATSEN = "vaste_plaatsen";

        @Column(Column.FieldType.INTEGER)
        public static final String COL_AANTAL_3METER_KRAMEN = "aantal_3meter_kramen";

        @Column(Column.FieldType.INTEGER)
        public static final String COL_AANTAL_4METER_KRAMEN = "aantal_4meter_kramen";

        @Column(Column.FieldType.INTEGER)
        public static final String COL_AANTAL_EXTRA_METERS = "aantal_extra_meters";

        @Column(Column.FieldType.INTEGER)
        public static final String COL_AANTAL_ELEKTRA = "aantal_elektra";

        @Column(Column.FieldType.INTEGER)
        public static final String COL_AFVALEILAND = "afvaleiland";
    }

    /**
     * /dagvergunning - Dagvergunning table columns definition
     */
    @Table(mTableDagvergunning)
    public class Dagvergunning {

        @Column(value = Column.FieldType.INTEGER, primaryKey = true)
        public static final String COL_ID = "_id";

        @Column(Column.FieldType.INTEGER)
        public static final String COL_MARKT_ID = "markt_id";

        @Column(Column.FieldType.INTEGER)
        public static final String COL_KOOPMAN_ID = "koopman_id";

        @Column(Column.FieldType.INTEGER)
        public static final String COL_SOLLICITATIE_ID = "sollicitatie_id";

        @Column(Column.FieldType.INTEGER)
        public static final String COL_REGISTRATIE_ACCOUNT_ID = "registratie_account_id";

        @Column(Column.FieldType.TEXT)
        public static final String COL_DAG = "dag";

        @Column(Column.FieldType.TEXT)
        public static final String COL_ERKENNINGSNUMMER_INVOER_METHODE = "erkenningsnummer_invoer_methode";

        @Column(Column.FieldType.TEXT)
        public static final String COL_REGISTRATIE_DATUMTIJD = "registratie_datumtijd";

        @Column(Column.FieldType.REAL)
        public static final String COL_REGISTRATIE_GEOLOCATIE_LAT = "registratie_geolocatie_lat";

        @Column(Column.FieldType.REAL)
        public static final String COL_REGISTRATIE_GEOLOCATIE_LONG = "registratie_geolocatie_long";

        @Column(Column.FieldType.TEXT)
        public static final String COL_ERKENNINGSNUMMER_INVOER_WAARDE = "erkenningsnummer_invoer_waarde";

        @Column(Column.FieldType.TEXT)
        public static final String COL_AANWEZIG = "aanwezig";

        @Column(Column.FieldType.TEXT)
        public static final String COL_STATUS_SOLLICITATIE = "status_solliciatie";

        @Column(Column.FieldType.INTEGER)
        public static final String COL_DOORGEHAALD = "doorgehaald";

        @Column(Column.FieldType.INTEGER)
        public static final String COL_EXTRA_METERS = "extra_meters";

        @Column(Column.FieldType.INTEGER)
        public static final String COL_TOTALE_LENGTE = "totale_lengte";

        @Column(Column.FieldType.TEXT)
        public static final String COL_NOTITIE = "notitie";

        @Column(Column.FieldType.TEXT)
        public static final String COL_AANMAAK_DATUMTIJD = "aanmaak_datumtijd";

        @Column(Column.FieldType.INTEGER)
        public static final String COL_AANTAL_ELEKTRA = "aantal_elektra";

        @Column(Column.FieldType.INTEGER)
        public static final String COL_AFVALEILAND = "afvaleiland";

        @Column(Column.FieldType.INTEGER)
        public static final String COL_AANTAL3METER_KRAMEN_VAST = "aantal3meter_kramen_vast";

        @Column(Column.FieldType.INTEGER)
        public static final String COL_AANTAL4METER_KRAMEN_VAST = "aantal4meter_kramen_vast";

        @Column(Column.FieldType.INTEGER)
        public static final String COL_AANTAL_EXTRA_METERS_VAST = "aantal_extra_meters_vast";

        @Column(Column.FieldType.INTEGER)
        public static final String COL_TOTALE_LENGTE_VAST = "totale_lengte_vast";

        @Column(Column.FieldType.INTEGER)
        public static final String COL_AANTAL_ELEKTRA_VAST = "aantal_elektra_vast";

        @Column(Column.FieldType.INTEGER)
        public static final String COL_AFVALEILAND_VAST = "afvaleiland_vast";

        @Column(Column.FieldType.INTEGER)
        public static final String COL_AANTAL3METER_KRAMEN = "aantal3meter_kramen";

        @Column(Column.FieldType.INTEGER)
        public static final String COL_AANTAL4METER_KRAMEN = "aantal4meter_kramen";
    }

    /**
     * /notitie - Notitie table columns definition
     */
    @Table(mTableNotitie)
    public class Notitie {

        @Column(value = Column.FieldType.INTEGER, primaryKey = true)
        public static final String COL_ID = "_id";

        @Column(Column.FieldType.INTEGER)
        public static final String COL_MARKT_ID = "markt_id";

        @Column(Column.FieldType.TEXT)
        public static final String COL_DAG = "dag";

        @Column(Column.FieldType.TEXT)
        public static final String COL_BERICHT = "bericht";

        @Column(Column.FieldType.TEXT)
        public static final String COL_AANGEMAAKT_DATUMTIJD = "aangemaakt_datumtijd";

        @Column(Column.FieldType.REAL)
        public static final String COL_AANGEMAAKT_GEOLOCATIE_LAT = "aangemaakt_geolocatie_lat";

        @Column(Column.FieldType.REAL)
        public static final String COL_AANGEMAAKT_GEOLOCATIE_LONG = "aangemaakt_geolocatie_long";

        @Column(Column.FieldType.INTEGER)
        public static final String COL_AFGEVINKT = "afgevinkt";

        @Column(Column.FieldType.TEXT)
        public static final String COL_AFGEVINKT_DATUMTIJD = "afgevinkt_datumtijd";

        @Column(Column.FieldType.INTEGER)
        public static final String COL_VERWIJDERD = "verwijderd";

        @Column(Column.FieldType.TEXT)
        public static final String COL_VERWIJDERD_DATUMTIJD = "verwijderd_datumtijd";
    }

    /**
     * Override of the insert function of the AbstractProvider class, in a way that it uses the
     * SQLite insertWithOnConflict function that replaces the record when trying to insert insert
     * a duplicate column value
     * @param uri Uri
     * @param values ContentValues
     * @return Uri to the inserted row
     */
    @Override
    public Uri insert(Uri uri, ContentValues values)
    {
        // check if we have at least one path segment
        List<String> segments = uri.getPathSegments();
        if (segments == null || segments.size() != 1) {
            return null;
        }

        // try to insert or replace record
        long rowId = 0;
        mDatabase.beginTransaction();
        try {
            rowId = mDatabase.insertWithOnConflict(
                    segments.get(0),
                    null,
                    values,
                    SQLiteDatabase.CONFLICT_REPLACE);

            // commit the transaction
            mDatabase.setTransactionSuccessful();

            // if no exception was thrown and we received an id, the row was inserted succesfully
            if (rowId > -1) {

                // send notification to loader
                if (getContext() != null) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }

                // return the uri where the inserted row can be found
                return ContentUris.withAppendedId(uri, rowId);
            }

        } catch (SQLiteException e) {
            Utility.log(getContext(), LOG_TAG, e.getMessage());
        } finally {
            mDatabase.endTransaction();
        }

        return null;
    }

    /**
     * Override the super class bulkinsert method so we can act differently on insert conflicts, notify
     * bound cursor loaders, and we run all the inserts transaction-based, which is much faster
     * @param uri the uri (table) to update
     * @param values the values we want to insert
     * @return amount of records inserted
     */
    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        int insertCount = 0;

        // check if we have at least one path segment (table name)
        List<String> segments = uri.getPathSegments();
        if (segments == null || segments.size() != 1) {
            return 0;
        }

        // start the transaction
        mDatabase.beginTransaction();
        try {
            // insert values and replace them if they already exist
            for(ContentValues value : values) {
                long _id = mDatabase.insertWithOnConflict(
                        segments.get(0),
                        null,
                        value,
                        SQLiteDatabase.CONFLICT_REPLACE);

                if (_id != -1) {
                    insertCount++;
                }
            }
            mDatabase.setTransactionSuccessful();
        } catch (SQLiteException e) {
            Utility.log(getContext(), LOG_TAG, e.getMessage());
        } finally {
            mDatabase.endTransaction();
        }

        // if records were inserted we notify the loaders bound to the given uri
        if (insertCount > 0 && getContext() != null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return insertCount;
    }

    /**
     * Catch non-standard table queries
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor cursor;
        Uri notificationUri = uri;

        // @todo refactor this to use a uri matcher?

        if (uri.getPath().equals(mUriDagvergunningJoined.getPath())) {

            // query the dagvergunningen table joined with it's linked tables with the given arguments
            cursor = queryDagvergunningenJoined(uri, projection, selection, selectionArgs, sortOrder, null);

            // subscribe the cursor to a different notification uri
            notificationUri = MakkelijkeMarktProvider.mUriDagvergunning;

        } else if (uri.getPath().equals(mUriKoopmanJoined.getPath())) {

            // query the koopman table joined with the sollicitatie table
            cursor = queryKoopmanJoined(uri, projection, selection, selectionArgs, sortOrder, null);
            notificationUri = MakkelijkeMarktProvider.mUriKoopman;

        } else if (uri.getPath().equals(mUriKoopmanJoinedGroupByErkenningsnummer.getPath())) {

            // query the koopman table joined with the sollicitatie table and grouped by erkennings nummer
            cursor = queryKoopmanJoined(uri, projection, selection, selectionArgs, sortOrder, Koopman.COL_ERKENNINGSNUMMER);
            notificationUri = MakkelijkeMarktProvider.mUriKoopman;

        } else if (uri.getPath().equals(mUriKoopmanJoinedGroupBySollicitatienummer.getPath())) {

            // query the koopman table joined with the sollicitatie table and grouped by sollicitatie nummer
            cursor = queryKoopmanJoined(uri, projection, selection, selectionArgs, sortOrder, Sollicitatie.COL_SOLLICITATIE_NUMMER);
            notificationUri = MakkelijkeMarktProvider.mUriKoopman;

        } else {

            // call the default query method of the super class
            cursor = super.query(uri, projection, selection, selectionArgs, sortOrder);
        }

        // set the uri that must be notified of any changes
        if (cursor != null && getContext() != null) {
            cursor.setNotificationUri(getContext().getContentResolver(), notificationUri);
        }

        return cursor;
    }

    /**
     * Query the dagvergunningen table joined with it's linked tables with the given arguments
     * @return a cursor containing the dagvergunning resultset
     */
    private Cursor queryDagvergunningenJoined(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder, String groupBy) {
        SQLiteQueryBuilder dagvergunningJoinedQueryBuilder = new SQLiteQueryBuilder();

        // left join the dagvergunning table with the linked koopman, account, and sollicitatie tables
        String tables = mTableDagvergunning +
                        " LEFT JOIN " + mTableKoopman + " ON (" +
                        mTableDagvergunning + "." + Dagvergunning.COL_KOOPMAN_ID + " = " +
                        mTableKoopman + "." + Koopman.COL_ID + ")" +
                        " LEFT JOIN " + mTableAccount + " ON (" +
                        mTableDagvergunning + "." + Dagvergunning.COL_REGISTRATIE_ACCOUNT_ID + " = " +
                        mTableAccount + "." + MakkelijkeMarktProvider.Account.COL_ID + ")" +
                        " LEFT JOIN " + mTableSollicitatie + " ON (" +
                        mTableDagvergunning + "." + Dagvergunning.COL_SOLLICITATIE_ID + " = " +
                        mTableSollicitatie + "." + Sollicitatie.COL_ID + ")";
        dagvergunningJoinedQueryBuilder.setTables(tables);

        // create a projection map that will rename the ambiguous columns, and just copy the
        // others with their original name (when using a projection map you have to specify all
        // columns that you need in the resultset)
        HashMap<String, String> columnMap = new HashMap<>();
        columnMap.putAll(createProjectionMap(mTableDagvergunning, Dagvergunning.COL_ID, "_id"));
        columnMap.putAll(createProjectionMap(mTableDagvergunning, Dagvergunning.COL_ERKENNINGSNUMMER_INVOER_WAARDE, null));
        columnMap.putAll(createProjectionMap(mTableDagvergunning, Dagvergunning.COL_ERKENNINGSNUMMER_INVOER_METHODE, null));
        columnMap.putAll(createProjectionMap(mTableDagvergunning, Dagvergunning.COL_REGISTRATIE_DATUMTIJD, null));
        columnMap.putAll(createProjectionMap(mTableDagvergunning, Dagvergunning.COL_REGISTRATIE_GEOLOCATIE_LAT, null));
        columnMap.putAll(createProjectionMap(mTableDagvergunning, Dagvergunning.COL_REGISTRATIE_GEOLOCATIE_LONG, null));
        columnMap.putAll(createProjectionMap(mTableDagvergunning, Dagvergunning.COL_TOTALE_LENGTE, null));
        columnMap.putAll(createProjectionMap(mTableDagvergunning, Dagvergunning.COL_STATUS_SOLLICITATIE, null));
        columnMap.putAll(createProjectionMap(mTableDagvergunning, Dagvergunning.COL_DOORGEHAALD, "dagvergunning_doorgehaald"));
        columnMap.putAll(createProjectionMap(mTableDagvergunning, Dagvergunning.COL_AANWEZIG, null));
        columnMap.putAll(createProjectionMap(mTableDagvergunning, Dagvergunning.COL_AANTAL3METER_KRAMEN, null));
        columnMap.putAll(createProjectionMap(mTableDagvergunning, Dagvergunning.COL_AANTAL3METER_KRAMEN_VAST, null));
        columnMap.putAll(createProjectionMap(mTableDagvergunning, Dagvergunning.COL_AANTAL4METER_KRAMEN, null));
        columnMap.putAll(createProjectionMap(mTableDagvergunning, Dagvergunning.COL_AANTAL4METER_KRAMEN_VAST, null));
        columnMap.putAll(createProjectionMap(mTableDagvergunning, Dagvergunning.COL_EXTRA_METERS, null));
        columnMap.putAll(createProjectionMap(mTableDagvergunning, Dagvergunning.COL_AANTAL_EXTRA_METERS_VAST, null));
        columnMap.putAll(createProjectionMap(mTableDagvergunning, Dagvergunning.COL_AANTAL_ELEKTRA, "dagvergunning_aantal_elektra"));
        columnMap.putAll(createProjectionMap(mTableDagvergunning, Dagvergunning.COL_AANTAL_ELEKTRA_VAST, null));
        columnMap.putAll(createProjectionMap(mTableDagvergunning, Dagvergunning.COL_NOTITIE, null));
        columnMap.putAll(createProjectionMap(mTableDagvergunning, Dagvergunning.COL_AFVALEILAND, "dagvergunning_afvaleiland"));
        columnMap.putAll(createProjectionMap(mTableDagvergunning, Dagvergunning.COL_AFVALEILAND_VAST, null));
        columnMap.putAll(createProjectionMap(mTableKoopman, Koopman.COL_ID, "koopman_koopman_id"));
        columnMap.putAll(createProjectionMap(mTableKoopman, Koopman.COL_STATUS, "koopman_status"));
        columnMap.putAll(createProjectionMap(mTableKoopman, Koopman.COL_VOORLETTERS, null));
        columnMap.putAll(createProjectionMap(mTableKoopman, Koopman.COL_ACHTERNAAM, null));
        columnMap.putAll(createProjectionMap(mTableKoopman, Koopman.COL_FOTO_MEDIUM_URL, null));
        columnMap.putAll(createProjectionMap(mTableAccount, Account.COL_ID, "account_account_id"));
        columnMap.putAll(createProjectionMap(mTableAccount, Account.COL_NAAM, null));
        columnMap.putAll(createProjectionMap(mTableSollicitatie, Sollicitatie.COL_ID, "sollicitatie_sollicitatie_id"));
        columnMap.putAll(createProjectionMap(mTableSollicitatie, Sollicitatie.COL_SOLLICITATIE_NUMMER, null));
        dagvergunningJoinedQueryBuilder.setProjectionMap(columnMap);

        // and run the query with the given arguments
        return dagvergunningJoinedQueryBuilder.query(mDatabase,
                projection,
                selection,
                selectionArgs,
                groupBy,
                null,
                sortOrder);
    }

    /**
     * Query the koopman table joined with the sollicitatie and markt tables
     * @return a cursor containing the koopman resultset
     */
    private Cursor queryKoopmanJoined(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder, String groupBy) {
        SQLiteQueryBuilder koopmanJoinedQueryBuilder = new SQLiteQueryBuilder();

        String tables = mTableKoopman +
                " LEFT JOIN " + mTableSollicitatie + " ON (" +
                mTableKoopman + "." + Koopman.COL_ID + " = " +
                mTableSollicitatie + "." + Sollicitatie.COL_KOOPMAN_ID + ")" +
                " LEFT JOIN " + mTableMarkt + " ON (" +
                mTableSollicitatie + "." + Sollicitatie.COL_MARKT_ID + " = " +
                mTableMarkt + "." + Markt.COL_ID + ")";
        koopmanJoinedQueryBuilder.setTables(tables);

        // create a projection map that will rename the ambiguous columns
        HashMap<String, String> columnMap = new HashMap<>();
        columnMap.putAll(createProjectionMap(mTableKoopman, Koopman.COL_ID, "_id"));
        columnMap.putAll(createProjectionMap(mTableKoopman, Koopman.COL_ERKENNINGSNUMMER, null));
        columnMap.putAll(createProjectionMap(mTableKoopman, Koopman.COL_STATUS, "koopman_status"));
        columnMap.putAll(createProjectionMap(mTableKoopman, Koopman.COL_VOORLETTERS, null));
        columnMap.putAll(createProjectionMap(mTableKoopman, Koopman.COL_ACHTERNAAM, null));
        columnMap.putAll(createProjectionMap(mTableKoopman, Koopman.COL_FOTO_URL, null));
        columnMap.putAll(createProjectionMap(mTableKoopman, Koopman.COL_FOTO_MEDIUM_URL, null));
        columnMap.putAll(createProjectionMap(mTableKoopman, Koopman.COL_PAS_UID, null));
        columnMap.putAll(createProjectionMap(mTableSollicitatie, Sollicitatie.COL_ID, "sollicitatie_id"));
        columnMap.putAll(createProjectionMap(mTableSollicitatie, Sollicitatie.COL_SOLLICITATIE_NUMMER, null));
        columnMap.putAll(createProjectionMap(mTableSollicitatie, Sollicitatie.COL_DOORGEHAALD, null));
        columnMap.putAll(createProjectionMap(mTableSollicitatie, Sollicitatie.COL_STATUS, "sollicitatie_status"));
        columnMap.putAll(createProjectionMap(mTableSollicitatie, Sollicitatie.COL_MARKT_ID, null));
        columnMap.putAll(createProjectionMap(mTableSollicitatie, Sollicitatie.COL_AANTAL_3METER_KRAMEN, null));
        columnMap.putAll(createProjectionMap(mTableSollicitatie, Sollicitatie.COL_AANTAL_4METER_KRAMEN, null));
        columnMap.putAll(createProjectionMap(mTableSollicitatie, Sollicitatie.COL_AANTAL_EXTRA_METERS, null));
        columnMap.putAll(createProjectionMap(mTableSollicitatie, Sollicitatie.COL_AANTAL_ELEKTRA, null));
        columnMap.putAll(createProjectionMap(mTableSollicitatie, Sollicitatie.COL_AFVALEILAND, null));
        columnMap.putAll(createProjectionMap(mTableMarkt, Markt.COL_ID, "markt_markt_id"));
        columnMap.putAll(createProjectionMap(mTableMarkt, Markt.COL_AFKORTING, null));
        koopmanJoinedQueryBuilder.setProjectionMap(columnMap);

        // and run the query with the given arguments
        return koopmanJoinedQueryBuilder.query(mDatabase,
                projection,
                selection,
                selectionArgs,
                groupBy,
                null,
                sortOrder);
    }

    /**
     * Helper function to create the sql to rename columns using the AS keyword
     * @param tableName the table name for the fully qualified column name
     * @param columnName the original column name
     * @param asColumnName the name to rename the column
     * @return a hashmap containing only one item with two strings containing the sql to rename
     */
    private HashMap<String, String> createProjectionMap(String tableName, String columnName, String asColumnName) {
        HashMap<String, String> map = new HashMap<>();

        // rename the column if we received a asColumnName
        if (asColumnName != null) {
            map.put(tableName + "." + columnName, tableName + "." + columnName + " AS " + asColumnName);
        } else {
            // else just use the original column name
            map.put(tableName + "." + columnName, columnName);
        }

        return map;
    }
}