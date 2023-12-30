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

import com.example.capstoneproject.adapters.PlantGuideAdapter;
import com.example.capstoneproject.adapters.PlantImagesAdapter;
import com.example.capstoneproject.adapters.PlantToxinAdapter;
import com.example.capstoneproject.models.HarmfulPlantModel;
import com.example.capstoneproject.models.ToxinModel;
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

public class AddHarmfulPlantActivity extends AppCompatActivity {
    FirebaseDatabase database;
    DatabaseReference harmfulPlantsRef, toxinsRef;
    StorageReference storageReference;
    private ImageView back;
    private EditText addHarmfulPlantName, addHarmfulPlantCommonNames, addHarmfulPlantDesc, addHarmfulPlantLocation, addHarmfulPlantParts,
            addHarmfulPlantGuides;
    private AutoCompleteTextView harmfulPlantToxinsATV;
    private ImageButton addPlantToxinBtn, addPlantGuideBtn;
    private TextView goToToxinsActivity;
    private RecyclerView harmfulPlantsImages, addedToxinRec, harmfulPlantGuidesRec;
    private Button addImagesBtn, addHarmfulPlantBtn;
    private ArrayList<ToxinModel> toxinsArray = new ArrayList<>();
    private ArrayList<Uri> uriArrayList = new ArrayList<>();
    private ArrayList<String> imageStringArray = new ArrayList<>();
    private ArrayList<HarmfulPlantModel> harmfulPlantsArray;
    private ArrayList<String> plantToxinsArray;
    private static final int READ_PERMISSION = 101;
    Uri uri;
    String toxinSelected, plantGuide;
    private PlantImagesAdapter plantImagesAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_harmful_plant);

        database = FirebaseDatabase.getInstance();
        harmfulPlantsRef = database.getReference("Harmful Plants");
        toxinsRef = database.getReference("Toxins");
        storageReference = FirebaseStorage.getInstance().getReference("Harmful Plants");

        back = findViewById(R.id.back);

        addHarmfulPlantName = findViewById(R.id.addHarmfulPlantName);
        addHarmfulPlantCommonNames = findViewById(R.id.addHarmfulPlantCommonNames);
        addHarmfulPlantDesc = findViewById(R.id.addHarmfulPlantDesc);
        addHarmfulPlantLocation = findViewById(R.id.addHarmfulPlantLocation);
        addHarmfulPlantParts = findViewById(R.id.addHarmfulPlantParts);
        addHarmfulPlantGuides = findViewById(R.id.addHarmfulPlantGuides);

        harmfulPlantToxinsATV = findViewById(R.id.harmfulPlantToxinsATV);

        addPlantToxinBtn = findViewById(R.id.addPlantToxinBtn);
        addPlantGuideBtn = findViewById(R.id.addPlantGuideBtn);

        goToToxinsActivity = findViewById(R.id.goToToxinsActivity);

        harmfulPlantsImages = findViewById(R.id.harmfulPlantsImages);
        addedToxinRec = findViewById(R.id.addedToxinRec);
        harmfulPlantGuidesRec = findViewById(R.id.harmfulPlantGuidesRec);

        addImagesBtn = findViewById(R.id.addImagesBtn);
        addHarmfulPlantBtn = findViewById(R.id.addHarmfulPlantBtn);

        fetchedToxins();

        harmfulPlantsImages.setLayoutManager(new GridLayoutManager(AddHarmfulPlantActivity.this, 2));
        plantImagesAdapter = new PlantImagesAdapter(AddHarmfulPlantActivity.this, uriArrayList);
        harmfulPlantsImages.setAdapter(plantImagesAdapter);
        plantImagesAdapter.notifyDataSetChanged();

        // setup photo picker for harmful plant
        if (ContextCompat.checkSelfPermission(AddHarmfulPlantActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(AddHarmfulPlantActivity.this,
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

        // setup ATV
        ArrayList<String> toxinsATV = new ArrayList<>();
        for (ToxinModel toxin: toxinsArray) {
            toxinsATV.add(toxin.getToxinName());
        }
        ArrayAdapter<String> atvAdapter = new ArrayAdapter<>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, toxinsATV);
        harmfulPlantToxinsATV.setAdapter(atvAdapter);

        harmfulPlantToxinsATV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                toxinSelected = parent.getItemAtPosition(position).toString();
            }
        });

        // setup toxins recyclerview
        plantToxinsArray = new ArrayList<>();

        addedToxinRec.setLayoutManager(new LinearLayoutManager(this));
        PlantToxinAdapter plantToxinAdapter = new PlantToxinAdapter(this, plantToxinsArray);
        addedToxinRec.setAdapter(plantToxinAdapter);
        plantToxinAdapter.notifyDataSetChanged();

        plantToxinAdapter.setOnItemClickListener(new PlantToxinAdapter.onPlantToxinListener() {
            @Override
            public void onDeleteClick(int position) {
                plantToxinsArray.remove(position);
                plantToxinAdapter.notifyItemRemoved(position);
            }
        });

        addPlantToxinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (toxinSelected == null) {
                    Toast.makeText(AddHarmfulPlantActivity.this, "Please select a toxin", Toast.LENGTH_SHORT).show();
                } else {
                    if (plantToxinsArray.contains(toxinSelected)) {
                        Toast.makeText(AddHarmfulPlantActivity.this, "Toxin is already on the list", Toast.LENGTH_SHORT).show();
                    } else {
                        plantToxinsArray.add(toxinSelected);
                        plantToxinAdapter.notifyDataSetChanged();
                        toxinSelected = null;
                        harmfulPlantToxinsATV.setText(null);
                    }
                }
            }
        });

        // setup the plant guide
        ArrayList<String> plantGuidesArray = new ArrayList<>();

        harmfulPlantGuidesRec.setLayoutManager(new LinearLayoutManager(this));
        PlantGuideAdapter plantGuideAdapter = new PlantGuideAdapter(this, plantGuidesArray);
        harmfulPlantGuidesRec.setAdapter(plantGuideAdapter);
        plantGuideAdapter.notifyDataSetChanged();

        plantGuideAdapter.setOnItemClickListener(new PlantGuideAdapter.onPlantGuideListener() {
            @Override
            public void onDeleteClick(int position) {
                plantGuidesArray.remove(position);
                plantGuideAdapter.notifyItemRemoved(position);
            }
        });

        addPlantGuideBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                plantGuide = addHarmfulPlantGuides.getText().toString().trim();
                if (plantGuide.isEmpty()) {
                    addHarmfulPlantGuides.setError("Please enter a plant guide");
                    addHarmfulPlantGuides.requestFocus();
                } else {
                    plantGuidesArray.add(plantGuide);
                    plantGuideAdapter.notifyDataSetChanged();
                    addHarmfulPlantGuides.setText("");
                    addHarmfulPlantGuides.requestFocus();
                }
            }
        });

        goToToxinsActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddHarmfulPlantActivity.this, ManagePlantToxinsActivity.class);
                intent.putExtra("toxinsArray", toxinsArray);
                startActivity(intent);
            }
        });

        addHarmfulPlantBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//              include plant pic
                String plantName = addHarmfulPlantName.getText().toString().trim();
                String plantCommonNames = addHarmfulPlantCommonNames.getText().toString().trim();
                String plantDesc = addHarmfulPlantDesc.getText().toString().trim();
                String abundantLocation = addHarmfulPlantLocation.getText().toString().trim();
                String plantHarmfulParts = addHarmfulPlantParts.getText().toString().trim();
                // include plantToxinsArray
                // include plantGuidesArray
                if (uriArrayList.isEmpty()) {
                    Toast.makeText(AddHarmfulPlantActivity.this, "Pictures of plant is not set", Toast.LENGTH_SHORT).show();
                } else if (plantName.isEmpty()) {
                    addHarmfulPlantName.setError("Plant Name cannot be empty");
                    addHarmfulPlantName.requestFocus();
                } else if (plantCommonNames.isEmpty()) {
                    addHarmfulPlantCommonNames.setError("Please specify common names of the plant");
                    addHarmfulPlantCommonNames.requestFocus();
                } else if (plantDesc.isEmpty()) {
                    addHarmfulPlantDesc.setError("Plant Description cannot be empty");
                    addHarmfulPlantDesc.requestFocus();
                } else if (abundantLocation.isEmpty()) {
                    addHarmfulPlantLocation.setError("Please specify abundant locations of the plant");
                    addHarmfulPlantLocation.requestFocus();
                } else if (plantHarmfulParts.isEmpty()) {
                    addHarmfulPlantParts.setError("Please specify harmful parts of the plant");
                    addHarmfulPlantParts.requestFocus();
                } else if (plantToxinsArray.isEmpty()) {
                    Toast.makeText(AddHarmfulPlantActivity.this, "Harmful plant must have at least one toxin", Toast.LENGTH_SHORT).show();
                } else if (plantGuidesArray.isEmpty()) {
                    Toast.makeText(AddHarmfulPlantActivity.this, "Harmful plant must have at least one guide", Toast.LENGTH_SHORT).show();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(AddHarmfulPlantActivity.this);
                    builder.setCancelable(false);
                    builder.setView(R.layout.progress_bar_layout);
                    AlertDialog dialog = builder.create();
                    dialog.show();

                    for (Uri imageUri: uriArrayList) {
                        StorageReference imageRef = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(imageUri));
                        imageRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        imageStringArray.add(uri.toString());

                                        if (uriArrayList.size() == imageStringArray.size()) {
                                            HarmfulPlantModel harmfulPlantModel = new HarmfulPlantModel(imageStringArray, plantName, plantCommonNames, plantDesc, abundantLocation, plantHarmfulParts,
                                                    plantToxinsArray, plantGuidesArray);

                                            harmfulPlantsRef.child(plantName).setValue(harmfulPlantModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        dialog.dismiss();
                                                        Toast.makeText(AddHarmfulPlantActivity.this, plantName + " is added successfully", Toast.LENGTH_SHORT).show();
                                                        Intent intent = new Intent(AddHarmfulPlantActivity.this, ManageBeneficialPlantsActivity.class);
                                                        startActivity(intent);
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

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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

    private void fetchedToxins() {
        Intent intent = getIntent();
        toxinsArray = (ArrayList<ToxinModel>) intent.getSerializableExtra("toxinsArray");
        harmfulPlantsArray = (ArrayList<HarmfulPlantModel>) intent.getSerializableExtra("harmfulPlantsArray");
    }
    private String getFileExtension(Uri imageUri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(imageUri));
    }
}