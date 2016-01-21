package com.amsterdam.marktbureau.makkelijkemarkt.data;

import android.net.Uri;

import com.amsterdam.marktbureau.makkelijkemarkt.MainActivity;

import de.triplet.simpleprovider.AbstractProvider;
import de.triplet.simpleprovider.Column;
import de.triplet.simpleprovider.Table;

public class MakkelijkeMarktProvider extends AbstractProvider {

    // use classname when logging
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    // set package name as provider authority
    private static final String mAuthority = MakkelijkeMarktProvider.class.getPackage().toString();

    // table names
    private static final String mTableAccount = "account";
    private static final String mTableMarkt = "markt";
    private static final String mTableDagvergunning = "dagvergunning";
    private static final String mTableKoopman = "koopman";
    private static final String mTableNotitie = "notitie";

    // uris for the tables
    public static Uri mUriAccount = Uri.parse("content://" + mAuthority + "/"+ mTableAccount);
    public static Uri mUriMarkt = Uri.parse("content://" + mAuthority + "/"+ mTableMarkt);
    public static Uri mUriDagvergunning = Uri.parse("content://" + mAuthority + "/"+ mTableDagvergunning);
    public static Uri mUriKoopman = Uri.parse("content://" + mAuthority + "/"+ mTableKoopman);
    public static Uri mUriNotitie = Uri.parse("content://" + mAuthority + "/"+ mTableNotitie);

    /**
     * Get the content provider authority name
     * @return String containing the authority name
     */
    @Override
    protected String getAuthority() {
        return mAuthority;
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

        @Column(value = Column.FieldType.INTEGER, unique = true)
        public static final String COL_ACCOUNT_ID = "id";

        @Column(Column.FieldType.TEXT)
        public static final String COL_NAAM = "naam";

        @Column(Column.FieldType.TEXT)
        public static final String COL_EMAIL = "email";

        @Column(Column.FieldType.TEXT)
        public static final String COL_USERNAME = "username";

        @Column(Column.FieldType.TEXT)
        public static final String COL_ROLE = "role";
    }
}
