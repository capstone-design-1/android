package org.techtown.cpastone_design;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private static String CHANNEL_ID = "channel1";
    private NotificationManager notificationManager;
    private static int NOTIFICATION_ID = 0;
    LinearLayout linearLayout;


    // 권한 리스트 (일단 문자만)
    String[] permission_list = {
            Manifest.permission.RECEIVE_SMS,
    };



    Api api = new Api();


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPermission();

        linearLayout = findViewById(R.id.linearLayout);



        // (1) 리시버에 의해 해당 액티비티가 새롭게 실행된 경우
        Intent passedIntent = getIntent();
        try {

            processIntent(passedIntent);
        } catch (IOException | InterruptedException | JSONException e) {
            e.printStackTrace();
        }

    }

    private void createTextView(String string){

        //1. 텍스트뷰 객체생성
        TextView textView = new TextView(getApplicationContext());

        //2. 텍스트뷰에 들어갈 문자설정
        textView.setText(string);

        //3. 텍스트뷰 글자크기 설정
        //textViewNm.setTextSize(12);//텍스트 크기

        //4. 텍스트뷰 글자타입 설정
        //textViewNm.setTypeface(null, Typeface.BOLD);

        //5. 텍스트뷰 ID설정
        textView.setId(0);

        //6. 레이아웃 설정
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT
                ,300);

        param.setMargins(30,303,30,30);

        textView.setPadding(10,10,10,10);
        textView.setBackgroundResource(R.drawable.round);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        }


        // 7. 설정한 레이아웃 텍스트뷰에 적용
        textView.setLayoutParams(param);

        //9. 생성및 설정된 텍스트뷰 레이아웃에 적용
        linearLayout.addView(textView);

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void processIntent(Intent intent) throws IOException, InterruptedException, JSONException {

        if(intent != null){
            // null 예외처리 해야 동작하는듯
            String string = intent.getStringExtra("string");
            if(string == null){
                Log.d("receive data", "string is null");
            }
            else{
                Log.d("receive data", string);
                List<String> url = checkUrl(string);

                if(url.isEmpty()){
                    createTextView("NO URL");
                }
                else{
                    for(int i=0; i < url.size() ; i++){
                        String temp = url.get(i);
                        Log.d("TEMP", temp);
                        if(temp.startsWith("http") || temp.startsWith("https")){
                            api.start(temp, this);
                        }
                        else{
                            api.start("http://"+url, this);
                        }
                    }

                    createTextView(res.toString());

                }
            }

        }
    }

    // (2) 이미 실행된 상태였는데 리시버에 의해 다시 켜진 경우
    // (이러한 경우 onCreate()를 거치지 않기 때문에 이렇게 오버라이드 해주어야 모든 경우에 SMS문자가 처리된다!
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onNewIntent(Intent intent) {
        try {
            processIntent(intent);
        } catch (IOException | InterruptedException | JSONException e) {
            e.printStackTrace();
        }

        super.onNewIntent(intent);
    }


    public void checkPermission(){

        //6.0 이상부터 권한 필요 (미만이면 안물어보고 넘어감)
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            return;

        for(String permission : permission_list){

            // 권한 허용 여부를 담고 있음
            int check = checkCallingOrSelfPermission(permission);

            if(check == PackageManager.PERMISSION_DENIED){
                // 안했으면 요청
                requestPermissions(permission_list,0);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==0)
        {
            for(int i=0; i<grantResults.length; i++)
            {
                // 허용 했으면
                if(grantResults[i]==PackageManager.PERMISSION_GRANTED){
                    Log.d("sms log", "권한 요청 완료");
                }
                else {
                    // 하나라도 안했으면 앱 종료
                    Toast.makeText(getApplicationContext(),"앱권한설정하세요",Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        }
    }

    // url 정규식
    // http 랑 www 없이 성공함!
    public static List<String> checkUrl(String content){
        try {
            String[] content_arr = content.split(" ");

            String REGEX = "^*((http|https)://)?(www.)?([a-zA-Z0-9]+)\\.[a-z]+([a-zA-z0-9.?#]+)?";
            Pattern p = Pattern.compile(REGEX, Pattern.CASE_INSENSITIVE);
            List<String> list = new ArrayList<>();

            Matcher m = p.matcher(content);
            while(m.find()){
                list.add(m.group());
            }

            return list;
        } catch (Exception e) {
            List<String> no = new ArrayList<>();
            System.out.println(e.toString());
            return no;
        }
    }


}