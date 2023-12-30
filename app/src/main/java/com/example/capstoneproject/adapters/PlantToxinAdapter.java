package com.example.capstoneproject.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.capstoneproject.R;

import java.util.ArrayList;

public class PlantToxinAdapter extends RecyclerView.Adapter<PlantToxinAdapter.PlantToxinViewHolder> {
    Context context;
    ArrayList<String> plantToxinsArray;
    private onPlantToxinListener listener;
    public interface onPlantToxinListener {
        void onDeleteClick(int position);
    }
    public void setOnItemClickListener(onPlantToxinListener clickListener) {
        listener = clickListener;
    }
    public PlantToxinAdapter(Context context, ArrayList<String> plantToxinsArray) {
        this.context = context;
        this.plantToxinsArray = plantToxinsArray;
    }

    @NonNull
    @Override
    public PlantToxinViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.harmful_plant_toxin_item, parent, false);
        return new PlantToxinViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull PlantToxinViewHolder holder, int position) {
        holder.harmfulPlantToxinName.setText(plantToxinsArray.get(position));
    }

    @Override
    public int getItemCount() {
        return plantToxinsArray.size();
    }

    class PlantToxinViewHolder extends RecyclerView.ViewHolder {
        private final TextView harmfulPlantToxinName;
        private ImageView deletePlantToxinBtn;

        public PlantToxinViewHolder(@NonNull View itemView, onPlantToxinListener listener) {
            super(itemView);
            harmfulPlantToxinName = itemView.findViewById(R.id.harmfulPlantToxinName);
            deletePlantToxinBtn = itemView.findViewById(R.id.deletePlantToxinBtn);

            deletePlantToxinBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onDeleteClick(getAdapterPosition());
                }
            });
        }
    }
}
