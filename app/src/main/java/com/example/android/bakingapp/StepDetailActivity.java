package com.example.android.bakingapp;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.example.android.bakingapp.data.Step;

import java.util.ArrayList;

public class StepDetailActivity extends AppCompatActivity {


    private ActionBar mActionBar;
    private ArrayList<Step> mSteps;
    private int mCurrentPosition;
    private ViewPager mPager;
    private Button mPreviousButton, mNextButton;
    private PagerAdapter mPageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setViewForOrientation(getResources().getConfiguration());
        if (savedInstanceState != null) {
            mSteps = savedInstanceState.getParcelableArrayList("Steps");
            mCurrentPosition = savedInstanceState.getInt("CurrentPosition", 0);
        }
        mActionBar = getSupportActionBar();
        if (mActionBar != null) {
            mActionBar.setDisplayHomeAsUpEnabled(true);
        }
        mPager = findViewById(R.id.step_detail_container);
        mPreviousButton = findViewById(R.id.button_previous);
        mNextButton = findViewById(R.id.button_next);
        showHideActionBar(getResources().getConfiguration());
        if (savedInstanceState == null) {
            Intent starterIntent = getIntent();
            if (starterIntent != null && starterIntent.hasExtra("steps") && starterIntent.hasExtra("position")) {
                mSteps = starterIntent.getParcelableArrayListExtra("steps");
                mCurrentPosition = starterIntent.getIntExtra("position", 0);
                if (starterIntent.hasExtra("recipe_name")) {
                    setTitle(starterIntent.getStringExtra("recipe_name"));
                }
            }
        }
        mPageAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPageAdapter);
        mPager.setCurrentItem(mCurrentPosition, true);
    }

    private void showHideActionBar(Configuration configuration) {
        if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mActionBar.hide();
        } else if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            mActionBar.show();
        }
    }

    private void setViewForOrientation(Configuration configuration) {
        if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            requestWindowFeature(Window.FEATURE_ACTION_BAR);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        setContentView(R.layout.activity_step_detail);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("Steps", mSteps);
        outState.putInt("CurrentPosition", mCurrentPosition);
    }

    public void onClickPrevious(View view) {
        if (mCurrentPosition > 0) {
            mNextButton.setEnabled(true);
            mCurrentPosition--;
            mPager.setCurrentItem(mCurrentPosition, true);
            if (mCurrentPosition == 0)
                mPreviousButton.setEnabled(false);
            else
                mPreviousButton.setEnabled(true);
        }
    }

    public void onClickNext(View view) {
        if (mCurrentPosition < mSteps.size() - 1) {
            mCurrentPosition++;
            mPreviousButton.setEnabled(true);
            mPager.setCurrentItem(mCurrentPosition, true);
            if (mCurrentPosition == mSteps.size() - 1)
                mNextButton.setEnabled(false);
            else {
                mNextButton.setEnabled(true);
            }
        }
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            mCurrentPosition = position;
            return StepDetailFragment.create(mSteps.get(position));
        }

        @Override
        public int getCount() {
            if (mSteps == null) return 0;
            return mSteps.size();
        }
    }
}
