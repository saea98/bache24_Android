package com.cmi.bache24.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.cmi.bache24.data.model.Report;
import com.cmi.bache24.data.model.User;
import com.cmi.bache24.security.Security;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by omar on 12/2/15.
 */
public class PreferencesManager {

    private SharedPreferences preferences;
    private static PreferencesManager instance;

    private String _TAG_PREFERENCES = "_DEFAULT_VALUES";
    private String _TAG_USER_INFO = "_UI";
    private String _TAG_USER_REGISTERED_WITH_EMAIL = "_TAG_USER_REGISTERED_WITH_EMAIL";
    private String _TAG_GET_REPORTS = "_TAG_GET_REPORTS";
    private String _TAG_TUTORIAL_FINISHED = "_TAG_TUTORIAL_FINISHED";
    public static final String _TAG_PICTURE_TO_SHOW = "_TAG_PICTURE_TO_SHOW";
    public static final String _TAG_REPORT_TUTORIAL_FINISHED = "_TAG_REPORT_TUTORIAL_FINISHED";
    private static final String _DATABASE_IMPORTED = "_DATABASE_IMPORTED";
    public static final String _ENABLE_NOTIFICATIONS = "_ENABLE_NOTIFICATIONS";

    public static synchronized PreferencesManager getInstance() {
        if (instance == null)
            instance = new PreferencesManager();

        return instance;
    }

    public void setUserInfo(Context context, User userInfo) {
        if (context == null)
            return;

        preferences = context.getSharedPreferences(_TAG_PREFERENCES, context.MODE_PRIVATE);

        Gson gson = new Gson();
        String jsonProgram = gson.toJson(userInfo);

        String encryptedUser = Security.encrypt(jsonProgram);

        SharedPreferences.Editor e = preferences.edit();
        e.putString(_TAG_USER_INFO, encryptedUser);

        e.commit();
    }

    public User getUserInfo (Context context) {
        User user = null;
        preferences = context.getSharedPreferences(_TAG_PREFERENCES, context.MODE_PRIVATE);
        String userString = preferences.getString(_TAG_USER_INFO, "");

        if (userString != "") {

            String decryptedUserString = Security.decrypt(userString);

            Gson gson = new Gson();
            user = gson.fromJson(decryptedUserString, User.class);
        }

        return user;
    }

    public void logoutSession(Context context) {
        if (context == null) {
            return;
        }

        preferences = context.getSharedPreferences(_TAG_PREFERENCES, context.MODE_PRIVATE);

        SharedPreferences.Editor e = preferences.edit();
        e.remove(_TAG_USER_INFO);

        e.commit();
    }

    public void removeReports(Context context) {

//        preferences = context.getSharedPreferences(_TAG_PREFERENCES, context.MODE_PRIVATE);
//
//        SharedPreferences.Editor e = preferences.edit();
//        e.remove(_TAG_GET_REPORTS);
//
//        e.commit();
    }

    public void setUserRegisteredWithEmail(Context context, boolean registerd) {
        preferences = context.getSharedPreferences(_TAG_PREFERENCES, context.MODE_PRIVATE);

        SharedPreferences.Editor e = preferences.edit();
        e.putBoolean(_TAG_USER_REGISTERED_WITH_EMAIL, registerd);

        e.commit();
    }

    public void addReports(Context context, List<Report> reports) {

//        if (context == null)
//            return;
//
//        String jsonString = new Gson().toJson(reports);
//
//        String reportsStringEncrypted = Security.encrypt(jsonString);
//
//        preferences = context.getSharedPreferences(_TAG_PREFERENCES, context.MODE_PRIVATE);
//
//        SharedPreferences.Editor editor = preferences.edit();
//        editor.putString(_TAG_GET_REPORTS, reportsStringEncrypted);
//
//        editor.commit();
    }

    public void setTutorialFinished(Context context, boolean finished) {
        preferences = context.getSharedPreferences(_TAG_PREFERENCES, context.MODE_PRIVATE);

        SharedPreferences.Editor e = preferences.edit();
        e.putBoolean(_TAG_TUTORIAL_FINISHED, finished);

        e.commit();
    }

    public boolean isTutorialFinished (Context context) {
        preferences = context.getSharedPreferences(_TAG_PREFERENCES, context.MODE_PRIVATE);

        return preferences.getBoolean(_TAG_TUTORIAL_FINISHED, false);
    }

