package com.example.android.bakingapp.data;

import android.support.v4.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import com.example.android.bakingapp.utils.NetworkUtils;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class RecipeLoader extends AsyncTaskLoader<ArrayList<Recipe>> {
    private static final String TAG = RecipeLoader.class.getName();

    public RecipeLoader(Context context) {
        super(context);
    }

    @Override
    public ArrayList<Recipe> loadInBackground() {
        URL requestUrl = NetworkUtils.buildUrl();
        ArrayList<Recipe> recipes = new ArrayList<>();
        try {
            String jsonResponse = NetworkUtils.getResponseFromHttpUrl(requestUrl);
            recipes = NetworkUtils.extractJSONResponse(jsonResponse);
        } catch (IOException e) {
            Log.e(TAG, "HTTP: Some error during http request");
        }
        return recipes;
    }
}
