package com.cmi.bache24.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.cmi.bache24.R;
import com.cmi.bache24.ui.fragment.ImageViewerFragment;
import com.cmi.bache24.util.Constants;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ImageViewerActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private ImageViewerFragment mImageViewerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer);

        mImageViewerFragment = (ImageViewerFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment);

        String imageUrl = "";

        if (getIntent() != null) {
            imageUrl = getIntent().getStringExtra(Constants.EXTRA_IMAGE_URL);
        }

        mImageViewerFragment.setImageUrl(imageUrl);

        setupToolbar();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private void setupToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
    }
}
