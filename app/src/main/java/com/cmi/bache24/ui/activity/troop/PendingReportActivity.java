package com.cmi.bache24.ui.activity.troop;

import android.os.Bundle;
import android.os.Handler;
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
import com.cmi.bache24.ui.fragment.troop.PendingReportFragment;
import com.cmi.bache24.util.Constants;
import com.cmi.bache24.util.PreferencesManager;
import com.cmi.bache24.util.Utils;
import com.google.gson.Gson;

public class PendingReportActivity extends AppCompatActivity implements PendingReportFragment.PendingReportFragmentListener {

    private Toolbar mToolbar;
    private TextView mToolbarTitle;
    private PendingReportFragment mPendingReportFragment;
    private Report mCurrentReport;
    private User currentUser;
    private boolean buttonClicked = false;
    private boolean cancelReportOnBackground = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_report);

        String reportString = getIntent().getStringExtra(Constants.EXTRA_REPORT_STATUS_DETAIL);
        if (reportString != "") {
            Gson gson = new Gson();
            mCurrentReport = gson.fromJson(reportString, Report.class);
        }

        mPendingReportFragment = (PendingReportFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment);

        mPendingReportFragment.setReport(mCurrentReport);

        setupToolbar();

        currentUser = PreferencesManager.getInstance().getUserInfo(this);

        if (!PreferencesManager.getInstance().isReportAttentionInProgress(this)) {
            PreferencesManager.getInstance().setReportAttentionInProgress(this, true);
            PreferencesManager.getInstance().setReportAttentionTicketValue(this, mCurrentReport.getTicket());

            sendReportUpdate(currentUser, Constants.TROOP_REPORT_STATUS_MARK_AS_TAKEN); // PARA ACTUALIZAR A STATUS COMO TOMADO EL REPORTE
        }
    }

    @Override
    protected void onResume() {

//        buttonClicked = false;

//        if (!PreferencesManager.getInstance().isSelectedReportFirstResume(this)) {
//
//            if (!PreferencesManager.getInstance().isSelectedReportCompleteClicked(this)) {
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

//        PreferencesManager.getInstance().setSelectedReportCompleteClicked(this, buttonClicked);
//
//        if (!PreferencesManager.getInstance().isSelectedReportCompleteClicked(this)) {
//            if (!cancelReportOnBackground) {
//                PreferencesManager.getInstance().clearReportAttentionData(this);
//                PreferencesManager.getInstance().clearSelectedReportAttentionData(this);
//                cancelReportAttention(currentUser, mCurrentReport);
//            }
//        }
    }

    @Override
    public void onBackPressed() {

        PreferencesManager.getInstance().setReportCanceled("FakeReportActivity-onBackPressed", this, false);

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

        ServicesManager.updateReportStatus(this, currentUser, mCurrentReport.getTicket(), status, new NewReportCallback() {

            @Override
            public void onSuccessRegister(String resultCode, Report reportCompleteInfo) {

            }

            @Override
            public void onSuccessRegisterReport(Report reportCompleteInfo) {

            }

            @Override
            public void onFailRegisterReport(String message) {
                int noOfRetry = PreferencesManager.getInstance().getCurrentNumberOfRetries(PendingReportActivity.this);

                if (noOfRetry < Constants.NUMBER_OF_RETRIES) {
                    if (mSetReportToStatus2Handler == null) {
                        mSetReportToStatus2Handler = new Handler();
                    }

//                    Log.i("SET_STATUS_2", "Retrying " + noOfRetry + " for report = " + mCurrentReport.getTicket());
                    PreferencesManager.getInstance().setSendReportToStatusRetry(PendingReportActivity.this, noOfRetry + 1);

                    mSetReportToStatus2Handler.postDelayed(mSetReportToStatus2Runnable, Constants.RETRY_INTERVAL_MILLISECONDS);
                }
                else
                {
                    PreferencesManager.getInstance().clearSendReportRetry(PendingReportActivity.this);
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

        ServicesManager.cancelReportAttention(this, currentUser, currentReport, new NewReportCallback() {

            @Override
            public void onSuccessRegister(String resultCode, Report reportCompleteInfo) {

            }

            @Override
            public void onSuccessRegisterReport(Report reportCompleteInfo) {
                PreferencesManager.getInstance().setReportCanceled("FakeReportActivity - cancelReportAttention", PendingReportActivity.this, true);
            }

            @Override
            public void onFailRegisterReport(String message) {

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
    public void onSendButtonClick() {
        buttonClicked = true;
    }

    @Override
    public void onSendReportError() {
        buttonClicked = false;
    }
}
