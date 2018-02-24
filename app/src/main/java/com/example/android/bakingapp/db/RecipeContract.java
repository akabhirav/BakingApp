package com.example.android.bakingapp.db;

import android.net.Uri;
import android.provider.BaseColumns;

public class RecipeContract {
    public static final String CONTENT_AUTHORITY = "com.example.android.bakingapp";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_RECIPE = "recipes";
    public static final String PATH_STEP = "steps";
    public static final String PATH_INGREDIENT = "ingredient";


    public static class RecipeEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_RECIPE)
                .build();
        public static final String TABLE_NAME = "recipes";
        public static final String COLUMN_RECIPE_ID = "recipe_id";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_SERVINGS = "servings";
        public static final String COLUMN_IMAGE = "image";
    }

    public static class StepEntry implements BaseColumns {
        public static Uri getContentUri(int recipeId) {
            return BASE_CONTENT_URI.buildUpon()
                    .appendPath(PATH_RECIPE)
                    .appendPath(String.valueOf(recipeId))
                    .appendPath(PATH_STEP)
                    .build();
        }

        public static final String TABLE_NAME = "steps";
        public static final String COLUMN_STEP_ID = "step_id";
        public static final String COLUMN_RECIPE_ID = "recipe_id";
        public static final String COLUMN_SHORT_DESCRIPTION = "short_description";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_VIDEO = "video";
        public static final String COLUMN_THUMBNAIL = "thumbnail";
    }

    public static class IngredientEntry implements BaseColumns {
        public static Uri getContentUri(int recipeId) {
            return BASE_CONTENT_URI.buildUpon()
                    .appendPath(PATH_RECIPE)
                    .appendPath(String.valueOf(recipeId))
                    .appendPath(PATH_INGREDIENT)
                    .build();
        }

        public static final String TABLE_NAME = "ingredient";
        public static final String COLUMN_RECIPE_ID = "recipe_id";
        public static final String COLUMN_QUANTITY = "quantity";
        public static final String COLUMN_MEASURE = "measure";
        public static final String COLUMN_INGREDIENT = "ingredient";

    }

}
