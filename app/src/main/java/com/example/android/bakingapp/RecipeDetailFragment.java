package com.example.android.bakingapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.bakingapp.data.Ingredient;
import com.example.android.bakingapp.data.Recipe;
import com.example.android.bakingapp.data.Step;
import com.example.android.bakingapp.utils.IngredientAdapter;

import java.util.ArrayList;


public class RecipeDetailFragment extends Fragment implements StepAdapter.StepAdapterOnClickHandler {
    Recipe mRecipe;
    RecipeDetailFragmentCallbacks recipeDetailFragmentCallbacks;

    public RecipeDetailFragment() {
    }

    public static RecipeDetailFragment create(Recipe recipe, RecipeDetailFragmentCallbacks recipeDetailFragmentCallbacks){
        RecipeDetailFragment fragment = new RecipeDetailFragment();
        fragment.setRecipe(recipe);
        return fragment;
    }

    public void setRecipe(Recipe recipe) {
        this.mRecipe = recipe;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null)
            mRecipe = savedInstanceState.getParcelable("Recipe");
        View rootView = inflater.inflate(R.layout.recipe_detail_fragment, container, false);
        RecyclerView mStepsRecyclerView = rootView.findViewById(R.id.rv_steps);
        RecyclerView mIngredientsRecyclerView = rootView.findViewById(R.id.rv_ingredients);
        LinearLayoutManager mIngredientLayoutManager = new LinearLayoutManager(getContext());
        mIngredientsRecyclerView.setLayoutManager(mIngredientLayoutManager);
        mIngredientsRecyclerView.setHasFixedSize(true);
        IngredientAdapter mIngredientAdapter = new IngredientAdapter(mRecipe.getIngredients());
        mIngredientsRecyclerView.setAdapter(mIngredientAdapter);
        LinearLayoutManager mStepLayoutManager = new LinearLayoutManager(getContext());
        mStepsRecyclerView.setLayoutManager(mStepLayoutManager);
        mStepsRecyclerView.setHasFixedSize(true);
        StepAdapter mStepAdapter = new StepAdapter(this, mRecipe.getSteps(), recipeDetailFragmentCallbacks.setSelectableStepList());
        mStepsRecyclerView.setAdapter(mStepAdapter);
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Activity parentActivity = getActivity();
        if(parentActivity instanceof DetailActivity){
            recipeDetailFragmentCallbacks = ((DetailActivity) parentActivity).onRequestCallbacks();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("Recipe", mRecipe);
    }

    interface RecipeDetailFragmentCallbacks {
        void onStepClickedHandler(int position);
        boolean setSelectableStepList();
    }

    @Override
    public void onClick(int position) {
        if (recipeDetailFragmentCallbacks != null)
            recipeDetailFragmentCallbacks.onStepClickedHandler(position);
    }
}