    public void setReportTutorialFinished(Context context, boolean finished) {
        preferences = context.getSharedPreferences(_TAG_PREFERENCES, context.MODE_PRIVATE);

        SharedPreferences.Editor e = preferences.edit();
        e.putBoolean(_TAG_REPORT_TUTORIAL_FINISHED, finished);

        e.commit();
    }

    public boolean isReportTutorialFinished(Context context) {
        preferences = context.getSharedPreferences(_TAG_PREFERENCES, context.MODE_PRIVATE);

        return preferences.getBoolean(_TAG_REPORT_TUTORIAL_FINISHED, false);
    }

    //Para saber cual es la ultima imagen a mostrar
    public void setPictureToShow(Context context, int currentPictureToShow) {
        preferences = context.getSharedPreferences(_TAG_PREFERENCES, context.MODE_PRIVATE);

        SharedPreferences.Editor e = preferences.edit();
        e.putInt(_TAG_PICTURE_TO_SHOW, currentPictureToShow);

        e.commit();
    }

    public int getPictureToShow(Context context) {
        preferences = context.getSharedPreferences(_TAG_PREFERENCES, context.MODE_PRIVATE);

        return preferences.getInt(_TAG_PICTURE_TO_SHOW, Constants.NO_PICTURE_TO_SHOW);
    }

    public void setNewReportPicture(Context context, String tag, String value) {
        preferences = context.getSharedPreferences(_TAG_PREFERENCES, context.MODE_PRIVATE);

        SharedPreferences.Editor e = preferences.edit();
        e.putString(tag, value);

        e.commit();
    }

    public String getNewReportPicture(Context context, String tag) {
        preferences = context.getSharedPreferences(_TAG_PREFERENCES, context.MODE_PRIVATE);

        return preferences.getString(tag, "");
    }

    public void setDatabaseImported(Context context, boolean imported) {
        preferences = context.getSharedPreferences(_TAG_PREFERENCES, context.MODE_PRIVATE);

        SharedPreferences.Editor e = preferences.edit();
        e.putBoolean(_DATABASE_IMPORTED, imported);

        e.commit();
    }

    public boolean isDatabaseImported (Context context) {

        preferences = context.getSharedPreferences(_TAG_PREFERENCES, context.MODE_PRIVATE);
        boolean imported = preferences.getBoolean(_DATABASE_IMPORTED, false);

        return imported;
    }

    /// CUADRILLA

    /*private static final String _REPORT_ATTENTION_IN_PROGRESS = "_REPORT_ATTENTION_IN_PROGRESS";

    public void setReportAttentionInProgress(Context context, boolean inProgress) {
        preferences = context.getSharedPreferences(_TAG_PREFERENCES, context.MODE_PRIVATE);

        SharedPreferences.Editor e = preferences.edit();
        e.putBoolean(_REPORT_ATTENTION_IN_PROGRESS, inProgress);

        e.commit();
    }

    public boolean isReportAttentionInProgress(Context context) {
        preferences = context.getSharedPreferences(_TAG_PREFERENCES, context.MODE_PRIVATE);
        boolean inProgress = preferences.getBoolean(_REPORT_ATTENTION_IN_PROGRESS, false);

        return inProgress;
    }



    public void clearReportAttentionData(Context context) {
        preferences = context.getSharedPreferences(_TAG_PREFERENCES, context.MODE_PRIVATE);

        SharedPreferences.Editor e = preferences.edit();
        e.remove(_REPORT_ATTENTION_IN_PROGRESS);
        e.remove(_REPORT_OPTION_CLICKED);
        e.remove(_REPORT_ATTENTION_FIRST_RESUME);

        e.commit();
    }
    */

    public static final String _REPORT_ATTENTION_IN_PROGRESS = "_REPORT_ATTENTION_IN_PROGRESS";
    public static final String _REPORT_ATTENTION_TICKET_VALUE = "_REPORT_ATTENTION_TICKET_VALUE";
    private static final String _REPORT_ATTENTION_FIRST_RESUME = "_REPORT_ATTENTION_FIRST_RESUME";
    private static final String _REPORT_OPTION_CLICKED = "_REPORT_OPTION_CLICKED";

    private static final String _SELECTED_REPORT_FIRST_RESUME = "_SELECTED_REPORT_FIRST_RESUME";
    private static final String _SELECTED_REPORT_COMPLETE_CLICKED = "_SELECTED_REPORT_COMPLETE_CLICKED";
    public static final String _REPORT_CANCELED = "_REPORT_CANCELED";

//    private static final String _SHOULD_SEND_REPORT_TO_STATUS_2 = "_SHOULD_SEND_REPORT_TO_STATUS_2";

