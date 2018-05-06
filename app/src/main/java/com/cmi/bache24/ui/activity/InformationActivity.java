package com.cmi.bache24.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.cmi.bache24.R;
import com.cmi.bache24.util.Constants;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class InformationActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private TextView mToolbarTitle;
    private String mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppThemePink);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);

        if (getIntent().getExtras() != null)
            mTitle = getIntent().getExtras().getString(Constants.BUNDLE_INFORMATION_ACTIVITY_TITLE, "");

        setupToolbar();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_information, menu);
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
        mToolbarTitle.setText(mTitle);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
