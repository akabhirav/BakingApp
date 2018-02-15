package com.example.android.bakingapp;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.example.android.bakingapp.data.Step;
import com.example.android.bakingapp.db.RecipeContract.StepEntry;

import java.util.ArrayList;

public class DetailActivity extends AppCompatActivity implements
        RecipeDetailFragment.RecipeDetailFragmentCallbacks,
        LoaderManager.LoaderCallbacks<Cursor> {


    private static final int STEP_DETAIL_LOADER = 4;
    private static final String STATE_STEPS = "steps";
    private static final int WHAT = 1;
    private boolean mTwoPane = false;
    private int recipeId;
    private String recipeName;
    private ArrayList<Step> mSteps;
    private ProgressBar mStepDetailsProgressBar;
    private FrameLayout mStepDetailFrameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState != null) {
            mSteps = savedInstanceState.getParcelableArrayList(STATE_STEPS);
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        if (findViewById(R.id.step_detail_container) != null)
            mTwoPane = true;
        if (savedInstanceState == null) {
            Intent starterIntent = getIntent();
            if (starterIntent != null && starterIntent.hasExtra("recipe_id")) {
                recipeId = starterIntent.getIntExtra("recipe_id", 0);
                recipeName = starterIntent.getStringExtra("recipe_name");
                setTitle(recipeName);
                RecipeDetailFragment recipeDetailFragment = RecipeDetailFragment.create(recipeId);
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().add(R.id.recipe_detail_container, recipeDetailFragment).commit();
                if (mTwoPane) {
                    mStepDetailsProgressBar = findViewById(R.id.pb_step_details);
                    mStepDetailFrameLayout = findViewById(R.id.step_detail_container);
                }
                getSupportLoaderManager().initLoader(STEP_DETAIL_LOADER, null, this);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(STATE_STEPS, mSteps);
    }

    public RecipeDetailFragment.RecipeDetailFragmentCallbacks onRequestCallbacks() {
        return this;
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
    public void onStepClickedHandler(int position) {
        if (mTwoPane) {
            StepDetailFragment stepDetailFragment = StepDetailFragment.create(mSteps.get(position));
            getSupportFragmentManager().beginTransaction().replace(R.id.step_detail_container, stepDetailFragment).commit();
        } else {
            Intent stepDetailIntent = new Intent(this, StepDetailActivity.class);
            stepDetailIntent.putExtra("steps", mSteps);
            stepDetailIntent.putExtra("position", position);
            stepDetailIntent.putExtra("recipe_name", recipeName);
            startActivity(stepDetailIntent);
        }
    }

    @Override
    public boolean setSelectableStepList() {
        return mTwoPane;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (mTwoPane) {
            mStepDetailsProgressBar.setVisibility(View.VISIBLE);
            mStepDetailFrameLayout.setVisibility(View.GONE);
        }
        return new CursorLoader(this,
                StepEntry.getContentUri(recipeId),
                null, null, null, null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mSteps = new ArrayList<>();
        while (data.moveToNext()) {
            Step step = new Step(
                    data.getInt(data.getColumnIndex(StepEntry.COLUMN_STEP_ID)),
                    data.getString(data.getColumnIndex(StepEntry.COLUMN_SHORT_DESCRIPTION)),
                    data.getString(data.getColumnIndex(StepEntry.COLUMN_DESCRIPTION)),
                    data.getString(data.getColumnIndex(StepEntry.COLUMN_VIDEO)),
                    data.getString(data.getColumnIndex(StepEntry.COLUMN_THUMBNAIL))
            );
            mSteps.add(step);
        }
        if (mTwoPane) {
            mStepDetailsProgressBar.setVisibility(View.GONE);
            mStepDetailFrameLayout.setVisibility(View.VISIBLE);
            Handler mHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    if (msg.what == WHAT) {
                        StepDetailFragment stepDetailFragment = StepDetailFragment.create(mSteps.get(0));
                        getSupportFragmentManager().beginTransaction().add(R.id.step_detail_container, stepDetailFragment).commit();
                    }
                }
            };
            mHandler.sendEmptyMessage(WHAT);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
