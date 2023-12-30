package com.example.capstoneproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.capstoneproject.adapters.PlantInfoGuideAdapter;
import com.example.capstoneproject.adapters.PlantInfoToxinAdapter;
import com.example.capstoneproject.models.HarmfulPlantModel;
import com.example.capstoneproject.models.ToxinModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AdminHarmfulPlantInfoActivity extends AppCompatActivity {
    FirebaseDatabase database;
    DatabaseReference harmfulPlantsRef, toxinsRef;
    private TextView toolbarHarmfulPlantName, viewHarmfulPlantName, viewHarmfulPlantCommonNames, viewHarmfulPlantDesc, viewHarmfulPlantAbundantLocations,
            viewHarmfulPlantParts;
    private ImageView back, editHarmfulPlantBtn;
    private ImageSlider plantImageSlider;
    private RecyclerView viewHarmfulPlantToxinsRec, viewHarmfulPlantGuidesRec;
    private ArrayList<String> plantToxinsKeys, plantGuides;
    private ArrayList<Uri> uriArrayList = new ArrayList<>();
    private ArrayList<String> imageStringArray = new ArrayList<>();
    private ArrayList<ToxinModel> plantToxinsArray = new ArrayList<>();
    String plantKey;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_harmful_plant_info);

        database = FirebaseDatabase.getInstance();
        harmfulPlantsRef = database.getReference("Harmful Plants");
        toxinsRef = database.getReference("Toxins");

        toolbarHarmfulPlantName = findViewById(R.id.toolbarHarmfulPlantName);
        back = findViewById(R.id.back);
        editHarmfulPlantBtn = findViewById(R.id.editHarmfulPlantBtn);

        plantImageSlider = findViewById(R.id.plantImageSlider);
        viewHarmfulPlantName = findViewById(R.id.viewHarmfulPlantName);
        viewHarmfulPlantCommonNames = findViewById(R.id.viewHarmfulPlantCommonNames);
        viewHarmfulPlantDesc = findViewById(R.id.viewHarmfulPlantDesc);
        viewHarmfulPlantAbundantLocations = findViewById(R.id.viewHarmfulPlantAbundantLocations);
        viewHarmfulPlantParts = findViewById(R.id.viewHarmfulPlantParts);

        viewHarmfulPlantToxinsRec = findViewById(R.id.viewHarmfulPlantToxinsRec);
        viewHarmfulPlantGuidesRec = findViewById(R.id.viewHarmfulPlantGuidesRec);

        fetchedDataFromPrevActivity();

        fetchedPlantInfoFromDB();

        toolbarHarmfulPlantName.setText(plantKey);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    private void fetchedDataFromPrevActivity() {
        Intent intent = getIntent();
        plantKey = intent.getStringExtra("plantName");
    }
    private void fetchedPlantInfoFromDB() {
        harmfulPlantsRef.child(plantKey).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    HarmfulPlantModel plant = dataSnapshot.getValue(HarmfulPlantModel.class);

                    // setup image slider
                    ArrayList<SlideModel> imageModels = new ArrayList<>();
                    imageStringArray = plant.getPlantImages();
                    for (String stringImage: imageStringArray) {
                        if (stringImage != null) {
                            imageModels.add(new SlideModel(stringImage, ScaleTypes.CENTER_CROP));
                        }
                    }
                    plantImageSlider.setImageList(imageModels, ScaleTypes.CENTER_CROP);

                    viewHarmfulPlantName.setText(plant.getPlantName());
                    viewHarmfulPlantCommonNames.setText(plant.getPlantCommonNames());
                    viewHarmfulPlantDesc.setText(plant.getPlantDesc());
                    viewHarmfulPlantAbundantLocations.setText(plant.getAbundantLocations());
                    viewHarmfulPlantParts.setText(plant.getPlantHarmfulParts());
                    plantToxinsKeys = plant.getPlantToxins();
                    plantGuides = plant.getPlantGuides();

                    // setup plant toxins recycler view
                    for (String toxin: plantToxinsKeys) {
                        toxinsRef.child(toxin).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                            @Override
                            public void onSuccess(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    ToxinModel toxinModel = dataSnapshot.getValue(ToxinModel.class);
                                    plantToxinsArray.add(toxinModel);
                                }
                                viewHarmfulPlantToxinsRec.setLayoutManager(new LinearLayoutManager(AdminHarmfulPlantInfoActivity.this));
                                PlantInfoToxinAdapter adapter = new PlantInfoToxinAdapter(AdminHarmfulPlantInfoActivity.this, plantToxinsArray);
                                viewHarmfulPlantToxinsRec.setAdapter(adapter);
                                adapter.notifyDataSetChanged();
                            }
                        });
                    }
                    // setup plant guides recycler view
                    viewHarmfulPlantGuidesRec.setLayoutManager(new LinearLayoutManager(AdminHarmfulPlantInfoActivity.this));
                    PlantInfoGuideAdapter adapter = new PlantInfoGuideAdapter(AdminHarmfulPlantInfoActivity.this, plantGuides);
                    viewHarmfulPlantGuidesRec.setAdapter(adapter);
                    adapter.notifyDataSetChanged();

                    editHarmfulPlantBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(AdminHarmfulPlantInfoActivity.this, UpdateHarmfulPlantActivity.class);
                            intent.putExtra("plant", plant);
                            startActivity(intent);
                        }
                    });
                }
            }
        });

    }
}