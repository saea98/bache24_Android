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

import com.cmi.bache24.R;
import com.cmi.bache24.data.model.User;
import com.cmi.bache24.ui.fragment.RegisterFragment;
import com.cmi.bache24.util.Constants;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class RegisterActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private RegisterFragment mRegisterFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        boolean isFromSocialNetwork = false;
        User userData = null;

        if (getIntent() != null) {
            isFromSocialNetwork = getIntent().getBooleanExtra(Constants.EXTRA_LOGIN_FROM_SOCIAL_NETWORK,
                                                              false);
            userData = getIntent().getParcelableExtra(Constants.EXTRA_REGISTER_USER_DATA);
        }

        mRegisterFragment = (RegisterFragment) getSupportFragmentManager()
                                                            .findFragmentById(R.id.fragment);
        mRegisterFragment.getArguments().putBoolean(Constants.EXTRA_LOGIN_FROM_SOCIAL_NETWORK,
                                                   isFromSocialNetwork);
        mRegisterFragment.updateViews(isFromSocialNetwork, userData);

        setupToolbar();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
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
        if (mRegisterFragment != null)
            mRegisterFragment.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (mRegisterFragment != null) {
            mRegisterFragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
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
                finish();
            }
        });
    }
}
