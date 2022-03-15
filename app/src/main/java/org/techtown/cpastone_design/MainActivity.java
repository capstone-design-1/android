package org.techtown.cpastone_design;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    RecyclerVierAdapter adapter;
    DeviceInfo device_info = new DeviceInfo();

    MsgList data;


    // 권한 리스트 (일단 문자만)
    String[] permission_list = {
            Manifest.permission.RECEIVE_SMS,
    };



    Api api = new Api();

    public static Context mContext;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPermission();

        Uri url = getIntent().getData();
        if(url != null){
            Intent intent = new Intent(MainActivity.this, Deeplink.class);
            intent.putExtra("url", url.toString());
            startActivity(intent);
            return;
        }

        mContext = this;

        init();
        ten_msg();

        /*
        // (1) 리시버에 의해 해당 액티비티가 새롭게 실행된 경우
        Intent passedIntent = getIntent();

        Log.d("new", "액티비티가 새롭게 실행된 경우");
        processIntent(passedIntent);

        // 동기화 버튼 기능
//        ImageButton btn = (ImageButton) findViewById(R.id.syncBtn);
//        btn.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View view) {
//                try {
//                    boolean result = api.syncDB(context);
//                    if(result == true){
//                        Toast myToast = Toast.makeText(context.getApplicationContext(),"동기화를 성공했습니다.", Toast.LENGTH_SHORT);
//                        myToast.show();
//                    }else{
//                        Toast myToast = Toast.makeText(context.getApplicationContext(),"동기화를 실패했습니다.", Toast.LENGTH_SHORT);
//                        myToast.show();
//                    }
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
*/

    }

    private void init(){
        RecyclerView recyclerView = findViewById(R.id.recyclerView);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        adapter = new RecyclerVierAdapter();
        recyclerView.setAdapter(adapter);
    }


    private void ten_msg() {
        JSONArray ten = null;
        try {
            ten = api.getSearchData(10,device_info.getDeviceId(this));
            for(int i = 0; i < ten.length(); i++){
                JSONObject temp = ten.getJSONObject(i);
                createmsg(temp.getString("previous_url"), temp);
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void createmsg(String string, JSONObject res) throws JSONException, IOException {

        data = new MsgList(string, res);
        adapter.addItem(data);

        /*
        //1. 텍스트뷰 객체생성
        TextView textView = new TextView(getApplicationContext());

        //2. 텍스트뷰에 들어갈 문자설정
        textView.setText(string);

        //3. 텍스트뷰 글자크기 설정
        //textViewNm.setTextSize(12);//텍스트 크기

        //4. 텍스트뷰 글자타입 설정
        //textViewNm.setTypeface(null, Typeface.BOLD);

        //5. 텍스트뷰 ID설정 & 같은 index에 json정보 저장
        textView.setId(arraymsg.size());
        System.out.println(arraymsg.size());
        arrayJson.add(res);

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

         */

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void processIntent(Intent intent)  {
        init();
        ten_msg();
        /*
        JSONObject res = null;
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
                    Log.d("msg","msg is null");
                }
                else{
                    for(int i=0; i < url.size() ; i++){
                        String temp = url.get(i);
                        Log.d("TEMP", temp);
                        if(temp.startsWith("http") || temp.startsWith("https")){
                            res = api.getAnalysis(temp, device_info.getDeviceId(this));
                        }
                        else{
                            res = api.getAnalysis("http://"+temp, device_info.getDeviceId(this));
                        }
                    }

                    createmsg(string, res);

                }
            }

        }
        */
    }
    /*

    // (2) 이미 실행된 상태였는데 리시버에 의해 다시 켜진 경우
    // (이러한 경우 onCreate()를 거치지 않기 때문에 이렇게 오버라이드 해주어야 모든 경우에 SMS문자가 처리된다!
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onNewIntent(Intent intent) {
        Log.d("reload", "다시켜진 경우");
        processIntent(intent);

        super.onNewIntent(intent);
    }
*/

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
            return no;
        }
    }


}