package com.example.capstoneproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.capstoneproject.adapters.BeneficialPlantNutrientAdapter;
import com.example.capstoneproject.adapters.PlantImagesAdapter;
import com.example.capstoneproject.models.BeneficialPlantModel;
import com.example.capstoneproject.models.NutrientModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Arrays;

public class UpdateBeneficialPlantActivity extends AppCompatActivity {
    private FirebaseDatabase database;
    private DatabaseReference beneficialPlantsRef;
    private DatabaseReference nutrientsRef;
    private StorageReference beneficialPlantsImagesRef;
    private ImageView back;
    private EditText updateBeneficialPlantName, updateBeneficialPlantCommonNames, updateBeneficialPlantDesc, updateBeneficialPlantLocation, updateBeneficialPlantNutrientsAmount;
    private RecyclerView beneficialPlantsImages, nutrientsRec;
    private Button addImagesBtn, updateBeneficialPlantBtn;
    private ImageButton addPlantNutrientBtn;
    private AutoCompleteTextView plantNutrientsATV;
    private ArrayList<Uri> uriArrayList = new ArrayList<>();
    private ArrayList<String> imageStringArray = new ArrayList<>();
    private ArrayList<String> plantNutrients = new ArrayList<>();
    private ArrayList<String> nutrients = new ArrayList<>();
    private static final int READ_PERMISSION = 101;
    private PlantImagesAdapter plantImagesAdapter;
    private BeneficialPlantModel plantModel;
    String nutrientSelected = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_beneficial_plant);
        database = FirebaseDatabase.getInstance();
        beneficialPlantsRef = database.getReference("Beneficial Plants");
        nutrientsRef = database.getReference("Nutrients");
        beneficialPlantsImagesRef = FirebaseStorage.getInstance().getReference("Beneficial Plants");

        beneficialPlantsImages = findViewById(R.id.beneficialPlantsImages);

        updateBeneficialPlantName = findViewById(R.id.updateBeneficialPlantName);
        updateBeneficialPlantCommonNames = findViewById(R.id.updateBeneficialPlantCommonNames);
        updateBeneficialPlantDesc = findViewById(R.id.updateBeneficialPlantDesc);
        updateBeneficialPlantLocation = findViewById(R.id.updateBeneficialPlantLocation);
        updateBeneficialPlantNutrientsAmount = findViewById(R.id.updateBeneficialPlantNutrientsAmount);
        updateBeneficialPlantBtn = findViewById(R.id.updateBeneficialPlantBtn);

        plantNutrientsATV = findViewById(R.id.plantNutrientsATV);
        nutrientsRec = findViewById(R.id.nutrientsRec);

        addImagesBtn = findViewById(R.id.addImagesBtn);
        addPlantNutrientBtn = findViewById(R.id.addPlantNutrientBtn);
        back = findViewById(R.id.back);

        fetchIntents();
        setUIValues();
        fetchNutrientsFromDB();

        if (ContextCompat.checkSelfPermission(UpdateBeneficialPlantActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(UpdateBeneficialPlantActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_PERMISSION);
        }
        beneficialPlantsImages.setLayoutManager(new GridLayoutManager(UpdateBeneficialPlantActivity.this, 2));
        plantImagesAdapter = new PlantImagesAdapter(UpdateBeneficialPlantActivity.this, uriArrayList);
        beneficialPlantsImages.setAdapter(plantImagesAdapter);
        plantImagesAdapter.notifyDataSetChanged();

        addImagesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                }
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Images"), 1);
            }
        });

        plantImagesAdapter.setOnItemClickListener(new PlantImagesAdapter.onImagesListener() {
            @Override
            public void onDeleteClick(int position) {
                uriArrayList.remove(position);
                plantImagesAdapter.notifyItemRemoved(position);
            }
        });

        nutrientsRec.setLayoutManager(new LinearLayoutManager(UpdateBeneficialPlantActivity.this));
        BeneficialPlantNutrientAdapter beneficialPlantNutrientAdapter = new BeneficialPlantNutrientAdapter(UpdateBeneficialPlantActivity.this, plantNutrients);
        nutrientsRec.setAdapter(beneficialPlantNutrientAdapter);
        beneficialPlantNutrientAdapter.notifyDataSetChanged();

        beneficialPlantNutrientAdapter.setOnItemClickListener(new BeneficialPlantNutrientAdapter.onPlantNutrientListener() {
            @Override
            public void onDeleteClick(int position) {
                plantNutrients.remove(position);
                beneficialPlantNutrientAdapter.notifyItemRemoved(position);
            }
        });

        addPlantNutrientBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nutrientSelected == null) {
                    Toast.makeText(UpdateBeneficialPlantActivity.this, "Please select nutrient", Toast.LENGTH_SHORT).show();
                } else {
                    if (plantNutrients.contains(nutrientSelected)) {
                        Toast.makeText(UpdateBeneficialPlantActivity.this, "Nutrient is already on the list", Toast.LENGTH_SHORT).show();
                    } else {
                        plantNutrients.add(nutrientSelected);
                        beneficialPlantNutrientAdapter.notifyDataSetChanged();
                        nutrientSelected = null;
                        plantNutrientsATV.setText(null);
                    }
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        updateBeneficialPlantBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // store the images to cloud storage and then fetched the images and store it to the imageStringArray

                String plantName = updateBeneficialPlantName.getText().toString().trim();
                String plantCommonNames = updateBeneficialPlantCommonNames.getText().toString().trim();
                String plantDesc = updateBeneficialPlantDesc.getText().toString().trim();
                String plantLocations = updateBeneficialPlantLocation.getText().toString().trim();
                String nutrientsAmount = updateBeneficialPlantNutrientsAmount.getText().toString().trim();
                String[] nutrientsArr = nutrientsAmount.split(", ");
                ArrayList<String> plantNutrientsAmount = new ArrayList<>(Arrays.asList(nutrientsArr));
                // include the plant nutrients array

                if (uriArrayList.isEmpty()) {
                    Toast.makeText(UpdateBeneficialPlantActivity.this, "Please select plant image", Toast.LENGTH_SHORT).show();
                } else if (plantName.isEmpty()) {
                    updateBeneficialPlantName.setError("Plant name is empty");
                    updateBeneficialPlantName.requestFocus();
                } else if (plantCommonNames.isEmpty()) {
                    updateBeneficialPlantCommonNames.setError("Plant common names are empty");
                    updateBeneficialPlantCommonNames.requestFocus();
                } else if (plantDesc.isEmpty()) {
                    updateBeneficialPlantDesc.setError("Plant description is empty");
                    updateBeneficialPlantDesc.requestFocus();
                } else if (plantLocations.isEmpty()) {
                    updateBeneficialPlantLocation.setError("Please specify plant abundant locations");
                    updateBeneficialPlantLocation.requestFocus();
                } else if (plantNutrients.isEmpty()) {
                    Toast.makeText(UpdateBeneficialPlantActivity.this, "Please specify some plant nutrients", Toast.LENGTH_SHORT).show();
                } else if (nutrientsAmount.isEmpty() || nutrientsArr.length != plantNutrients.size()) {
                    updateBeneficialPlantNutrientsAmount.setError("Please specify the right amount for each nutrient");
                    updateBeneficialPlantNutrientsAmount.requestFocus();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(UpdateBeneficialPlantActivity.this);
                    builder.setCancelable(false);
                    builder.setView(R.layout.progress_bar_layout);
                    AlertDialog dialog = builder.create();
                    dialog.show();

                    for (Uri imageUri: uriArrayList) {

                        StorageReference imageRef = beneficialPlantsImagesRef.child(System.currentTimeMillis() + "." + getFileExtension(imageUri));
                        imageRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        imageStringArray.add(uri.toString());

                                        if (imageStringArray.size() == uriArrayList.size()) {
                                            BeneficialPlantModel model = new BeneficialPlantModel(imageStringArray, plantName, plantCommonNames, plantDesc, plantLocations, plantNutrients, plantNutrientsAmount);

                                            beneficialPlantsRef.child(plantName).setValue(model).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        for (String image: imageStringArray) {
                                                            StorageReference deleteImageRef = FirebaseStorage.getInstance().getReferenceFromUrl(image);
                                                            deleteImageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void unused) {
                                                                    Log.d("demo", "Image deleted");
                                                                }
                                                            });
                                                        }
                                                        Toast.makeText(UpdateBeneficialPlantActivity.this, plantName + " is updated successfully", Toast.LENGTH_SHORT).show();
                                                        dialog.dismiss();
                                                    }
                                                }
                                            });
                                        }
                                    }
                                });
                            }
                        });
                    }
                }
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            if (data.getClipData() != null) {
                int x = data.getClipData().getItemCount();

                for (int i = 0; i < x; i++) {
                    uriArrayList.add(data.getClipData().getItemAt(i).getUri());
                }
                plantImagesAdapter.notifyDataSetChanged();
            } else if (data.getData() != null) {
                String imageUrl = data.getData().getPath();
                uriArrayList.add(Uri.parse(imageUrl));
            }
        }
    }
    private void fetchIntents() {
        Intent intent = getIntent();
        plantModel = (BeneficialPlantModel) intent.getSerializableExtra("plantModel");
    }
    private void setUIValues() {
        // set Images
        for (String image: plantModel.getPlantImages()) {
            if (image != null) {
                Uri imageUri = Uri.parse(image);
                uriArrayList.add(imageUri);
            }
        }
        // set info
        updateBeneficialPlantName.setText(plantModel.getPlantName());
        updateBeneficialPlantCommonNames.setText(plantModel.getPlantCommonNames());
        updateBeneficialPlantDesc.setText(plantModel.getPlantDesc());
        updateBeneficialPlantLocation.setText(plantModel.getPlantAbundantLocations());
        updateBeneficialPlantNutrientsAmount.setText(String.join(", ", plantModel.getPlantNutrientsAmount()));
        plantNutrients = plantModel.getPlantNutrients();
    }
    private void fetchNutrientsFromDB() {
        nutrientsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                nutrients.clear();
                for (DataSnapshot data: snapshot.getChildren()) {
                    nutrients.add(data.getKey());
                }
                ArrayAdapter<String> nutrientsATVAdapter = new ArrayAdapter<>(UpdateBeneficialPlantActivity.this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, nutrients);
                plantNutrientsATV.setAdapter(nutrientsATVAdapter);

                plantNutrientsATV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        nutrientSelected = parent.getItemAtPosition(position).toString();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UpdateBeneficialPlantActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private String getFileExtension(Uri imageUri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(imageUri));
    }
}