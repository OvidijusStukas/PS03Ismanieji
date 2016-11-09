package edu.stukas.ovidijus.terrarea.database;

import android.provider.BaseColumns;

/**
 * @author Ovidijus Stukas
 */

public class TerritoryContract {

    public static final String SQL_CREATE_TERRITORIES =
            "CREATE TABLE " + TerritoryEntry.TABLE_NAME + "(" +
                    TerritoryEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    TerritoryEntry.COLUMN_NAME_USER_ID + " INTEGER," +
                    TerritoryEntry.COLUMN_NAME_TITLE + " TEXT," +
                    TerritoryEntry.COLUMN_NAME_AREA + " REAL," +
                    TerritoryEntry.COLUMN_NAME_PERIMETER + " REAL)";

    public static final String SQL_DELETE_TERRITORIES =
            "DROP TABLE IF EXISTS " + TerritoryEntry.TABLE_NAME;

    private TerritoryContract() {}

    public static class TerritoryEntry implements BaseColumns {
        public static final String TABLE_NAME            = "territory";
        public static final String COLUMN_NAME_USER_ID   = "user_id";
        public static final String COLUMN_NAME_TITLE     = "title";
        public static final String COLUMN_NAME_AREA      = "area";
        public static final String COLUMN_NAME_PERIMETER = "perimeter";
    }
}
