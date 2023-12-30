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

import com.bumptech.glide.Glide;
import com.example.capstoneproject.R;
import com.example.capstoneproject.models.UserAccountModel;

import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder>{
    Context context;
    ArrayList<UserAccountModel> usersArray;
    private onUserListener listener;
    public interface onUserListener {
        void onItemClick(int position);
        void onDeleteClick(int position);
    }
    public UserAdapter(Context context, ArrayList<UserAccountModel> usersArray) {
        this.context = context;
        this.usersArray = usersArray;
    }
    public void setOnItemClickListener(onUserListener clickListener) {
        listener = clickListener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item_layout, parent, false);
        return new UserViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        Glide.with(context).load(usersArray.get(position).profileImg).into(holder.userProfileImg);
        String completeName = usersArray.get(position).firstname + " " + usersArray.get(position).middlename.substring(0, 1) + ". " + usersArray.get(position).lastname;
        holder.userCompleteName.setText(completeName);
        holder.userRole.setText(usersArray.get(position).role);
    }

    @Override
    public int getItemCount() {
        return usersArray.size();
    }

    public void searchLists(ArrayList<UserAccountModel> array) {
        usersArray = array;
        notifyDataSetChanged();
    }

    class UserViewHolder extends RecyclerView.ViewHolder {
        private CardView userItem;
        private ImageView userProfileImg, deleteUserBtn;
        private TextView userCompleteName, userRole;

        public UserViewHolder(@NonNull View itemView, onUserListener listener) {
            super(itemView);
            userItem = itemView.findViewById(R.id.userItem);
            userProfileImg = itemView.findViewById(R.id.userProfileImg);
            userCompleteName = itemView.findViewById(R.id.userCompleteName);
            userRole = itemView.findViewById(R.id.userRole);
            deleteUserBtn = itemView.findViewById(R.id.deleteUserBtn);

            userItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(getAdapterPosition());
                }
            });

            deleteUserBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onDeleteClick(getAdapterPosition());
                }
            });
        }
    }
}