    public void setReportAttentionInProgress(Context context, boolean isInProcess) {
        preferences = context.getSharedPreferences(_TAG_PREFERENCES, context.MODE_PRIVATE);

        SharedPreferences.Editor e = preferences.edit();
        e.putBoolean(_REPORT_ATTENTION_IN_PROGRESS, isInProcess);

        e.commit();
    }

    public boolean isReportAttentionInProgress(Context context) {
        preferences = context.getSharedPreferences(_TAG_PREFERENCES, context.MODE_PRIVATE);
        boolean firstResume = preferences.getBoolean(_REPORT_ATTENTION_IN_PROGRESS, false);

        return firstResume;
    }

    public void setReportAttentionTicketValue(Context context, String reportTicket) {
        preferences = context.getSharedPreferences(_TAG_PREFERENCES, context.MODE_PRIVATE);

        SharedPreferences.Editor e = preferences.edit();
        e.putString(_REPORT_ATTENTION_TICKET_VALUE, reportTicket);

        e.commit();
    }

    public String getReportAttentionTicketValue(Context context) {
        preferences = context.getSharedPreferences(_TAG_PREFERENCES, context.MODE_PRIVATE);
        String reportTicket = preferences.getString(_REPORT_ATTENTION_TICKET_VALUE, "");

        return reportTicket;
    }

    public void clearReportAttentionData(Context context) {

        if (context == null)
            return;

        preferences = context.getSharedPreferences(_TAG_PREFERENCES, context.MODE_PRIVATE);

        SharedPreferences.Editor e = preferences.edit();
        e.remove(_REPORT_ATTENTION_IN_PROGRESS);
        e.remove(_REPORT_ATTENTION_TICKET_VALUE);

        e.commit();
    }

    public boolean isReportOptionClicked(Context context) {
        preferences = context.getSharedPreferences(_TAG_PREFERENCES, context.MODE_PRIVATE);
        boolean reportOptionClicked = preferences.getBoolean(_REPORT_OPTION_CLICKED, false);

        return reportOptionClicked;
    }

    /// CUADRILLA

    public void setSelectedReportFirstResume(Context context, boolean firstResume) {
        preferences = context.getSharedPreferences(_TAG_PREFERENCES, context.MODE_PRIVATE);

        SharedPreferences.Editor e = preferences.edit();
        e.putBoolean(_SELECTED_REPORT_FIRST_RESUME, firstResume);

        e.commit();
    }

    public boolean isSelectedReportFirstResume(Context context) {
        preferences = context.getSharedPreferences(_TAG_PREFERENCES, context.MODE_PRIVATE);
        boolean firstResume = preferences.getBoolean(_SELECTED_REPORT_FIRST_RESUME, false);

        return firstResume;
    }

    public void setSelectedReportCompleteClicked(Context context, boolean complete) {
        preferences = context.getSharedPreferences(_TAG_PREFERENCES, context.MODE_PRIVATE);

        SharedPreferences.Editor e = preferences.edit();
        e.putBoolean(_SELECTED_REPORT_COMPLETE_CLICKED, complete);

        e.commit();
    }

    public boolean isSelectedReportCompleteClicked(Context context) {
        preferences = context.getSharedPreferences(_TAG_PREFERENCES, context.MODE_PRIVATE);
        boolean reportOptionClicked = preferences.getBoolean(_SELECTED_REPORT_COMPLETE_CLICKED, false);

        return reportOptionClicked;
    }

    public void clearSelectedReportAttentionData(Context context) {
        preferences = context.getSharedPreferences(_TAG_PREFERENCES, context.MODE_PRIVATE);

        SharedPreferences.Editor e = preferences.edit();
        e.remove(_SELECTED_REPORT_FIRST_RESUME);
        e.remove(_SELECTED_REPORT_COMPLETE_CLICKED);

        e.commit();
    }

    public void setReportCanceled(String activity, Context context, boolean canceled) {
        preferences = context.getSharedPreferences(_TAG_PREFERENCES, context.MODE_PRIVATE);

        Log.i("VALID_REPORT_ACT_LOG", "FROM = " + activity + ", setReportCanceled = " + canceled);

        SharedPreferences.Editor e = preferences.edit();
        e.putBoolean(_REPORT_CANCELED, canceled);

        e.commit();
    }

