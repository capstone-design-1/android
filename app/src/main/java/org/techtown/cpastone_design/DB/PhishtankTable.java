package org.techtown.cpastone_design.DB;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class PhishtankTable extends DBModel{
    private SQLiteDatabase db;
    private String TABLE_NAME = "phishtank_info";

    public PhishtankTable(Context context) {
        super(context);
        db = getWritableDatabase();
    }

    public void insert(String detail, int url_id){
        String query = String.format("INSERT INTO %s (detail, url_id) VALUES ('%s', %d)",TABLE_NAME, detail, url_id);
        db.execSQL(query);
    }

    public ArrayList<PhishtankTableList> selectAll(){
        Cursor phishtank_table_cursor = db.rawQuery(String.format("SELECT * FROM %s", TABLE_NAME), null);
        ArrayList<PhishtankTableList> return_data = makePhishTankList(phishtank_table_cursor);

        phishtank_table_cursor.close();
        return return_data;
    }

    public ArrayList<PhishtankTableList> selectMaliciousSite(int url_id){
        Cursor phishtank_table_cursor = db.rawQuery(String.format("SELECT * FROM %s WHERE url_id=%d", TABLE_NAME, url_id), null);
        ArrayList<PhishtankTableList> return_data = makePhishTankList(phishtank_table_cursor);

        phishtank_table_cursor.close();
        return return_data;
    }

    public ArrayList<PhishtankTableList> makePhishTankList(Cursor phishtank_table_cursor){
        ArrayList<PhishtankTableList> return_data = new ArrayList<>();

        if(phishtank_table_cursor.moveToFirst()){
            do{
                return_data.add(new PhishtankTableList(phishtank_table_cursor.getInt(0),
                        phishtank_table_cursor.getInt(1),
                        phishtank_table_cursor.getString(2)));
            } while(phishtank_table_cursor.moveToNext());
        }

        return return_data;
    }
}
