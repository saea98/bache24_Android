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
import com.cmi.bache24.ui.fragment.troop.WrongReportFragment;
import com.cmi.bache24.util.Constants;
import com.cmi.bache24.util.PreferencesManager;
import com.cmi.bache24.util.Utils;
import com.google.gson.Gson;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class WrongReportActivity extends AppCompatActivity implements WrongReportFragment.WrongReportFragmentListener {

    private Toolbar mToolbar;
    private TextView mToolbarTitle;
    private WrongReportFragment mWrongReportFragment;
    private Report mCurrentReport;
    private User currentUser;
    private boolean buttonClicked = false;
    private boolean cancelReportOnBackground = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppThemePink);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wrong_report);

        String reportString = getIntent().getStringExtra(Constants.EXTRA_REPORT_STATUS_DETAIL);
        if (reportString != "") {
            Gson gson = new Gson();
            mCurrentReport = gson.fromJson(reportString, Report.class);
        }

        mWrongReportFragment = (WrongReportFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment);

        mWrongReportFragment.setReport(mCurrentReport);

        setupToolbar();

        currentUser = PreferencesManager.getInstance().getUserInfo(this);

        if (!PreferencesManager.getInstance().isReportAttentionInProgress(this)) {
            PreferencesManager.getInstance().setReportAttentionInProgress(this, true);
            PreferencesManager.getInstance().setReportAttentionTicketValue(this, mCurrentReport.getTicket());

//            PreferencesManager.getInstance().setShouldSendReportToStatus2(this, true);

            sendReportUpdate(currentUser, Constants.TROOP_REPORT_STATUS_MARK_AS_TAKEN); // PARA ACTUALIZAR A STATUS COMO TOMADO EL REPORTE
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onResume() {

        buttonClicked = false;

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
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onBackPressed() {

        PreferencesManager.getInstance().setReportCanceled("WrongReportActivity - onBackPressed", this, false);

        super.onBackPressed();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (mWrongReportFragment != null) {
            mWrongReportFragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
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
//                Log.i("SET_STATUS_2", "Success, no need to retry for report = " + mCurrentReport.getTicket());
            }

            @Override
            public void onFailRegisterReport(String message) {

                int noOfRetry = PreferencesManager.getInstance().getCurrentNumberOfRetries(WrongReportActivity.this);

                if (noOfRetry < Constants.NUMBER_OF_RETRIES) {
                    if (mSetReportToStatus2Handler == null) {
                        mSetReportToStatus2Handler = new Handler();
                    }

//                    Log.i("SET_STATUS_2", "Retrying " + noOfRetry + " for report = " + mCurrentReport.getTicket());
                    PreferencesManager.getInstance().setSendReportToStatusRetry(WrongReportActivity.this, noOfRetry + 1);

                    mSetReportToStatus2Handler.postDelayed(mSetReportToStatus2Runnable, Constants.RETRY_INTERVAL_MILLISECONDS);
                }
                else
                {
                    PreferencesManager.getInstance().clearSendReportRetry(WrongReportActivity.this);
//                    Log.i("SET_STATUS_2", "End of retry " + noOfRetry + " for report = " + mCurrentReport.getTicket());
                }

                // OLD

                /*if (PreferencesManager.getInstance().shouldSendReportToStatus2(WrongReportActivity.this)) {
                    if (mSetReportToStatus2Handler == null) {
                        mSetReportToStatus2Handler = new Handler();
                    }

                    Log.i("SET_STATUS_2", "Retrying for report = " + mCurrentReport.getTicket());

                    mSetReportToStatus2Handler.postDelayed(mSetReportToStatus2Runnable, 5000);
                }
                else
                {
                    Log.i("SET_STATUS_2", "End of retry for report = " + mCurrentReport.getTicket());
                }*/
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
                PreferencesManager.getInstance().setReportCanceled("WrongReportActivity - cancelReportAttention", WrongReportActivity.this, true);
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
    public void onCompletReportClick() {
        buttonClicked = true;
    }

    @Override
    public void onSendReportError() {
        buttonClicked = false;
    }
}
