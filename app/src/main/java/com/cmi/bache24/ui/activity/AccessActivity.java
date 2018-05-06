package com.cmi.bache24.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.cmi.bache24.R;
import com.cmi.bache24.ui.fragment.AccessFragment;
import com.cmi.bache24.util.Constants;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class AccessActivity extends AppCompatActivity {

    private AccessFragment mAccessFragment;
    private boolean mShouldShowActivationMessage = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_access);

        if (getIntent() != null) {
            mShouldShowActivationMessage = getIntent().getBooleanExtra(Constants.EXTRA_SHOULD_SHOW_ACTIVATION_MESSAGE,
                                            false);
        }

        mAccessFragment = (AccessFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment);

        if (mAccessFragment != null) {
            mAccessFragment.setShouldShowActivationMessage(mShouldShowActivationMessage);
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
