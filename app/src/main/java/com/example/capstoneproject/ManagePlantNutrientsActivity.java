package com.example.capstoneproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.capstoneproject.adapters.AdminNutrientAdapter;
import com.example.capstoneproject.adapters.ToxinsAdapter;
import com.example.capstoneproject.models.NutrientModel;
import com.example.capstoneproject.models.ToxinModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ManagePlantNutrientsActivity extends AppCompatActivity {
    FirebaseDatabase database;
    DatabaseReference nutrientsRef;
    private SearchView searchNutrient;
    private RecyclerView plantNutrientRec;
    private FloatingActionButton addNutrient;
    private ImageView back;
    private ArrayList<NutrientModel> nutrientsArray = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_plant_nutrients);
        database = FirebaseDatabase.getInstance();
        nutrientsRef = database.getReference("Nutrients");

        searchNutrient = findViewById(R.id.searchNutrient);
        plantNutrientRec = findViewById(R.id.plantNutrientRec);

        addNutrient = findViewById(R.id.addNutrient);
        back = findViewById(R.id.back);

        fetchIntent();

        plantNutrientRec.setLayoutManager(new LinearLayoutManager(ManagePlantNutrientsActivity.this));
        AdminNutrientAdapter adapter = new AdminNutrientAdapter(ManagePlantNutrientsActivity.this, nutrientsArray);
        plantNutrientRec.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        adapter.setOnItemClickListener(new AdminNutrientAdapter.onItemClickListener() {
            @Override
            public void onItemClick(int position) {
                // pending
            }

            @Override
            public void onEditClick(int position) {
                String name = nutrientsArray.get(position).getNutrientName();
                String desc = nutrientsArray.get(position).getNutrientDesc();
                String benefits = nutrientsArray.get(position).getNutrientBenefits();
                openEditNutrientDialog(name, desc, benefits, position);
            }

            @Override
            public void onDeleteClick(int position) {
                openDeleteDialog(adapter, position);
            }
        });

        searchNutrient.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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

        addNutrient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAddNutrientDialog(adapter);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    private void fetchIntent() {
        Intent intent = getIntent();
        nutrientsArray = (ArrayList<NutrientModel>) intent.getSerializableExtra("nutrientsArray");
    }
    private void openAddNutrientDialog(AdminNutrientAdapter adapter) {
        ConstraintLayout addNutrientDialog = findViewById(R.id.addNutrientDialog);
        View view = LayoutInflater.from(ManagePlantNutrientsActivity.this).inflate(R.layout.add_nutrient_dialog, addNutrientDialog);
        TextView nutrientName = view.findViewById(R.id.nutrientName);
        TextView nutrientDesc = view.findViewById(R.id.nutrientDesc);
        TextView nutrientBenefits = view.findViewById(R.id.nutrientBenefits);
        Button addNutrientBtn = view.findViewById(R.id.addNutrientBtn);

        AlertDialog.Builder builder = new AlertDialog.Builder(ManagePlantNutrientsActivity.this);
        builder.setView(view);
        final AlertDialog dialog = builder.create();

        addNutrientBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nutrientName.getText().toString().trim();
                String desc = nutrientDesc.getText().toString().trim();
                String benefits = nutrientBenefits.getText().toString().trim();

                if (name.isEmpty()) {
                    nutrientName.setError("Nutrient name is empty");
                    nutrientName.requestFocus();
                } else if (desc.isEmpty()) {
                    nutrientDesc.setError("Nutrient Description is empty");
                    nutrientDesc.requestFocus();
                } else if (benefits.isEmpty()) {
                    nutrientBenefits.setError("Please specify some nutrient benefits");
                    nutrientBenefits.requestFocus();
                } else {
                    NutrientModel nutrientModel = new NutrientModel(name, desc, benefits);

                    nutrientsRef.child(name).setValue(nutrientModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                nutrientsArray.add(nutrientModel);
                                adapter.notifyDataSetChanged();
                                Toast.makeText(ManagePlantNutrientsActivity.this, "Nutrient added", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ManagePlantNutrientsActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    });
                }
            }
        });

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        dialog.show();
    }
    private void openEditNutrientDialog(String fetchName, String fetchtDesc, String fetchBenefits, int position) {
        ConstraintLayout editNutrientDialog = findViewById(R.id.editNutrientDialog);
        View view = LayoutInflater.from(ManagePlantNutrientsActivity.this).inflate(R.layout.update_nutrient_dialog, editNutrientDialog);
        TextView nutrientName = view.findViewById(R.id.nutrientName);
        TextView nutrientDesc = view.findViewById(R.id.nutrientDesc);
        TextView nutrientBenefits = view.findViewById(R.id.nutrientBenefits);
        Button updateNutrientBtn = view.findViewById(R.id.updateNutrientBtn);

        nutrientName.setText(fetchName);
        nutrientDesc.setText(fetchtDesc);
        nutrientBenefits.setText(fetchBenefits);

        nutrientName.setEnabled(false);

        AlertDialog.Builder builder = new AlertDialog.Builder(ManagePlantNutrientsActivity.this);
        builder.setView(view);
        final AlertDialog dialog = builder.create();

        updateNutrientBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nutrientName.getText().toString().trim();
                String desc = nutrientDesc.getText().toString().trim();
                String benefits = nutrientBenefits.getText().toString().trim();

                if (name.isEmpty()) {
                    nutrientName.setError("Nutrient name is empty");
                    nutrientName.requestFocus();
                } else if (desc.isEmpty()) {
                    nutrientDesc.setError("Nutrient Description is empty");
                    nutrientDesc.requestFocus();
                } else if (benefits.isEmpty()) {
                    nutrientBenefits.setError("Please specify some nutrient benefits");
                    nutrientBenefits.requestFocus();
                } else {
                    dialog.dismiss();

                    AlertDialog.Builder builder1 = new AlertDialog.Builder(ManagePlantNutrientsActivity.this);
                    builder1.setView(R.layout.progress_bar_layout);
                    builder1.setCancelable(false);
                    AlertDialog dialog1 = builder1.create();
                    dialog1.show();

                    Toast.makeText(ManagePlantNutrientsActivity.this, "Updating nutrient", Toast.LENGTH_SHORT).show();

                    NutrientModel nutrientModel = new NutrientModel(name, desc, benefits);

                    nutrientsRef.child(name).setValue(nutrientModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                nutrientsArray.set(position, nutrientModel);
                                Toast.makeText(ManagePlantNutrientsActivity.this, name + " is updated", Toast.LENGTH_SHORT).show();
                                dialog1.dismiss();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ManagePlantNutrientsActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            dialog1.dismiss();
                        }
                    });
                }
            }
        });

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        dialog.show();
    }
    private void openDeleteDialog(AdminNutrientAdapter adapter, int position) {
        CardView deleteDialog = findViewById(R.id.deleteDialog);
        View view = LayoutInflater.from(ManagePlantNutrientsActivity.this).inflate(R.layout.delete_confirmation_dialog, deleteDialog);
        TextView deleteDialogText = view.findViewById(R.id.deleteDialogText);
        TextView cancelDelete = view.findViewById(R.id.cancelDelete);
        TextView confirmDelete = view.findViewById(R.id.confirmDelete);

        AlertDialog.Builder builder = new AlertDialog.Builder(ManagePlantNutrientsActivity.this);
        builder.setView(view);
        final AlertDialog alertDialog = builder.create();

        String name = nutrientsArray.get(position).getNutrientName();
        String dialogText = "Are you sure you want to delete " + name + "?";
        deleteDialogText.setText(dialogText);

        AlertDialog.Builder builder1 = new AlertDialog.Builder(ManagePlantNutrientsActivity.this);
        builder1.setCancelable(false);
        builder1.setView(R.layout.delete_progress_bar);
        AlertDialog dialog = builder1.create();

        confirmDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();

                dialog.show();

                Toast.makeText(ManagePlantNutrientsActivity.this, "Deleting " + name, Toast.LENGTH_SHORT).show();
                nutrientsRef.child(name).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        nutrientsArray.remove(position);
                        adapter.notifyItemRemoved(position);
                        Toast.makeText(ManagePlantNutrientsActivity.this, name + " deleted", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ManagePlantNutrientsActivity.this, "Delete error: " + e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        cancelDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }
    private void searchLists(AdminNutrientAdapter adapter, String text) {
        ArrayList<NutrientModel> lists = new ArrayList<>();
        for (NutrientModel nutrient: nutrientsArray) {
            if (nutrient.getNutrientName().toLowerCase().contains(text)) {
                lists.add(nutrient);
            }
        }
        adapter.searchLists(lists);
    }
}