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
import com.example.capstoneproject.models.NutrientModel;

import java.util.ArrayList;

public class AdminNutrientAdapter extends RecyclerView.Adapter<AdminNutrientAdapter.AdminNutrientViewHolder> {
    Context context;
    ArrayList<NutrientModel> nutrientsArray;
    onItemClickListener listener;

    public AdminNutrientAdapter(Context context, ArrayList<NutrientModel> nutrientsArray) {
        this.context = context;
        this.nutrientsArray = nutrientsArray;
    }
    public interface onItemClickListener {
        void onItemClick(int position);
        void onEditClick(int position);
        void onDeleteClick(int position);
    }
    public void setOnItemClickListener(onItemClickListener clickListener) {
        listener = clickListener;
    }

    @NonNull
    @Override
    public AdminNutrientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_nutrient_item, parent, false);
        return new AdminNutrientViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminNutrientViewHolder holder, int position) {
        holder.nutrientName.setText(nutrientsArray.get(position).getNutrientName());
    }

    @Override
    public int getItemCount() {
        return nutrientsArray.size();
    }
    public void searchLists(ArrayList<NutrientModel> array) {
        nutrientsArray = array;
        notifyDataSetChanged();
    }

    class AdminNutrientViewHolder extends RecyclerView.ViewHolder {
        CardView nutrientItem;
        TextView nutrientName;
        ImageView editNutrientBtn, deleteNutrientBtn;
        public AdminNutrientViewHolder(@NonNull View itemView, onItemClickListener listener) {
            super(itemView);
            nutrientItem = itemView.findViewById(R.id.nutrientItem);
            nutrientName = itemView.findViewById(R.id.nutrientName);
            editNutrientBtn = itemView.findViewById(R.id.editNutrientBtn);
            deleteNutrientBtn = itemView.findViewById(R.id.deleteNutrientBtn);

            nutrientItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(getAdapterPosition());
                }
            });

            editNutrientBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onEditClick(getAdapterPosition());
                }
            });

            deleteNutrientBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onDeleteClick(getAdapterPosition());
                }
            });
        }
    }

}
