package com.example.android.bakingapp;

import android.app.Fragment;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.android.bakingapp.data.Ingredient;
import com.example.android.bakingapp.data.Recipe;
import com.example.android.bakingapp.data.Step;

import java.util.ArrayList;

public class DetailActivity extends AppCompatActivity implements RecipeDetailFragment.RecipeDetailFragmentCallbacks {


    private static final String STATE_RECIPE = "recipe";
    private boolean mTwoPane = false;
    private Recipe mRecipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if(savedInstanceState != null){
            mRecipe = savedInstanceState.getParcelable(STATE_RECIPE);
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        if(findViewById(R.id.step_detail_container) != null)
            mTwoPane = true;
        if (savedInstanceState == null) {
            Intent starterIntent = getIntent();
            if (starterIntent != null && starterIntent.hasExtra("Recipe")) {
                mRecipe = starterIntent.getParcelableExtra("Recipe");
                setTitle(mRecipe.getName());
                RecipeDetailFragment recipeDetailFragment = RecipeDetailFragment.create(mRecipe, this);
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().add(R.id.recipe_detail_container, recipeDetailFragment).commit();
                if(mTwoPane){
                    StepDetailFragment stepDetailFragment = StepDetailFragment.create(mRecipe.getSteps().get(0));
                    fragmentManager.beginTransaction().add(R.id.step_detail_container, stepDetailFragment).commit();
                }
            }
        }
    }

    public RecipeDetailFragment.RecipeDetailFragmentCallbacks onRequestCallbacks(){
        return this;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(STATE_RECIPE, mRecipe);
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
        if(mTwoPane){
            StepDetailFragment stepDetailFragment = StepDetailFragment.create(mRecipe.getSteps().get(position));
            getSupportFragmentManager().beginTransaction().replace(R.id.step_detail_container, stepDetailFragment).commit();
        } else {
            Intent stepDetailIntent = new Intent(this, StepDetailActivity.class);
            stepDetailIntent.putExtra("Steps", mRecipe.getSteps());
            stepDetailIntent.putExtra("position", position);
            stepDetailIntent.putExtra("RecipeName", mRecipe.getName());
            startActivity(stepDetailIntent);
        }
    }

    @Override
    public boolean setSelectableStepList() {
        return mTwoPane;
    }
}
