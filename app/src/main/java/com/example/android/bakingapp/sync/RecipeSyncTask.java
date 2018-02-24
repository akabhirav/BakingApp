package com.example.android.bakingapp.sync;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.example.android.bakingapp.data.Ingredient;
import com.example.android.bakingapp.data.Recipe;
import com.example.android.bakingapp.data.Step;
import com.example.android.bakingapp.db.RecipeContract.IngredientEntry;
import com.example.android.bakingapp.db.RecipeContract.RecipeEntry;
import com.example.android.bakingapp.db.RecipeContract.StepEntry;
import com.example.android.bakingapp.utils.NetworkUtils;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

class RecipeSyncTask {

    private static final String TAG = RecipeSyncTask.class.getName();

    synchronized static void syncRecipes(Context context) {
        try {
            URL requestUrl = NetworkUtils.buildUrl();
            String jsonResponse = NetworkUtils.getResponseFromHttpUrl(requestUrl);
            ArrayList<Recipe> recipes;
            recipes = NetworkUtils.extractJSONResponse(jsonResponse);
            Cursor recipeIds = context.getContentResolver()
                    .query(RecipeEntry.CONTENT_URI, new String[]{RecipeEntry.COLUMN_RECIPE_ID},
                            null, null, null);
            if (recipeIds != null) {
                ArrayList<Integer> recipeIdsInResponse = new ArrayList<>();
                ArrayList<Integer> recipeIdsInDb = new ArrayList<>();
                for (Recipe recipe : recipes) {
                    recipeIdsInResponse.add(recipe.getId());
                }
                while (recipeIds.moveToNext()) {
                    recipeIdsInDb.add(recipeIds.getInt(recipeIds.getColumnIndex(RecipeEntry.COLUMN_RECIPE_ID)));
                }
                recipeIdsInResponse.removeAll(recipeIdsInDb);
                if (recipeIdsInResponse.size() > 0) {
                    ArrayList<Recipe> newRecipes = new ArrayList<>();
                    for (Recipe recipe : recipes) {
                        if (recipeIdsInResponse.contains(recipe.getId())) {
                            newRecipes.add(recipe);
                        }
                    }
                    massInsert(context, newRecipes);
                }
                recipeIds.close();
            } else {
                massInsert(context, recipes);
            }

        } catch (IOException e) {
            Log.e(TAG, "HTTP: Some error during http request");
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    private static void massInsert(Context context, ArrayList<Recipe> newRecipes) {
        for (Recipe recipe : newRecipes) {
            ArrayList<ContentValues> stepContentValuesArrayList = new ArrayList<>();
            ArrayList<ContentValues> ingredientContentValuesArrayList = new ArrayList<>();
            ContentValues recipeValues = new ContentValues();
            int recipeId = recipe.getId();
            recipeValues.put(RecipeEntry.COLUMN_IMAGE, recipe.getImage());
            recipeValues.put(RecipeEntry.COLUMN_NAME, recipe.getName());
            recipeValues.put(RecipeEntry.COLUMN_RECIPE_ID, recipeId);
            recipeValues.put(RecipeEntry.COLUMN_SERVINGS, recipe.getServings());
            ArrayList<Step> steps = recipe.getSteps();
            for (Step step : steps) {
                ContentValues stepValues = new ContentValues();
                stepValues.put(StepEntry.COLUMN_DESCRIPTION, step.getDescription());
                stepValues.put(StepEntry.COLUMN_SHORT_DESCRIPTION, step.getShortDescription());
                stepValues.put(StepEntry.COLUMN_STEP_ID, step.getId());
                stepValues.put(StepEntry.COLUMN_THUMBNAIL, step.getThumbnailURL());
                stepValues.put(StepEntry.COLUMN_VIDEO, step.getVideoURL());
                stepContentValuesArrayList.add(stepValues);
            }
            ArrayList<Ingredient> ingredients = recipe.getIngredients();
            for (Ingredient ingredient : ingredients) {
                ContentValues ingredientValues = new ContentValues();
                ingredientValues.put(IngredientEntry.COLUMN_INGREDIENT, ingredient.getIngredient());
                ingredientValues.put(IngredientEntry.COLUMN_QUANTITY, ingredient.getQuantity());
                ingredientValues.put(IngredientEntry.COLUMN_MEASURE, ingredient.getMeasure());
                ingredientContentValuesArrayList.add(ingredientValues);
            }
            context.getContentResolver().insert(RecipeEntry.CONTENT_URI, recipeValues);
            context.getContentResolver().bulkInsert(
                    StepEntry.getContentUri(recipeId),
                    stepContentValuesArrayList.toArray(new ContentValues[]{}));
            context.getContentResolver().bulkInsert(
                    IngredientEntry.getContentUri(recipeId),
                    ingredientContentValuesArrayList.toArray(new ContentValues[]{}));

        }
    }
}
