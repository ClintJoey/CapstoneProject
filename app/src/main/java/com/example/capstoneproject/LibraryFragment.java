package com.example.capstoneproject;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.capstoneproject.adapters.UserBeneficialPlantAdapter;
import com.example.capstoneproject.models.BeneficialPlantModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class LibraryFragment extends Fragment {
    FirebaseDatabase database;
    DatabaseReference beneficialPlantsRef;
    private SearchView searchPlant;
    private RecyclerView beneficialPlantsRec;
    private LinearLayout isLoading;
    private ArrayList<BeneficialPlantModel> beneficialPlantArrays = new ArrayList<>();
    UserBeneficialPlantAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_library, container, false);

        database = FirebaseDatabase.getInstance();
        beneficialPlantsRef = database.getReference("Beneficial Plants");

        searchPlant = view.findViewById(R.id.searchBeneficialPlants);
        beneficialPlantsRec = view.findViewById(R.id.beneficialPlantsRec);
        isLoading = view.findViewById(R.id.isLoading);

        beneficialPlantsRec.setLayoutManager(new LinearLayoutManager(view.getContext()));
        adapter = new UserBeneficialPlantAdapter(view.getContext(), beneficialPlantArrays);
        beneficialPlantsRec.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        beneficialPlantsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    beneficialPlantArrays.clear();
                    for (DataSnapshot data: snapshot.getChildren()) {
                        BeneficialPlantModel model = data.getValue(BeneficialPlantModel.class);
                        beneficialPlantArrays.add(model);
                    }
                }
                adapter.notifyDataSetChanged();

                // if is finished
                isLoading.setVisibility(View.GONE);
                beneficialPlantsRec.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(view.getContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        adapter.setOnItemClickListener(new UserBeneficialPlantAdapter.onItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(view.getContext(), UserBeneficialPlantInfoAcivity.class);
                intent.putExtra("plantName", beneficialPlantArrays.get(position).getPlantName());
                startActivity(intent);
            }
        });

        searchPlant.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchLists(adapter, newText);
                return true;
            }
        });
        return view;
    }
    private void searchLists(UserBeneficialPlantAdapter adapter, String text) {
        ArrayList<BeneficialPlantModel> lists = new ArrayList<>();
        for (BeneficialPlantModel plant: beneficialPlantArrays) {
            if (plant.getPlantName().toLowerCase().contains(text)) {
                lists.add(plant);
            }
        }
        adapter.searchLists(lists);
    }
}