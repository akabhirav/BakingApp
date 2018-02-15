package com.example.android.bakingapp;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.android.bakingapp.data.StepAdapter;
import com.example.android.bakingapp.db.RecipeContract;
import com.example.android.bakingapp.db.RecipeContract.RecipeEntry;
import com.example.android.bakingapp.db.RecipeContract.StepEntry;
import com.example.android.bakingapp.utils.IngredientAdapter;


public class RecipeDetailFragment extends Fragment implements StepAdapter.StepAdapterOnClickHandler,
        LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = RecipeDetailFragment.class.getName();
    RecipeDetailFragmentCallbacks recipeDetailFragmentCallbacks;
    private int recipeId;
    private final static int STEPS_LOADER = 2;
    private final static int INGREDIENTS_LOADER = 3;
    private StepAdapter mStepAdapter;
    private IngredientAdapter mIngredientAdapter;
    private ProgressBar mStepsProgressBar, mIngredientsProgressBar;

    public RecipeDetailFragment() {
    }

    public static RecipeDetailFragment create(int recipeId) {
        RecipeDetailFragment fragment = new RecipeDetailFragment();
        fragment.setRecipe(recipeId);
        return fragment;
    }

    public void setRecipe(int recipeId) {
        this.recipeId = recipeId;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.recipe_detail_fragment, container, false);
        RecyclerView mStepsRecyclerView = rootView.findViewById(R.id.rv_steps);
        mStepsProgressBar = rootView.findViewById(R.id.pb_steps_loader);
        mIngredientsProgressBar = rootView.findViewById(R.id.pb_ingredients_loader);
        RecyclerView mIngredientsRecyclerView = rootView.findViewById(R.id.rv_ingredients);
        LinearLayoutManager mIngredientLayoutManager = new LinearLayoutManager(getContext());
        mIngredientsRecyclerView.setLayoutManager(mIngredientLayoutManager);
        mIngredientsRecyclerView.setHasFixedSize(true);
        mIngredientAdapter = new IngredientAdapter();
        mIngredientsRecyclerView.setAdapter(mIngredientAdapter);
        LinearLayoutManager mStepLayoutManager = new LinearLayoutManager(getContext());
        mStepsRecyclerView.setLayoutManager(mStepLayoutManager);
        mStepsRecyclerView.setHasFixedSize(true);
        mStepAdapter = new StepAdapter(this, recipeDetailFragmentCallbacks.setSelectableStepList());
        mStepsRecyclerView.setAdapter(mStepAdapter);
        getActivity().getSupportLoaderManager().initLoader(STEPS_LOADER, null, this);
        getActivity().getSupportLoaderManager().initLoader(INGREDIENTS_LOADER, null, this);
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Activity parentActivity = getActivity();
        if (parentActivity instanceof DetailActivity) {
            recipeDetailFragmentCallbacks = ((DetailActivity) parentActivity).onRequestCallbacks();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case STEPS_LOADER:
                mStepsProgressBar.setVisibility(View.VISIBLE);
                return new CursorLoader(getContext(),
                        StepEntry.getContentUri(recipeId),
                        new String[]{StepEntry.COLUMN_DESCRIPTION},
                        null, null, null);
            case INGREDIENTS_LOADER:
                mIngredientsProgressBar.setVisibility(View.VISIBLE);
                return new CursorLoader(getContext(),
                        RecipeEntry.CONTENT_URI.buildUpon().appendPath(String.valueOf(recipeId))
                                .appendPath(RecipeContract.PATH_INGREDIENT).build()
                        , null, null, null, null);
            default:
                Log.e(TAG, "Unknown Loader with id" + id);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        int loaderId = loader.getId();
        switch (loaderId) {
            case STEPS_LOADER:
                mStepsProgressBar.setVisibility(View.GONE);
                mStepAdapter.swapCursor(data);
                break;
            case INGREDIENTS_LOADER:
                mIngredientsProgressBar.setVisibility(View.GONE);
                mIngredientAdapter.swapCursor(data);
                break;
            default:
                Log.e(TAG, "Unknown Loader with id" + loaderId);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        int loaderId = loader.getId();
        switch (loaderId) {
            case STEPS_LOADER:
                mStepAdapter.swapCursor(null);
                break;
            case INGREDIENTS_LOADER:
                mIngredientAdapter.swapCursor(null);
                break;
            default:
                Log.e(TAG, "Unknown Loader with id" + loaderId);
        }

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
