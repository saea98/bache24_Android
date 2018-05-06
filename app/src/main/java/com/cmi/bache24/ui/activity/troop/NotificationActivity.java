package com.cmi.bache24.ui.activity.troop;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.cmi.bache24.R;
import com.cmi.bache24.ui.activity.LoginActivity;
import com.cmi.bache24.util.Constants;
import com.cmi.bache24.util.PreferencesManager;

public class NotificationActivity extends AppCompatActivity {

    boolean isAppInForeground = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppThemePink);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        TextView messageTextView = (TextView) findViewById(R.id.textview_message);
        Button acceptButton = (Button) findViewById(R.id.button_accept);

        messageTextView.setText("");

        if (getIntent().getExtras() != null) {
            messageTextView.setText(getIntent().
                    getExtras().getString(Constants.SQUAD_NOTIFICATION_MESSAGE_ARG));
            isAppInForeground = getIntent().getExtras().getBoolean(Constants.SQUAD_NOTIFICATION_IS_APP_IN_FOREGROUND);
        }

        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeNotificationDetail();
            }
        });
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        closeNotificationDetail();
    }

    private void closeNotificationDetail() {
        if (isAppInForeground) {
            finish();
        }
        else
        {
            Intent squadNextActivity;

            if (PreferencesManager.getInstance().getUserInfo(this) != null) {
                squadNextActivity = new Intent(this, TroopMainActivity.class);
            } else {
                squadNextActivity = new Intent(this, LoginActivity.class);
            }

            squadNextActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(squadNextActivity);
            overridePendingTransition(R.anim.enter_from_right, R.anim.enter_from_left);
        }
    }
}
