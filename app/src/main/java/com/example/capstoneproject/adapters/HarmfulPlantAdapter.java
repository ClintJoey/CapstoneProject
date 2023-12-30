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
import com.example.capstoneproject.models.HarmfulPlantModel;

import java.util.ArrayList;

public class HarmfulPlantAdapter extends RecyclerView.Adapter<HarmfulPlantAdapter.HarmfulPlantViewHolder>{
    Context context;
    ArrayList<HarmfulPlantModel> harmfulPlantArrays;
    private onHarmfulPlantListener listener;
    public interface onHarmfulPlantListener {
        void onItemClick(int position);
        void onDeleteClick(int position);
    }

    public HarmfulPlantAdapter(Context context, ArrayList<HarmfulPlantModel> harmfulPlantArrays) {
        this.context = context;
        this.harmfulPlantArrays = harmfulPlantArrays;
    }

    public void setOnItemClickListener(onHarmfulPlantListener clickListener) {
        listener = clickListener;
    }
    @NonNull
    @Override
    public HarmfulPlantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.harmful_plant_item, parent, false);
        return new HarmfulPlantViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull HarmfulPlantViewHolder holder, int position) {
        holder.harmfulPlantName.setText(harmfulPlantArrays.get(position).getPlantName());
    }

    @Override
    public int getItemCount() {
        return harmfulPlantArrays.size();
    }

    public void searchLists(ArrayList<HarmfulPlantModel> array) {
        harmfulPlantArrays = array;
        notifyDataSetChanged();
    }

    class HarmfulPlantViewHolder extends RecyclerView.ViewHolder {
        final private TextView harmfulPlantName;
        private ImageView deleteHarmfulPlantBtn;
        private CardView harmfulPlantItem;
        public HarmfulPlantViewHolder(@NonNull View itemView, onHarmfulPlantListener listener) {
            super(itemView);
            harmfulPlantItem = itemView.findViewById(R.id.harmfulPlantItem);
            harmfulPlantName = itemView.findViewById(R.id.harmfulPlantName);
            deleteHarmfulPlantBtn = itemView.findViewById(R.id.deleteHarmfulPlantBtn);

            harmfulPlantItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(getAdapterPosition());
                }
            });
            deleteHarmfulPlantBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onDeleteClick(getAdapterPosition());
                }
            });
        }
    }
}
