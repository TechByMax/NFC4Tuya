package com.maximeg.nfc4tuya.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class SceneDBHelper extends SQLiteOpenHelper {

    public static class SceneEntry implements BaseColumns {
        public static final String TABLE_NAME = "scene";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_ID = "id";
    }

    private static final String SQL_CREATE_SCENE_ENTRIES =
            "CREATE TABLE " + SceneEntry.TABLE_NAME + " (" +
                    SceneEntry._ID + " INTEGER PRIMARY KEY," +
                    SceneEntry.COLUMN_NAME + " TEXT," +
                    SceneEntry.COLUMN_ID + " TEXT)";

    private static final String SQL_DELETE_SCENE_ENTRIES =
            "DROP TABLE IF EXISTS " + SceneEntry.TABLE_NAME;

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Scene.db";

    public SceneDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void deleteEntries(SQLiteDatabase db){
        db.execSQL("DELETE FROM " + SceneEntry.TABLE_NAME);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_SCENE_ENTRIES);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_SCENE_ENTRIES);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
