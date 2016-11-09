package edu.stukas.ovidijus.terrarea.database;

import android.provider.BaseColumns;

/**
 * @author Ovidijus Stukas
 */

public class TerritoryPositionContract {

    public static final String SQL_CREATE_TERRITORY_POSITIONS =
            "CREATE TABLE " + TerritoryPositionEntry.TABLE_NAME + "(" +
            TerritoryPositionEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            TerritoryPositionEntry.COLUMN_NAME_TERRITORY + " INTEGER," +
            TerritoryPositionEntry.COLUMN_NAME_LATITUDE + " REAL," +
            TerritoryPositionEntry.COLUMN_NAME_LONGITUDE + " REAL)";

    public static final String SQL_DELETE_TERRITORY_POSITIONS =
            "DROP TABLE IF EXISTS " + TerritoryPositionEntry.TABLE_NAME;

    private TerritoryPositionContract() {}

    public static class TerritoryPositionEntry implements BaseColumns {
        public static final String TABLE_NAME            = "territory_position";
        public static final String COLUMN_NAME_TERRITORY = "territory_id";
        public static final String COLUMN_NAME_LATITUDE  = "latitude";
        public static final String COLUMN_NAME_LONGITUDE = "longitude";
    }
}
