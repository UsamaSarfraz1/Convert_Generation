package com.cgitsoft.convertgeneration.Notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;

import androidx.core.app.NotificationCompat;

import com.cgitsoft.convertgeneration.R;
import com.cgitsoft.convertgeneration.activities.AttendanceDetailActivity;
import com.google.firebase.messaging.RemoteMessage;

import static com.cgitsoft.convertgeneration.Constants.NOTIFICATION_ID;

public class showNotification {
    public static void showNotificationToAdmin(RemoteMessage remoteMessage, Context context){
        NotificationManager mNotificationManager;
        NotificationCompat.Builder mBuilder;
        final String NOTIFICATION_CHANNEL_ID = "383838";

        String Message = remoteMessage.getData().get("Message");
        String Title = remoteMessage.getData().get("Title");
        String id = remoteMessage.getData().get("id");
        NOTIFICATION_ID=Integer.parseInt(id);

        AudioAttributes att = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                .build();

        Intent intent =new Intent(context, AttendanceDetailActivity.class);
        PendingIntent pendingIntent =PendingIntent.getActivity(context,NOTIFICATION_ID,intent,PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        mBuilder = new NotificationCompat.Builder(context,NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.mipmap.logo)
                .setContentTitle(Title)
                .setContentText(Message)
                .setAutoCancel(true)
                .setSound(defaultSound)
                .setContentIntent(pendingIntent);

        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
        {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "COVERT_GENERATIONS", importance);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setSound(defaultSound, att);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
            mBuilder.setAutoCancel(true);
            mBuilder.setSound(defaultSound);
            mNotificationManager.createNotificationChannel(notificationChannel);
            mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
        }else {
            mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
        }
    }
}
