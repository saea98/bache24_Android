package com.cmi.bache24.receiver;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.cmi.bache24.R;
import com.cmi.bache24.ui.activity.ReportStatusDetailActivity;
import com.cmi.bache24.ui.activity.SplashActivity;
import com.cmi.bache24.ui.activity.troop.NotificationActivity;
import com.cmi.bache24.util.Constants;
import com.cmi.bache24.util.PreferencesManager;
import com.cmi.bache24.util.Utils;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by omar on 1/22/17.
 */

public class CustomFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        if (!PreferencesManager.getInstance().notificationsEnabled(getApplicationContext())) {
//            Log.d("NotificaitonsEn", "Notifications are disabled");
            return;
        }

        /*Log.d(TAG, "From: " + remoteMessage.getFrom());

        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
        }

        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }*/

        if (remoteMessage.getData().containsKey(Constants.BACHE_ID_KEY) ||
            remoteMessage.getData().containsKey(Constants.SQUAD_LONG_MESSAGE_KEY)) {

            if (remoteMessage.getData().containsKey(Constants.BACHE_ID_KEY) &&
                !remoteMessage.getData().get(Constants.BACHE_ID_KEY).isEmpty()) {
                processCitizenPush(getApplicationContext(), remoteMessage);
            } else {
                processSquadPush(getApplicationContext(), remoteMessage);
            }
        } else {
            super.onMessageReceived(remoteMessage);
        }
    }

    private void processCitizenPush(Context context, RemoteMessage remoteMessage) {
        Intent resultIntent = null;

        if (Utils.isAppIsInBackground(context)) {
            resultIntent = new Intent(context, SplashActivity.class);
        } else {
            resultIntent = new Intent(context, ReportStatusDetailActivity.class);
        }

        resultIntent.putExtra(Constants.IS_FROM_NOTIFICATION, true);

        if (remoteMessage.getData().size() > 0 && remoteMessage.getData().containsKey(Constants.BACHE_ID_KEY)) {
            resultIntent.putExtra(Constants.NOTIFICATION_BACHE_ID, remoteMessage.getData().get(Constants.BACHE_ID_KEY).toString());
        }

        showNotificationMessage(context, context.getString(R.string.app_name), remoteMessage.getNotification().getBody(), resultIntent);
    }

    private void processSquadPush(Context context, RemoteMessage remoteMessage) {
        String shortMessage = remoteMessage.getNotification().getBody();

        String longMessage = "";

        if (remoteMessage.getData().containsKey(Constants.SQUAD_LONG_MESSAGE_KEY)) {
            longMessage = remoteMessage.getData().get(Constants.SQUAD_LONG_MESSAGE_KEY);
        }

        Intent resultIntent = new Intent(context, NotificationActivity.class);

        resultIntent.putExtra(Constants.SQUAD_NOTIFICATION_MESSAGE_ARG, longMessage);
        resultIntent.putExtra(Constants.SQUAD_NOTIFICATION_IS_APP_IN_FOREGROUND, Utils.isAppInForeground(context));

        showNotificationMessage(context, context.getString(R.string.app_name), shortMessage, resultIntent);
    }

    private void showNotificationMessage(Context context, String title, String message, Intent intent) {

        if (Utils.isAppIsInBackground(context)) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        }

        Utils.getInstance().showNotificationMessage(context, title, message, intent);
    }
}
