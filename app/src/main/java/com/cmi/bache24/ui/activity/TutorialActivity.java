package com.cmi.bache24.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.cmi.bache24.R;
import com.cmi.bache24.ui.adapter.TutorialSlideAdapter;
import com.cmi.bache24.ui.fragment.TutorialFragment;
import com.cmi.bache24.util.PreferencesManager;

import java.util.ArrayList;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class TutorialActivity extends AppCompatActivity implements View.OnClickListener {

    private TutorialSlideAdapter mPagerAdapter;
    private List<Fragment> mFragmentsList;
    private ViewPager mPager;
    private Button mButtonNext;
    private ImageView mImgIndicator1;
    private ImageView mImgIndicator2;
    private ImageView mImgIndicator3;
    private int mTutorialPosition = 0;

    boolean isFromHelp = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        if (getIntent() != null)
            isFromHelp = getIntent().getBooleanExtra("_IS_FROM_HELP", false);

        mFragmentsList = new ArrayList<>();

        mFragmentsList.add(TutorialFragment.newInstance(0));
        mFragmentsList.add(TutorialFragment.newInstance(1));
        mFragmentsList.add(TutorialFragment.newInstance(2));

        mPager = (ViewPager) findViewById(R.id.tutorial_pager);

        mButtonNext = (Button) findViewById(R.id.button_next);
        mImgIndicator1 = (ImageView) findViewById(R.id.image_indicator_1);
        mImgIndicator2 = (ImageView) findViewById(R.id.image_indicator_2);
        mImgIndicator3 = (ImageView) findViewById(R.id.image_indicator_3);

        mButtonNext.setVisibility(View.INVISIBLE);

        mButtonNext.setOnClickListener(this);

        setupViewPager();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_tutorial, menu);
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
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == mButtonNext.getId()) {
            if (mTutorialPosition + 1 == mPagerAdapter.getCount()) {
                finishTutorial();
            } else {
                mPager.setCurrentItem(mTutorialPosition + 1);
            }
        }
    }

    private void setupViewPager() {
        mPagerAdapter = new TutorialSlideAdapter(getSupportFragmentManager(), mFragmentsList);
        mPager.setAdapter(mPagerAdapter);
        mPager.setOffscreenPageLimit(mFragmentsList.size());

        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mTutorialPosition = position;
                showPagerIndicator(mTutorialPosition);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void showPagerIndicator(int position) {
        mImgIndicator1.setImageResource(R.drawable.circular_shape_pager_deselected);
        mImgIndicator2.setImageResource(R.drawable.circular_shape_pager_deselected);
        mImgIndicator3.setImageResource(R.drawable.circular_shape_pager_deselected);

        switch (position) {
            case 0:
                mImgIndicator1.setImageResource(R.drawable.circular_shape_pager_selected);
                mButtonNext.setVisibility(View.INVISIBLE);
                break;
            case 1:
                mImgIndicator2.setImageResource(R.drawable.circular_shape_pager_selected);
                mButtonNext.setVisibility(View.INVISIBLE);
                break;
            case 2:
                mImgIndicator3.setImageResource(R.drawable.circular_shape_pager_selected);
                mButtonNext.setVisibility(View.VISIBLE);
                break;
            default:
                mImgIndicator1.setImageResource(R.drawable.circular_shape_pager_selected);
                break;
        }
    }

    private void finishTutorial() {

        if (!isFromHelp) {
            PreferencesManager.getInstance().setTutorialFinished(this, true);

            Intent loginActivityIntent = new Intent(this, LoginActivity.class);
            loginActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(loginActivityIntent);
            overridePendingTransition(R.anim.enter_from_right, R.anim.enter_from_left);
        } else
            finish();
    }
}
