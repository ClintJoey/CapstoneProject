package com.example.capstoneproject.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.capstoneproject.R;
import com.example.capstoneproject.models.BeneficialPlantModel;

import java.util.ArrayList;

public class UserBeneficialPlantAdapter extends RecyclerView.Adapter<UserBeneficialPlantAdapter.UserBeneficialPlantViewHolder> {
    Context context;
    ArrayList<BeneficialPlantModel> beneficialPlantArrays;
    onItemClickListener listener;
    public interface onItemClickListener {
        void onItemClick(int position);
    }

    public UserBeneficialPlantAdapter(Context context, ArrayList<BeneficialPlantModel> beneficialPlantArrays) {
        this.context = context;
        this.beneficialPlantArrays = beneficialPlantArrays;
    }
    public void setOnItemClickListener(onItemClickListener clickListener) {
        listener = clickListener;
    }

    @NonNull
    @Override
    public UserBeneficialPlantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_beneficial_plant_layout, parent, false);
        return new UserBeneficialPlantViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull UserBeneficialPlantViewHolder holder, int position) {
        Glide.with(context).load(beneficialPlantArrays.get(position).getPlantImages().get(0)).into(holder.plantImage);
        holder.plantName.setText(beneficialPlantArrays.get(position).getPlantName());

        int nutrientsCount = beneficialPlantArrays.get(position).getPlantNutrients().size();

        if (nutrientsCount < 1) {
            holder.plantNutrients.setText("Contain " + nutrientsCount + " nutrient");
        } else {
            holder.plantNutrients.setText("Contain " + nutrientsCount + " nutrients");
        }
    }

    @Override
    public int getItemCount() {
        return beneficialPlantArrays.size();
    }
    public void searchLists(ArrayList<BeneficialPlantModel> array) {
        beneficialPlantArrays = array;
        notifyDataSetChanged();
    }
    class UserBeneficialPlantViewHolder extends RecyclerView.ViewHolder {
        CardView beneficialPlantItem;
        ImageView plantImage;
        TextView plantName, plantNutrients;
        public UserBeneficialPlantViewHolder(@NonNull View itemView, onItemClickListener listener) {
            super(itemView);
            beneficialPlantItem = itemView.findViewById(R.id.beneficialPlantItem);
            plantImage = itemView.findViewById(R.id.plantImage);
            plantName = itemView.findViewById(R.id.plantName);
            plantNutrients = itemView.findViewById(R.id.plantNutrients);

            beneficialPlantItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(getAdapterPosition());
                }
            });
        }
    }
}
