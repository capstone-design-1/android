package org.techtown.cpastone_design;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class Deeplink extends AppCompatActivity {
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intent);

        Intent intent_param = getIntent();
        String url = intent_param.getStringExtra("url");

        final JSONObject[] result = new JSONObject[1];
        Context context = this;
        final CountDownLatch latch = new CountDownLatch(1);
        TextView text1 = findViewById(R.id.result);
        text1.setText("분석 중 입니다.");

        try {
            new Thread(){
                public void run() {
                    try {
                        result[0] = isMaliciousURL(url, context);
                    } catch (IOException | JSONException | InterruptedException e) {
                        text1.setText("서버 에러");
                        e.printStackTrace();
                    }
                    latch.countDown();
                }
            }.start();
            latch.await();

            if(result[0].getBoolean("result")){
                if(result[0].getBoolean("is_malicious")){
                    text1.setText("유해한 URL");
                }
                else{
                    text1.setText("안전한 URL\n"+url);
                }
            }
            else{
                text1.setText("서버 에러");
            }
        } catch (JSONException e) {
            text1.setText("서버 에러");
            e.printStackTrace();
        } catch (InterruptedException e) {
            text1.setText("서버 에러");
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public JSONObject isMaliciousURL(String url, Context context) throws JSONException, IOException, InterruptedException {
        /*  전달 받은 URL을 분석

        Args:
            - url: 검사할 URL
            - context: ???
         */

        Api api = new Api();
        DeviceInfo device_info = new DeviceInfo();
        JSONObject return_data = new JSONObject();

        // API 서버에 분석 요청 및 응답 받아오기
        JSONObject res = api.getAnalysis(url, device_info.getDeviceId(context));

        if(res.getInt("status_code") == 200){
            return_data.put("result", true);

            if(res.getInt("is_malicious") == 1){
                return_data.put("is_malicious", true);
            }
            else{
                return_data.put("is_malicious", false);
            }
        }
        else{
            return_data.put("result", false);
        }

        return return_data;
    }
}
