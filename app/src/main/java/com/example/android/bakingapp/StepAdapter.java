package com.example.android.bakingapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.bakingapp.data.Step;

import java.util.ArrayList;

public class StepAdapter extends RecyclerView.Adapter<StepAdapter.StepAdapterViewHolder> {

    private StepAdapterOnClickHandler mClickHandler;
    private ArrayList<Step> mSteps;

    StepAdapter(StepAdapterOnClickHandler clickHandler, ArrayList<Step> steps){
        this.mClickHandler = clickHandler;
        this.mSteps = steps;
    }

    interface StepAdapterOnClickHandler{
        void onClick(Step step);
    }
    @Override
    public StepAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View rootView = inflater.inflate(R.layout.step_list_layout, parent, false);
        return new StepAdapterViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(StepAdapterViewHolder holder, int position) {
        Step step = mSteps.get(position);
        holder.mStepShortDescTextView.setText(step.getShortDescription());
    }

    @Override
    public int getItemCount() {
        if(mSteps == null) return 0;
        return mSteps.size();
    }

    class StepAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView mStepShortDescTextView;

        StepAdapterViewHolder(View itemView) {
            super(itemView);
            mStepShortDescTextView = itemView.findViewById(R.id.tv_step_short_desc);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mClickHandler.onClick(mSteps.get(adapterPosition));
        }
    }
}
