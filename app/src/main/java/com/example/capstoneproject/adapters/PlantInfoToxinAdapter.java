package com.example.capstoneproject.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.capstoneproject.R;
import com.example.capstoneproject.models.ToxinModel;

import java.util.ArrayList;

public class PlantInfoToxinAdapter extends RecyclerView.Adapter<PlantInfoToxinAdapter.PlantInfoToxinViewHolder>{
    Context context;
    private ArrayList<ToxinModel> plantToxinsArray;

    public PlantInfoToxinAdapter(Context context, ArrayList<ToxinModel> plantToxinsArray) {
        this.context = context;
        this.plantToxinsArray = plantToxinsArray;
    }

    @NonNull
    @Override
    public PlantInfoToxinViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.plant_info_toxin_item, parent, false);
        return new PlantInfoToxinViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlantInfoToxinViewHolder holder, int position) {
        holder.plantInfoToxinName.setText(plantToxinsArray.get(position).getToxinName());
        holder.plantInfoAmount.setText(plantToxinsArray.get(position).getToxinConsumptionLevel());
        if (plantToxinsArray.get(position).getToxinConsumptionLevel().equals("Low")) {
            holder.plantInfoAmount.setTextColor(ContextCompat.getColor(context, R.color.dangerColor));
        } else if (plantToxinsArray.get(position).getToxinConsumptionLevel().equals("Average")) {
            holder.plantInfoAmount.setTextColor(ContextCompat.getColor(context, R.color.orangeColor));
        } else {
            holder.plantInfoAmount.setTextColor(ContextCompat.getColor(context, R.color.midGreen));
        }
        holder.plantInfoDescription.setText(plantToxinsArray.get(position).getToxinDesc());
        holder.plantInfoEffects.setText(plantToxinsArray.get(position).getToxinEffects());
    }

    @Override
    public int getItemCount() {
        return plantToxinsArray.size();
    }

    class PlantInfoToxinViewHolder extends RecyclerView.ViewHolder {
        private TextView plantInfoToxinName, plantInfoAmount, plantInfoDescription, plantInfoEffects;
        public PlantInfoToxinViewHolder(@NonNull View itemView) {
            super(itemView);
            plantInfoToxinName = itemView.findViewById(R.id.plantInfoToxinName);
            plantInfoAmount = itemView.findViewById(R.id.plantInfoAmount);
            plantInfoDescription = itemView.findViewById(R.id.plantInfoDescription);
            plantInfoEffects = itemView.findViewById(R.id.plantInfoEffects);
        }
    }

}
