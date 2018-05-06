package com.cmi.bache24.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.cmi.bache24.R;
import com.cmi.bache24.ui.fragment.ReportDetailFragment;
import com.cmi.bache24.util.Constants;
import com.cmi.bache24.util.PreferencesManager;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
//import com.google.android.gms.location.places.Place;
//import com.google.android.gms.location.places.ui.PlaceAutocomplete;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ReportDetailActivity extends AppCompatActivity
                                  implements ReportDetailFragment.ReportDetailFragmentListener,
                                            View.OnClickListener {

    private Toolbar mToolbar;

    private String mNewReportAddressString;
    private double mNewReportLongitude;
    private double mNewReportLatitude;
    private boolean mNewReportIsTemporary;
    private int mNewReportPositionInList;
    private Button cancelTemporaryReportButton;
    private Button deleteTemporaryReportButton;
    private TextView titleTextView;

    private ReportDetailFragment reportDetailFragment;

    public static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 8827;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppThemePink);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_detail);

        if (getIntent() != null) {
            mNewReportAddressString = getIntent().getStringExtra(Constants.EXTRA_NEW_REPORT_ADDRESS_AS_STRING);
            mNewReportLongitude = getIntent().getDoubleExtra(Constants.EXTRA_NEW_REPORT_LONGITUDE, 0);
            mNewReportLatitude = getIntent().getDoubleExtra(Constants.EXTRA_NEW_REPORT_LATITUDE, 0);
            mNewReportIsTemporary = getIntent().getBooleanExtra(Constants.EXTRA_NEW_REPORT_IS_TEMPORARY, false);
            mNewReportPositionInList = getIntent().getIntExtra(Constants.EXTRA_NEW_REPORT_POSITION_IN_LIST, -1);
        }

        reportDetailFragment = (ReportDetailFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment);

        reportDetailFragment.setReportLocation(mNewReportLatitude,
                                               mNewReportLongitude,
                                               mNewReportAddressString,
                                               mNewReportIsTemporary,
                                               mNewReportPositionInList);

        setupToolbar();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        /*if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                if (reportDetailFragment != null) {
                    reportDetailFragment.setPlace(place);
                }
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                if (reportDetailFragment != null) {
                    reportDetailFragment.setPlace(null);
                }
            } else if (requestCode == RESULT_CANCELED) {
                if (reportDetailFragment != null) {
                    reportDetailFragment.setPlace(null);
                }
            }
        }*/
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
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (reportDetailFragment != null) {
            reportDetailFragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void setupToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onNewReportFinished();
            }
        });

        cancelTemporaryReportButton = (Button) findViewById(R.id.button_cancel_temp);
        deleteTemporaryReportButton = (Button) findViewById(R.id.button_delete_temp);
        titleTextView = (TextView) findViewById(R.id.textview_title);

        cancelTemporaryReportButton.setVisibility(mNewReportIsTemporary ? View.VISIBLE : View.GONE);
        deleteTemporaryReportButton.setVisibility(mNewReportIsTemporary ? View.VISIBLE : View.GONE);

        cancelTemporaryReportButton.setOnClickListener(this);
        deleteTemporaryReportButton.setOnClickListener(this);

        if (mNewReportIsTemporary) {
            titleTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.medium_font_size));
        }
    }

    @Override
    public void onNewReportFinished() {
        finishNewReport(true);
    }

    @Override
    public void onBackPressed() {
        PreferencesManager.getInstance().setCancelTemporaryReport(this, true);
        finishNewReport(false);
    }

    /*@Override
    public void onTutorialClicked() {

    }*/

    /*@Override
    public void onCaptureLocation() {
        try {
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                        .build(this);
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException ex) {
            GooglePlayServicesUtil.getErrorDialog(ex.getConnectionStatusCode(), this, 0);
        } catch (GooglePlayServicesNotAvailableException ex) {

        }
    }*/

    @Override
    public void onNewReportCanceled() {
        finishNewReport(false);
    }

    private void finishNewReport(boolean completed) {
        Intent result = new Intent();
        result.putExtra(Constants.BUNDLE_REGISTER_REPORT_RESULT, completed);

        setResult(RESULT_OK, result);

        finish();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == cancelTemporaryReportButton.getId()) {
            PreferencesManager.getInstance().setCancelTemporaryReport(this, true);
            finish();
        } else if (v.getId() == deleteTemporaryReportButton.getId()) {
            PreferencesManager.getInstance().deleteTemporaryReport(this, mNewReportPositionInList);
            finish();
        }
    }
}
