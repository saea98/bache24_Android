package com.cmi.bache24.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.LabeledIntent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.Signature;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.v7.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Patterns;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.cmi.bache24.R;
import com.cmi.bache24.ui.activity.LoginActivity;
import com.cmi.bache24.ui.dialog.UserBannedDialog;
import com.cmi.bache24.ui.dialog.interfaces.MessageDialogListener;
//import com.parse.ParseInstallation;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by omar on 11/22/15.
 */
public class Utils {

    private static Utils instance = null;

    public static synchronized Utils getInstance() {
        if (instance == null)
            instance = new Utils();

        return instance;
    }

    public void startActivityWithParameters(Activity origin, Activity destination, Intent parameters) {

    }

    public static int getScreenWidth(Context context){
        int width;

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        Point size = new Point();
        display.getSize(size);
        width = size.x;

        return width;
    }

    public static int getScreenHeight(Context context) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }

    public static int convertDpToPx(Context context, int dp){
        return Math.round(dp * (context.getResources().getDisplayMetrics().xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    public static int convertPxToDp(int px){
        return Math.round(px / (Resources.getSystem().getDisplayMetrics().xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    public static String bitmapToBase64(Bitmap bitmap, int quality) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();

        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    public static String encodeFileToBase64Binary(File file)
            throws IOException {

//        File file = new File(fileName);
        byte[] bytes = loadFile(file);
//        byte[] encoded = Base64.encodeBase64(bytes);
//        String encodedString = new String(encoded);

        String encodedString = Base64.encodeToString(bytes, Base64.DEFAULT);

        return encodedString;
    }

    private static byte[] loadFile(File file) throws IOException {
        InputStream is = new FileInputStream(file);

        long length = file.length();
        if (length > Integer.MAX_VALUE) {
            // File is too large
        }
        byte[] bytes = new byte[(int)length];

        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length
                && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
            offset += numRead;
        }

        if (offset < bytes.length) {
            throw new IOException("Could not completely read file "+file.getName());
        }

        is.close();
        return bytes;
    }

    public String convertTimestampToDate (String strTimestamp) {
        SimpleDateFormat sdfFormat = new SimpleDateFormat("dd/MM/yyyy   hh:mm");

        return sdfFormat.format(new Date(Long.parseLong(strTimestamp) * 1000));
    }

    public String getCurrentTimestamp() {
        Long timestampLong = System.currentTimeMillis() / 1000;

        return timestampLong.toString();
    }

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state))
            return true;

        return false;
    }

    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
            Environment.MEDIA_MOUNTED_READ_ONLY.equals(state))
            return true;

        return false;
    }

    public int getImagesSize(Context context) {
        int screenWidth = getScreenWidth(context);

        screenWidth = screenWidth - Utils.convertDpToPx(context, 16 * 2) - (5 * (4 - 1));

        int squareSide = (int) (screenWidth / 4) - 5;//(4 - 0.4)) - 40;

        return squareSide;
    }

    public String getHashForToken(String token) {
//        token = "Hola123";
        String hashBase64 = "";

        String originalString = token + Constants.KEY_FOR_HASH;

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(originalString.getBytes());

            byte byteData[] = md.digest();

            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < byteData.length; i++) {
                sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
            }

            System.out.println("Hex format : " + sb.toString());

            byte[] data = sb.toString().getBytes("UTF-8");
            hashBase64 = Base64.encodeToString(data, Base64.DEFAULT);

