package com.example.capstoneproject.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.capstoneproject.R;
import com.example.capstoneproject.models.AddedReportModel;

import java.util.ArrayList;

public class AddedReportAdapter extends RecyclerView.Adapter<AddedReportAdapter.AddedReportViewHolder> {

    Context context;
    final private ArrayList<AddedReportModel> addedReportItems;
    private OnItemClickListener listener;

    public interface OnItemClickListener{
        void onIncreaseCount(int position);
        void onDecreaseCount(int position);
        void onDeleteClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener clickListener) {
        listener = clickListener;
    }

    public AddedReportAdapter(Context context, ArrayList<AddedReportModel> addedReportItems) {
        this.context = context;
        this.addedReportItems = addedReportItems;
    }

    @NonNull
    @Override
    public AddedReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_report_layout, parent, false);
        return new AddedReportViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull AddedReportViewHolder holder, int position) {
        holder.reportAddedPlantName.setText(addedReportItems.get(position).getName());
        holder.addReportPlantCount.setText(String.valueOf(addedReportItems.get(position).getCount()));
    }

    @Override
    public int getItemCount() {
        return addedReportItems.size();
    }

    class AddedReportViewHolder extends RecyclerView.ViewHolder {

        final private TextView reportAddedPlantName, addReportPlantCount;
        final private ImageButton removeAddedPlantItemBtn;
        final private ImageView minusBtn, plusBtn;

        public AddedReportViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            reportAddedPlantName = itemView.findViewById(R.id.reportAddedPlantName);
            addReportPlantCount = itemView.findViewById(R.id.addReportPlantCount);
            removeAddedPlantItemBtn = itemView.findViewById(R.id.removeAddedPlantItemBtn);
            minusBtn = itemView.findViewById(R.id.minusBtn);
            plusBtn = itemView.findViewById(R.id.plusBtn);

            minusBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onDecreaseCount(getAdapterPosition());
                }
            });

            plusBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onIncreaseCount(getAdapterPosition());
                }
            });

            removeAddedPlantItemBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onDeleteClick(getAdapterPosition());
                }
            });
        }
    }

}
