package com.example.android.bakingapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.bakingapp.data.Recipe;

import java.util.ArrayList;


public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeAdapterViewHolder> {
    private ArrayList<Recipe> mRecipes;
    private RecipeAdapterOnClickHandler mClickHandler;

    RecipeAdapter(RecipeAdapterOnClickHandler clickHandler){
        this.mClickHandler = clickHandler;
    }

    public interface RecipeAdapterOnClickHandler{
        void onClick(Recipe recipe);
    }

    @Override
    public RecipeAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View rootView = inflater.inflate(R.layout.recipe_list_layout, parent, false);
        return new RecipeAdapterViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(RecipeAdapterViewHolder holder, int position) {
        Recipe recipe = mRecipes.get(position);
        holder.mRecipeNameTextView.setText(recipe.getName());
    }

    void swapRecipeList(ArrayList<Recipe> recipes){
        this.mRecipes = recipes;
        notifyDataSetChanged();
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        if(mRecipes == null) return 0;
        return mRecipes.size();
    }

    class RecipeAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView mRecipeNameTextView;

        RecipeAdapterViewHolder(View itemView) {
            super(itemView);
            mRecipeNameTextView =  itemView.findViewById(R.id.tv_recipe_name);
            itemView.setOnClickListener(this);
        }

        /**
         * Called when a view has been clicked.
         *
         * @param v The view that was clicked.
         */
        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mClickHandler.onClick(mRecipes.get(adapterPosition));
        }
    }
}
