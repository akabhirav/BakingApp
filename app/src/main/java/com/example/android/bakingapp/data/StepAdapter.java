package com.example.android.bakingapp.data;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.bakingapp.R;
import com.example.android.bakingapp.db.RecipeContract.StepEntry;

public class StepAdapter extends RecyclerView.Adapter<StepAdapter.StepAdapterViewHolder> {

    private StepAdapterOnClickHandler mClickHandler;
    private Cursor mSteps;
    private Context mContext;
    private int selectedPosition;
    private boolean selectableList;

    public StepAdapter(StepAdapterOnClickHandler clickHandler, boolean selectableList) {
        this.mClickHandler = clickHandler;
        this.selectableList = selectableList;
    }

    public void swapCursor(Cursor data) {
        mSteps = data;
        notifyDataSetChanged();
    }

    public interface StepAdapterOnClickHandler {
        void onClick(int position);
    }

    @Override
    public StepAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View rootView = inflater.inflate(R.layout.step_list_layout, parent, false);
        return new StepAdapterViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(StepAdapterViewHolder holder, int position) {
        mSteps.moveToPosition(position);
        if (selectableList) {
            if (selectedPosition == position) {
                holder.itemView.setBackgroundColor(mContext.getResources().getColor(R.color.colorPrimaryLight));
                holder.mStepShortDescTextView.setTextColor(mContext.getResources().getColor(android.R.color.white));
            } else {
                holder.itemView.setBackgroundColor(mContext.getResources().getColor(android.R.color.white));
                holder.mStepShortDescTextView.setTextColor(mContext.getResources().getColor(android.R.color.black));
            }
        }
        holder.mStepShortDescTextView.setText(
                mSteps.getString(mSteps.getColumnIndex(StepEntry.COLUMN_DESCRIPTION))
        );
    }

    @Override
    public int getItemCount() {
        if (mSteps == null) return 0;
        return mSteps.getCount();
    }

    class StepAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView mStepShortDescTextView;

        StepAdapterViewHolder(View itemView) {
            super(itemView);
            mStepShortDescTextView = itemView.findViewById(R.id.tv_step_desc);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mClickHandler.onClick(adapterPosition);
            if (selectableList) {
                selectedPosition = adapterPosition;
                notifyDataSetChanged();
            }
        }
    }
}
