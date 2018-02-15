package com.example.android.bakingapp.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.example.android.bakingapp.db.RecipeContract.*;

public class RecipeDbHelper extends SQLiteOpenHelper {
    private static int DATABASE_VERSION = 1;
    private static String DATABASE_NAME = "recipes.db";

    public RecipeDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + RecipeEntry.TABLE_NAME + "(" +
                RecipeEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                RecipeEntry.COLUMN_RECIPE_ID + " INTEGER UNIQUE NOT NULL, " +
                RecipeEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                RecipeEntry.COLUMN_IMAGE + " TEXT, " +
                RecipeEntry.COLUMN_SERVINGS + " TEXT, " +
                "UNIQUE(" + RecipeEntry.COLUMN_RECIPE_ID + ") ON CONFLICT IGNORE" +
                ")");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + StepEntry.TABLE_NAME + "(" +
                StepEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                StepEntry.COLUMN_STEP_ID + " INTEGER NOT NULL, " +
                StepEntry.COLUMN_RECIPE_ID + " INTEGER NOT NULL, " +
                StepEntry.COLUMN_DESCRIPTION + " TEXT, " +
                StepEntry.COLUMN_SHORT_DESCRIPTION + " TEXT, " +
                StepEntry.COLUMN_VIDEO + " TEXT, " +
                StepEntry.COLUMN_THUMBNAIL + " TEXT" +
                ")");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + IngredientEntry.TABLE_NAME + "(" +
                IngredientEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                StepEntry.COLUMN_RECIPE_ID + " INTEGER NOT NULL, " +
                IngredientEntry.COLUMN_QUANTITY + " REAL, " +
                IngredientEntry.COLUMN_MEASURE + " TEXT, " +
                IngredientEntry.COLUMN_INGREDIENT + " TEXT" +
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}