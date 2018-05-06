package com.cmi.bache24.receiver;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;

//import com.apptentive.android.sdk.Apptentive;
import com.cmi.bache24.data.model.User;
import com.cmi.bache24.ui.activity.ReportStatusDetailActivity;
import com.cmi.bache24.ui.activity.SplashActivity;
import com.cmi.bache24.ui.activity.troop.NotificationActivity;
import com.cmi.bache24.util.Constants;
import com.cmi.bache24.util.PreferencesManager;
import com.cmi.bache24.util.Utils;
//import com.parse.ParsePushBroadcastReceiver;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by omar on 1/26/16.
 * http://www.androidhive.info/2015/06/android-push-notifications-using-parse-com/
 */
public class CustomPushReceiver /*extends ParsePushBroadcastReceiver*/ {

    private final String TAG = CustomPushReceiver.class.getSimpleName();

    private Intent parseIntent;

    public CustomPushReceiver() {
        super();
    }

//    @Override
    protected void onPushReceive(Context context, Intent intent) {

        if (intent == null)
            return;

        try {
            JSONObject json = new JSONObject(intent.getExtras().getString("com.parse.Data"));

            parseIntent = intent;

            if (json.has("idBache")) {

//                User currentUser = PreferencesManager.getInstance().getUserInfo(context);
//                if (currentUser == null)
//                    return;
//                currentUser.getUserType() == Constants.USER_TYPE_TROOP &&

                if (json.get("idBache").toString().trim().equals("")) {
                    parsePushSquad(context, json);
                } else {
                    parsePushJson(context, json);
                }
            } else {
//                super.onPushReceive(context, intent);
            }

        } catch (JSONException e) {

        }
    }

//    @Override
    protected void onPushDismiss(Context context, Intent intent) {
//        super.onPushDismiss(context, intent);
    }

//    @Override
    protected void onPushOpen(Context context, Intent intent) {

        try {
            JSONObject json = new JSONObject(intent.getExtras().getString("com.parse.Data"));

            if (json.has("apptentive")) {
                if (!Utils.isAppIsInBackground(context)) {
//                    if (Apptentive.canShowMessageCenter()) {
//                        Apptentive.showMessageCenter(context);
//                    }
                } else {
                    PreferencesManager.getInstance().setShowApptentiveMessageCenter(context, true);
//                    super.onPushOpen(context, intent);
                }
            } else {
//                super.onPushOpen(context, intent);
            }

        } catch (JSONException ex) {
//            super.onPushOpen(context, intent);
        }

    }

    private void parsePushJson(Context context, JSONObject json) {
        try {
            String message = json.getString("alert");

            Intent resultIntent = null;
            if (Utils.isAppIsInBackground(context)) {
                resultIntent = new Intent(context, SplashActivity.class);
            } else {
                resultIntent = new Intent(context, ReportStatusDetailActivity.class);
            }

            resultIntent.putExtra(Constants.IS_FROM_NOTIFICATION, true);
            resultIntent.putExtra(Constants.NOTIFICATION_BACHE_ID, json.getString("idBache"));
                showNotificationMessage(context, "Bache 24", message, resultIntent);

        } catch (JSONException e) {

        }
    }

    private void showNotificationMessage(Context context, String title, String message, Intent intent) {

        intent.putExtras(parseIntent.getExtras());

        if (Utils.isAppIsInBackground(context)) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        }

        Utils.getInstance().showNotificationMessage(context, title, message, intent);
    }

    private void parsePushSquad(Context context, JSONObject json) {
        try {
            String shortMessage = json.getString("alert");
            String longMessage = json.getString("mensaje");

            Intent resultIntent = new Intent(context, NotificationActivity.class);

            resultIntent.putExtra(Constants.SQUAD_NOTIFICATION_MESSAGE_ARG, longMessage);
            resultIntent.putExtra(Constants.SQUAD_NOTIFICATION_IS_APP_IN_FOREGROUND, isAppInForeground(context));

            showNotificationMessage(context, "Bache 24", shortMessage, resultIntent);

        } catch (JSONException e) {

        }
    }

    public boolean isAppInForeground(Context context)
    {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> processInfos = activityManager.getRunningAppProcesses();

        if (processInfos == null)
        {
            return false;
        }

        String packageName = context.getPackageName();
        for (int i = 0; i < processInfos.size(); i++)
        {
            ActivityManager.RunningAppProcessInfo item = processInfos.get(i);

            if (item.importance == item.IMPORTANCE_FOREGROUND && item.processName.equals(packageName))
            {
                return true;
            }
        }

        return false;
    }
}
