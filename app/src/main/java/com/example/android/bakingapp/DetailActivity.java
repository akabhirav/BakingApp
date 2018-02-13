package com.example.android.bakingapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.android.bakingapp.data.Recipe;
import com.example.android.bakingapp.data.Step;

public class DetailActivity extends AppCompatActivity implements StepAdapter.StepAdapterOnClickHandler {

    private RecyclerView mStepsRecyclerView;
    private StepAdapter mStepAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Intent starterIntent =  getIntent();
        if (starterIntent != null && starterIntent.hasExtra("Recipe")) {
            Recipe recipe = starterIntent.getParcelableExtra("Recipe");
            mStepsRecyclerView = findViewById(R.id.rv_steps);
            LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
            mStepsRecyclerView.setLayoutManager(mLayoutManager);
            mStepsRecyclerView.setHasFixedSize(true);
            mStepAdapter = new StepAdapter(this, recipe.getSteps());
            mStepsRecyclerView.setAdapter(mStepAdapter);
        }
    }

    @Override
    public void onClick(Step step) {
        Intent stepDetailIntent = new Intent(this, StepDetailActivity.class);
        stepDetailIntent.putExtra("Step", step);
        startActivity(stepDetailIntent);
    }
}
