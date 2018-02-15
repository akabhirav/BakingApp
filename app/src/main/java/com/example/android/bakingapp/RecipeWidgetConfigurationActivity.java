package com.example.android.bakingapp;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.android.bakingapp.data.Recipe;
import com.example.android.bakingapp.data.RecipeWidgetAdapter;
import com.example.android.bakingapp.db.RecipeContract.IngredientEntry;
import com.example.android.bakingapp.db.RecipeContract.RecipeEntry;

public class RecipeWidgetConfigurationActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>,
        RecipeWidgetAdapter.RecipeWidgetAdapterOnClickHandler {

    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private static final int RECIPES_LOADER = 10;
    private static final int INGREDIENTS_LOADER = 11;
    private static final String PREFS_NAME = "AppWidget";
    private static final String PREF_PREFIX_KEY = "appwidget";
    private RecipeWidgetAdapter mAdapter;
    private ProgressBar progressBar;
    private String mRecipeName;

    public RecipeWidgetConfigurationActivity() {
        super();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setResult(RESULT_CANCELED);
        setContentView(R.layout.activity_recipe_widget_configuration);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        final RecyclerView recyclerView = findViewById(R.id.rv_recipe_name);
        progressBar = findViewById(R.id.pb_recipes_loader);

        // Defined array values to show in ListView
        mAdapter = new RecipeWidgetAdapter(this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(mAdapter);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
        }
        getSupportLoaderManager().initLoader(RECIPES_LOADER, null, this);
    }

    private void createWidget(Context context, String widgetText, String widgetTitle) {
        // Store the string locally
        saveTitlePref(context, mAppWidgetId, widgetText, widgetTitle);

        // It is the responsibility of the configuration activity to update the app widget
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        RecipeWidgetProvider.updateAppWidget(context, appWidgetManager, mAppWidgetId);

        // Make sure we pass back the original appWidgetId
        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
        setResult(RESULT_OK, resultValue);
        finish();
    }

    static void saveTitlePref(Context context, int appWidgetId, String text, String title) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putString(PREF_PREFIX_KEY + appWidgetId + "text", text);
        prefs.putString(PREF_PREFIX_KEY + appWidgetId + "title", title);
        prefs.apply();
    }

    static String loadTitlePrefText(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        String titleValue = prefs.getString(PREF_PREFIX_KEY + appWidgetId + "text", null);
        if (titleValue != null) {
            return titleValue;
        } else {
            return context.getString(R.string.appwidget_text);
        }
    }

    static String loadTitlePrefTitle(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        String titleValue = prefs.getString(PREF_PREFIX_KEY + appWidgetId + "title", null);
        if (titleValue != null) {
            return titleValue;
        } else {
            return context.getString(R.string.appwidget_text);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case RECIPES_LOADER:
                progressBar.setVisibility(View.VISIBLE);
                return new CursorLoader(this, RecipeEntry.CONTENT_URI,
                        new String[]{RecipeEntry.COLUMN_RECIPE_ID, RecipeEntry.COLUMN_NAME},
                        null, null, null);
            case INGREDIENTS_LOADER:
                int recipeId = args.getInt("recipe_id", 0);
                return new CursorLoader(this, IngredientEntry.getContentUri(recipeId),
                        null, null, null, null);
            default:
                return null;
        }

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        int id = loader.getId();
        switch (id) {
            case RECIPES_LOADER:
                progressBar.setVisibility(View.GONE);
                if (data == null) return;
                mAdapter.swapCursor(data);
                break;
            case INGREDIENTS_LOADER:
                String widgetText = createIngredientsList(data);
                createWidget(getApplicationContext(), widgetText, mRecipeName);
                break;
            default:
        }

    }

    private String createIngredientsList(Cursor ingredientsCursor) {
        StringBuilder builder = new StringBuilder();
        while (ingredientsCursor.moveToNext()) {
            builder.append(getString(R.string.bullet)).append(" ")
                    .append(ingredientsCursor.getDouble(ingredientsCursor.getColumnIndex(IngredientEntry.COLUMN_QUANTITY)))
                    .append(ingredientsCursor.getString(ingredientsCursor.getColumnIndex(IngredientEntry.COLUMN_MEASURE)))
                    .append(ingredientsCursor.getString(ingredientsCursor.getColumnIndex(IngredientEntry.COLUMN_INGREDIENT)))
                    .append("\n");
        }
        return builder.toString();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    @Override
    public void onClick(int recipeId, String recipeName) {
        Bundle args = new Bundle();
        mRecipeName = recipeName;
        args.putInt("recipe_id", recipeId);
        getSupportLoaderManager().initLoader(INGREDIENTS_LOADER, args, this);
    }
}
