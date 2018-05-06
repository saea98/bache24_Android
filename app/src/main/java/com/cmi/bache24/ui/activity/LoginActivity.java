package com.cmi.bache24.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.cmi.bache24.ui.activity.troop.TroopMainActivity;
import com.cmi.bache24.ui.dialog.interfaces.MessageDialogListener;
import com.cmi.bache24.ui.dialog.MultipleDevicesDialog;
import com.cmi.bache24.ui.dialog.UserActivationDialog;
import com.cmi.bache24.util.Constants;
import com.cmi.bache24.util.PreferencesManager;
import com.cmi.bache24.util.Utils;
import com.facebook.FacebookSdk;
import com.cmi.bache24.R;
import com.cmi.bache24.ui.fragment.LoginFragment;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import io.fabric.sdk.android.Fabric;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class LoginActivity extends AppCompatActivity {

    private LoginFragment mLoginFragment;
    private boolean mShouldShowRegisterDialog = false;
    private boolean mShouldShowBannedDialog = false;
    private boolean mShouldShowMultipleDevicesDialog = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setTheme(R.style.AppTheme);

        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());

        TwitterAuthConfig authConfig = new TwitterAuthConfig(Constants.TWITTER_KEY, Constants.TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));

        setContentView(R.layout.activity_login);

        mLoginFragment = (LoginFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);

        if (getIntent() != null) {
            mShouldShowRegisterDialog = getIntent().getBooleanExtra("_SHOULD_SHOW_REGISTER_ALERT", false);
            mShouldShowBannedDialog = getIntent().getBooleanExtra("_SHOULD_SHOW_BANNED_ALERT", false);
            mShouldShowMultipleDevicesDialog = getIntent().getBooleanExtra("_SHOULD_SHOW_TOKEN_DISABLED_ALERT", false);
        }

        if (mShouldShowRegisterDialog)
            showRegisterSuccessDialog();

        if (mShouldShowBannedDialog)
            Utils.showUserBannedDialog(this, false);

        if (mShouldShowMultipleDevicesDialog)
            this.showMultipleDevicesDialog();

        onNewIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        String action = intent.getAction();
        String data = intent.getDataString();
        if (intent.ACTION_VIEW.equals(action) && data != null) {
            String recipeId = data.substring(data.lastIndexOf("/") + 1);
            if (recipeId.equals("activar"))
            {
                if (PreferencesManager.getInstance().getUserInfo(this) != null) {

                    Intent reportActivityIntent = new Intent(this, ReportActivity.class);

                    reportActivityIntent.putExtra(Constants.IS_FROM_NOTIFICATION, false);
                    reportActivityIntent.putExtra(Constants.NOTIFICATION_BACHE_ID, 0);

                    reportActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(reportActivityIntent);
                    overridePendingTransition(R.anim.enter_from_right, R.anim.enter_from_left);
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(com.cmi.bache24.R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == com.cmi.bache24.R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mLoginFragment != null)
            mLoginFragment.onActivityResult(requestCode, resultCode, data);
    }

    private void showRegisterSuccessDialog() {
        final UserActivationDialog userActivationDialog = new UserActivationDialog();
        userActivationDialog.setDialogListener(new MessageDialogListener() {

            @Override
            public void onAccept() {
                userActivationDialog.dismiss();
            }
        });
        userActivationDialog.setCancelable(false);
        userActivationDialog.show(getFragmentManager(), "");
    }

    private void showMultipleDevicesDialog() {
        final MultipleDevicesDialog multipleDevicesDialog = new MultipleDevicesDialog();
        multipleDevicesDialog.setupDialog(new MessageDialogListener() {

            @Override
            public void onAccept() {
                multipleDevicesDialog.dismiss();

            }
        });
        multipleDevicesDialog.setCancelable(false);
        multipleDevicesDialog.show(getFragmentManager(), "");
    }
}
