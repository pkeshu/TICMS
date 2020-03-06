package com.imaginology.texas.Notification.PrivateMessage;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class SecreteMessage {
    public static void sendSecreteMessage(Context context,Long receiverId){
        Intent intent=new Intent(context,PrivateMessageActivity.class);
        intent.putExtra("receiverId",receiverId);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
