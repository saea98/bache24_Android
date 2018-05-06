package com.cmi.bache24.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.cmi.bache24.R;
import com.cmi.bache24.data.model.Report;
import com.cmi.bache24.ui.fragment.ReportStatusDetailFragment;
import com.cmi.bache24.util.Constants;
import com.cmi.bache24.util.PreferencesManager;
import com.google.gson.Gson;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ReportStatusDetailActivity extends AppCompatActivity
                    implements ReportStatusDetailFragment.ReportStatusDetailFragmentListener {

    private Toolbar mToolbar;
    private TextView mToolbarTitle;
    private Report mCurrentReport;
    private ReportStatusDetailFragment mReportStatusDetailFragment;

    private boolean mIsFromNotification = false;
    private String mBacheId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppThemePink);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_status_detail);

        String reportString = getIntent().getStringExtra(Constants.EXTRA_REPORT_STATUS_DETAIL);
        if (reportString != "") {
            Gson gson = new Gson();
            mCurrentReport = gson.fromJson(reportString, Report.class);
        }

        mIsFromNotification = getIntent().getBooleanExtra(Constants.IS_FROM_NOTIFICATION, false);
        mBacheId = getIntent().getStringExtra(Constants.NOTIFICATION_BACHE_ID);

        mReportStatusDetailFragment = (ReportStatusDetailFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment);
        if (mIsFromNotification) {
            mCurrentReport = new Report();
            mCurrentReport.setTicket(mBacheId);
        }

        mReportStatusDetailFragment.showReportDetail(mCurrentReport, mIsFromNotification);

        setupToolbar();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_report_status_detail, menu);
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (mReportStatusDetailFragment != null) {
            mReportStatusDetailFragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void setupToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbarTitle = (TextView) findViewById(R.id.toolbar_title);

        if (mCurrentReport != null) {
            mToolbarTitle.setText(PreferencesManager.getInstance().getStatusNameForId(this, mCurrentReport.getEtapaId()));
        }

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
    public void onReportTitleChanged(String title) {
        if (mToolbarTitle != null) {
            if (mToolbarTitle.getText().toString().trim().isEmpty())
                mToolbarTitle.setText(title);
        }
    }
}
