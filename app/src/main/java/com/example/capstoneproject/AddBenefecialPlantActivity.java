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
import com.google.android.gms.tasks.OnFailureListener;
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

public class AddBenefecialPlantActivity extends AppCompatActivity {
    private FirebaseDatabase database;
    private DatabaseReference beneficialPlantsRef;
    private DatabaseReference nutrientsRef;
    private StorageReference beneficialPlantsImagesRef;
    private ImageView back;
    private EditText addBeneficialPlantName, addBeneficialPlantCommonNames, addBeneficialPlantDesc, addBeneficialPlantLocation, addBeneficialPlantNutrientsAmount;
    private RecyclerView beneficialPlantsImages, nutrientsRec;
    private Button addImagesBtn, addBeneficialPlantBtn;
    private ImageButton addPlantNutrientBtn;
    private AutoCompleteTextView plantNutrientsATV;
    private ArrayList<String> nutrientsArray = new ArrayList<>();
    private ArrayList<Uri> uriArrayList = new ArrayList<>();
    private ArrayList<String> imageStringArray = new ArrayList<>();
    private static final int READ_PERMISSION = 101;
    private PlantImagesAdapter plantImagesAdapter;
    String nutrientSelected = null;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_benefecial_plant);
        database = FirebaseDatabase.getInstance();
        beneficialPlantsRef = database.getReference("Beneficial Plants");
        nutrientsRef = database.getReference("Nutrients");
        beneficialPlantsImagesRef = FirebaseStorage.getInstance().getReference("Beneficial Plants");

        beneficialPlantsImages = findViewById(R.id.beneficialPlantsImages);

        addBeneficialPlantName = findViewById(R.id.addBeneficialPlantName);
        addBeneficialPlantCommonNames = findViewById(R.id.addBeneficialPlantCommonNames);
        addBeneficialPlantDesc = findViewById(R.id.addBeneficialPlantDesc);
        addBeneficialPlantLocation = findViewById(R.id.addBeneficialPlantLocation);
        addBeneficialPlantNutrientsAmount = findViewById(R.id.addBeneficialPlantNutrientsAmount);
        addBeneficialPlantBtn = findViewById(R.id.addBeneficialPlantBtn);

        plantNutrientsATV = findViewById(R.id.plantNutrientsATV);
        nutrientsRec = findViewById(R.id.nutrientsRec);

        addImagesBtn = findViewById(R.id.addImagesBtn);
        addPlantNutrientBtn = findViewById(R.id.addPlantNutrientBtn);
        back = findViewById(R.id.back);

        fetchNutrientsFromDB();

        beneficialPlantsImages.setLayoutManager(new GridLayoutManager(AddBenefecialPlantActivity.this, 2));
        plantImagesAdapter = new PlantImagesAdapter(AddBenefecialPlantActivity.this, uriArrayList);
        beneficialPlantsImages.setAdapter(plantImagesAdapter);
        plantImagesAdapter.notifyDataSetChanged();

        if (ContextCompat.checkSelfPermission(AddBenefecialPlantActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(AddBenefecialPlantActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_PERMISSION);
        }

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

        ArrayList<String> plantNutrients = new ArrayList<>();

        nutrientsRec.setLayoutManager(new LinearLayoutManager(AddBenefecialPlantActivity.this));
        BeneficialPlantNutrientAdapter beneficialPlantNutrientAdapter = new BeneficialPlantNutrientAdapter(AddBenefecialPlantActivity.this, plantNutrients);
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
                    Toast.makeText(AddBenefecialPlantActivity.this, "Please select nutrient", Toast.LENGTH_SHORT).show();
                } else {
                    if (plantNutrients.contains(nutrientSelected)) {
                        Toast.makeText(AddBenefecialPlantActivity.this, "Nutrient is already on the list", Toast.LENGTH_SHORT).show();
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

        addBeneficialPlantBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // store the images to cloud storage and then fetched the images and store it to the imageStringArray

                String plantName = addBeneficialPlantName.getText().toString().trim();
                String plantCommonNames = addBeneficialPlantCommonNames.getText().toString().trim();
                String plantDesc = addBeneficialPlantDesc.getText().toString().trim();
                String plantLocations = addBeneficialPlantLocation.getText().toString().trim();
                String nutrientsAmount = addBeneficialPlantNutrientsAmount.getText().toString().trim();
                String[] nutrientsArr = nutrientsAmount.split(", ");
                ArrayList<String> plantNutrientsAmount = new ArrayList<>(Arrays.asList(nutrientsArr));
                // include the plant nutrients array

                if (uriArrayList.isEmpty()) {
                    Toast.makeText(AddBenefecialPlantActivity.this, "Pictures of plant is not set", Toast.LENGTH_SHORT).show();
                } else if (plantName.isEmpty()) {
                    addBeneficialPlantName.setError("Plant name is empty");
                    addBeneficialPlantName.requestFocus();
                } else if (plantCommonNames.isEmpty()) {
                    addBeneficialPlantCommonNames.setError("Plant common names are empty");
                    addBeneficialPlantCommonNames.requestFocus();
                } else if (plantDesc.isEmpty()) {
                    addBeneficialPlantDesc.setError("Plant description is empty");
                    addBeneficialPlantDesc.requestFocus();
                } else if (plantLocations.isEmpty()) {
                    addBeneficialPlantLocation.setError("Please specify plant abundant locations");
                    addBeneficialPlantLocation.requestFocus();
                } else if (plantNutrients.isEmpty()) {
                    Toast.makeText(AddBenefecialPlantActivity.this, "Please specify some plant nutrients", Toast.LENGTH_SHORT).show();
                } else if (nutrientsAmount.isEmpty() || nutrientsArr.length != plantNutrients.size()) {
                    addBeneficialPlantNutrientsAmount.setError("Please specify the right amount for each nutrient");
                    addBeneficialPlantNutrientsAmount.requestFocus();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(AddBenefecialPlantActivity.this);
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

                                        if (uriArrayList.size() == imageStringArray.size()) {
                                            BeneficialPlantModel plantModel = new BeneficialPlantModel(imageStringArray, plantName, plantCommonNames, plantDesc, plantLocations, plantNutrients, plantNutrientsAmount);

                                            beneficialPlantsRef.child(plantName).setValue(plantModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        dialog.dismiss();
                                                        Toast.makeText(AddBenefecialPlantActivity.this, plantName + " is added successfully", Toast.LENGTH_SHORT).show();
                                                        Intent intent = new Intent(AddBenefecialPlantActivity.this, ManageBeneficialPlantsActivity.class);
                                                        startActivity(intent);
                                                    }
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(AddBenefecialPlantActivity.this, "Error: " + e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                                                    dialog.dismiss();
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
    private void fetchNutrientsFromDB() {
        nutrientsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                nutrientsArray.clear();
                for (DataSnapshot data: snapshot.getChildren()) {
                    nutrientsArray.add(data.getKey());
                }
                ArrayAdapter<String> nutrientsATVAdapter = new ArrayAdapter<>(AddBenefecialPlantActivity.this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, nutrientsArray);
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
                Toast.makeText(AddBenefecialPlantActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private String getFileExtension(Uri imageUri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(imageUri));
    }
}