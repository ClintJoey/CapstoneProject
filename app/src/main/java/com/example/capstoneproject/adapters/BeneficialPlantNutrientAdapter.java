package com.example.capstoneproject.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.capstoneproject.R;

import java.util.ArrayList;

public class BeneficialPlantNutrientAdapter extends RecyclerView.Adapter<BeneficialPlantNutrientAdapter.BeneficialPlantNutrientViewHolder> {
    Context context;
    ArrayList<String> plantNutrients;
    private onPlantNutrientListener listener;
    public interface onPlantNutrientListener {
        void onDeleteClick(int position);
    }
    public void setOnItemClickListener (onPlantNutrientListener clickListener) {
        listener = clickListener;
    }

    public BeneficialPlantNutrientAdapter(Context context, ArrayList<String> plantNutrients) {
        this.context = context;
        this.plantNutrients = plantNutrients;
    }

    @NonNull
    @Override
    public BeneficialPlantNutrientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.beneficial_plant_nutrient_item, parent, false);
        return new BeneficialPlantNutrientViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull BeneficialPlantNutrientViewHolder holder, int position) {
        holder.nutrientName.setText(plantNutrients.get(position));
    }

    @Override
    public int getItemCount() {
        return plantNutrients.size();
    }

    class BeneficialPlantNutrientViewHolder extends RecyclerView.ViewHolder {
        TextView nutrientName;
        ImageView deleteBeneficialPlantNutrientBtn;
        public BeneficialPlantNutrientViewHolder(@NonNull View itemView, onPlantNutrientListener listener) {
            super(itemView);
            nutrientName = itemView.findViewById(R.id.nutrientName);
            deleteBeneficialPlantNutrientBtn = itemView.findViewById(R.id.deleteBeneficialPlantNutrientBtn);

            deleteBeneficialPlantNutrientBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onDeleteClick(getAdapterPosition());
                }
            });
        }
    }
}
