package org.techtown.cpastone_design.DB;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.lang.reflect.Array;
import java.util.ArrayList;

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

    public ArrayList<UrlTableList> select(){
        Cursor url_table_cursor = db.rawQuery(String.format("SELECT * FROM %s", TABLE_NAME), null);
        ArrayList<UrlTableList> return_data = new ArrayList<>();

        if(url_table_cursor.moveToFirst()){
            do{
                return_data.add(new UrlTableList(url_table_cursor.getInt(0),
                                                url_table_cursor.getString(1),
                                                url_table_cursor.getString(2),
                                                url_table_cursor.getInt(3),
                                                url_table_cursor.getString(4),
                                                url_table_cursor.getInt(5),
                                                url_table_cursor.getString(6)));
            } while(url_table_cursor.moveToNext());
        }

        url_table_cursor.close();
        return return_data;
    }
}