package com.youme.candid.ourchat.fcmServices;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.util.Patterns;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.youme.candid.ourchat.ChatActivity;
import com.youme.candid.ourchat.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


import static android.content.ContentValues.TAG;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private String body = "";
    private String title = "";
    private String type = "";
    private String sound = "";
    private String userId = "";
    private String click_action = "";
    private String profileImage = "";
    private String notify_id = "";
    private String image_url = "";
    Bitmap bitmap;
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        if (remoteMessage.getData()!=null) {
            Log.d("msg", "Message: " + remoteMessage.getData());

            Log.d(TAG, "From: " + remoteMessage.getFrom());
           // Log.d(TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody());

            userId = remoteMessage.getData().get("userId");
            body = remoteMessage.getData().get("body");
            type = remoteMessage.getData().get("type");
            sound = remoteMessage.getData().get("sound");
            title = remoteMessage.getData().get("title");
            image_url = remoteMessage.getData().get("image");
            click_action = remoteMessage.getData().get("click_action");
            profileImage = remoteMessage.getData().get("profileImage");
            notify_id = remoteMessage.getData().get("notify_id");
        }
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        sendNotification(body, type, title, sound, userId, click_action, profileImage, notify_id, image_url,defaultSoundUri);
    }



    private void sendNotification(String body, String type, String title, String sound, String userId, String click_action,
                                  String profileImage, String notify_id, String image_url, Uri defaultSoundUri) {

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        String CHANNEL_ID = "MyFirebaseDemoo";// The id of the channel.
        CharSequence name = "MyChannel";// The user-visible name of the channel.
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel mChannel = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            notificationManager.createNotificationChannel(mChannel);
        }

        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("body", body);
        intent.putExtra("type", type);
        intent.putExtra("title", title);
        intent.putExtra("sound", sound);
        intent.putExtra("userId", userId);
        intent.putExtra("click_action", click_action);
        intent.putExtra("notify_id", notify_id);
        intent.putExtra("profileImage", profileImage);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);


        if (image_url != null && Patterns.WEB_URL.matcher(image_url).matches()) {

             bitmap = getBitmapFromURL(image_url);
        }
        NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle();
        bigPictureStyle.bigPicture(bitmap);
        bigPictureStyle.setBigContentTitle(title);
        bigPictureStyle.setSummaryText(body);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.iconchat))
                .setContentTitle(sound)
                .setContentText(body)
                .setDefaults(Notification.DEFAULT_ALL)
                .setStyle(bigPictureStyle)
                .setPriority(Notification.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
           notificationBuilder.setSmallIcon(R.drawable.iconchat);
           notificationBuilder.setColor(getResources().getColor(R.color.colorPrimary));
        } else {
            notificationBuilder.setSmallIcon(R.drawable.iconchat);
        }


        notificationManager.notify(1, notificationBuilder.build());
    }


    /**
     * Downloading push notification image before displaying it in
     * the notification tray
     */
    public Bitmap getBitmapFromURL(String strURL) {
        try {
            URL url = new URL(strURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


}
