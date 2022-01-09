package org.techtown.cpastone_design;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class Api {
    // main function
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void start(String search_url) throws IOException {
        String API_URL = "http://13.124.101.242:8080/api/report/all?url=";
        String encode_search_url = Base64.getUrlEncoder().encodeToString(search_url.getBytes());
        String request_url = API_URL + encode_search_url;

        new Thread(){
            public void run() {
                try {
                    getData(request_url);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public void getData(String request_url) throws IOException {
        URL obj = new URL(request_url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Accept", "application/json");
        int responseCode = con.getResponseCode();

        System.out.println(responseCode);

        BufferedReader rd = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            sb.append(line);
        }
        rd.close();
        con.disconnect();
        System.out.println(sb.toString());
    }
}
