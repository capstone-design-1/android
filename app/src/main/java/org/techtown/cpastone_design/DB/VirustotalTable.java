package org.techtown.cpastone_design.DB;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class VirustotalTable extends DBModel{
    private SQLiteDatabase db;
    private String TABLE_NAME = "virustotal_info";

    public VirustotalTable(Context context) {
        super(context);
        db = getWritableDatabase();
    }

    public void insert(String detail, int url_id){
        String query = String.format("INSERT INTO %s (detail, url_id) VALUES ('%s', %d)",TABLE_NAME, url_id);
        db.execSQL(query);
    }

    public ArrayList<VirustotalTableList> select(){
        Cursor virustotal_table_cursor = db.rawQuery(String.format("SELECT * FROM %s", TABLE_NAME), null);
        ArrayList<VirustotalTableList> return_data = new ArrayList<>();

        if(virustotal_table_cursor.moveToFirst()){
            do{
                return_data.add(new VirustotalTableList(virustotal_table_cursor.getInt(0),
                        virustotal_table_cursor.getInt(1),
                        virustotal_table_cursor.getString(2)));
            } while(virustotal_table_cursor.moveToNext());
        }

        virustotal_table_cursor.close();
        return return_data;
    }
}
