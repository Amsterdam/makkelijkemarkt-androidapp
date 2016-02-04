/*
 * Copyright (C) 2016 Gemeente Amsterdam, Marktbureau
 */
package com.amsterdam.marktbureau.makkelijkemarkt.data;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

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

    // table names
    public static final String mTableAccount = "account";
    public static final String mTableMarkt = "markt";
    public static final String mTableKoopman = "koopman";
    public static final String mTableDagvergunning = "dagvergunning";
    public static final String mTableNotitie = "notitie";
    public static final String mTableSollicitatie = "sollicitatie";

    // uris for the tables
    public static Uri mUriAccount = Uri.parse("content://" + mPackageName + "/" + mTableAccount);
    public static Uri mUriMarkt = Uri.parse("content://" + mPackageName + "/" + mTableMarkt);
    public static Uri mUriKoopman = Uri.parse("content://" + mPackageName + "/" + mTableKoopman);
    public static Uri mUriDagvergunning = Uri.parse("content://" + mPackageName + "/" + mTableDagvergunning);
    public static Uri mUriNotitie = Uri.parse("content://" + mPackageName + "/" + mTableNotitie);
    public static Uri mUriSollicitatie = Uri.parse("content://" + mPackageName + "/" + mTableSollicitatie);

    // other uris
    public static Uri mUriDagvergunningJoined = Uri.parse("content://" + mPackageName + "/" + mTableDagvergunning + "joined");

    /**
     * Get the content provider authority name
     *
     * @return String containing the authority name
     */
    @Override
    protected String getAuthority() {
        return MakkelijkeMarktProvider.class.getPackage().toString();
    }

    /**
     * Get the version number of the table model definition
     *
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

        @Column(Column.FieldType.INTEGER)
        public static final String COL_STATUS = "status";
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

        @Column(Column.FieldType.INTEGER)
        public static final String COL_REGISTRATIE_DATUMTIJD = "registratie_datumtijd";

        @Column(Column.FieldType.REAL)
        public static final String COL_REGISTRATIE_GEOLOCATIE_LAT = "registratie_geolocatie_lat";

        @Column(Column.FieldType.REAL)
        public static final String COL_REGISTRATIE_GEOLOCATIE_LONG = "registratie_geolocatie_long";

        @Column(Column.FieldType.TEXT)
        public static final String COL_ERKENNINGSNUMMER_INVOER_WAARDE = "erkenningsnummer_invoer_waarde";

        @Column(Column.FieldType.TEXT)
        public static final String COL_AANWEZIG = "aanwezig";

        @Column(Column.FieldType.INTEGER)
        public static final String COL_DOORGEHAALD = "doorgehaald";

        @Column(Column.FieldType.INTEGER)
        public static final String COL_EXTRA_METERS = "extra_meters";

        @Column(Column.FieldType.INTEGER)
        public static final String COL_TOTALE_LENGTE = "totale_lengte";

        @Column(Column.FieldType.TEXT)
        public static final String COL_NOTITIE = "notitie";

        @Column(Column.FieldType.INTEGER)
        public static final String COL_AANMAAK_DATUMTIJD = "aanmaak_datumtijd";

        @Column(Column.FieldType.INTEGER)
        public static final String COL_AANTAL_ELEKTRA = "aantal_elektra";

        @Column(Column.FieldType.INTEGER)
        public static final String COL_KRACHTSTROOM = "krachtstroom";

        @Column(Column.FieldType.INTEGER)
        public static final String COL_REINIGING = "reiniging";

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
        public static final String COL_KRACHTSTROOM_VAST = "krachtstroom_vast";

        @Column(Column.FieldType.TEXT)
        public static final String COL_STATUS_SOLLICITATIE = "status_solliciatie";

        @Column(Column.FieldType.INTEGER)
        public static final String COL_AANTAL3METER_KRAMEN = "aantal3meter_kramen";

        @Column(Column.FieldType.INTEGER)
        public static final String COL_AANTAL4METER_KRAMEN = "aantal4meter_kramen";
    }

//    /**
//     * /notitie - Notitie table columns definition
//     */
//    @Table(mTableNotitie)
//    public class Notitie {
//
//        @Column(value = Column.FieldType.INTEGER, primaryKey = true)
//        public static final String COL_ID = "_id";
//
//        @Column(Column.FieldType.INTEGER)
//        public static final String COL_MARKT_ID = "markt_id";
//
//        @Column(Column.FieldType.TEXT)
//        public static final String COL_DAG = "dag";
//
//        @Column(Column.FieldType.TEXT)
//        public static final String COL_BERICHT = "bericht";
//
//        @Column(Column.FieldType.REAL)
//        public static final String COL_AANGEMAAKT_GEOLOCATIE_LAT = "aangemaakt_geolocatie_lat";
//
//        @Column(Column.FieldType.REAL)
//        public static final String COL_AANGEMAAKT_GEOLOCATIE_LONG = "aangemaakt_geolocatie_long";
//
//        @Column(Column.FieldType.INTEGER)
//        public static final String COL_AFGEVINKT_STATUS = "afgevinkt_status";
//
//        @Column(Column.FieldType.INTEGER)
//        public static final String COL_VERWIJDERD = "verwijderd";
//
//        @Column(Column.FieldType.INTEGER)
//        public static final String COL_AANGEMAAKT_DATUMTIJD = "aangemaakt_datumtijd";
//
//        @Column(Column.FieldType.INTEGER)
//        public static final String COL_AFGEVINKT_DATUMTIJD = "afgevinkt_datumtijd";
//
//        @Column(Column.FieldType.INTEGER)
//        public static final String COL_VERWIJDERD_DATUMTIJD = "verwijderd_datumtijd";
//    }

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
        public static final String COL_KRACHTSTROOM = "krachtstroom";
    }

    /**
     * Override of the insert function of the AbstractProvider class, in a way that it uses the
     * SQLite insertOrThrow function that throws a SQLiteConstraintException when trying to insert
     * insert a duplicate column value, and we can act on that
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

        // try to insert or throw an exception
        long rowId = mDatabase.insertOrThrow(segments.get(0), null, values);

        // if no exception was thrown and we received an id, the row was inserted succesfully
        if (rowId > -1) {

            // send notification to loader
            getContext().getContentResolver().notifyChange(uri, null);

            // return the uri where the inserted row can be found
            return ContentUris.withAppendedId(uri, rowId);
        }

        return null;
    }

    /**
     * Catch non-standard table queries
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        // @todo refactor this to use a uri matcher?

        if (uri.getPath().equals(mUriDagvergunningJoined.getPath())) {

            // query the dagvergunningen table joined with it's linked tables with the given arguments
            return getDagvergunningenJoined(uri, projection, selection, selectionArgs, sortOrder);
        }

        return super.query(uri, projection, selection, selectionArgs, sortOrder);
    }

    /**
     * Query the dagvergunningen table joined with it's linked tables with the given arguments
     * @param uri the given uri of the content
     * @param projection the columns we need
     * @param selection the columns in the WHERE clause
     * @param selectionArgs the arguments in the WHERE clause
     * @param sortOrder the sorting params
     * @return a cursor containing the resultset
     */
    private Cursor getDagvergunningenJoined(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder dagvergunningKoopmanQueryBuilder = new SQLiteQueryBuilder();

        // left join the dagvergunning table with the linked koopman, account, and sollicitatie tables
        String tables = mTableDagvergunning +
                        " LEFT JOIN " + mTableKoopman + " ON (" +
                        mTableDagvergunning + "." + Dagvergunning.COL_KOOPMAN_ID + " = " +
                        mTableKoopman + "." + Koopman.COL_ID + ")" +
                        " LEFT JOIN " + mTableAccount + " ON (" +
                        mTableDagvergunning + "." + Dagvergunning.COL_REGISTRATIE_ACCOUNT_ID + " = " +
                        mTableAccount + "." + Account.COL_ID + ")" +
                        " LEFT JOIN " + mTableSollicitatie + " ON (" +
                        mTableDagvergunning + "." + Dagvergunning.COL_SOLLICITATIE_ID + " = " +
                        mTableSollicitatie + "." + Sollicitatie.COL_ID + ")";
        dagvergunningKoopmanQueryBuilder.setTables(tables);

        // create a projection map that will rename the ambiguous columns, and just copy the
        // others with their original name (when using a projection map you have to specify all
        // columns that you need in the resultset)
        HashMap<String, String> columnMap = new HashMap<>();

        // rename ambiguous columns
        columnMap.putAll(createProjectionMap(mTableDagvergunning, Dagvergunning.COL_ID, "_id"));
        columnMap.putAll(createProjectionMap(mTableKoopman, Koopman.COL_ID, "koopman_koopman_id"));
        columnMap.putAll(createProjectionMap(mTableAccount, Account.COL_ID, "account_account_id"));
        columnMap.putAll(createProjectionMap(mTableSollicitatie, Sollicitatie.COL_ID, "sollicitatie_sollicitatie_id"));

        // dagvergunning columns copied
        columnMap.putAll(createProjectionMap(mTableDagvergunning, Dagvergunning.COL_ERKENNINGSNUMMER_INVOER_WAARDE, null));
        columnMap.putAll(createProjectionMap(mTableDagvergunning, Dagvergunning.COL_REGISTRATIE_DATUMTIJD, null));
        columnMap.putAll(createProjectionMap(mTableDagvergunning, Dagvergunning.COL_TOTALE_LENGTE, null));
        columnMap.putAll(createProjectionMap(mTableDagvergunning, Dagvergunning.COL_STATUS_SOLLICITATIE, null));

        // koopman columns copied
        columnMap.putAll(createProjectionMap(mTableKoopman, Koopman.COL_VOORLETTERS, null));
        columnMap.putAll(createProjectionMap(mTableKoopman, Koopman.COL_ACHTERNAAM, null));
        columnMap.putAll(createProjectionMap(mTableKoopman, Koopman.COL_FOTO_MEDIUM_URL, null));

        // account columns copied
        columnMap.putAll(createProjectionMap(mTableAccount, Account.COL_NAAM, null));

        // sollicitatie columns copied
        columnMap.putAll(createProjectionMap(mTableSollicitatie, Sollicitatie.COL_SOLLICITATIE_NUMMER, null));

        // apply the mapping
        dagvergunningKoopmanQueryBuilder.setProjectionMap(columnMap);

        // and run the query with the given arguments
        return dagvergunningKoopmanQueryBuilder.query(mDatabase,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
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
            map.put(tableName + "." + columnName,
                    tableName + "." + columnName + " AS " + asColumnName);
        } else {
            // else just use the original column name
            map.put(tableName + "." + columnName,
                    columnName);
        }

        return map;
    }
}