package com.example.capstoneproject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.example.capstoneproject.adapters.AddedReportAdapter;
import com.example.capstoneproject.models.AddedReportModel;
import com.example.capstoneproject.models.UserAccountModel;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class BottomSheetFragment extends BottomSheetDialogFragment {
    private String[] pPlantItems = {"Gmelina", "Balinghoy", "Ipil-ipil", "Hagunoy", "Talahib", "Monggo", "Patani"};
    String plantSelected;
    RecyclerView reportLists;
    private Button reportBtn;
    ArrayList<AddedReportModel> addedReportItems;
    AddedReportAdapter addedReportAdapter;
    private FirebaseAuth auth;
    private FirebaseDatabase database;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bottom_sheet, container, false);
        
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        reportLists = view.findViewById(R.id.reportLists);
        reportBtn = view.findViewById(R.id.reportBtn);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        loadDataFromSharedPref();

        AutoCompleteTextView poisonousPlantsATV = view.findViewById(R.id.poisonousPlants);
        ArrayAdapter<String> poisonousPlantsArray = new ArrayAdapter<>(view.getContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, pPlantItems);

        poisonousPlantsATV.setAdapter(poisonousPlantsArray);

        poisonousPlantsATV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                plantSelected = parent.getItemAtPosition(position).toString();
            }
        });

        view.findViewById(R.id.infoPlant).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (plantSelected != null) {
                    Intent intent = new Intent(view.getContext(), HarmfulPlantInfoActivity.class);
                    intent.putExtra("plantKey", plantSelected);
                    startActivity(intent);
                } else {
                    Toast.makeText(view.getContext(), "Please select a plant", Toast.LENGTH_SHORT).show();
                }
            }
        });

        reportLists.setLayoutManager(new LinearLayoutManager(getContext()));
        addedReportAdapter = new AddedReportAdapter(getContext(), addedReportItems);
        reportLists.setAdapter(addedReportAdapter);
        addedReportAdapter.notifyDataSetChanged();

        view.findViewById(R.id.addReportPlant).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (plantSelected != null) {
                    if (addedReportItems.stream().filter(obj -> obj.getName().equals(plantSelected)).findFirst().isPresent()) {
                        Toast.makeText(view.getContext(), "Plant is already on the lists", Toast.LENGTH_SHORT).show();
                    } else {
                        addedReportItems.add(new AddedReportModel(plantSelected, 1));
                        addedReportAdapter.notifyDataSetChanged();
                        saveToSharedPref();
                    }
                } else {
                    Toast.makeText(view.getContext(), "Please select a plant", Toast.LENGTH_SHORT).show();
                }
            }
        });

        addedReportAdapter.setOnItemClickListener(new AddedReportAdapter.OnItemClickListener() {
            @Override
            public void onIncreaseCount(int position) {
                addedReportItems.get(position).increaseCount();
                addedReportAdapter.notifyItemChanged(position);
                saveToSharedPref();
            }

            @Override
            public void onDecreaseCount(int position) {
                if (addedReportItems.get(position).getCount() == 1) {
                    addedReportItems.remove(position);
                    addedReportAdapter.notifyItemRemoved(position);
                    saveToSharedPref();
                } else {
                    addedReportItems.get(position).decreaseCount();
                    addedReportAdapter.notifyItemChanged(position);
                    saveToSharedPref();
                }
            }

            @Override
            public void onDeleteClick(int position) {
                addedReportItems.remove(position);
                addedReportAdapter.notifyItemRemoved(position);
                saveToSharedPref();
            }
        });

        reportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isFinished = false;
                
                while (!isFinished) {

                    if (!addedReportItems.isEmpty()) {
                        Toast.makeText(view.getContext(), "Sending Report", Toast.LENGTH_SHORT).show();

                        DatabaseReference userRef = database.getReference("testUsers2");
                        DatabaseReference reportRef = database.getReference("testReport");

                        String userKey = auth.getCurrentUser().getUid();
                        String reportKey = reportRef.push().getKey();

                        // send report to user's arraylist reports
                        HashMap<String, Object> newReport = new HashMap<>();

                        // fetch user's info here
                        userRef.child(userKey).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                // insert report id to the user's reports array
                                UserAccountModel userAccountModel = snapshot.getValue(UserAccountModel.class);
                                if (snapshot.exists()) {
                                    ArrayList<String> reportsFromDB = userAccountModel.reports;
                                    int reportsSize = reportsFromDB.size();

                                    newReport.put(String.valueOf(reportsSize), reportKey);
                                    userRef.child(userKey).child("reports").updateChildren(newReport);
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(view.getContext(), "Error: " + error.getMessage().toString(), Toast.LENGTH_SHORT).show();
                            }
                        });
                        uploadReport(reportRef, reportKey, userKey, addedReportItems);
                        Toast.makeText(view.getContext(), "Report sent", Toast.LENGTH_SHORT).show();
                        
                        // delete from sharedPref and dismiss the dialog
                        clearDataFromSharedPref();
                        isFinished = true;
                        dismiss();
                    } else {
                        Toast.makeText(view.getContext(), "Report list is empty", Toast.LENGTH_SHORT).show();
                        isFinished = true;
                    }
                }
                
            }
        });
    }
    private void uploadReport(DatabaseReference reference, String reportKey, String userUid, ArrayList<AddedReportModel> addedReportItems) {

        int gmelinaCount = 0;
        int balinghoyCount = 0;
        int ipilCount = 0;
        int hagunoyCount = 0;
        int talahibCount = 0;
        int monggoCount = 0;
        int pataniCount = 0;

        for (int i = 0; i < addedReportItems.size(); i++) {

            if (addedReportItems.get(i).getName().equals("Gmelina")) {
                gmelinaCount += addedReportItems.get(i).getCount();
            }
            if (addedReportItems.get(i).getName().equals("Balinghoy")) {
                balinghoyCount += addedReportItems.get(i).getCount();
            }
            if (addedReportItems.get(i).getName().equals("Ipil-ipil")) {
                ipilCount += addedReportItems.get(i).getCount();
            }
            if (addedReportItems.get(i).getName().equals("Hagunoy")) {
                hagunoyCount += addedReportItems.get(i).getCount();
            }
            if (addedReportItems.get(i).getName().equals("Talahib")) {
                talahibCount += addedReportItems.get(i).getCount();
            }
            if (addedReportItems.get(i).getName().equals("Monggo")) {
                monggoCount += addedReportItems.get(i).getCount();
            }
            if (addedReportItems.get(i).getName().equals("Patani")) {
                pataniCount += addedReportItems.get(i).getCount();
            }
        }
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/YYYY");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm a");

        Map<String, Object> report = new HashMap<>();
        report.put("reportId", reportKey);
        report.put("userUid", userUid);
        report.put("Gmelina", gmelinaCount);
        report.put("Balinghoy", balinghoyCount);
        report.put("Ipil-ipil", ipilCount);
        report.put("Hagunoy", hagunoyCount);
        report.put("Talahib", talahibCount);
        report.put("Monggo", monggoCount);
        report.put("Patani", pataniCount);
        report.put("date", dateFormat.format(date));
        report.put("time", timeFormat.format(date));

        reference.child(reportKey).setValue(report);
    }
    private void saveToSharedPref() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("shared pref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(addedReportItems);
        editor.putString("report lists", json);
        editor.apply();
    }
    private void loadDataFromSharedPref() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("shared pref", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("report lists", null);
        Type type = new TypeToken<ArrayList<AddedReportModel>>() {}.getType();
        addedReportItems = gson.fromJson(json, type);

        if (addedReportItems == null || addedReportItems.isEmpty()) {
            addedReportItems = new ArrayList<>();
        }
    }

    private void clearDataFromSharedPref() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("shared pref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("report lists");
        editor.apply();
    }
}