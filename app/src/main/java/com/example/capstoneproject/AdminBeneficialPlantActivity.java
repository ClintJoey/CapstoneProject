package com.example.capstoneproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.capstoneproject.adapters.PlantInfoNutrientAdapter;
import com.example.capstoneproject.models.BeneficialPlantModel;
import com.example.capstoneproject.models.NutrientModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class AdminBeneficialPlantActivity extends AppCompatActivity {
    FirebaseDatabase database;
    DatabaseReference beneficialPlantsRef;
    DatabaseReference nutrientsRef;
    private ImageSlider plantImageSlider;
    private TextView toolBarName, viewBeneficialPlantName, viewBeneficialPlantCommonNames, viewBeneficialPlantDesc, viewBeneficialPlantAbundantLocations;
    private RecyclerView viewBeneficialPlantNutrientsRec;
    private ImageView back, editBeneficialPlantBtn;
    private ArrayList<NutrientModel> plantNutrientsArray = new ArrayList<>();
    private BeneficialPlantModel plantModel;
    String fetchedPlantName = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_beneficial_plant);

        database = FirebaseDatabase.getInstance();
        beneficialPlantsRef = database.getReference("Beneficial Plants");
        nutrientsRef = database.getReference("Nutrients");

        plantImageSlider = findViewById(R.id.plantImageSlider);
        toolBarName = findViewById(R.id.toolBarName);
        viewBeneficialPlantName = findViewById(R.id.viewBeneficialPlantName);
        viewBeneficialPlantCommonNames = findViewById(R.id.viewBeneficialPlantCommonNames);
        viewBeneficialPlantDesc = findViewById(R.id.viewBeneficialPlantDesc);
        viewBeneficialPlantAbundantLocations = findViewById(R.id.viewBeneficialPlantAbundantLocations);
        viewBeneficialPlantNutrientsRec = findViewById(R.id.viewBeneficialPlantNutrientsRec);

        back = findViewById(R.id.back);
        editBeneficialPlantBtn = findViewById(R.id.editBeneficialPlantBtn);

        fetchIntents();

        fetchPlantInfo();

        toolBarName.setText(fetchedPlantName);

        editBeneficialPlantBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminBeneficialPlantActivity.this, UpdateBeneficialPlantActivity.class);
                intent.putExtra("plantModel", plantModel);
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
        fetchedPlantName = intent.getStringExtra("plantName");
    }
    private void fetchPlantInfo() {
        beneficialPlantsRef.child(fetchedPlantName).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                plantModel = snapshot.getValue(BeneficialPlantModel.class);
                setUIValues(plantModel);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AdminBeneficialPlantActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void setUIValues(BeneficialPlantModel beneficialPlant) {
        // Image slider
        ArrayList<SlideModel> imageModels = new ArrayList<>();
        // add the images from plant images
        for (String image: beneficialPlant.getPlantImages()) {
            if (image != null) {
                 imageModels.add(new SlideModel(image, ScaleTypes.CENTER_CROP));
            }
        }
        plantImageSlider.setImageList(imageModels, ScaleTypes.CENTER_CROP);
        viewBeneficialPlantName.setText(beneficialPlant.getPlantName());
        viewBeneficialPlantCommonNames.setText(beneficialPlant.getPlantCommonNames());
        viewBeneficialPlantDesc.setText(beneficialPlant.getPlantDesc());
        viewBeneficialPlantAbundantLocations.setText(beneficialPlant.getPlantAbundantLocations());

        viewBeneficialPlantNutrientsRec.setLayoutManager(new LinearLayoutManager(AdminBeneficialPlantActivity.this));
        PlantInfoNutrientAdapter adapter = new PlantInfoNutrientAdapter(AdminBeneficialPlantActivity.this, plantNutrientsArray, plantModel.getPlantNutrientsAmount());
        viewBeneficialPlantNutrientsRec.setAdapter(adapter);

        for (String key: plantModel.getPlantNutrients()) {
            nutrientsRef.child(key).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                @Override
                public void onSuccess(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        NutrientModel model = dataSnapshot.getValue(NutrientModel.class);
                        plantNutrientsArray.add(model);
                    }
                    adapter.notifyDataSetChanged();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(AdminBeneficialPlantActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}