    public boolean isReportCanceled(Context context) {
        preferences = context.getSharedPreferences(_TAG_PREFERENCES, context.MODE_PRIVATE);
        boolean reportCanceled = preferences.getBoolean(_REPORT_CANCELED, false);

        return reportCanceled;
    }

    /// STATUS LIST

    private String _TAG_STATUS_MAP_PREFERENCES = "_TAG_STATUS_MAP_PREFERENCES";

    public void setStatusMap(Context context, HashMap<Integer, String> statusMap) {
        preferences = context.getSharedPreferences(_TAG_STATUS_MAP_PREFERENCES, context.MODE_PRIVATE);

        SharedPreferences.Editor e = preferences.edit();

        e.clear();
        e.commit();

        for (Map.Entry entry : statusMap.entrySet()) {
            e.putString(entry.getKey().toString(), entry.getValue().toString());
        }

        e.commit();
    }

    public HashMap<Integer, String> getStatusMap(Context context) {
        preferences = context.getSharedPreferences(_TAG_STATUS_MAP_PREFERENCES, context.MODE_PRIVATE);

        HashMap<Integer, String> statusMap = new HashMap<>();

        for (Map.Entry entry : preferences.getAll().entrySet()) {
            statusMap.put(Integer.parseInt(entry.getKey().toString()), entry.getValue().toString());
        }

        return statusMap;
    }

    private HashMap<Integer, String> mStatusHashMap;

    public String getStatusNameForId(Context context, int id) {

        if (mStatusHashMap == null) {
            mStatusHashMap = getStatusMap(context);
        }

        String statusName = mStatusHashMap.get(id);

        if (statusName == null || statusName == "") {
            statusName = Constants.REPORT_STATUS_MAP.get(id);
        }

        if (statusName == null || statusName == "") {
            return "";
        }

        return statusName;
    }

    private static final String _SHOW_APPTENTIVE_MESSAGE_CENTER = "_SHOW_APPTENTIVE_MESSAGE_CENTER";

    public void setShowApptentiveMessageCenter(Context context, boolean showApptentiveMessageCenter) {
        preferences = context.getSharedPreferences(_TAG_PREFERENCES, context.MODE_PRIVATE);

        SharedPreferences.Editor e = preferences.edit();
        e.putBoolean(_SHOW_APPTENTIVE_MESSAGE_CENTER, showApptentiveMessageCenter);

        e.commit();
    }

    public boolean showAppTentiveMessageCenter(Context context) {
        preferences = context.getSharedPreferences(_TAG_PREFERENCES, context.MODE_PRIVATE);
        boolean reportCanceled = preferences.getBoolean(_SHOW_APPTENTIVE_MESSAGE_CENTER, false);

        return reportCanceled;
    }

    public void clearAppTentiveMessageCenterValues(Context context) {
        preferences = context.getSharedPreferences(_TAG_PREFERENCES, context.MODE_PRIVATE);

        SharedPreferences.Editor e = preferences.edit();
        e.remove(_SHOW_APPTENTIVE_MESSAGE_CENTER);

        e.commit();
    }

    /*public void setShouldSendReportToStatus2(Context context, boolean shouldSendReport) {

        preferences = context.getSharedPreferences(_TAG_PREFERENCES, context.MODE_PRIVATE);

        SharedPreferences.Editor e = preferences.edit();
        e.putBoolean(_SHOULD_SEND_REPORT_TO_STATUS_2, shouldSendReport);

        e.commit();
    }

    public boolean shouldSendReportToStatus2(Context context) {
        preferences = context.getSharedPreferences(_TAG_PREFERENCES, context.MODE_PRIVATE);

        return preferences.getBoolean(_SHOULD_SEND_REPORT_TO_STATUS_2, false);
    }*/

    private static final String _SEND_REPORT_TO_STATUS_2_NUMBER_OF_RETRIES = "_SEND_REPORT_TO_STATUS_2_NUMBER_OF_RETRIES";

    public void setSendReportToStatusRetry(Context context, int currentNumber) {
        preferences = context.getSharedPreferences(_TAG_PREFERENCES, context.MODE_PRIVATE);

        SharedPreferences.Editor e = preferences.edit();
        e.putInt(_SEND_REPORT_TO_STATUS_2_NUMBER_OF_RETRIES, currentNumber);

        e.commit();
    }

    public int getCurrentNumberOfRetries(Context context) {
        preferences = context.getSharedPreferences(_TAG_PREFERENCES, context.MODE_PRIVATE);

        return preferences.getInt(_SEND_REPORT_TO_STATUS_2_NUMBER_OF_RETRIES, 0);
    }

