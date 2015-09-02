package com.inautix.achyutaz.goclean;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Jhaz on 8/19/2015.
 */
public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "CleanDBName.db";
    public static final String CLEAN_TABLE_NAME = "LocTime";
    public static final String CLEAN_COLUMN_ID = "id";
    public static final String CLEAN_COLUMN_LOCATION = "location";
    public static final String CLEAN_COLUMN_TIME = "time";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME , null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "create table " + CLEAN_TABLE_NAME +
                        " (id integer,location text primary key,time text)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+CLEAN_TABLE_NAME);
        onCreate(db);
    }

    public boolean insertContact  (String location, String time)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("location", location);
        contentValues.put("time", time);
        db.insert(CLEAN_TABLE_NAME, null, contentValues);
        return true;
    }

}
