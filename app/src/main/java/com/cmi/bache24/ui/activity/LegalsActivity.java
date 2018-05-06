package com.cmi.bache24.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.cmi.bache24.R;
import com.cmi.bache24.data.model.User;
import com.cmi.bache24.data.remote.ServicesManager;
import com.cmi.bache24.data.remote.interfaces.RegisterUserCallback;
import com.cmi.bache24.ui.activity.troop.TroopMainActivity;
import com.cmi.bache24.ui.dialog.interfaces.MessageDialogListener;
import com.cmi.bache24.ui.dialog.UserActivationDialog;
import com.cmi.bache24.util.Constants;
import com.cmi.bache24.util.PreferencesManager;
import com.cmi.bache24.util.Utils;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class LegalsActivity extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout mProgressLayout;
    private Button mCancelButton;
    private Button mAcceptButton;
    private User mUserInfo;
    private RelativeLayout mLayoutLegasls;
    private RelativeLayout mLayoutActivation;
    private Toolbar mToolbar;
    private TextView mToolbarTitle;
    private Button mButtonAccess;
    private ScrollView mScrollViewPrivacy;
    private ScrollView mScrollViewTerms;
    private int currentText = 1;
    private boolean mIsFromSocialNetwork = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_legals);

        setupToolbar();

        mCancelButton = (Button) findViewById(R.id.button_return);
        mAcceptButton = (Button) findViewById(R.id.button_accept);
        mProgressLayout = (LinearLayout) findViewById(R.id.progress_layout);
        mLayoutLegasls = (RelativeLayout) findViewById(R.id.layout_legals);
        mLayoutActivation = (RelativeLayout) findViewById(R.id.layout_activation);
        mButtonAccess = (Button) findViewById(R.id.button_access);
        mScrollViewPrivacy = (ScrollView) findViewById(R.id.scrollView2);
        mScrollViewTerms = (ScrollView) findViewById(R.id.scrollView3);

        mLayoutLegasls.setVisibility(View.VISIBLE);
        mLayoutActivation.setVisibility(View.GONE);


        mScrollViewTerms.setVisibility(View.GONE);
        mProgressLayout.setVisibility(View.GONE);
        mCancelButton.setOnClickListener(this);
        mAcceptButton.setOnClickListener(this);
        mButtonAccess.setOnClickListener(this);

        if (getIntent() != null) {
            mUserInfo = getIntent().getParcelableExtra(Constants.EXTRA_REGISTER_USER_DATA);
            mIsFromSocialNetwork = getIntent().getBooleanExtra(Constants.EXTRA_LOGIN_FROM_SOCIAL_NETWORK, false);
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == mCancelButton.getId()) {
            if (currentText == 1)
                finish();
            else {
                currentText = 1;
                mScrollViewPrivacy.setVisibility(View.VISIBLE);
                mScrollViewTerms.setVisibility(View.GONE);
                mToolbarTitle.setText(getResources().getString(R.string.menu_privacy));
            }
        } else if (view.getId() == mAcceptButton.getId()) {
            if (currentText == 1) {
                currentText = 2;
                mScrollViewPrivacy.setVisibility(View.GONE);
                mScrollViewTerms.setVisibility(View.VISIBLE);
                mToolbarTitle.setText(getResources().getString(R.string.menu_terms));
            } else {
                registerUser(mUserInfo);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (currentText == 1)
            super.onBackPressed();
        else {
            currentText = 1;
            mScrollViewPrivacy.setVisibility(View.VISIBLE);
            mScrollViewTerms.setVisibility(View.GONE);

            mToolbarTitle.setText(getResources().getString(R.string.menu_privacy));
        }
    }

    private void showLogin() {
        Intent loginActivityIntent = new Intent(this, AccessActivity.class);
        loginActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        loginActivityIntent.putExtra(Constants.EXTRA_SHOULD_SHOW_ACTIVATION_MESSAGE, true);

        startActivity(loginActivityIntent);
        overridePendingTransition(R.anim.enter_from_right, R.anim.enter_from_left);
    }

    private void registerUser(User userInfo) {
        if (userInfo == null) {
            Toast.makeText(this, Constants.ERROR_MISSING_DATA_DESCRIPTION, Toast.LENGTH_SHORT).show();
            return;
        }

        mProgressLayout.setVisibility(View.VISIBLE);

        ServicesManager.registerUser(userInfo, new RegisterUserCallback() {
            @Override
            public void onRegisterSuccess(User userCompleteInfo) {
                if (mIsFromSocialNetwork) {
                    PreferencesManager.getInstance().setUserInfo(LegalsActivity.this, userCompleteInfo);
                    showReportActivity();
                } else {
                    showLogin();
                }
            }

            @Override
            public void onRegisterFail(String message) {
                if (message != "") {
                    Toast.makeText(LegalsActivity.this, message, Toast.LENGTH_SHORT).show();
                }
                mProgressLayout.setVisibility(View.GONE);
            }

            @Override
            public void userBanned() {
                Utils.showLogin(LegalsActivity.this);
            }

            @Override
            public void onTokenDisabled() {

            }
        });
    }

    private void showSuccessDialog() {
        final UserActivationDialog userActivationDialog = new UserActivationDialog();
        userActivationDialog.setDialogListener(new MessageDialogListener() {

            @Override
            public void onAccept() {
                userActivationDialog.dismiss();

                showLogin();
            }
        });
        userActivationDialog.setCancelable(false);
        userActivationDialog.show(getFragmentManager(), "");
    }

    private void setupToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        mToolbarTitle = (TextView) findViewById(R.id.toolbar_title);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void showReportActivity() {
        Intent userMainActivityIntent = null;

        if (mUserInfo.getUserType() == Constants.USER_TYPE_CITIZEN)
            userMainActivityIntent = new Intent(this, ReportActivity.class);
        else if (mUserInfo.getUserType() == Constants.USER_TYPE_TROOP)
            userMainActivityIntent = new Intent(this, TroopMainActivity.class);
        else
            userMainActivityIntent = new Intent(this, ReportActivity.class);

        userMainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(userMainActivityIntent);
        overridePendingTransition(R.anim.enter_from_right, R.anim.enter_from_left);
    }
}
