package com.cmi.bache24.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Process;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.cmi.bache24.R;
import com.cmi.bache24.ui.activity.troop.NotificationActivity;
import com.cmi.bache24.ui.activity.troop.TroopMainActivity;
import com.cmi.bache24.ui.dialog.NewVersionDialog;
import com.cmi.bache24.ui.dialog.interfaces.MessageDialogListener;
import com.cmi.bache24.util.Constants;
import com.cmi.bache24.util.PreferencesManager;
import com.cmi.bache24.util.Utils;

import android.os.Build.VERSION_CODES;

import com.crashlytics.android.Crashlytics;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import io.fabric.sdk.android.Fabric;

public class SplashActivity extends AppCompatActivity {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private FrameLayout mLayoutSplashImage1;
    private FrameLayout mLayoutSplashImage2;

    private boolean mIsFromNotification = false;
    private String mBacheId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(Constants.TWITTER_KEY, Constants.TWITTER_SECRET);

        Fabric.with(this, new Twitter(authConfig), new Crashlytics());

        setContentView(R.layout.activity_splash);

        if (Build.VERSION.SDK_INT < VERSION_CODES.KITKAT) {
            showAndroidVersionAlert();

        } else {
            mIsFromNotification = getIntent().getBooleanExtra(Constants.IS_FROM_NOTIFICATION, false);
            mBacheId = getIntent().getStringExtra(Constants.NOTIFICATION_BACHE_ID);

            mLayoutSplashImage1 = (FrameLayout) findViewById(R.id.layout_splash_image_1);
            mLayoutSplashImage2 = (FrameLayout) findViewById(R.id.layout_splash_image_2);

            mLayoutSplashImage1.setVisibility(View.INVISIBLE);
            mLayoutSplashImage2.setVisibility(View.INVISIBLE);

            if (getIntent() != null) {
                processIntent(getIntent());
            } else {
                showSplashImage1();
            }
        }

    }

    private void showSplashImage1() {
        mLayoutSplashImage1.setVisibility(View.VISIBLE);
        mLayoutSplashImage2.setVisibility(View.INVISIBLE);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                showSplashImage2();
            }
        }, Constants.SPLASH_TIME_IN_MILLISECONDS);
    }

    private void requestPermissions() {
        Log.i("SplashActivity", "requestPermissions");
    }

    private void showSplashImage2() {
        mLayoutSplashImage1.setVisibility(View.INVISIBLE);
        mLayoutSplashImage2.setVisibility(View.VISIBLE);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                showLoginActivity();
            }
        }, Constants.SPLASH_TIME_IN_MILLISECONDS);
    }

    private void showLoginActivity() {
        if (PreferencesManager.getInstance().getUserInfo(this) != null) {

            Intent reportActivityIntent = null;

            if (!PreferencesManager.getInstance().isTutorialFinished(this)) {
                reportActivityIntent = new Intent(this, TutorialActivity.class);
                reportActivityIntent.putExtra("_IS_FROM_HELP", false);
            } else {
                if (PreferencesManager.getInstance().getUserInfo(this).getUserType() == Constants.USER_TYPE_CITIZEN)
                    reportActivityIntent = new Intent(this, ReportActivity.class);
                else if (PreferencesManager.getInstance().getUserInfo(this).getUserType() == Constants.USER_TYPE_TROOP)
                    reportActivityIntent = new Intent(this, TroopMainActivity.class);
                else
                    reportActivityIntent = new Intent(this, ReportActivity.class);
            }

            reportActivityIntent.putExtra(Constants.IS_FROM_NOTIFICATION, mIsFromNotification);
            reportActivityIntent.putExtra(Constants.NOTIFICATION_BACHE_ID, mBacheId);

            reportActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(reportActivityIntent);
            overridePendingTransition(R.anim.enter_from_right, R.anim.enter_from_left);
        } else {
            Intent loginActivityIntent = null;

            if (!PreferencesManager.getInstance().isTutorialFinished(this)) {
                loginActivityIntent = new Intent(this, TutorialActivity.class);
                loginActivityIntent.putExtra("_IS_FROM_HELP", false);
            } else
                loginActivityIntent = new Intent(this, LoginActivity.class);

            loginActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(loginActivityIntent);
            overridePendingTransition(R.anim.enter_from_right, R.anim.enter_from_left);
        }
    }

    private void processIntent(Intent intent) {
        if (intent.getExtras() != null) {
            if (intent.getExtras().containsKey("from") && intent.getExtras().containsKey("idBache")) {
                if (intent.getExtras().getString("from").equals(getString(R.string.fbi))) {

//                    Log.i("getExtras", "getExtras = " + intent.getExtras().toString());

                    if (intent.getExtras().containsKey(Constants.BACHE_ID_KEY) &&
                            !intent.getExtras().getString(Constants.BACHE_ID_KEY, "").isEmpty()) {

                        startCitizenMainScreen(intent.getExtras());

                    } else if (intent.getExtras().containsKey(Constants.SQUAD_LONG_MESSAGE_KEY) &&
                            !intent.getExtras().getString(Constants.SQUAD_LONG_MESSAGE_KEY, "").isEmpty()) {

                        startSquadMainScreen(intent.getExtras());

                    } else {
                        showSplashImage1();
                    }

                } else {
                    showSplashImage1();
                }
            } else {
                showSplashImage1();
            }
        } else {
            showSplashImage1();
        }
    }

    private void startCitizenMainScreen(Bundle extras) {
        mIsFromNotification = true;
        mBacheId = extras.getString(Constants.BACHE_ID_KEY);
        showSplashImage1();
    }

    private void startSquadMainScreen(Bundle extras) {
        String longMessage = extras.getString(Constants.SQUAD_LONG_MESSAGE_KEY);
        Intent resultIntent = new Intent(this, NotificationActivity.class);

        resultIntent.putExtra(Constants.SQUAD_NOTIFICATION_MESSAGE_ARG, longMessage);
        resultIntent.putExtra(Constants.SQUAD_NOTIFICATION_IS_APP_IN_FOREGROUND, true);

        resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(resultIntent);
        overridePendingTransition(R.anim.enter_from_right, R.anim.enter_from_left);
    }

    private void showAndroidVersionAlert() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("Lo sentimos, por cuestiones de seguridad, tu sistema operativo no es compatible con esta versión de Bache 24. ¡Gracias por contribuir a mejorar nuestra ciudad!")
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
                        moveTaskToBack(true);
                        Process.killProcess(Process.myPid());
                        finish();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.splash_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
