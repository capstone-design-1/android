package org.techtown.cpastone_design;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.content.Context.NOTIFICATION_SERVICE;

public class SMSReceiver extends BroadcastReceiver {



    private static final String TAG = "SmsReceiver";
    DeviceInfo device_info = new DeviceInfo();
    //mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    private static String CHANNEL_ID = "channel1";
    private NotificationManager notificationManager;
    private static int NOTIFICATION_ID = 0;

    Api api = new Api();

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onReceive(Context context, Intent intent) {
        // 문자 오면 onReceive() 가 호출이 됨

        //((MainActivity)MainActivity.mContext).onNewIntent(intent);

        createNotificationChannel(context);
        Bundle bundle = intent.getExtras();

        // 메소드 밑에 있음
        SmsMessage[] messages = parseSmsMessage(bundle);

        if(messages.length>0){

            /*
            // 문자 보낸 사람
            String sender = messages[0].getOriginatingAddress();
            Log.d(TAG, "sender: "+sender);

             */

            // 문자 내용
            String contents = messages[0].getMessageBody().toString();
            Log.d(TAG, contents);

            /*
            // 수신 날짜/시간 데이터
            Date receivedDate = new Date(messages[0].getTimestampMillis());
            Log.d(TAG, "received date: "+receivedDate);

             */

            try {
                if(IsMalicious(context, contents) == true) {
                    sendNotification(context, contents);
                }
                else{
                    Log.d("IsMalicious","안위험함");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //sendToActivity(context, contents);

        }
    }


    private void sendToActivity(Context context, String str){
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("string", str);
        context.startActivity(intent);
    }

    // 정형화된 코드라는데 뭔지 모르겠음
    private SmsMessage[] parseSmsMessage(Bundle bundle){
        Object[] objs = (Object[])bundle.get("pdus");
        SmsMessage[] messages = new SmsMessage[objs.length];

        for(int i=0;i<objs.length;i++){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                String format = bundle.getString("format");
                messages[i] = SmsMessage.createFromPdu((byte[])objs[i], format);
            }
            else{
                messages[i] = SmsMessage.createFromPdu((byte[])objs[i]);
            }

        }
        return messages;
    }


    //채널을 만드는 메소드
    public void createNotificationChannel(Context context) {
        //notification manager 생성
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        // 기기(device)의 SDK 버전 확인 ( SDK 26 버전 이상인지 - VERSION_CODES.O = 26)
        if (android.os.Build.VERSION.SDK_INT
                >= android.os.Build.VERSION_CODES.O) {
            //Channel 정의 생성자( construct 이용 )
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID
                    , "Test Notification", notificationManager.IMPORTANCE_HIGH);
            //Channel에 대한 기본 설정
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription("Notification from Mascot");
            // Manager을 이용하여 Channel 생성
            notificationManager.createNotificationChannel(notificationChannel);
        }

    }

    // Notification Builder를 만드는 메소드
    private NotificationCompat.Builder getNotificationBuilder(Context context, String str) {
        Intent intent = new Intent(context, MainActivity.class);
        //intent.putExtra("string",str);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notifyBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle("url_link")
                .setContentText("악성 URL이 탐지 되었습니다.")
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.alert);
        return notifyBuilder;
    }

    // Notification을 보내는 메소드
    public void sendNotification(Context context, String str) {
        // Builder 생성
        NotificationCompat.Builder notifyBuilder = getNotificationBuilder(context,str);
        // Manager를 통해 notification 디바이스로 전달
        notificationManager.notify(NOTIFICATION_ID, notifyBuilder.build());
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
            List<String> no = null;
            System.out.println(e.toString());
            return no;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private Boolean IsMalicious(Context context, String string) throws InterruptedException, JSONException, IOException {
        JSONObject res = null;
        if(string == null) {
            // null 예외처리 해야 동작하는듯
            Log.d("receive data", "string is null");
        }
        else{
            Log.d("receive data", string);
            List<String> url = checkUrl(string);

            if(url.isEmpty()){
                Log.d("msg","msg is null");
                return false;
            }
            else{
                for(int i=0; i < url.size() ; i++){
                    String temp = url.get(i);
                    Log.d("TEMP", temp);
                    if(temp.startsWith("http") || temp.startsWith("https")){
                        res = api.getAnalysis(temp, device_info.getDeviceId(context));
                    }
                    else{
                        res = api.getAnalysis("http://"+temp, device_info.getDeviceId(context));
                    }
                }
                if (res.getInt("is_malicious") == 1) {
                    Log.d("res result", "1");
                    return true;
                }
                else{
                    Log.d("res result", "0");
                    return false;
                }
            }

        }
        return false;
    }

}