package com.example.android.bakingapp.utils;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.bakingapp.R;
import com.example.android.bakingapp.StepAdapter;
import com.example.android.bakingapp.data.Ingredient;

import java.util.ArrayList;

public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.IngredientAdapterViewHolder> {

    private ArrayList<Ingredient> ingredients;

    public IngredientAdapter(ArrayList<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    @Override
    public IngredientAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View rootView = inflater.inflate(R.layout.ingredient_list_layout, parent, false);
        return new IngredientAdapterViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(IngredientAdapterViewHolder holder, int position) {
        Ingredient ingredient = ingredients.get(position);
        holder.mIngredientText.setText(String.format("%s %s %s", ingredient.getQuantity(),
                ingredient.getMeasure(),
                ingredient.getIngredient()));
    }

    @Override
    public int getItemCount() {
        if (ingredients == null) return 0;
        return ingredients.size();
    }

    class IngredientAdapterViewHolder extends RecyclerView.ViewHolder {
        private TextView mIngredientText;

        IngredientAdapterViewHolder(View itemView) {
            super(itemView);
            mIngredientText = itemView.findViewById(R.id.tv_ingredient_text);
        }
    }

}