//            String base64Hash =

        } catch (NoSuchAlgorithmException ex) {

        } catch (UnsupportedEncodingException ex) {

        }

        return hashBase64;
    }

    public String printKeyHash(Activity context) {
        PackageInfo packageInfo;
        String key = null;
        try {
            //getting application package name, as defined in manifest
            String packageName = context.getApplicationContext().getPackageName();

            //Retriving package info
            packageInfo = context.getPackageManager().getPackageInfo(packageName,
                    PackageManager.GET_SIGNATURES);

            Log.e("Package Name=", context.getApplicationContext().getPackageName());

            for (Signature signature : packageInfo.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                key = new String(Base64.encode(md.digest(), 0));

                // String key = new String(Base64.encodeBytes(md.digest()));
                Log.e("Key Hash=", key);
            }
        } catch (PackageManager.NameNotFoundException e1) {
            Log.e("Name not found", e1.toString());
        }
        catch (NoSuchAlgorithmException e) {
            Log.e("No such an algorithm", e.toString());
        } catch (Exception e) {
            Log.e("Exception", e.toString());
        }

        return key;
    }

    public int getStreetType(Context context, String streetName) {
        String[] primaryStreets = context.getResources().getStringArray(R.array.streets_array);

        int type = 2;


        for (int i = 0; i < primaryStreets.length; i++) {
            if (streetName.contains(primaryStreets[i])) {
                type = 1;
                break;
            }
        }

        return type;
    }

    public void share(Context context, String subject,  String message) {
        Resources resources = context.getResources();

        Intent emailIntent = new Intent();
        emailIntent.setAction(Intent.ACTION_SEND);
        // Native email client doesn't currently support HTML, but it doesn't hurt to try in case they fix it
        emailIntent.putExtra(Intent.EXTRA_TEXT, message);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.setType("message/rfc822");

        PackageManager pm = context.getPackageManager();
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.setType("text/plain");


        Intent openInChooser = Intent.createChooser(emailIntent, "Seleccione para compartir");

        List<ResolveInfo> resInfo = pm.queryIntentActivities(sendIntent, 0);
        List<LabeledIntent> intentList = new ArrayList<>();
        for (int i = 0; i < resInfo.size(); i++) {
            // Extract the label, append it, and repackage it in a LabeledIntent
            ResolveInfo ri = resInfo.get(i);
            String packageName = ri.activityInfo.packageName;
            if(packageName.contains("android.email")) {
                emailIntent.setPackage(packageName);
            } else if(packageName.contains("twitter") || packageName.contains("facebook") || packageName.contains("whatsapp") || packageName.contains("android.gm")) {
                Intent intent = new Intent();
                intent.setComponent(new ComponentName(packageName, ri.activityInfo.name));
                intent.setAction(Intent.ACTION_SEND);
                intent.setType("text/plain");
                if(packageName.contains("twitter")) {
                    intent.putExtra(Intent.EXTRA_TEXT, message);//resources.getString(R.string.share_twitter));
                } else if(packageName.contains("facebook")) {
                    // Warning: Facebook IGNORES our text. They say "These fields are intended for users to express themselves. Pre-filling these fields erodes the authenticity of the user voice."
                    // One workaround is to use the Facebook SDK to post, but that doesn't allow the user to choose how they want to share. We can also make a custom landing page, and the link
                    // will show the <meta content ="..."> text from that page with our link in Facebook.
                    intent.putExtra(Intent.EXTRA_TEXT, message);//resources.getString(R.string.share_facebook));
                } else if(packageName.contains("com.whatsapp")) {
                    intent.putExtra(Intent.EXTRA_TEXT, message);//resources.getString(R.string.share_sms));
                } else if(packageName.contains("android.gm")) { // If Gmail shows up twice, try removing this else-if clause and the reference to "android.gm" above
                    intent.putExtra(Intent.EXTRA_TEXT, message);//Html.fromHtml(resources.getString(R.string.share_email_gmail)));
                    intent.putExtra(Intent.EXTRA_SUBJECT, subject);//resources.getString(R.string.share_email_subject));
                    intent.setType("message/rfc822");
                }

                intentList.add(new LabeledIntent(intent, packageName, ri.loadLabel(pm), ri.icon));
            }
        }

        // convert intentList to array
        LabeledIntent[] extraIntents = intentList.toArray( new LabeledIntent[ intentList.size() ]);

        openInChooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, extraIntents);
        context.startActivity(openInChooser);
    }

    //http://stackoverflow.com/questions/10311834/how-to-check-if-location-services-are-enabled
    public void enableLocalizationIfNecessary(final Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {

        }

        try {
            network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {

        }

        if (!gps_enabled && !network_enabled) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(context);

            dialog.setMessage(context.getResources().getString(R.string.localization_enable));
            dialog.setPositiveButton(context.getResources().getString(R.string.localization_settings), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent openSettingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    context.startActivity(openSettingsIntent);
                }
            });
            dialog.setNegativeButton(context.getResources().getString(R.string.localization_cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            dialog.show();
        }
    }

    public void showNotificationMessage(Context context, String title, String message, Intent intent) {

        // Check for empty push message
        if (TextUtils.isEmpty(message))
            return;

//        if (isAppIsInBackground(context)) {
            // notification icon
            int icon = getNotificationIcon();//R.mipmap.ic_launcher;

            int mNotificationId = 1002;

            PendingIntent resultPendingIntent =
                    PendingIntent.getActivity(
                            context,
                            0,
                            intent,
                            PendingIntent.FLAG_CANCEL_CURRENT
                    );

//            NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                    context);
            Notification notification = mBuilder.setTicker(title).setWhen(0)
                    .setAutoCancel(true)
                    .setContentTitle(title)
//                    .setStyle(inboxStyle)
                    .setContentIntent(resultPendingIntent)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
//                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), icon))
                    .setSmallIcon(icon)
                    .setContentText(message)
                    .setColor(context.getResources().getColor(R.color.primary))
                    .build();

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(mNotificationId, notification);
//        } else {
//            intent.putExtra("title", title);
//            intent.putExtra("message", message);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//            context.startActivity(intent);
//        }
    }

    private int getNotificationIcon() {
        boolean useWhiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP);
        return useWhiteIcon ? R.drawable.icon_silhouette : R.drawable.bache_icon;
    }

    /**
     * Method checks if the app is in background or not
     *
     * @param context
     * @return
     */
    public static boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            /*List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }*/

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                List<ActivityManager.AppTask> taskInfo = am.getAppTasks();
                String packagename;

                for (ActivityManager.AppTask task : taskInfo) {
                    packagename = task.getTaskInfo().baseIntent.getComponent().getPackageName();

                    if (packagename.equals(context.getPackageName())) {
                        isInBackground = false;
                    }
                }
            } else {
                List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
                ComponentName componentInfo = taskInfo.get(0).topActivity;
                if (componentInfo.getPackageName().equals(context.getPackageName())) {
                    isInBackground = false;
                }
            }
        }

        return isInBackground;
    }

    public static boolean isAppInForeground(Context context)
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

    public boolean isInternetAvailable(Context context) {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);

        boolean isAvailable = isNetworkAvailable(context);

        /*if (isNetworkAvailable(context)) {
            try {
                HttpURLConnection urlc = (HttpURLConnection) (new URL("http://52.33.96.33").openConnection());
                urlc.setRequestProperty("User-Agent", "Test");
                urlc.setRequestProperty("Connection", "close");
                urlc.setConnectTimeout(1500);
                urlc.connect();
                isAvailable = urlc.getResponseCode() == 200;
            } catch (IOException e) {
                Log.e("INTERNET_CONNECTION", "Error checking internet connection", e);
            }
        } else {
            Log.d("INTERNET_CONNECTION", "No network available!");
        }*/

        if (!isAvailable) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(context);

            dialog.setMessage(context.getResources().getString(R.string.internet_unavailable));
            dialog.setPositiveButton(context.getResources().getString(R.string.internet_unavailable_button), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            dialog.setNegativeButton(context.getResources().getString(R.string.localization_cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            dialog.show();
        }

        return isAvailable;

        /*boolean isAvailable = false;

        try {
            InetAddress ipAddr = InetAddress.getByName("google.com"); //You can replace it with your name

            if (ipAddr.equals("")) {
                isAvailable = false;
            } else {
                isAvailable = true;
            }

        } catch (Exception e) {
            isAvailable = false;
        }

        if (!isAvailable) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(context);

            dialog.setMessage(context.getResources().getString(R.string.internet_unavailable));
            dialog.setPositiveButton(context.getResources().getString(R.string.internet_unavailable_button), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            dialog.setNegativeButton(context.getResources().getString(R.string.localization_cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            dialog.show();
        }

        return isAvailable;*/
    }

    private boolean isNetworkAvailable(Context context) {
        if (context == null)
            return true;

        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        return activeNetworkInfo != null;
    }

    public static void showLogin(Context activity) {

        removeFromParse();

        PreferencesManager.getInstance().logoutSession(activity);
        PreferencesManager.getInstance().removeReports(activity);

        Intent loginActivityIntent = new Intent(activity, LoginActivity.class);
        loginActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        loginActivityIntent.putExtra("_SHOULD_SHOW_BANNED_ALERT", true);
        activity.startActivity(loginActivityIntent);
        ((Activity) activity).overridePendingTransition(R.anim.enter_from_right, R.anim.enter_from_left);
    }

    public static void showLoginForBadToken(Context activity) {
        removeFromParse();

        PreferencesManager.getInstance().logoutSession(activity);
        PreferencesManager.getInstance().removeReports(activity);

        Intent loginActivityIntent = new Intent(activity, LoginActivity.class);
        loginActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        loginActivityIntent.putExtra("_SHOULD_SHOW_TOKEN_DISABLED_ALERT", true);
        activity.startActivity(loginActivityIntent);
        ((Activity) activity).overridePendingTransition(R.anim.enter_from_right, R.anim.enter_from_left);
    }

    public static void showUserBannedDialog(final Context activity, final boolean shouldShowLogin) {
        final UserBannedDialog userBannedDialog = new UserBannedDialog();
        userBannedDialog.setDialogListener(new MessageDialogListener() {

            @Override
            public void onAccept() {
                userBannedDialog.dismiss();

                /*removeFromParse();

                PreferencesManager.getInstance().logoutSession(activity);
                PreferencesManager.getInstance().removeReports(activity);

                if (shouldShowLogin) {
                    Intent loginActivityIntent = new Intent(activity, LoginActivity.class);
                    loginActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    activity.startActivity(loginActivityIntent);
                    ((Activity) activity).overridePendingTransition(R.anim.enter_from_right, R.anim.enter_from_left);
                }*/
            }
        });
        userBannedDialog.setCancelable(false);
        userBannedDialog.show(((Activity) activity).getFragmentManager(), "");
    }

    private static void removeFromParse() {
        /*ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put("userEmail", "");
        installation.saveInBackground();*/

        /*ParsePush.subscribeInBackground("B24-CDMX", new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.i("PARSE", "Error on subscription =>");
                } else {
                    Log.i("PARSE", "Subscription Success");
                }
            }
        });*/
    }

    public static void hideKeyboard(Context context, View view) {
        InputMethodManager in = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public static boolean isValidEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static String getVersionName(Context context) {
        String versionName = "";
        try {

            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);

            versionName = packageInfo.versionName;

        } catch (PackageManager.NameNotFoundException ex) {
            versionName = "1.0";
        }

        return versionName;
    }

    public static String loadJSONFromAssets(Context context, String filename) {
        String json = null;
        try {

            InputStream is = context.getAssets().open(filename);

            int size = is.available();

            byte[] buffer = new byte[size];

            is.read(buffer);

            is.close();

            json = new String(buffer, "UTF-8");


        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    public static void Log(String origin, String method, String value) {
        Log.i("BACHE24_LOG", "Origin = " + origin + ", Method = " + method + ", Value = " + value);
    }

    public static String clearText(String originalValue) {
        String cleanText = "";

        cleanText = originalValue.replaceAll("[-+.^:,()]", "").toLowerCase();
        cleanText = cleanText.replace("  ", " ");
        cleanText = Normalizer.normalize(cleanText, Normalizer.Form.NFD);
        cleanText = cleanText.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");

        return cleanText;
    }

    /*
    public static boolean hasPermissionInManifest(Context context, String permissionName) {
        final String packageName = context.getPackageName();
        try {
            final PackageInfo packageInfo = context.getPackageManager().getPackageInfo(packageName, PackageManager.GET_PERMISSIONS);
            final String[] declaredPermissions = packageInfo.requestedPermissions;

            if (declaredPermissions != null && declaredPermissions.length > 0) {
                for (String p : declaredPermissions) {
                    if (p.equals(permissionName)) {
                        return true;
                    }
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return false;
    }
    */
}
