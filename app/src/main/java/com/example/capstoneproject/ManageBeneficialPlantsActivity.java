package com.example.capstoneproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.capstoneproject.adapters.AdminBeneficialPlantAdapter;
import com.example.capstoneproject.adapters.AdminNutrientAdapter;
import com.example.capstoneproject.adapters.HarmfulPlantAdapter;
import com.example.capstoneproject.models.BeneficialPlantModel;
import com.example.capstoneproject.models.NutrientModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ManageBeneficialPlantsActivity extends AppCompatActivity {
    FirebaseDatabase database;
    DatabaseReference beneficialPlantsRef;
    private ImageView back;
    private FloatingActionButton addBeneficialPlant;
    private RecyclerView beneficialPlantsRec;
    private SearchView searchBeneficialPlants;
    private ArrayList<BeneficialPlantModel> beneficialPlantsArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_beneficial_plants);
        database = FirebaseDatabase.getInstance();
        beneficialPlantsRef = database.getReference("Beneficial Plants");

        beneficialPlantsRec = findViewById(R.id.beneficialPlantsRec);
        searchBeneficialPlants = findViewById(R.id.searchBeneficialPlants);

        back = findViewById(R.id.back);
        addBeneficialPlant = findViewById(R.id.addBeneficialPlant);

        fetchIntents();

        beneficialPlantsRec.setLayoutManager(new LinearLayoutManager(ManageBeneficialPlantsActivity.this));
        AdminBeneficialPlantAdapter adapter = new AdminBeneficialPlantAdapter(ManageBeneficialPlantsActivity.this, beneficialPlantsArray);
        beneficialPlantsRec.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        adapter.setOnItemClickListener(new AdminBeneficialPlantAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(ManageBeneficialPlantsActivity.this, AdminBeneficialPlantActivity.class);
                intent.putExtra("plantName", beneficialPlantsArray.get(position).getPlantName());
                startActivity(intent);
            }

            @Override
            public void onDeleteClick(int position) {
                openDeleteDialog(adapter, position);
            }
        });

        searchBeneficialPlants.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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

        addBeneficialPlant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManageBeneficialPlantsActivity.this, AddBenefecialPlantActivity.class);
                startActivity(intent);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    private void fetchIntents() {
        Intent intent = getIntent();
        beneficialPlantsArray = (ArrayList<BeneficialPlantModel>) intent.getSerializableExtra("beneficialPlantsArray");
    }
    private void openDeleteDialog(AdminBeneficialPlantAdapter adapter, int position) {
        // pending create a delete dialog
        CardView deleteDialog = findViewById(R.id.deleteDialog);
        View view = LayoutInflater.from(ManageBeneficialPlantsActivity.this).inflate(R.layout.delete_confirmation_dialog, deleteDialog);
        TextView deleteDialogText = view.findViewById(R.id.deleteDialogText);
        TextView cancelDelete = view.findViewById(R.id.cancelDelete);
        TextView confirmDelete = view.findViewById(R.id.confirmDelete);

        AlertDialog.Builder builder = new AlertDialog.Builder(ManageBeneficialPlantsActivity.this);
        builder.setView(view);
        AlertDialog dialog = builder.create();

        String plantName = beneficialPlantsArray.get(position).getPlantName();
        String dialogText = "Are you sure you want to delete " + plantName + "?";
        deleteDialogText.setText(dialogText);

        confirmDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ManageBeneficialPlantsActivity.this, "Deleting " + plantName, Toast.LENGTH_SHORT).show();
                beneficialPlantsRef.child(plantName).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        beneficialPlantsArray.remove(position);
                        adapter.notifyItemRemoved(position);
                        dialog.dismiss();
                        Toast.makeText(ManageBeneficialPlantsActivity.this, plantName + " deleted", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ManageBeneficialPlantsActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        cancelDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        dialog.show();
    }
    private void searchLists(AdminBeneficialPlantAdapter adapter, String text) {
        ArrayList<BeneficialPlantModel> lists = new ArrayList<>();
        for (BeneficialPlantModel plant: beneficialPlantsArray) {
            if (plant.getPlantName().toLowerCase().contains(text)) {
                lists.add(plant);
            }
        }
        adapter.searchLists(lists);
    }
}