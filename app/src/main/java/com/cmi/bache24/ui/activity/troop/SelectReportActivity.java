package com.cmi.bache24.ui.activity.troop;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.cmi.bache24.R;
import com.cmi.bache24.data.model.Report;
import com.cmi.bache24.data.model.User;
import com.cmi.bache24.ui.fragment.troop.SelectReportFragment;
import com.cmi.bache24.util.Constants;
import com.cmi.bache24.util.PreferencesManager;
import com.google.gson.Gson;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SelectReportActivity extends AppCompatActivity implements SelectReportFragment.SelectReportFragmentListener {

    private Toolbar mToolbar;
    private TextView mToolbarTitle;
    private Report mCurrentReport;
    private SelectReportFragment mSelectReportFragment;
    private User currentUser;
    private boolean buttonClicked = false;
    private boolean finishedOnResume = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppThemePink);

        Log.i("SelectReportActivityi", "onCreate");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_report);

        String reportString = getIntent().getStringExtra(Constants.EXTRA_REPORT_STATUS_DETAIL);
        if (reportString != "") {
            Gson gson = new Gson();
            mCurrentReport = gson.fromJson(reportString, Report.class);
        }

        mSelectReportFragment = (SelectReportFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment);

        mSelectReportFragment.showReportDetail(mCurrentReport);

        setupToolbar();

        currentUser = PreferencesManager.getInstance().getUserInfo(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.WRONG_REPORT_REQUEST_CODE ||
            requestCode == Constants.FAKE_REPORT_REQUEST_CODE ||
            requestCode == Constants.VALID_REPORT_REQUEST_CODE ||
            requestCode == Constants.PENDING_REPORT_REQUEST_CODE ||
            requestCode == Constants.REALLOCATE_REPORT_REQUEST_CODE ||
            requestCode == Constants.INVALID_REPORT_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    setResult(this.RESULT_OK, null);
                    finish();
                }
            }
        } else {
            if (mSelectReportFragment != null)
                mSelectReportFragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (mSelectReportFragment != null) {
            mSelectReportFragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
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

    @Override
    public void onOptionClick() {
        PreferencesManager.getInstance().setSelectedReportFirstResume(this, true);
    }
}
