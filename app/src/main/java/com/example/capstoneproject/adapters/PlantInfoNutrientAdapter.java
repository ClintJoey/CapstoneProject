package com.example.capstoneproject.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.capstoneproject.R;
import com.example.capstoneproject.models.NutrientModel;

import java.util.ArrayList;

public class PlantInfoNutrientAdapter extends RecyclerView.Adapter<PlantInfoNutrientAdapter.PlantNutrientViewHolder> {
    Context context;
    ArrayList<NutrientModel> nutrientsArray;
    ArrayList<String> plantNutrientsAmount;

    public PlantInfoNutrientAdapter(Context context, ArrayList<NutrientModel> nutrientsArray, ArrayList<String> plantNutrientsAmount) {
        this.context = context;
        this.nutrientsArray = nutrientsArray;
        this.plantNutrientsAmount = plantNutrientsAmount;
    }

    @NonNull
    @Override
    public PlantNutrientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.plant_info_nutrient_layout, parent, false);
        return new PlantNutrientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlantNutrientViewHolder holder, int position) {
        holder.plantInfoNutrientName.setText(nutrientsArray.get(position).getNutrientName());
        holder.plantNutrientAmount.setText(plantNutrientsAmount.get(position));
        holder.plantNutrientInfoDescription.setText(nutrientsArray.get(position).getNutrientDesc());
        holder.plantNutrientInfoBenefits.setText(nutrientsArray.get(position).getNutrientBenefits());
    }

    @Override
    public int getItemCount() {
        return nutrientsArray.size();
    }

    class PlantNutrientViewHolder extends RecyclerView.ViewHolder {
        TextView plantInfoNutrientName, plantNutrientAmount, plantNutrientInfoDescription, plantNutrientInfoBenefits;
        public PlantNutrientViewHolder(@NonNull View itemView) {
            super(itemView);
            plantInfoNutrientName = itemView.findViewById(R.id.plantInfoNutrientName);
            plantNutrientAmount = itemView.findViewById(R.id.plantNutrientAmount);
            plantNutrientInfoDescription = itemView.findViewById(R.id.plantNutrientInfoDescription);
            plantNutrientInfoBenefits = itemView.findViewById(R.id.plantNutrientInfoBenefits);
        }
    }
}