    public void clearSendReportRetry(Context context) {
        preferences = context.getSharedPreferences(_TAG_PREFERENCES, context.MODE_PRIVATE);

        SharedPreferences.Editor e = preferences.edit();
        e.remove(_SEND_REPORT_TO_STATUS_2_NUMBER_OF_RETRIES);

        e.commit();
    }

    // LOCAL REPORTS - Start

    private String _TAG_LOCAL_REPORTS = "_TAG_LOCAL_REPORTS";

    public void addLocalReport(Context context, Report report) {
        if (context == null)
            return;

        List<Report> allTemporaryReports = getLocalReports(context);

        allTemporaryReports.add(report);

        saveTemporaryReports(context, allTemporaryReports);
    }

    public List<Report> getLocalReports(Context context) {
        preferences = context.getSharedPreferences(_TAG_PREFERENCES, context.MODE_PRIVATE);

        String reportsEncrypted = preferences.getString(_TAG_LOCAL_REPORTS, "");

        List<Report> allTemporaryReports = null;

        if (reportsEncrypted != null && reportsEncrypted != "") {
            reportsEncrypted = Security.decrypt(reportsEncrypted);
            allTemporaryReports = new Gson().fromJson(reportsEncrypted, new TypeToken<List<Report>>(){}.getType());
        }
        else
        {
            allTemporaryReports = new ArrayList<>();
        }

        return allTemporaryReports;
    }

    private void saveTemporaryReports(Context context, List<Report> temporaryReports) {
        preferences = context.getSharedPreferences(_TAG_PREFERENCES, context.MODE_PRIVATE);

        String jsonString = new Gson().toJson(temporaryReports);

        String reportsStringEncrypted = Security.encrypt(jsonString);

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(_TAG_LOCAL_REPORTS, reportsStringEncrypted);

        editor.commit();
    }

    public void deleteTemporaryReport(Context context, int position) {
        if (position > -1) {
            List<Report> allTemporaryReports = getLocalReports(context);

            if (allTemporaryReports.size() > 0 && position < allTemporaryReports.size()) {
                allTemporaryReports.remove(position);

                saveTemporaryReports(context, allTemporaryReports);
            }
        }
    }

    private String _TAG_LOCAL_REPORTS_EDIT_ADDRESS = "_TAG_LOCAL_REPORTS_EDIT_ADDRESS";
    private String _TAG_LOCAL_REPORTS_CANCEL = "_TAG_LOCAL_REPORTS_CANCEL";

    public void setEditAddress(Context context, boolean editAddress) {
        preferences = context.getSharedPreferences(_TAG_PREFERENCES, context.MODE_PRIVATE);

        SharedPreferences.Editor e = preferences.edit();
        e.putBoolean(_TAG_LOCAL_REPORTS_EDIT_ADDRESS, editAddress);

        e.commit();
    }

    public boolean shouldEditAddress(Context context) {
        preferences = context.getSharedPreferences(_TAG_PREFERENCES, context.MODE_PRIVATE);

        return preferences.getBoolean(_TAG_LOCAL_REPORTS_EDIT_ADDRESS, false);
    }

    public void setCancelTemporaryReport(Context context, boolean cancel) {
        preferences = context.getSharedPreferences(_TAG_PREFERENCES, context.MODE_PRIVATE);

        SharedPreferences.Editor e = preferences.edit();
        e.putBoolean(_TAG_LOCAL_REPORTS_CANCEL, cancel);

        e.commit();
    }

    public boolean shouldCancelTemporaryReport(Context context) {
        preferences = context.getSharedPreferences(_TAG_PREFERENCES, context.MODE_PRIVATE);

        return preferences.getBoolean(_TAG_LOCAL_REPORTS_CANCEL, false);
    }

    // LOCAL REPORTS - End

    public void setNotificationsEnabled(Context context, boolean notificationsEnabled) {
        preferences = context.getSharedPreferences(_TAG_PREFERENCES, context.MODE_PRIVATE);

        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(_ENABLE_NOTIFICATIONS, notificationsEnabled);
        editor.apply();
    }

    public boolean notificationsEnabled(Context context) {
        preferences = context.getSharedPreferences(_TAG_PREFERENCES, context.MODE_PRIVATE);

        return preferences.getBoolean(_ENABLE_NOTIFICATIONS, true);
    }
}
