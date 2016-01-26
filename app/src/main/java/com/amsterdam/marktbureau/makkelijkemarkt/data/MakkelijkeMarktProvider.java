/*
 * Copyright (C) 2016 Gemeente Amsterdam, Marktbureau
 */
package com.amsterdam.marktbureau.makkelijkemarkt.data;

import android.net.Uri;

import com.amsterdam.marktbureau.makkelijkemarkt.MainActivity;

import de.triplet.simpleprovider.AbstractProvider;
import de.triplet.simpleprovider.Column;
import de.triplet.simpleprovider.Table;

/**
 *
 * @author marcolangebeeke
 */
public class MakkelijkeMarktProvider extends AbstractProvider {

    // use classname when logging
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    // get package name
    public static final String mPackageName = MakkelijkeMarktProvider.class.getPackage().getName();

    // table names
    private static final String mTableAccount = "account";
    private static final String mTableMarkt = "markt";
    private static final String mTableDagvergunning = "dagvergunning";
    private static final String mTableKoopman = "koopman";
    private static final String mTableNotitie = "notitie";

    // uris for the tables
    public static Uri mUriAccount = Uri.parse("content://" + mPackageName + "/"+ mTableAccount);
    public static Uri mUriMarkt = Uri.parse("content://" + mPackageName + "/"+ mTableMarkt);
    public static Uri mUriDagvergunning = Uri.parse("content://" + mPackageName + "/"+ mTableDagvergunning);
    public static Uri mUriKoopman = Uri.parse("content://" + mPackageName + "/"+ mTableKoopman);
    public static Uri mUriNotitie = Uri.parse("content://" + mPackageName + "/"+ mTableNotitie);

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
        public static final String _ID = "_id";

        @Column(value = Column.FieldType.INTEGER, unique = true)
        public static final String COL_ID = "id";

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
        public static final String _ID = "_id";

        @Column(value = Column.FieldType.INTEGER, unique = true)
        public static final String COL_ID = "id";

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
}