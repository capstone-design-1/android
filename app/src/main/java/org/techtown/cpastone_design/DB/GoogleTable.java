package org.techtown.cpastone_design.DB;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class GoogleTable extends DBModel{
    private SQLiteDatabase db;
    private String TABLE_NAME = "google_info";

    public GoogleTable(Context context) {
        super(context);
        db = getWritableDatabase();
    }

    public void insert(String detail, int url_id){
        String query = String.format("INSERT INTO %s (detail, url_id) VALUES ('%s', %d)",TABLE_NAME, detail, url_id);
        db.execSQL(query);
    }

    public ArrayList<GoogleTableList> select(){
        Cursor google_table_cursor = db.rawQuery(String.format("SELECT * FROM %s", TABLE_NAME), null);
        ArrayList<GoogleTableList> return_data = new ArrayList<>();

        if(google_table_cursor.moveToFirst()){
            do{
                return_data.add(new GoogleTableList(google_table_cursor.getInt(0),
                        google_table_cursor.getInt(1),
                        google_table_cursor.getString(2)));
            } while(google_table_cursor.moveToNext());
        }

        google_table_cursor.close();
        return return_data;
    }
}
