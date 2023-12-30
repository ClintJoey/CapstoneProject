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
import com.example.capstoneproject.models.ToxinModel;

import java.util.ArrayList;

public class ToxinsAdapter extends RecyclerView.Adapter<ToxinsAdapter.ToxinViewHolder>{
    Context context;
    private ArrayList<ToxinModel> toxinsArray;
    private onToxinItemClickListener listener;
    public interface onToxinItemClickListener {
        void onItemClick(int position);
        void onEditClick(int position);
        void onDeleteClick(int position);
    }
    public void setOnItemClickListener(onToxinItemClickListener clickListener) {
        listener = clickListener;
    }
    public ToxinsAdapter(Context context, ArrayList<ToxinModel> toxinsArray) {
        this.context = context;
        this.toxinsArray = toxinsArray;
    }

    @NonNull
    @Override
    public ToxinViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.toxin_item_layout, parent, false);
        return new ToxinViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ToxinViewHolder holder, int position) {
        holder.toxinName.setText(toxinsArray.get(position).getToxinName());
    }

    @Override
    public int getItemCount() {
        return toxinsArray.size();
    }

    public void searchLists(ArrayList<ToxinModel> array) {
        toxinsArray = array;
        notifyDataSetChanged();
    }

    class ToxinViewHolder extends RecyclerView.ViewHolder {
        final private TextView toxinName;
        private CardView toxinItem;
        private ImageView editToxinBtn, deleteToxinBtn;
        public ToxinViewHolder(@NonNull View itemView, onToxinItemClickListener listener) {
            super(itemView);
            toxinItem = itemView.findViewById(R.id.toxinItem);
            toxinName = itemView.findViewById(R.id.toxinName);
            editToxinBtn = itemView.findViewById(R.id.editToxinBtn);
            deleteToxinBtn = itemView.findViewById(R.id.deleteToxinBtn);

            toxinItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(getAdapterPosition());
                }
            });
            editToxinBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onEditClick(getAdapterPosition());
                }
            });
            deleteToxinBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onDeleteClick(getAdapterPosition());
                }
            });
        }
    }
}
