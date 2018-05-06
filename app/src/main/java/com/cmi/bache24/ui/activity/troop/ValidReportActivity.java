package com.cmi.bache24.ui.activity.troop;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.cmi.bache24.R;
import com.cmi.bache24.data.model.Report;
import com.cmi.bache24.data.model.User;
import com.cmi.bache24.data.remote.ServicesManager;
import com.cmi.bache24.data.remote.interfaces.NewReportCallback;
import com.cmi.bache24.ui.fragment.troop.ValidReportFragment;
import com.cmi.bache24.util.Constants;
import com.cmi.bache24.util.PreferencesManager;
import com.cmi.bache24.util.Utils;
import com.google.gson.Gson;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ValidReportActivity extends AppCompatActivity implements ValidReportFragment.ValidReportFragmentListener {

    private Toolbar mToolbar;
    private TextView mToolbarTitle;
    private ValidReportFragment mValidReportFragment;
    private Report mCurrentReport;
    private User currentUser;
    private boolean buttonClicked = false;
    private boolean cancelReportOnBackground = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppThemePink);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_valid_report);

        String reportString = getIntent().getStringExtra(Constants.EXTRA_REPORT_STATUS_DETAIL);
        if (reportString != "") {
            Gson gson = new Gson();
            mCurrentReport = gson.fromJson(reportString, Report.class);
        }

        int noBaches = getIntent().getIntExtra("REPORT_NO_BACHES", 0);

        mValidReportFragment = (ValidReportFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment);

        mValidReportFragment.setReport(mCurrentReport, noBaches);

        setupToolbar();

        currentUser = PreferencesManager.getInstance().getUserInfo(this);

        if (!PreferencesManager.getInstance().isReportAttentionInProgress(this)) {

            Utils.Log("ValidReportActivity", "onCreate", "Start report attention for " + mCurrentReport.getTicket());

            PreferencesManager.getInstance().setReportAttentionInProgress(this, true);
            PreferencesManager.getInstance().setReportAttentionTicketValue(this, mCurrentReport.getTicket());

            sendReportUpdate(currentUser, Constants.TROOP_REPORT_STATUS_MARK_AS_TAKEN);
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onResume() {

//        buttonClicked = false;
//
//        Log.i("VALID_REPORT_ACT_LOG", "onResume");
//
//        if (!PreferencesManager.getInstance().isSelectedReportFirstResume(this)) {
//
//            Log.i("VALID_REPORT_ACT_LOG", "!PreferencesManager.getInstance().isSelectedReportFirstResume");
//
//            if (!PreferencesManager.getInstance().isSelectedReportCompleteClicked(this)) {
//
//                Log.i("VALID_REPORT_ACT_LOG", "!PreferencesManager.getInstance().isSelectedReportCompleteClicked(this)");
//
//                PreferencesManager.getInstance().clearSelectedReportAttentionData(this);
//                cancelReportOnBackground = true;
//
//                finish();
//            }
//        }

        if (PreferencesManager.getInstance().isSelectedReportFirstResume(this)) {
            PreferencesManager.getInstance().setSelectedReportCompleteClicked(this, false);
            PreferencesManager.getInstance().setSelectedReportFirstResume(this, false);
        }

        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();

//        Log.i("VALID_REPORT_ACT_LOG", "[" + mCurrentReport.getTicket() + "] Action = " + "onPause");
//
//        PreferencesManager.getInstance().setSelectedReportCompleteClicked(this, buttonClicked);
//
//        if (!PreferencesManager.getInstance().isSelectedReportCompleteClicked(this)) {
//            Log.i("VALID_REPORT_ACT_LOG", "[" + mCurrentReport.getTicket() + "] Action = " + "!PreferencesManager.getInstance().isSelectedReportCompleteClicked(this)");
//
//            if (!cancelReportOnBackground) {
//
//                Log.i("VALID_REPORT_ACT_LOG", "[" + mCurrentReport.getTicket() + "] Action = " + "!cancelReportOnBackground");
//
//                PreferencesManager.getInstance().clearReportAttentionData(this);
//                PreferencesManager.getInstance().clearSelectedReportAttentionData(this);
//                cancelReportAttention(currentUser, mCurrentReport);
//            }
//        }
    }

    @Override
    public void onBackPressed() {

        if (mValidReportFragment.shouldInterceptOnBackPressed()) {
            return;
        }

        PreferencesManager.getInstance().setReportCanceled("ValidReportActivity - onBackPressed", this, false);

        super.onBackPressed();
    }

    private void setupToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbarTitle = (TextView) findViewById(R.id.toolbar_title);

        if (mCurrentReport != null)
            mToolbarTitle.setText(mCurrentReport.getTicket());

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void sendReportUpdate(User currentUser, int status) {

        if (!Utils.getInstance().isInternetAvailable(this))
            return;

        Utils.Log("ValidReportActivity", "sendReportUpdate", "Report update " + mCurrentReport.getTicket());

        ServicesManager.updateReportStatus(this, currentUser, mCurrentReport.getTicket(), status, new NewReportCallback() {

            @Override
            public void onSuccessRegister(String resultCode, Report reportCompleteInfo) {

            }

            @Override
            public void onSuccessRegisterReport(Report reportCompleteInfo) {

            }

            @Override
            public void onFailRegisterReport(String message) {
                int noOfRetry = PreferencesManager.getInstance().getCurrentNumberOfRetries(ValidReportActivity.this);

                if (noOfRetry < Constants.NUMBER_OF_RETRIES) {
                    if (mSetReportToStatus2Handler == null) {
                        mSetReportToStatus2Handler = new Handler();
                    }

//                    Log.i("SET_STATUS_2", "Retrying " + noOfRetry + " for report = " + mCurrentReport.getTicket());
                    PreferencesManager.getInstance().setSendReportToStatusRetry(ValidReportActivity.this, noOfRetry + 1);

                    mSetReportToStatus2Handler.postDelayed(mSetReportToStatus2Runnable, Constants.RETRY_INTERVAL_MILLISECONDS);
                }
                else
                {
                    PreferencesManager.getInstance().clearSendReportRetry(ValidReportActivity.this);
//                    Log.i("SET_STATUS_2", "End of retry " + noOfRetry + " for report = " + mCurrentReport.getTicket());
                }
            }

            @Override
            public void userBanned() {

            }

            @Override
            public void onTokenDisabled() {

            }
        });
    }

    Handler mSetReportToStatus2Handler;
    Runnable mSetReportToStatus2Runnable = new Runnable() {
        @Override
        public void run() {
            sendReportUpdate(currentUser, Constants.TROOP_REPORT_STATUS_MARK_AS_TAKEN); // PARA ACTUALIZAR A STATUS COMO TOMADO EL REPORTE
        }
    };

    /*private void cancelReportAttention(User currentUser, Report currentReport) {

        if (!Utils.getInstance().isInternetAvailable(this))
            return;

//        Log.i("VALID_REPORT_ACT_LOG", "[" + mCurrentReport.getTicket() + "] Action = " + "cancelReportAttention");
        Utils.Log("ValidReportActivity", "cancelReportAttention", "Cancel report attention for = " + currentReport.getTicket());

        ServicesManager.cancelReportAttention(this, currentUser, currentReport, new NewReportCallback() {

            @Override
            public void onSuccessRegister(String resultCode, Report reportCompleteInfo) {

            }

            @Override
            public void onSuccessRegisterReport(Report reportCompleteInfo) {
//                Log.i("VALID_REPORT_ACT_LOG", "[" + mCurrentReport.getTicket() + "] Action = " + "onSuccessRegisterReport");

                Utils.Log("ValidReportActivity", "onSuccessRegisterReport", "success report ");

                PreferencesManager.getInstance().setReportCanceled("ValidReportActivity - cancelReportAttention - Success", ValidReportActivity.this, true);
            }

            @Override
            public void onFailRegisterReport(String message) {
//                Log.i("VALID_REPORT_ACT_LOG", "[" + mCurrentReport.getTicket() + "] Action = " + "onFailRegisterReport");
                Utils.Log("ValidReportActivity", "onFailRegisterReport", "fail report ");
            }

            @Override
            public void userBanned() {

            }

            @Override
            public void onTokenDisabled() {

            }
        });
    }*/

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (mValidReportFragment != null) {
            mValidReportFragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onCompletReportClick() {
        buttonClicked = true;
    }

    @Override
    public void onSendReportError() {
        buttonClicked = false;
    }
}
