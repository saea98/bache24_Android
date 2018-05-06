package com.cmi.bache24.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cmi.bache24.R;
import com.cmi.bache24.data.model.Report;
import com.cmi.bache24.ui.fragment.UserHistoryFragment;
import com.cmi.bache24.util.Constants;
import com.google.gson.Gson;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class UserHistoryActivity extends AppCompatActivity implements
        UserHistoryFragment.UserHistoryAndProfileListener,
        View.OnClickListener {

    private Toolbar mToolbar;
    private TextView mToolbarTitle;
    private Button mEditUser;
    private Button mCancelEdit;
    private UserHistoryFragment mUserHistoryFragment;

    private RelativeLayout.LayoutParams mParams1;
    private RelativeLayout.LayoutParams mParams2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppThemePink);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_history);

        mUserHistoryFragment = (UserHistoryFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment);

        mParams1 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mParams2 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        mParams1.setMargins(0, 0, 0, 0);
        mParams2.setMargins(60, 0, 0, 0);

        setupToolbar();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_user_history, menu);
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

    private void setupToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbarTitle = (TextView) findViewById(R.id.toolbar_title);
        mEditUser = (Button) findViewById(R.id.button_edit);
        mCancelEdit = (Button) findViewById(R.id.button_cancel);

        mCancelEdit.setVisibility(View.GONE);
        mCancelEdit.setEnabled(false);
        mCancelEdit.setOnClickListener(this);

        mEditUser.setVisibility(View.INVISIBLE);
        mEditUser.setEnabled(false);
        mEditUser.setOnClickListener(this);

        mToolbarTitle.setLayoutParams(mParams1);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishActivity();
            }
        });
    }

    @Override
    public void onHistorySelect() {
        mToolbarTitle.setText(getResources().getString(R.string.userprofile_history_button));

        mEditUser.setVisibility(View.INVISIBLE);
        mEditUser.setEnabled(false);

        mCancelEdit.setVisibility(View.GONE);
        mCancelEdit.setEnabled(false);
        mToolbarTitle.setLayoutParams(mParams1);
    }

    @Override
    public void onProfileSelect() {
        mToolbarTitle.setText(getResources().getString(R.string.userprofile_profile_button));
        mEditUser.setVisibility(View.VISIBLE);
        mEditUser.setEnabled(true);

        mCancelEdit.setVisibility(View.GONE);
        mCancelEdit.setEnabled(true);
        mToolbarTitle.setLayoutParams(mParams1);
    }

    @Override
    public void onReportSelected(Report report) {
        Intent intentReportStatusDetail = new Intent(this, ReportStatusDetailActivity.class);

        Gson gson = new Gson();
        String jsonReport = gson.toJson(report);
        intentReportStatusDetail.putExtra(Constants.EXTRA_REPORT_STATUS_DETAIL, jsonReport);

        startActivity(intentReportStatusDetail);
        overridePendingTransition(R.anim.enter_from_right, R.anim.enter_from_left);
    }

    @Override
    public void onUpdateFinished() {
        mEditUser.setEnabled(true);
        mEditUser.setText(getResources().getString(R.string.profile_edit));

        mCancelEdit.setEnabled(true);
        mCancelEdit.setVisibility(View.GONE);
        mToolbarTitle.setLayoutParams(mParams1);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
    }

    @Override
    public void onUpdateFailed() {
        mEditUser.setEnabled(true);
        mCancelEdit.setEnabled(true);
        mToolbarTitle.setLayoutParams(mParams2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mUserHistoryFragment != null)
            mUserHistoryFragment.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == mEditUser.getId()) {
            if (mEditUser.getText().equals(getResources().getString(R.string.profile_edit))) {
                if (mUserHistoryFragment != null) {
                    mUserHistoryFragment.editUser();
                    mCancelEdit.setVisibility(View.VISIBLE);
                    mToolbarTitle.setLayoutParams(mParams2);
                    mEditUser.setText(getResources().getString(R.string.profile_save));
                    getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                    getSupportActionBar().setDisplayShowHomeEnabled(false);
                }

            } else if (mEditUser.getText().equals(getResources().getString(R.string.profile_save))) {
                hideKeyboardIfNecessary();

                if (mUserHistoryFragment != null) {
                    if (mUserHistoryFragment.validUserInfo()) {
                        mEditUser.setEnabled(false);
                        mCancelEdit.setEnabled(false);
                        mUserHistoryFragment.endEditUser();
                    }
                }
            }
        } else if (view.getId() == mCancelEdit.getId()) {
            hideKeyboardIfNecessary();
            if (mUserHistoryFragment != null) {
                mUserHistoryFragment.cancelEditUser();
                mEditUser.setEnabled(true);
                mEditUser.setText(getResources().getString(R.string.profile_edit));

                mCancelEdit.setEnabled(true);
                mCancelEdit.setVisibility(View.GONE);
                mToolbarTitle.setLayoutParams(mParams1);

                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                getSupportActionBar().setDisplayShowHomeEnabled(false);
            }
        }
    }

    private void hideKeyboardIfNecessary() {
        View viewK = getCurrentFocus();
        if (viewK != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(viewK.getWindowToken(), 0);
        }
    }

    @Override
    public void onBackPressed() {
        finishActivity();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (mUserHistoryFragment != null) {
            mUserHistoryFragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void finishActivity() {
        Intent result = new Intent();
        result.putExtra(Constants.BUNDLE_REGISTER_REPORT_RESULT, true);

        setResult(RESULT_OK, result);

        finish();
    }
}
