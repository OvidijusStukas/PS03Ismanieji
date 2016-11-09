package edu.stukas.ovidijus.terrarea.database;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.preference.PreferenceManager;
import android.util.LongSparseArray;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import edu.stukas.ovidijus.terrarea.data.Territory;
import edu.stukas.ovidijus.terrarea.util.TerrareaSettingKeys;

/**
 * @author Ovidijus Stukas
 */

public class TerrareaDatabaseHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Terrarea.db";

    private static final String QUERY_SELECT_ALL_TERRITORIES =
            "SELECT T.*" +
            ",P." + TerritoryPositionContract.TerritoryPositionEntry.COLUMN_NAME_LATITUDE +
            ",P." + TerritoryPositionContract.TerritoryPositionEntry.COLUMN_NAME_LONGITUDE +
            " FROM " + TerritoryContract.TerritoryEntry.TABLE_NAME + " T" +
            " LEFT JOIN " + TerritoryPositionContract.TerritoryPositionEntry.TABLE_NAME + " P" +
            " ON P." + TerritoryPositionContract.TerritoryPositionEntry.COLUMN_NAME_TERRITORY + " =" +
            " T." + TerritoryContract.TerritoryEntry._ID +
            " WHERE T." + TerritoryContract.TerritoryEntry.COLUMN_NAME_USER_ID + " = ?";

    private Context context;

    public TerrareaDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TerritoryContract.SQL_CREATE_TERRITORIES);
        db.execSQL(TerritoryPositionContract.SQL_CREATE_TERRITORY_POSITIONS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(TerritoryPositionContract.SQL_DELETE_TERRITORY_POSITIONS);
        db.execSQL(TerritoryContract.SQL_DELETE_TERRITORIES);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public Territory insertTerritory(Territory territory) {
        SQLiteDatabase db = getWritableDatabase();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        boolean saveArea = preferences.getBoolean(TerrareaSettingKeys.SETTING_KEY_SAVE_AREA, true);
        boolean savePositions = preferences.getBoolean(TerrareaSettingKeys.SETTING_KEY_SAVE_POSITION, true);
        boolean savePerimeter = preferences.getBoolean(TerrareaSettingKeys.SETTING_KEY_SAVE_PERIMETER, true);

        ContentValues values = new ContentValues();
        values.put(TerritoryContract.TerritoryEntry.COLUMN_NAME_TITLE, territory.getName());
        values.put(TerritoryContract.TerritoryEntry.COLUMN_NAME_AREA, saveArea ? territory.getArea() : null);
        values.put(TerritoryContract.TerritoryEntry.COLUMN_NAME_PERIMETER, savePerimeter ? territory.getPerimeter() : null);
        values.put(TerritoryContract.TerritoryEntry.COLUMN_NAME_USER_ID, 1);

        long newId = db.insert(TerritoryContract.TerritoryEntry.TABLE_NAME, null, values);
        territory.setId(newId);

        if (savePositions)
        {
            for (LatLng position : territory.getPositions()) {
                ContentValues positionValues = new ContentValues();
                positionValues.put(TerritoryPositionContract.TerritoryPositionEntry.COLUMN_NAME_TERRITORY, newId);
                positionValues.put(TerritoryPositionContract.TerritoryPositionEntry.COLUMN_NAME_LATITUDE, position.latitude);
                positionValues.put(TerritoryPositionContract.TerritoryPositionEntry.COLUMN_NAME_LONGITUDE, position.longitude);

                db.insert(TerritoryPositionContract.TerritoryPositionEntry.TABLE_NAME, null, positionValues);
            }
        }

        return territory;
    }

    public void updateTerritory(Territory territory)
    {
        SQLiteDatabase db = getWritableDatabase();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        boolean saveArea = preferences.getBoolean(TerrareaSettingKeys.SETTING_KEY_SAVE_AREA, true);
        boolean savePositions = preferences.getBoolean(TerrareaSettingKeys.SETTING_KEY_SAVE_POSITION, true);
        boolean savePerimeter = preferences.getBoolean(TerrareaSettingKeys.SETTING_KEY_SAVE_PERIMETER, true);

        ContentValues values = new ContentValues();
        values.put(TerritoryContract.TerritoryEntry.COLUMN_NAME_TITLE, territory.getName());
        values.put(TerritoryContract.TerritoryEntry.COLUMN_NAME_AREA, saveArea ? territory.getArea() : null);
        values.put(TerritoryContract.TerritoryEntry.COLUMN_NAME_PERIMETER, savePerimeter ? territory.getPerimeter() : null);

        String whereClauseTerritory = String.format("%s = %d", TerritoryContract.TerritoryEntry._ID, territory.getId());
        db.update(TerritoryContract.TerritoryEntry.TABLE_NAME, values, whereClauseTerritory, null);


        String whereClausePosition = String.format("%s = %d", TerritoryPositionContract.TerritoryPositionEntry.COLUMN_NAME_TERRITORY, territory.getId());
        db.delete(TerritoryPositionContract.TerritoryPositionEntry.TABLE_NAME, whereClausePosition, null);

        if (savePositions)
        {
            for (LatLng position : territory.getPositions()) {
                ContentValues positionValues = new ContentValues();
                positionValues.put(TerritoryPositionContract.TerritoryPositionEntry.COLUMN_NAME_TERRITORY, territory.getId());
                positionValues.put(TerritoryPositionContract.TerritoryPositionEntry.COLUMN_NAME_LATITUDE, position.latitude);
                positionValues.put(TerritoryPositionContract.TerritoryPositionEntry.COLUMN_NAME_LONGITUDE, position.longitude);

                db.insert(TerritoryPositionContract.TerritoryPositionEntry.TABLE_NAME, null, positionValues);
            }
        }
    }

    public void deleteTerritory(long id)
    {
        SQLiteDatabase db = getWritableDatabase();

        String whereClausePosition = String.format("%s = %d", TerritoryPositionContract.TerritoryPositionEntry.COLUMN_NAME_TERRITORY, id);
        String whereClauseTerritory = String.format("%s = %d", TerritoryContract.TerritoryEntry._ID, id);
        db.delete(TerritoryPositionContract.TerritoryPositionEntry.TABLE_NAME, whereClausePosition, null);
        db.delete(TerritoryContract.TerritoryEntry.TABLE_NAME,  whereClauseTerritory, null);
    }

    public List<Territory> getTerritories() {
        LongSparseArray<Territory> array = new LongSparseArray<>();
        SQLiteDatabase db = getReadableDatabase();

        // TODO: get this:
        long user_id = 1;

        Cursor cursor = db.rawQuery(QUERY_SELECT_ALL_TERRITORIES, new String[] {String.valueOf(user_id)});

        while (cursor.moveToNext()) {
            long territoryId = cursor.getLong(cursor.getColumnIndex(TerritoryContract.TerritoryEntry._ID));
            Territory territory = array.get(territoryId, null);
            if (territory == null)
            {
                territory = new Territory(
                    cursor.getString(cursor.getColumnIndex(TerritoryContract.TerritoryEntry.COLUMN_NAME_TITLE)),
                    cursor.getDouble(cursor.getColumnIndex(TerritoryContract.TerritoryEntry.COLUMN_NAME_AREA)),
                    cursor.getDouble(cursor.getColumnIndex(TerritoryContract.TerritoryEntry.COLUMN_NAME_PERIMETER))
                );
                territory.setId(territoryId);

                LatLng position = new LatLng(
                    cursor.getDouble(cursor.getColumnIndex(TerritoryPositionContract.TerritoryPositionEntry.COLUMN_NAME_LATITUDE)),
                    cursor.getDouble(cursor.getColumnIndex(TerritoryPositionContract.TerritoryPositionEntry.COLUMN_NAME_LONGITUDE))
                );
                territory.getPositions().add(position);

                array.put(territoryId, territory);
            }
            else {
                LatLng position = new LatLng(
                    cursor.getDouble(cursor.getColumnIndex(TerritoryPositionContract.TerritoryPositionEntry.COLUMN_NAME_LATITUDE)),
                    cursor.getDouble(cursor.getColumnIndex(TerritoryPositionContract.TerritoryPositionEntry.COLUMN_NAME_LONGITUDE))
                );
                territory.getPositions().add(position);
            }
        }

        cursor.close();

        List<Territory> territories = new ArrayList<>(array.size());
        for (int i = 0; i < array.size(); i++)
            territories.add(array.valueAt(i));

        return territories;
    }
}
