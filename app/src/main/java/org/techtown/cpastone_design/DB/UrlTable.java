package org.techtown.cpastone_design.DB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class UrlTable extends DBModel{
    private SQLiteDatabase db;
    private String TABLE_NAME = "url_info";

    public UrlTable(Context context) {
        super(context);
        db = getWritableDatabase();
    }

    public void insert(String previous_url, int malicious, String site_image){
        String destination_url = previous_url;
        String query = String.format("INSERT INTO %s (previous_url, destination_url, count, date, malicious, site_image) VALUES('%s', '%s', %d, CURRENT_TIMESTAMP, %d, '%s')",TABLE_NAME, previous_url, destination_url, 1, malicious, site_image);

        db.execSQL(query);
    }
}
