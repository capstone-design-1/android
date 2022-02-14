package org.techtown.cpastone_design.DB;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class UrlTable extends DBModel{
    private SQLiteDatabase db;
    private String TABLE_NAME = "url_info";
    private Context context;

    public UrlTable(Context context) {
        super(context);
        this.context = context;
        db = getWritableDatabase();
    }

    public void insert(String previous_url, int malicious, String site_image){
        String destination_url = previous_url;
        String query = String.format("INSERT INTO %s (previous_url, destination_url, count, date, malicious, site_image) VALUES('%s', '%s', %d, CURRENT_TIMESTAMP, %d, '%s')",TABLE_NAME, previous_url, destination_url, 1, malicious, site_image);

        db.execSQL(query);
    }

    public void deleteAll(){
        String query = String.format("DELETE FROM %s", TABLE_NAME);
        db.execSQL(query);
    }

    public ArrayList<UrlTableList> selectAll(){
        Cursor url_table_cursor = db.rawQuery(String.format("SELECT * FROM %s", TABLE_NAME), null);
        ArrayList<UrlTableList> return_data = makeUrlTableList(url_table_cursor);

        url_table_cursor.close();
        return return_data;
    }

    public ArrayList<UrlTableList> selectURL(String url){
        Cursor url_table_cursor = db.rawQuery(String.format("SELECT * FROM %s WHERE previous_url='%s'", TABLE_NAME, url), null);
        ArrayList<UrlTableList> return_data = makeUrlTableList(url_table_cursor);

        url_table_cursor.close();
        return return_data;
    }

    public JSONArray selectMaliciousSite() throws JSONException {
        JSONArray return_data = new JSONArray();
        Cursor url_table_cursor = db.rawQuery(String.format("SELECT * FROM %s WHERE malicious = 1", TABLE_NAME), null);

        if(url_table_cursor.moveToFirst()){
            GoogleTable google_table = new GoogleTable(this.context);
            MalwaresTable malwares_table = new MalwaresTable(this.context);
            PhishtankTable phishtank_table = new PhishtankTable(this.context);
            VirustotalTable virustotal_table = new VirustotalTable(this.context);

            do{
                JSONObject data = new JSONObject();
                ArrayList<GoogleTableList> google_data = google_table.selectMaliciousSite(url_table_cursor.getInt(0));
                ArrayList<MalwaresTableList> malwares_data = malwares_table.selectMaliciousSite(url_table_cursor.getInt(0));
                ArrayList<PhishtankTableList> phishtank_data = phishtank_table.selectMaliciousSite(url_table_cursor.getInt(0));
                ArrayList<VirustotalTableList> virustotal_data = virustotal_table.selectMaliciousSite(url_table_cursor.getInt(0));
                // TODO, 아래 toString 문자열로 반환하게 하는 코드 작성
                data.put("google", google_data.toString());
                data.put("malwares", malwares_data.toString());
                data.put("phishtank", phishtank_data.toString());
                data.put("virustotal", virustotal_data.toString());
                data.put("idx", url_table_cursor.getInt(0));
                data.put("site_image", url_table_cursor.getString(6));

                return_data.put(data);

            } while(url_table_cursor.moveToNext());
        }

        System.out.println(return_data.toString());

        return return_data;
    }

    public ArrayList<UrlTableList> makeUrlTableList(Cursor url_table_cursor){
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

        return return_data;
    }
}