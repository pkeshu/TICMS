package com.imaginology.texas.Notification.PushNotification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Base64;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.imaginology.texas.Notification.PushNotification.NotificationReply.NotificationReply;
import com.imaginology.texas.R;
import com.imaginology.texas.util.LoginChecker;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import static android.app.Notification.GROUP_ALERT_SUMMARY;


public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private boolean isEnable;

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        Log.d("FCM Token:", "Fresh fcm device token: " + token);
        SharedPreferences sharedPref = getSharedPreferences("DeviceToken", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("token", token);
        editor.apply();
    }

    public static String getFcmToken(Context mContext) {
        return mContext.getSharedPreferences("DeviceToken", Context.MODE_PRIVATE).getString("DeviceToken", "");
    }


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        SharedPreferences sharedPreferences = getSharedPreferences("NotificationCheck", Context.MODE_PRIVATE);
        Boolean checkNotification = sharedPreferences.getBoolean("isNotificationOn", true);

        Log.d("OnMessageReceived", "Firebase Messeging service called");
        Intent intent = new Intent(this, NotificationReply.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, getString(R.string.default_notification_channel_id));
        String message = new Gson().toJson(remoteMessage.getData());
        Log.d("Message::::: {}", remoteMessage.getData().toString());
        Map<String, String> msg = remoteMessage.getData();
//        Integer id=Integer.valueOf(msg.get("id"));
        Integer id = 0;
        if (id != 0)
            new AllNotification().markNotificationAsRead(id);
        intent.putExtra("notificationTitle", msg.get("title"));
        intent.putExtra("sender", msg.get("sender"));
        intent.putExtra("notificationMessage", msg.get("body"));
        intent.putExtra("sentDate", msg.get("sentDate"));
        intent.putExtra("notificationId", msg.get("notificationId"));
        intent.putExtra("faculty", msg.get("notificationType"));
        Log.d("setting::::", checkNotification.toString());

        if (checkNotification) {
            //intent.putExtra("semester",msg.get())
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

            String sender = msg.get("sender");
            Log.d("profilePicture:", sender);
            String senderName = null, imageUrl = null;
            JSONObject jsonObject = null;
            Bitmap image = null;
            try {
                jsonObject = new JSONObject(sender);
                senderName = jsonObject.getString("senderName");
                imageUrl = jsonObject.getString("profilePicture");
                image = StringToBitMap(imageUrl);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (senderName != null)
                notificationBuilder.setContentTitle(senderName).setColor(getResources().getColor(R.color.colorPrimary));
            else
                notificationBuilder.setContentTitle(msg.get("title"));
            notificationBuilder.setContentText(msg.get("body"));
            notificationBuilder.setAutoCancel(true);
            notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);
            notificationBuilder.setLargeIcon(image);
            notificationBuilder.setContentIntent(pendingIntent);
            notificationBuilder.setGroupAlertBehavior(GROUP_ALERT_SUMMARY);
            notificationBuilder.setSound(notification);
            notificationBuilder.setDefaults(Notification.DEFAULT_SOUND);
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (notificationManager != null)
                if (LoginChecker.IsLoggedIn(this)) {
                    notificationManager.notify(1, notificationBuilder.build());

                }
        }
    }

    public Bitmap StringToBitMap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

}
