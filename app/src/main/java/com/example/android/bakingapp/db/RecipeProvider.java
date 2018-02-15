package com.example.android.bakingapp.db;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import static com.example.android.bakingapp.db.RecipeContract.CONTENT_AUTHORITY;
import static com.example.android.bakingapp.db.RecipeContract.IngredientEntry;
import static com.example.android.bakingapp.db.RecipeContract.PATH_INGREDIENT;
import static com.example.android.bakingapp.db.RecipeContract.PATH_RECIPE;
import static com.example.android.bakingapp.db.RecipeContract.PATH_STEP;
import static com.example.android.bakingapp.db.RecipeContract.RecipeEntry;
import static com.example.android.bakingapp.db.RecipeContract.StepEntry;

public class RecipeProvider extends ContentProvider {

    private RecipeDbHelper mDbHelper;
    private static final int CODE_RECIPE = 100;
    private static final int CODE_STEPS = 101;
    private static final int CODE_INGREDIENTS = 102;
    private static UriMatcher sUriMatcher = buildUriMatcher();

    private static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(CONTENT_AUTHORITY, PATH_RECIPE, CODE_RECIPE);
        uriMatcher.addURI(CONTENT_AUTHORITY,
                PATH_RECIPE + "/#/" + PATH_STEP, CODE_STEPS);
        uriMatcher.addURI(CONTENT_AUTHORITY,
                PATH_RECIPE + "/#/" + PATH_INGREDIENT, CODE_INGREDIENTS);
        return uriMatcher;
    }


    @Override
    public boolean onCreate() {
        mDbHelper = new RecipeDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            case CODE_RECIPE:
                retCursor = db.query(RecipeEntry.TABLE_NAME, null, null, null, null, null, null);
                break;
            case CODE_STEPS:
                selection = "recipe_id=?";
                selectionArgs = new String[]{uri.getPathSegments().get(1)};
                retCursor = db.query(StepEntry.TABLE_NAME, null, selection, selectionArgs, null, null, null);
                break;
            case CODE_INGREDIENTS:
                selection = "recipe_id=?";
                selectionArgs = new String[]{uri.getPathSegments().get(1)};
                retCursor = db.query(IngredientEntry.TABLE_NAME, null, selection, selectionArgs, null, null, null);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri " + uri);
        }
        return retCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int entries = 0;
        int recipeId;
        switch (sUriMatcher.match(uri)) {
            case CODE_RECIPE:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(RecipeEntry.TABLE_NAME, null, value);
                        if (_id != -1)
                            entries++;
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                break;
            case CODE_STEPS:
                db.beginTransaction();
                try {
                    recipeId = Integer.parseInt(uri.getPathSegments().get(1));
                    for (ContentValues value : values) {
                        value.put(StepEntry.COLUMN_RECIPE_ID, recipeId);
                        long _id = db.insert(StepEntry.TABLE_NAME, null, value);
                        if (_id != -1)
                            entries++;
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                break;
            case CODE_INGREDIENTS:
                db.beginTransaction();
                try {
                    recipeId = Integer.parseInt(uri.getPathSegments().get(1));
                    for (ContentValues value : values) {
                        value.put(IngredientEntry.COLUMN_RECIPE_ID, recipeId);
                        long _id = db.insert(IngredientEntry.TABLE_NAME, null, value);
                        if (_id != -1)
                            entries++;
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri " + uri);
        }
        return entries;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int recipeId;
        long rowId;
        Uri returnUri = RecipeEntry.CONTENT_URI;
        if (values == null) return null;
        switch (sUriMatcher.match(uri)) {
            case CODE_RECIPE:
                rowId = db.insert(RecipeEntry.TABLE_NAME, null, values);
                returnUri = returnUri.buildUpon().appendPath(String.valueOf(rowId)).build();
                break;
            case CODE_STEPS:
                recipeId = Integer.parseInt(uri.getPathSegments().get(1));
                values.put(StepEntry.COLUMN_RECIPE_ID, recipeId);
                rowId = db.insert(StepEntry.TABLE_NAME, null, values);
                returnUri = returnUri.buildUpon().appendPath(String.valueOf(recipeId))
                        .appendPath(RecipeContract.PATH_STEP).appendPath(String.valueOf(rowId)).build();
                break;
            case CODE_INGREDIENTS:
                recipeId = Integer.parseInt(uri.getPathSegments().get(1));
                values.put(IngredientEntry.COLUMN_RECIPE_ID, recipeId);
                rowId = db.insert(IngredientEntry.TABLE_NAME, null, values);
                returnUri = returnUri.buildUpon().appendPath(String.valueOf(recipeId))
                        .appendPath(RecipeContract.PATH_INGREDIENT).appendPath(String.valueOf(rowId)).build();
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri " + uri);
        }
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        throw new UnsupportedOperationException("Unknown uri " + uri);
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        throw new UnsupportedOperationException("Unknown uri " + uri);
    }
}
