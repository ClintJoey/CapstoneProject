package com.example.capstoneproject.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.capstoneproject.R;
import com.example.capstoneproject.models.BeneficialPlantModel;

import java.util.ArrayList;

public class AdminBeneficialPlantAdapter extends RecyclerView.Adapter<AdminBeneficialPlantAdapter.AdminBeneficialPlantViewHolder> {
    Context context;
    ArrayList<BeneficialPlantModel> beneficialPlantsArray;
    public OnItemClickListener listener;

    public AdminBeneficialPlantAdapter(Context context, ArrayList<BeneficialPlantModel> beneficialPlantsArray) {
        this.context = context;
        this.beneficialPlantsArray = beneficialPlantsArray;
    }
    public interface OnItemClickListener {
        void onItemClick(int position);
        void onDeleteClick(int position);
    }
    public void setOnItemClickListener(OnItemClickListener clickListener) {
        listener = clickListener;
    }

    @NonNull
    @Override
    public AdminBeneficialPlantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_beneficial_plant_item, parent, false);
        return new AdminBeneficialPlantViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminBeneficialPlantViewHolder holder, int position) {
        holder.beneficialPlantName.setText(beneficialPlantsArray.get(position).getPlantName());
    }

    @Override
    public int getItemCount() {
        return beneficialPlantsArray.size();
    }
    public void searchLists(ArrayList<BeneficialPlantModel> array) {
        beneficialPlantsArray = array;
        notifyDataSetChanged();
    }

    class AdminBeneficialPlantViewHolder extends RecyclerView.ViewHolder {
        TextView beneficialPlantName;
        ImageView deleteBeneficialPlantBtn;
        CardView beneficialPlantItem;
        public AdminBeneficialPlantViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            beneficialPlantName = itemView.findViewById(R.id.beneficialPlantName);
            deleteBeneficialPlantBtn = itemView.findViewById(R.id.deleteBeneficialPlantBtn);
            beneficialPlantItem = itemView.findViewById(R.id.beneficialPlantItem);

            beneficialPlantItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(getAdapterPosition());
                }
            });

            deleteBeneficialPlantBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onDeleteClick(getAdapterPosition());
                }
            });
        }
    }
}
