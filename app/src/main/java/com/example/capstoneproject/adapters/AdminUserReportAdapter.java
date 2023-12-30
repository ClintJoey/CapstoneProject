package com.example.capstoneproject.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.capstoneproject.R;
import com.example.capstoneproject.models.ReportModel;
import com.example.capstoneproject.models.UserAccountModel;

import java.util.ArrayList;

public class AdminUserReportAdapter extends RecyclerView.Adapter<AdminUserReportAdapter.AdminUserReportViewHolder>{
    Context context;
    ArrayList<UserAccountModel> usersArray;
    ArrayList<ReportModel> userReportsArray;

    public AdminUserReportAdapter(Context context, ArrayList<UserAccountModel> usersArray, ArrayList<ReportModel> userReportsArray) {
        this.context = context;
        this.usersArray = usersArray;
        this.userReportsArray = userReportsArray;
    }

    @NonNull
    @Override
    public AdminUserReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_user_report_layout, parent, false);
        return new AdminUserReportViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminUserReportViewHolder holder, int position) {
        String name = "[deleted user] plant reports";
        for (UserAccountModel user: usersArray) {
            if (userReportsArray.get(position).getUserUid().equals(user.userUid)) {
                name = user.firstname + "'s plant reports";
            }
        }
        // init item values
        holder.userFirstname.setText(name);
        holder.gmelinaCount.setText(String.valueOf(userReportsArray.get(position).getGmelina()));
        holder.balinghoyCount.setText(String.valueOf(userReportsArray.get(position).getBalinghoy()));
        holder.hagunoyCount.setText(String.valueOf(userReportsArray.get(position).getHagunoy()));
        holder.monggoCount.setText(String.valueOf(userReportsArray.get(position).getMonggo()));
        holder.talahibCount.setText(String.valueOf(userReportsArray.get(position).getTalahib()));
        holder.ipilCount.setText(String.valueOf(userReportsArray.get(position).getIpil_ipil()));
        holder.pataniCount.setText(String.valueOf(userReportsArray.get(position).getPatani()));
        holder.userReportDate.setText(userReportsArray.get(position).getDate());
        holder.userReportTime.setText(userReportsArray.get(position).getTime());

    }

    @Override
    public int getItemCount() {
        return userReportsArray.size();
    }

    public void filterLists(ArrayList<ReportModel> array) {
        userReportsArray = array;
        notifyDataSetChanged();
    }

    class AdminUserReportViewHolder extends RecyclerView.ViewHolder {
        final private TextView userFirstname, gmelinaCount, balinghoyCount, hagunoyCount, monggoCount, talahibCount,
                ipilCount, pataniCount, userReportDate, userReportTime;
        public AdminUserReportViewHolder(@NonNull View itemView) {
            super(itemView);
            userFirstname = itemView.findViewById(R.id.userFirstname);
            gmelinaCount = itemView.findViewById(R.id.gmelinaCount);
            balinghoyCount = itemView.findViewById(R.id.balinghoyCount);
            hagunoyCount = itemView.findViewById(R.id.hagunoyCount);
            monggoCount = itemView.findViewById(R.id.monggoCount);
            talahibCount = itemView.findViewById(R.id.talahibCount);
            ipilCount = itemView.findViewById(R.id.ipilCount);
            pataniCount = itemView.findViewById(R.id.pataniCount);
            userReportDate = itemView.findViewById(R.id.userReportDate);
            userReportTime = itemView.findViewById(R.id.userReportTime);
        }
    }
}
