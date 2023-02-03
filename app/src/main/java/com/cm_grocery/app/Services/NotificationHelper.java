package com.cm_grocery.app.Services;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.cm_grocery.app.Activity.NewOrdersActivity;
import com.cm_grocery.app.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

public class NotificationHelper {

    private Context context;
    public static final String FCM_CHANNEL_ID = "ORDERS CHANNEL";
    public static final String FCM_CHANNEL_NAME = "JOB ALERTS";
    public static final String FCM_CHANNEL_DESC = "This channel receives job related notifications";

    private NotificationHelper(Context context) {
        // private constructor
        this.context = context;
    }

    private static NotificationHelper notificationHelper;

    public static NotificationHelper getInstance(Context context) {
        if (notificationHelper == null) {
            notificationHelper = new NotificationHelper(context);
        }
        return notificationHelper;
    }

    public void createJobNotification(JSONObject jsonObject) throws JSONException {
        String title = jsonObject.getString("title");
        //String message = jsonObject.getString("message");
        Log.d("OrderData", "createJobNotification: " + jsonObject);
        createNotificationChannel(FCM_CHANNEL_ID, FCM_CHANNEL_NAME, FCM_CHANNEL_DESC, context);

        Uri soundUri = Uri.parse("android.resource://" + context.getApplicationContext().getPackageName() + "/" + R.raw.vendor_delivery_notification);
        playNotificationSound();
        Intent notifyIntent = null;
        notifyIntent = new Intent(context, NewOrdersActivity.class);
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        @SuppressLint("UnspecifiedImmutableFlag") PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, FCM_CHANNEL_ID);
        notificationBuilder.setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.bhanu_mart_delivery_boy_app_logo)
                .setTicker("Order")
                .setContentTitle(title)
                .setContentText(title)
                .setSound(soundUri)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationManagerCompat.IMPORTANCE_HIGH)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(title));

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(generateUniqueNotificationId(), notificationBuilder.build());
    }

    private MediaPlayer mMediaPlayer;

    public void playNotificationSound() {
        stopNotificationSound();
        try {
            Uri alarmSound = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.vendor_delivery_notification);
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setDataSource(context, alarmSound);
            final AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            if (audioManager != null && audioManager.getStreamVolume(AudioManager.STREAM_RING) != 0) {
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_RING);
                mMediaPlayer.setLooping(false);
                mMediaPlayer.prepare();
                mMediaPlayer.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopNotificationSound() {
        try {
            if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createNotificationChannel(String channelId, String channelName, String channelDesc, Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            AudioAttributes audioAttrib = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();
            //Uri soundUri = Uri.parse("android.resource://" + context.getApplicationContext().getPackageName() + "/" + R.raw.vendor_delivery_notification);

            Uri soundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getPackageName() + "/" + R.raw.vendor_delivery_notification);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
            channel.setDescription(channelDesc);
            channel.setImportance(importance);
            channel.enableLights(true);
            channel.setLightColor(Color.RED);
            channel.enableVibration(false);
            channel.setSound(soundUri, audioAttrib);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    private int generateUniqueNotificationId() {
        // this id helps in removing and clearing the notifications, so we need to store it
        Random random = new Random();
        return random.nextInt(1000000);
    }
}
