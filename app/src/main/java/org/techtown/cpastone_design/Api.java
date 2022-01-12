package org.techtown.cpastone_design;

import android.os.Build;

import androidx.annotation.RequiresApi;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.concurrent.CountDownLatch;

public class Api {
    // main function
    @RequiresApi(api = Build.VERSION_CODES.O)
    public JSONObject start(String search_url) throws IOException, InterruptedException {
        String API_URL = "http://13.124.101.242:8080/api/report/all?url=";
        String encode_search_url = Base64.getUrlEncoder().encodeToString(search_url.getBytes());
        String request_url = API_URL + encode_search_url;
        final JSONObject[] response_json = new JSONObject[1];

        // Thread return 값을 받기 위한 설정
        final CountDownLatch latch = new CountDownLatch(1);
        new Thread(){
            public void run() {
                try {
                    response_json[0] = getData(request_url);
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
                latch.countDown();
            }
        }.start();
        latch.await();

        return response_json[0];
    }

    public JSONObject getData(String request_url) throws IOException, JSONException {
        // TODO
        // Timeout 예외처리
        
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
            return response_json;

            // How to Use
            // https://codechacha.com/ko/how-to-parse-json-in-android/
            // responseJson.getJSONObject("virustotal").getString("harmless")
        }
        else{
            System.out.println("[!] api server down.");
            return null;
        }
    }
}