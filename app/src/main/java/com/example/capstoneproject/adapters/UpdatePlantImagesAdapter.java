package com.example.capstoneproject.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.capstoneproject.R;

import java.util.ArrayList;

public class UpdatePlantImagesAdapter extends RecyclerView.Adapter<UpdatePlantImagesAdapter.UpdatePlantImagesViewHolder> {
    Context context;
    ArrayList<Uri> uriArrayList;
    onImagesListener listener;
    public interface onImagesListener {
        void onDeleteClick(int position);
    }
    public void setOnItemClickListener (onImagesListener clickListener) {
        listener = clickListener;
    }

    public UpdatePlantImagesAdapter(Context context, ArrayList<Uri> uriArrayList) {
        this.context = context;
        this.uriArrayList = uriArrayList;
    }

    @NonNull
    @Override
    public UpdatePlantImagesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.plant_image_layout, parent, false);
        return new UpdatePlantImagesViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull UpdatePlantImagesViewHolder holder, int position) {
        holder.plantImage.setImageURI(uriArrayList.get(position));
    }

    @Override
    public int getItemCount() {
        return uriArrayList.size();
    }

    class UpdatePlantImagesViewHolder extends RecyclerView.ViewHolder {
        ImageView plantImage, removeImageBtn;
        public UpdatePlantImagesViewHolder(@NonNull View itemView, onImagesListener listener) {
            super(itemView);
            plantImage = itemView.findViewById(R.id.plantImage);
            removeImageBtn = itemView.findViewById(R.id.removeImageBtn);

            removeImageBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onDeleteClick(getAdapterPosition());
                }
            });
        }
    }
}
