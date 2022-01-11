package org.techtown.cpastone_design;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import static android.content.Context.NOTIFICATION_SERVICE;

public class SMSReceiver extends BroadcastReceiver {

    private static final String TAG = "SmsReceiver";


    private static String CHANNEL_ID = "channel1";
    private static String CHANEL_NAME = "Channel1";

    @Override
    public void onReceive(Context context, Intent intent) {
        // 문자 오면 onReceive() 가 호출이 됨

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

            showNoti(context, contents);
            sendToActivity(context, contents);

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

    public void showNoti(Context context, String string){

        NotificationCompat.Builder builder = null;
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra("string", string);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder = new NotificationCompat.Builder(context, CHANNEL_ID);
        }else {
            builder = new NotificationCompat.Builder(context);
        }

        builder.setContentTitle("sms_app")
                .setContentText("")
                .setSmallIcon(R.drawable.alert)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);


        Notification notification = builder.build();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(1, notification);
    }



}