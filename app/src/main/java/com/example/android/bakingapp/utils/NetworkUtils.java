package com.example.android.bakingapp.utils;

import android.content.Context;
import android.graphics.Movie;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.android.bakingapp.data.Ingredient;
import com.example.android.bakingapp.data.Recipe;
import com.example.android.bakingapp.data.Step;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class NetworkUtils {
    private static final String TAG = "NetworkUtils";
    private static final String BAD_RESPONSE = "BAD_RESPONSE";

    /**
     * Method that returns the url for fetching a list of movies from tmdb
     *
     * @return final url
     */
    public static URL buildUrl() {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(Constants.HTTPS).path(Constants.BASE_URL);
        Uri uri = builder.build();
        URL url = null;
        try {
            url = new URL(uri.toString());
        } catch (MalformedURLException e) {
            Log.e(TAG, "MalformedURL: " + e.toString());
        }
        return url;
    }

    /**
     * Method that fetches response from url and returns the response
     *
     * @param url url from which data is to be fetched
     * @return json response from the http request on url
     * @throws IOException exception thrown when error opening connection
     */
    @Nullable
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        String jsonResponse = null;
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setConnectTimeout(5000);
        urlConnection.setReadTimeout(2000);
        try {
            if (urlConnection.getResponseCode() == 200) {
                InputStream in = urlConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                jsonResponse = result.toString();
            } else {
                jsonResponse = BAD_RESPONSE;
                Log.e(TAG, "Bad Response: " + urlConnection.getResponseCode());
            }

        } catch (Exception e) {
            Log.e(TAG, "Some Exception: " + e.toString());
        } finally {
            urlConnection.disconnect();
        }
        return jsonResponse;
    }

    /**
     * Method to extract a list of movies with only id, title, and image url from json
     *
     * @param jsonResponse json string which contains the data
     * @return returns an {@link ArrayList<Recipe>}
     */
    public static ArrayList<Recipe> extractJSONResponse(String jsonResponse) {
        ArrayList<Recipe> recipes = new ArrayList<>();
        try {
            JSONArray response = new JSONArray(jsonResponse);
            for (int i = 0; i < response.length(); i++) {
                JSONObject recipe = response.getJSONObject(i);
                JSONArray stepsJSON = recipe.getJSONArray("steps");
                JSONArray ingredientsJSON = recipe.getJSONArray("ingredients");
                ArrayList<Step> steps = new ArrayList<>();
                ArrayList<Ingredient> ingredients = new ArrayList<>();
                int j;
                for (j = 0; j < stepsJSON.length(); j++){
                    JSONObject step = stepsJSON.getJSONObject(j);
                    int id = step.getInt("id");
                    String shortDescription = step.getString("shortDescription");
                    String description = step.getString("description");
                    String videoURL = step.getString("videoURL");
                    String thumbnailURL = step.getString("thumbnailURL");
                    steps.add(new Step(id, shortDescription, description, videoURL, thumbnailURL));
                }
                for (j = 0; j < ingredientsJSON.length(); j++){
                    JSONObject ingredient = ingredientsJSON.getJSONObject(j);
                    double quantity = ingredient.getDouble("quantity");
                    String measure = ingredient.getString("measure");
                    String ingredientName = ingredient.getString("ingredient");
                    ingredients.add(new Ingredient(quantity, measure, ingredientName));
                }
                int id = recipe.getInt("id");
                String name = recipe.getString("name");
                int servings = recipe.getInt("servings");
                String image = recipe.getString("image");
                recipes.add(new Recipe(id, servings, name, image, ingredients, steps));
            }
        } catch (JSONException e) {
            Log.e(TAG, "JSON Exception: " + e.getMessage());
        }
        return recipes;
    }

    /**
     * @param context context from where it is called
     * @return whether the device is online or not
     */
    public static boolean isOnline(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isConnectedOrConnecting();
        }
        return false;
    }
}