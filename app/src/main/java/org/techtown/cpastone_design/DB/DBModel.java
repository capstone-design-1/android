package org.techtown.cpastone_design.DB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

// Reference https://abss.tistory.com/150

public class DBModel extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "info.db";

    public DBModel(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String url_info_table_query = "CREATE TABLE url_info (" +
                "url_id INTEGER PRIMARY KEY, " +
                "previous_url TEXT," +
                "destination_url TEXT," +
                "count INTEGER," +
                "date TEXT," +
                "malicious INTEGER," +
                "site_image TEXT)";

        db.execSQL(url_info_table_query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
