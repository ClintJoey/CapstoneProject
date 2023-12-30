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

public class PlantGuideAdapter extends RecyclerView.Adapter<PlantGuideAdapter.PlantGuideViewHolder>{
    Context context;
    ArrayList<String> plantGuidesArray;
    private onPlantGuideListener listener;
    public interface onPlantGuideListener {
        void onDeleteClick(int position);
    }
    public void setOnItemClickListener(onPlantGuideListener clickListener) {
        listener = clickListener;
    }
    public PlantGuideAdapter(Context context, ArrayList<String> plantGuidesArray) {
        this.context = context;
        this.plantGuidesArray = plantGuidesArray;
    }

    @NonNull
    @Override
    public PlantGuideViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.harmful_plant_guide_item, parent, false);
        return new PlantGuideViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull PlantGuideViewHolder holder, int position) {
        holder.plantGuide.setText(plantGuidesArray.get(position));
    }

    @Override
    public int getItemCount() {
        return plantGuidesArray.size();
    }

    class PlantGuideViewHolder extends RecyclerView.ViewHolder {
        final private TextView plantGuide;
        private ImageView deletePlantGuideBtn;
        public PlantGuideViewHolder(@NonNull View itemView, onPlantGuideListener listener) {
            super(itemView);
            plantGuide = itemView.findViewById(R.id.plantGuide);
            deletePlantGuideBtn = itemView.findViewById(R.id.deletePlantGuideBtn);

            deletePlantGuideBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onDeleteClick(getAdapterPosition());
                }
            });
        }
    }
}
