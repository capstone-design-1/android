package org.techtown.cpastone_design;

import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.techtown.cpastone_design.DB.GoogleTable;
import org.techtown.cpastone_design.DB.MalwaresTable;
import org.techtown.cpastone_design.DB.PhishtankTable;
import org.techtown.cpastone_design.DB.UrlTable;
import org.techtown.cpastone_design.DB.UrlTableList;
import org.techtown.cpastone_design.DB.VirustotalTable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Base64;
import java.util.concurrent.CountDownLatch;

public class Api {

    @RequiresApi(api = Build.VERSION_CODES.O)
    public JSONObject getAnalysis(String search_url, Context context) throws IOException, InterruptedException, JSONException {

        String API_URL = "http://13.124.101.242:8080/api/report/all?url=";
        String encode_search_url = Base64.getUrlEncoder().encodeToString(search_url.getBytes());
        String request_url = API_URL + encode_search_url;
        final JSONObject[] response_json = new JSONObject[1];

        // Thread return 값을 받기 위한 설정
        final CountDownLatch latch = new CountDownLatch(1);
        new Thread(){
            public void run() {
                try {
                    response_json[0] = parseData(request_url);
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
                latch.countDown();
            }
        }.start();
        latch.await();


        if(response_json[0].getInt("status_code") == 200){
            ArrayList<UrlTableList> url_table_list = searchUrl(search_url, context);

            if(url_table_list.size() == 0){
                saveData(response_json[0], context);
            }
        }

        return response_json[0];
    }

    public boolean syncDB(Context context) throws InterruptedException, JSONException {
        System.out.println("-===========================");
        String API_URL = "http://13.124.101.242:8080/db/sync";
        final JSONObject[] response_json = new JSONObject[1];

        // Thread return 값을 받기 위한 설정
        final CountDownLatch latch = new CountDownLatch(1);
        new Thread(){
            public void run() {
                try {
                    response_json[0] = parseData(API_URL);
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
                latch.countDown();
            }
        }.start();
        latch.await();

        if(response_json[0].getInt("status_code") == 200){
            UrlTable url_table = new UrlTable(context);
            GoogleTable google_table = new GoogleTable(context);
            MalwaresTable malwares_table = new MalwaresTable(context);
            PhishtankTable phishtank_table = new PhishtankTable(context);
            VirustotalTable virustotal_table = new VirustotalTable(context);

            url_table.deleteAll();
            google_table.deleteAll();
            malwares_table.deleteAll();
            phishtank_table.deleteAll();
            virustotal_table.deleteAll();

            for(int i=0; i<response_json[0].getInt("count"); i++){
                JSONObject tmp_url = response_json[0].getJSONArray("url_table").getJSONObject(i);
                JSONObject tmp_google = response_json[0].getJSONArray("google").getJSONObject(i);
                JSONObject tmp_malwares = response_json[0].getJSONArray("malwares").getJSONObject(i);
                JSONObject tmp_phishtank = response_json[0].getJSONArray("phishtank").getJSONObject(i);
                JSONObject tmp_virustotal = response_json[0].getJSONArray("virustotal").getJSONObject(i);

                url_table.insert(tmp_url.getString("previous_url"), tmp_url.getInt("malicious"), tmp_url.getString("site_image"));
                google_table.insert(tmp_google.getString("detail"), tmp_google.getInt("url_id"));
                malwares_table.insert(tmp_malwares.getString("detail"), tmp_malwares.getInt("url_id"));
                phishtank_table.insert(tmp_phishtank.getString("detail"), tmp_phishtank.getInt("url_id"));
                virustotal_table.insert(tmp_virustotal.getString("detail"), tmp_virustotal.getInt("url_id"));
            }

            return true;
        }

        return false;
    }

    public JSONObject parseData(String request_url) throws IOException, JSONException {
        URL obj = new URL(request_url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        con.setRequestMethod("GET");
        con.setRequestProperty("Accept", "application/json");
        int response_code = con.getResponseCode();

        if (response_code == 200 || response_code == 400){
            BufferedReader rd = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                sb.append(line);
            }
            rd.close();
            con.disconnect();

            JSONObject response_json = null;
            response_json = new JSONObject(sb.toString());
            response_json.put("status_code", response_code);
            return response_json;

            // How to Use
            // https://codechacha.com/ko/how-to-parse-json-in-android/
            // responseJson.getJSONObject("virustotal").getString("harmless")
        }
        else{
            System.out.println("[!] api server down.");

            JSONObject return_data = new JSONObject();
            return_data.put("error", "api server down");
            return_data.put("status_code", response_code);

            return return_data;
        }
    }
    
    // DB에 결과 저장
    public void saveData(JSONObject data, Context context) throws JSONException {
        UrlTable url_table = new UrlTable(context);
        GoogleTable google_table = new GoogleTable(context);
        MalwaresTable malwares_table = new MalwaresTable(context);
        PhishtankTable phishtank_table = new PhishtankTable(context);
        VirustotalTable virustotal_table = new VirustotalTable(context);

        url_table.insert(data.getString("url"), data.getInt("is_malicious"), data.getString("site_image"));
        ArrayList<UrlTableList> url_table_list = url_table.selectURL(data.getString("url"));

        google_table.insert(data.getJSONObject("detail").getJSONObject("google_safe_browsing").toString(), url_table_list.get(0).url_id);
        malwares_table.insert(data.getJSONObject("detail").getJSONObject("malwares").toString(), url_table_list.get(0).url_id);
        phishtank_table.insert(data.getJSONObject("detail").getJSONObject("phishtank").toString(), url_table_list.get(0).url_id);
        virustotal_table.insert(data.getJSONObject("detail").getJSONObject("virustotal").toString(), url_table_list.get(0).url_id);
    }

    // DB에 저장되었는지 확인
    public ArrayList<UrlTableList> searchUrl(String url, Context context){
        UrlTable url_table = new UrlTable(context);
        ArrayList<UrlTableList> url_table_list = url_table.selectURL(url);

        return url_table_list;
    }
}