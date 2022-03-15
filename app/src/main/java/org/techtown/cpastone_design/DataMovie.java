package org.techtown.cpastone_design;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class DataMovie {
    String malicious;
    String title;
    Bitmap image;

    public DataMovie(String title, JSONObject res) throws JSONException, IOException {
        //this.image = image;
        System.out.println("데이터 전송 : " + res);
        this.title = title;
        int temp = res.getInt("malicious");
        if(temp == 0){
            this.malicious = "무해함";
        }
        else{
            this.malicious = "유해함";
        }
        Thread img = new Thread(){
            @Override
            public void run(){
                try {
                    Log.d("IMAGE", res.getString("site_image"));
                    URL imgUrl = new URL("http://13.124.101.242:8080"+res.getString("site_image"));

                    HttpURLConnection conn = (HttpURLConnection)imgUrl.openConnection();
                    conn.setDoInput(true); //Server 통신에서 입력 가능한 상태로 만듦
                    conn.connect(); //연결된 곳에 접속할 때 (connect() 호출해야 실제 통신 가능함)

                    InputStream is = conn.getInputStream(); //inputStream 값 가져오기
                    image = BitmapFactory.decodeStream(is); // Bitmap으로 반환
                    

                } catch (MalformedURLException | JSONException e) {
                    e.printStackTrace();
                    Log.d("IMAGE","업로드 실패함");
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

        };
        img.start();


    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public String getMalicious(){
        return malicious;
    }

    public void  setMalicious(String malicious){
        this.malicious = malicious;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}