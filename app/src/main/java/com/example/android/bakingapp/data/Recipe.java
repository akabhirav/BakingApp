package com.example.android.bakingapp.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Recipe implements Parcelable {
    private int id, servings;
    private String name, image;
    private ArrayList<Ingredient> ingredients;
    private ArrayList<Step> steps;

    public Recipe(int id, int servings, String name, String image, ArrayList<Ingredient> ingredients, ArrayList<Step> steps){
        this.id = id;
        this.servings = servings;
        this.name = name;
        this.image = image;
        this.ingredients = ingredients;
        this.steps = steps;
    }

    public int getId() {
        return id;
    }

    public ArrayList<Ingredient> getIngredients() {
        return ingredients;
    }

    public ArrayList<Step> getSteps() {
        return steps;
    }

    public int getServings() {
        return servings;
    }

    public String getImage() {
        return image;
    }

    public String getName() {
        return name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeTypedList(this.steps);
        dest.writeString(this.name);
        dest.writeString(this.image);
        dest.writeTypedList(this.ingredients);
        dest.writeInt(this.servings);
    }

    protected Recipe(Parcel in) {
        this.id = in.readInt();
        this.steps = in.createTypedArrayList(Step.CREATOR);
        this.name = in.readString();
        this.image = in.readString();
        this.ingredients = in.createTypedArrayList(Ingredient.CREATOR);
        this.servings = in.readInt();
    }

    public static final Parcelable.Creator<Recipe> CREATOR = new Parcelable.Creator<Recipe>() {
        @Override
        public Recipe createFromParcel(Parcel source) {
            return new Recipe(source);
        }

        @Override
        public Recipe[] newArray(int size) {
            return new Recipe[size];
        }
    };
}
