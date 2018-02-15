package com.example.android.bakingapp.data;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.bakingapp.R;
import com.example.android.bakingapp.db.RecipeContract;

public class RecipeWidgetAdapter extends RecyclerView.Adapter<RecipeWidgetAdapter.RecipeWidgetAdapterViewHolder> {
    private Cursor mRecipes;
    private RecipeWidgetAdapter.RecipeWidgetAdapterOnClickHandler mClickHandler;

    public RecipeWidgetAdapter(RecipeWidgetAdapter.RecipeWidgetAdapterOnClickHandler clickHandler) {
        this.mClickHandler = clickHandler;
    }

    public interface RecipeWidgetAdapterOnClickHandler {
        void onClick(int recipeId, String recipeName);
    }

    @Override
    public RecipeWidgetAdapter.RecipeWidgetAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context mContext = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View rootView = inflater.inflate(R.layout.recipe_widget_list_layout, parent, false);
        return new RecipeWidgetAdapter.RecipeWidgetAdapterViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(RecipeWidgetAdapter.RecipeWidgetAdapterViewHolder holder, int position) {
        if (mRecipes.moveToPosition(position))
            holder.mRecipeNameTextView.setText(mRecipes.getString(mRecipes.getColumnIndex(RecipeContract.RecipeEntry.COLUMN_NAME)));
    }

    public void swapCursor(Cursor recipes) {
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
        if (mRecipes == null) return 0;
        return mRecipes.getCount();
    }

    class RecipeWidgetAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView mRecipeNameTextView;

        RecipeWidgetAdapterViewHolder(View itemView) {
            super(itemView);
            mRecipeNameTextView = itemView.findViewById(R.id.tv_recipe_name);
            itemView.setOnClickListener(this);
        }

        /**
         * Called when a view has been clicked.
         *
         * @param v The view that was clicked.
         */
        @Override
        public void onClick(View v) {
            mRecipes.moveToPosition(getAdapterPosition());
            mClickHandler.onClick(
                    mRecipes.getInt(mRecipes.getColumnIndex(RecipeContract.RecipeEntry.COLUMN_RECIPE_ID)),
                    mRecipes.getString(mRecipes.getColumnIndex(RecipeContract.RecipeEntry.COLUMN_NAME))
            );
        }
    }
}
