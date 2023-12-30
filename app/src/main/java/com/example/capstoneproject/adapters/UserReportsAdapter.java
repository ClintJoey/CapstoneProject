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

import java.util.ArrayList;
import java.util.List;

public class UserReportsAdapter extends RecyclerView.Adapter<UserReportsAdapter.UserReportsViewHolder> {
    Context context;
    private List<ReportModel> reportModels;

    public UserReportsAdapter(Context context, List<ReportModel> reportModels) {
        this.context = context;
        this.reportModels = reportModels;
    }

    @NonNull
    @Override
    public UserReportsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_report_layout, parent, false);
        return new UserReportsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserReportsViewHolder holder, int position) {
        String gmelina = String.valueOf(reportModels.get(position).getGmelina());
        String balinghoy = String.valueOf(reportModels.get(position).getBalinghoy());
        String hagunoy = String.valueOf(reportModels.get(position).getHagunoy());
        String monggo = String.valueOf(reportModels.get(position).getMonggo());
        String talahib = String.valueOf(reportModels.get(position).getTalahib());
        String ipil = String.valueOf(reportModels.get(position).getIpil_ipil());
        String patani = String.valueOf(reportModels.get(position).getPatani());

        holder.gmelinaCount.setText(gmelina);
        holder.balinghoyCount.setText(balinghoy);
        holder.hagunoyCount.setText(hagunoy);
        holder.monggoCount.setText(monggo);
        holder.talahibCount.setText(talahib);
        holder.ipilCount.setText(ipil);
        holder.pataniCount.setText(patani);

        holder.userReportDate.setText(reportModels.get(position).getDate());
        holder.userReportTime.setText(reportModels.get(position).getTime());
    }

    @Override
    public int getItemCount() {
        return reportModels.size();
    }

    public void filterLists(ArrayList<ReportModel> array) {
        reportModels = array;
        notifyDataSetChanged();
    }

    class UserReportsViewHolder extends RecyclerView.ViewHolder {
        final private TextView gmelinaCount, balinghoyCount, hagunoyCount, monggoCount, talahibCount, ipilCount, pataniCount, userReportDate, userReportTime;

        public UserReportsViewHolder(@NonNull View itemView) {
            super(itemView);

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
