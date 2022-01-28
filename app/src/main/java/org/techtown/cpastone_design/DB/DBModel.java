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
                "url_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "previous_url TEXT," +
                "destination_url TEXT," +
                "count INTEGER," +
                "date TEXT," +
                "malicious INTEGER," +
                "site_image TEXT)";
        
        String virustotal_info_table_query = "CREATE TABLE virustotal_info (" +
                "v_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "url_id INTEGER," +
                "detail TEXT," +
                "FOREIGN KEY(url_id) REFERENCES url_info(url_id))";

        String malwares_info_table_query = "CREATE TABLE malwares_info (" +
                "m_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "url_id INTEGER," +
                "detail TEXT," +
                "FOREIGN KEY(url_id) REFERENCES url_info(url_id))";

        String google_info_table_query = "CREATE TABLE google_info (" +
                "g_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "url_id INTEGER," +
                "detail TEXT," +
                "FOREIGN KEY(url_id) REFERENCES url_info(url_id))";

        String phishtank_info_table_query = "CREATE TABLE phishtank_info (" +
                "p_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "url_id INTEGER," +
                "detail TEXT," +
                "FOREIGN KEY(url_id) REFERENCES url_info(url_id))";

        db.execSQL(url_info_table_query);
        db.execSQL(virustotal_info_table_query);
        db.execSQL(malwares_info_table_query);
        db.execSQL(google_info_table_query);
        db.execSQL(phishtank_info_table_query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
