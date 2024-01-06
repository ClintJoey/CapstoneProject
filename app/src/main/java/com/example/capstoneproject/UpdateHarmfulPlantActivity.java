package com.example.capstoneproject;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
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
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.capstoneproject.adapters.PlantGuideAdapter;
import com.example.capstoneproject.adapters.PlantImagesAdapter;
import com.example.capstoneproject.adapters.PlantToxinAdapter;
import com.example.capstoneproject.models.HarmfulPlantModel;
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

public class UpdateHarmfulPlantActivity extends AppCompatActivity {
    FirebaseDatabase database;
    DatabaseReference harmfulPlantRef, toxinsRef;
    StorageReference harmfulPlantImageRef;
    private EditText updateHarmfulPlantName, updateHarmfulPlantCommonNames, updateHarmfulPlantDesc, updateHarmfulPlantLocation, updateHarmfulPlantParts,
            updateHarmfulPlantGuides;
    private AutoCompleteTextView harmfulPlantToxinsATV;
    private ImageButton addPlantToxinBtn, addPlantGuideBtn;
    private ImageView back;
    private Button addImagesBtn, updateHarmfulPlantBtn;
    private RecyclerView harmfulPlantsImages, addedToxinRec, harmfulPlantGuidesRec;
    private HarmfulPlantModel plant;
    private ArrayList<Uri> uriArrayList = new ArrayList<>();
    private ArrayList<String> imageStringArray = new ArrayList<>();
    private ArrayList<String> plantToxinsArray, plantGuidesArray;
    private ArrayList<String> toxinKeys = new ArrayList<>();
    private ArrayList<HarmfulPlantModel> harmfulPlantsArray;
    private static final int READ_PERMISSION = 101;
    String toxinSelected, plantGuide;
    private PlantImagesAdapter plantImagesAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_harmful_plant);

        database = FirebaseDatabase.getInstance();
        harmfulPlantRef = database.getReference("Harmful Plants");
        toxinsRef = database.getReference("Toxins");
        harmfulPlantImageRef = FirebaseStorage.getInstance().getReference("Harmful Plants");

        updateHarmfulPlantName = findViewById(R.id.updateHarmfulPlantName);
        updateHarmfulPlantCommonNames = findViewById(R.id.updateHarmfulPlantCommonNames);
        updateHarmfulPlantDesc = findViewById(R.id.updateHarmfulPlantDesc);
        updateHarmfulPlantLocation = findViewById(R.id.updateHarmfulPlantLocation);
        updateHarmfulPlantParts = findViewById(R.id.updateHarmfulPlantParts);
        updateHarmfulPlantGuides = findViewById(R.id.updateHarmfulPlantGuides);
        harmfulPlantToxinsATV = findViewById(R.id.harmfulPlantToxinsATV);

        harmfulPlantsImages = findViewById(R.id.harmfulPlantsImages);
        addedToxinRec = findViewById(R.id.addedToxinRec);
        harmfulPlantGuidesRec = findViewById(R.id.harmfulPlantGuidesRec);

        addImagesBtn = findViewById(R.id.addImagesBtn);
        back = findViewById(R.id.back);

        addPlantToxinBtn = findViewById(R.id.addPlantToxinBtn);
        addPlantGuideBtn = findViewById(R.id.addPlantGuideBtn);
        updateHarmfulPlantBtn = findViewById(R.id.updateHarmfulPlantBtn);

        getPlant();

        setHarmfulPlantInfo();

        // setup photo picker
        if (ContextCompat.checkSelfPermission(UpdateHarmfulPlantActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(UpdateHarmfulPlantActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_PERMISSION);
        }
        harmfulPlantsImages.setLayoutManager(new GridLayoutManager(UpdateHarmfulPlantActivity.this, 2));
        plantImagesAdapter = new PlantImagesAdapter(UpdateHarmfulPlantActivity.this, uriArrayList);
        harmfulPlantsImages.setAdapter(plantImagesAdapter);
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

        // don't change the plant name because it is the key for the plant in db
        updateHarmfulPlantName.setEnabled(false);

        // setup recycler view for plant toxins
        addedToxinRec.setLayoutManager(new LinearLayoutManager(UpdateHarmfulPlantActivity.this));
        PlantToxinAdapter plantToxinAdapter = new PlantToxinAdapter(UpdateHarmfulPlantActivity.this, plantToxinsArray);
        addedToxinRec.setAdapter(plantToxinAdapter);
        plantToxinAdapter.notifyDataSetChanged();

        // setup plant toxins ATV
        getToxinsFromDB();

        addPlantToxinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (toxinSelected == null) {
                    Toast.makeText(UpdateHarmfulPlantActivity.this, "Please select a toxin", Toast.LENGTH_SHORT).show();
                } else {
                    if (plantToxinsArray.contains(toxinSelected)) {
                        Toast.makeText(UpdateHarmfulPlantActivity.this, "Toxin is already on the list", Toast.LENGTH_SHORT).show();
                    } else {
                        plantToxinsArray.add(toxinSelected);
                        plantToxinAdapter.notifyDataSetChanged();
                        toxinSelected = null;
                        harmfulPlantToxinsATV.setText(null);
                    }

                }
            }
        });

        plantToxinAdapter.setOnItemClickListener(new PlantToxinAdapter.onPlantToxinListener() {
            @Override
            public void onDeleteClick(int position) {
                plantToxinsArray.remove(position);
                plantToxinAdapter.notifyItemRemoved(position);
            }
        });

        // setup recycler view for plant guides
        harmfulPlantGuidesRec.setLayoutManager(new LinearLayoutManager(UpdateHarmfulPlantActivity.this));
        PlantGuideAdapter plantGuideAdapter = new PlantGuideAdapter(UpdateHarmfulPlantActivity.this, plantGuidesArray);
        harmfulPlantGuidesRec.setAdapter(plantGuideAdapter);
        plantGuideAdapter.notifyDataSetChanged();

        addPlantGuideBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                plantGuide = updateHarmfulPlantGuides.getText().toString().trim();
                if (plantGuide.isEmpty()) {
                    updateHarmfulPlantGuides.setError("Please enter a guide");
                    updateHarmfulPlantGuides.requestFocus();
                } else {
                    plantGuidesArray.add(plantGuide);
                    plantGuideAdapter.notifyDataSetChanged();
                    updateHarmfulPlantGuides.setText("");
                }
            }
        });

        plantGuideAdapter.setOnItemClickListener(new PlantGuideAdapter.onPlantGuideListener() {
            @Override
            public void onDeleteClick(int position) {
                plantGuidesArray.remove(position);
                plantGuideAdapter.notifyItemRemoved(position);
            }
        });

        updateHarmfulPlantBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateHarmfulPlant();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void getPlant() {
        Intent intent = getIntent();
        plant = (HarmfulPlantModel) intent.getSerializableExtra("plant");
        harmfulPlantsArray = (ArrayList<HarmfulPlantModel>) intent.getSerializableExtra("harmfulPlantsArray");
    }
    private void setHarmfulPlantInfo() {
        // setup plant images
        imageStringArray = plant.getPlantImages();
        for (String plantImage: imageStringArray) {
            uriArrayList.add(Uri.parse(plantImage));
        }
        // plant info
        updateHarmfulPlantName.setText(plant.getPlantName());
        updateHarmfulPlantCommonNames.setText(plant.getPlantCommonNames());
        updateHarmfulPlantDesc.setText(plant.getPlantDesc());
        updateHarmfulPlantLocation.setText(plant.getAbundantLocations());
        updateHarmfulPlantParts.setText(plant.getPlantHarmfulParts());
        plantToxinsArray = plant.getPlantToxins();
        plantGuidesArray = plant.getPlantGuides();
    }
    private void getToxinsFromDB() {
        toxinsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data: snapshot.getChildren()) {
                    toxinKeys.add(data.getKey());
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(UpdateHarmfulPlantActivity.this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, toxinKeys);
                harmfulPlantToxinsATV.setAdapter(adapter);

                harmfulPlantToxinsATV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        toxinSelected = parent.getItemAtPosition(position).toString();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UpdateHarmfulPlantActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
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

    private void updateHarmfulPlant() {
        // include plant pic
        String plantName = updateHarmfulPlantName.getText().toString().trim();
        String plantCommonNames = updateHarmfulPlantCommonNames.getText().toString().trim();
        String plantDesc = updateHarmfulPlantDesc.getText().toString().trim();
        String abundantLocations = updateHarmfulPlantLocation.getText().toString().trim();
        String harmfulParts = updateHarmfulPlantParts.getText().toString().trim();
        // include plant toxins array
        // include plant guides array

        if (uriArrayList.isEmpty()) {
            Toast.makeText(UpdateHarmfulPlantActivity.this, "Please select plant image", Toast.LENGTH_SHORT).show();
        } else if (plantCommonNames.isEmpty()) {
            updateHarmfulPlantCommonNames.setError("Please specify common names of the plant");
            updateHarmfulPlantCommonNames.requestFocus();
        } else if (plantDesc.isEmpty()) {
            updateHarmfulPlantDesc.setError("Plant Description cannot be empty");
            updateHarmfulPlantDesc.requestFocus();
        } else if (abundantLocations.isEmpty()) {
            updateHarmfulPlantLocation.setError("Please specify abundant locations of the plant");
            updateHarmfulPlantLocation.requestFocus();
        } else if (harmfulParts.isEmpty()) {
            updateHarmfulPlantParts.setError("Please specify harmful parts of the plant");
        } else if (plantToxinsArray.isEmpty()) {
            Toast.makeText(UpdateHarmfulPlantActivity.this, "Harmful plant must have at least one toxin", Toast.LENGTH_SHORT).show();
        } else if (plantGuidesArray.isEmpty()) {
            Toast.makeText(UpdateHarmfulPlantActivity.this, "Harmful plant must have at least one guide", Toast.LENGTH_SHORT).show();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(UpdateHarmfulPlantActivity.this);
            builder.setCancelable(false);
            builder.setView(R.layout.progress_bar_layout);
            AlertDialog dialog = builder.create();
            dialog.show();

            // TODO: This is still not working
            for (Uri imageUri: uriArrayList) {

                StorageReference imageRef = harmfulPlantImageRef.child(System.currentTimeMillis() + "." + getFileExtension(imageUri));
                imageRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                imageStringArray.add(uri.toString());

                                if (uriArrayList.size() == imageStringArray.size()) {
                                    HarmfulPlantModel plant = new HarmfulPlantModel(imageStringArray, plantName, plantCommonNames, plantDesc,
                                            abundantLocations, harmfulParts, plantToxinsArray, plantGuidesArray);

                                    harmfulPlantRef.child(plantName).setValue(plant).addOnCompleteListener(new OnCompleteListener<Void>() {
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

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }
}