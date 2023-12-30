package com.example.capstoneproject.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.capstoneproject.R;

import java.util.ArrayList;

public class PlantInfoGuideAdapter extends RecyclerView.Adapter<PlantInfoGuideAdapter.PlantInfoGuideViewHolder>{
    Context context;
    private ArrayList<String> plantGuidesArray;

    public PlantInfoGuideAdapter(Context context, ArrayList<String> plantGuidesArray) {
        this.context = context;
        this.plantGuidesArray = plantGuidesArray;
    }

    @NonNull
    @Override
    public PlantInfoGuideViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.plant_info_guide_item, parent, false);
        return new PlantInfoGuideViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlantInfoGuideViewHolder holder, int position) {
        String guide = "\u2022 " + plantGuidesArray.get(position);
        holder.plantInfoGuide.setText(guide);
    }

    @Override
    public int getItemCount() {
        return plantGuidesArray.size();
    }

    class PlantInfoGuideViewHolder extends RecyclerView.ViewHolder {
        private TextView plantInfoGuide;
        public PlantInfoGuideViewHolder(@NonNull View itemView) {
            super(itemView);
            plantInfoGuide = itemView.findViewById(R.id.plantInfoGuide);
        }
    }
}
