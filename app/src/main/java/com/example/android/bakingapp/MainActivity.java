package com.example.android.bakingapp;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import com.example.android.bakingapp.db.RecipeContract;
import com.example.android.bakingapp.db.RecipeContract.RecipeEntry;
import com.example.android.bakingapp.sync.RecipeSyncUtils;

public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>,
        RecipeAdapter.RecipeAdapterOnClickHandler {

    private static final int RECIPE_CURSOR_LOADER = 1;

    private ProgressBar mRecipesProgressBar;
    private RecipeAdapter mRecipeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecipesProgressBar = findViewById(R.id.pb_recipes_loader);
        RecyclerView mRecipesRecyclerView = findViewById(R.id.rv_recipes);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecipesRecyclerView.setLayoutManager(mLayoutManager);
        mRecipesRecyclerView.setHasFixedSize(true);
        mRecipeAdapter = new RecipeAdapter(this);
        mRecipesRecyclerView.setAdapter(mRecipeAdapter);
        LoaderManager loaderManager = getSupportLoaderManager();
        loaderManager.initLoader(RECIPE_CURSOR_LOADER, null, this);
        RecipeSyncUtils.startImmediateSync(this);
    }

    /**
     * Instantiate and return a new Loader for the given ID.
     *
     * @param id   The ID whose loader is to be created.
     * @param args Any arguments supplied by the caller.
     * @return Return a new Loader instance that is ready to start loading.
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        mRecipesProgressBar.setVisibility(View.VISIBLE);
        return new CursorLoader(this, RecipeEntry.CONTENT_URI,
                null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mRecipesProgressBar.setVisibility(View.GONE);
        mRecipeAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mRecipeAdapter.swapCursor(null);
    }

    @Override
    public void onClick(int recipeId, String recipeName) {
        Intent detailsIntent = new Intent(this, DetailActivity.class);
        detailsIntent.putExtra("recipe_id", recipeId);
        detailsIntent.putExtra("recipe_name", recipeName);
        startActivity(detailsIntent);
    }
}
