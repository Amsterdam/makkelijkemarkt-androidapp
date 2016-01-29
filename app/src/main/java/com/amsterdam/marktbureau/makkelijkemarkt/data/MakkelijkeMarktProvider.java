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
 * Database provider for caching the content from the makkelijke markt api locally in an sqlite db
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
    private static final String mTableKoopman = "koopman";
    private static final String mTableDagvergunning = "dagvergunning";
    private static final String mTableNotitie = "notitie";
    private static final String mTableSollicitatie = "sollicitatie";

    // uris for the tables
    public static Uri mUriAccount = Uri.parse("content://" + mPackageName + "/" + mTableAccount);
    public static Uri mUriMarkt = Uri.parse("content://" + mPackageName + "/" + mTableMarkt);
    public static Uri mUriKoopman = Uri.parse("content://" + mPackageName + "/" + mTableKoopman);
    public static Uri mUriDagvergunning = Uri.parse("content://" + mPackageName + "/" + mTableDagvergunning);
    public static Uri mUriNotitie = Uri.parse("content://" + mPackageName + "/" + mTableNotitie);
    public static Uri mUriSollicitatie = Uri.parse("content://" + mPackageName + "/" + mTableSollicitatie);

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

        @Column(Column.FieldType.INTEGER)
        public static final String COL_PERFECT_VIEW_NUMMER = "perfect_view_nummer";

        @Column(Column.FieldType.TEXT)
        public static final String COL_FOTO = "foto";

        @Column(Column.FieldType.INTEGER)
        public static final String COL_STATUS = "status";

        @Column(Column.FieldType.INTEGER)
        public static final String COL_FOTO_LAST_UPDATE = "foto_last_update";

        @Column(Column.FieldType.TEXT)
        public static final String COL_FOTO_HASH = "foto_hash";
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
        public static final String COL_FACTUUR_ID = "factuur_id";

        @Column(Column.FieldType.INTEGER)
        public static final String COL_REGISTRATIE_ACCOUNT_ID = "registratie_account_id";

        @Column(Column.FieldType.INTEGER)
        public static final String COL_DOORGEHAALD_ACCOUNT_ID = "doorgehaald_account_id";

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
        public static final String COL_DOORGEHAALD_DATUMTIJD = "doorgehaald_datumtijd";

        @Column(Column.FieldType.REAL)
        public static final String COL_DOORGEHAALD_GEOLOCATIE_LAT = "doorgehaald_geolocatie_lat";

        @Column(Column.FieldType.REAL)
        public static final String COL_DOORGEHAALD_GEOLOCATIE_LONG = "doorgehaald_geolocatie_long";

        @Column(Column.FieldType.INTEGER)
        public static final String COL_EXTRA_METERS = "extra_meters";

        @Column(Column.FieldType.TEXT)
        public static final String COL_NOTITIE = "notitie";

        @Column(Column.FieldType.INTEGER)
        public static final String COL_AANMAAK_DATUMTIJD = "aanmaak_datumtijd";

        @Column(Column.FieldType.INTEGER)
        public static final String COL_VERWIJDERD_DATUMTIJD = "verwijderd_datumtijd";

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

        @Column(Column.FieldType.REAL)
        public static final String COL_AANGEMAAKT_GEOLOCATIE_LAT = "aangemaakt_geolocatie_lat";

        @Column(Column.FieldType.REAL)
        public static final String COL_AANGEMAAKT_GEOLOCATIE_LONG = "aangemaakt_geolocatie_long";

        @Column(Column.FieldType.INTEGER)
        public static final String COL_AFGEVINKT_STATUS = "afgevinkt_status";

        @Column(Column.FieldType.INTEGER)
        public static final String COL_VERWIJDERD = "verwijderd";

        @Column(Column.FieldType.INTEGER)
        public static final String COL_AANGEMAAKT_DATUMTIJD = "aangemaakt_datumtijd";

        @Column(Column.FieldType.INTEGER)
        public static final String COL_AFGEVINKT_DATUMTIJD = "afgevinkt_datumtijd";

        @Column(Column.FieldType.INTEGER)
        public static final String COL_VERWIJDERD_DATUMTIJD = "verwijderd_datumtijd";
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
        public static final String COL_KRACHTSTROOM = "krachtstroom";
    }
}