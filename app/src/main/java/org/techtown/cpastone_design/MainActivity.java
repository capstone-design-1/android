package org.techtown.cpastone_design;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {


    // 권한 리스트 (일단 문자만)
    String[] permission_list = {
            Manifest.permission.RECEIVE_SMS,
    };


    TextView textView;
    Button button;
  
    private static String CHANNEL_ID = "channel1";
    private static String CHANEL_NAME = "Channel1";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView)findViewById(R.id.textView);
        button = (Button)findViewById(R.id.button);


        // (1) 리시버에 의해 해당 액티비티가 새롭게 실행된 경우
        Intent passedIntent = getIntent();
        processIntent(passedIntent);

    }


    private void processIntent(Intent intent){

        if(intent != null){
            // null 예외처리 해야 동작하는듯
            String string = intent.getStringExtra("string");
            if(string == null){
                Log.d("receive data", "string is null");
            }
            else{
                Log.d("receive data", string);
                String url = checkUrl(string);
                if(url.isEmpty()){
                    textView.setText("No url");
                }
                else{
                    textView.setText(url);
                }


            }

        }
    }

    // (2) 이미 실행된 상태였는데 리시버에 의해 다시 켜진 경우
    // (이러한 경우 onCreate()를 거치지 않기 때문에 이렇게 오버라이드 해주어야 모든 경우에 SMS문자가 처리된다!
    @Override
    protected void onNewIntent(Intent intent) {
        processIntent(intent);

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
    public static String checkUrl(String content){
        try {
            String REGEX = "^*((http|https)://)?(www.)?([a-zA-Z0-9]+)\\.[a-z]+([a-zA-z0-9.?#]+)?";
            Pattern p = Pattern.compile(REGEX, Pattern.CASE_INSENSITIVE);
            Matcher m = p.matcher(content);
            if (m.find()) {
                return m.group();
            }
            return "";
        } catch (Exception e) {
            return "";
        }
    }


}