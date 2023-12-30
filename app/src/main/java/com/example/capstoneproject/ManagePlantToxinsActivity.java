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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.capstoneproject.adapters.ToxinsAdapter;
import com.example.capstoneproject.adapters.UserAdapter;
import com.example.capstoneproject.models.HarmfulPlantModel;
import com.example.capstoneproject.models.ToxinModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ManagePlantToxinsActivity extends AppCompatActivity {
    private ImageView back;
    private RecyclerView plantToxinsRec;
    private SearchView searchToxins;
    private FloatingActionButton addToxins;
    FirebaseDatabase database;
    DatabaseReference toxinRef;
    String levelItemSelected;
    private ArrayList<ToxinModel> toxinsArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_plant_toxins);

        database = FirebaseDatabase.getInstance();
        toxinRef = database.getReference("Toxins");

        back = findViewById(R.id.back);
        addToxins = findViewById(R.id.addToxins);

        plantToxinsRec = findViewById(R.id.plantToxinsRec);
        searchToxins = findViewById(R.id.searchToxins);

        fetchedFromAdminPage();

        plantToxinsRec.setLayoutManager(new LinearLayoutManager(this));
        ToxinsAdapter adapter = new ToxinsAdapter(this, toxinsArray);
        plantToxinsRec.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        adapter.setOnItemClickListener(new ToxinsAdapter.onToxinItemClickListener() {
            @Override
            public void onItemClick(int position) {
                // pending
            }

            @Override
            public void onEditClick(int position) {
                String toxinName = toxinsArray.get(position).getToxinName();
                String toxinDesc = toxinsArray.get(position).getToxinDesc();
                String toxinConsumptionLevel = toxinsArray.get(position).getToxinConsumptionLevel();
                String toxinEffects = toxinsArray.get(position).getToxinEffects();
                openUpdateToxinDialog(toxinName, toxinDesc, toxinConsumptionLevel, toxinEffects, position);
            }

            @Override
            public void onDeleteClick(int position) {
                openDeleteDialog(adapter, position);
            }
        });

        searchToxins.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        addToxins.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAddToxinDialog(adapter);
            }
        });
    }
    public void openAddToxinDialog(ToxinsAdapter adapter) {
        ConstraintLayout addToxinDialog = findViewById(R.id.addToxinDialog);
        View view = LayoutInflater.from(ManagePlantToxinsActivity.this).inflate(R.layout.add_toxin_dialog, addToxinDialog);
        Button addToxinBtn = view.findViewById(R.id.addToxinBtn);
        EditText toxinName = view.findViewById(R.id.toxinName);
        EditText toxinDesc = view.findViewById(R.id.toxinDesc);
        EditText toxinEffects = view.findViewById(R.id.toxinEffects);
        AutoCompleteTextView toxinLevel = view.findViewById(R.id.toxinLevel);

        String[] level = {"Low", "Average", "High"};
        ArrayAdapter<String> levelAdapter = new ArrayAdapter<>(view.getContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, level);
        toxinLevel.setAdapter(levelAdapter);

        toxinLevel.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                levelItemSelected = parent.getItemAtPosition(position).toString();
                Toast.makeText(ManagePlantToxinsActivity.this, levelItemSelected, Toast.LENGTH_SHORT).show();
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(ManagePlantToxinsActivity.this);
        builder.setView(view);
        final AlertDialog alertDialog = builder.create();

        addToxinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = toxinName.getText().toString().trim();
                String desc = toxinDesc.getText().toString().trim();
                String level = levelItemSelected;
                String effects = toxinEffects.getText().toString().trim();

                if (name.isEmpty()) {
                    toxinName.setError("Toxin name is empty");
                } else if (desc.isEmpty()) {
                    toxinDesc.setError("Toxin Description is empty");
                } else if (level == null) {
                    Toast.makeText(ManagePlantToxinsActivity.this, "No Toxin Level Selected", Toast.LENGTH_SHORT).show();
                } else if (effects.isEmpty()) {
                    toxinEffects.setError("Toxin Effects is empty");
                } else {
                    ToxinModel toxinModel = new ToxinModel(name, desc, level, effects);

                    Toast.makeText(ManagePlantToxinsActivity.this, "Sending", Toast.LENGTH_SHORT).show();
                    toxinRef.child(name).setValue(toxinModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                toxinsArray.add(toxinModel);
                                adapter.notifyDataSetChanged();
                                levelItemSelected = null;
                                Toast.makeText(ManagePlantToxinsActivity.this, "Toxin Added Successful", Toast.LENGTH_SHORT).show();
                                alertDialog.dismiss();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ManagePlantToxinsActivity.this, "Error: " + e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }
    public void openUpdateToxinDialog(String toxinName, String toxinDesc, String toxinConsumptionLevel, String toxinEffects, int position) {
        LinearLayout editToxinDialog = findViewById(R.id.editToxinDialog);
        View view = LayoutInflater.from(ManagePlantToxinsActivity.this).inflate(R.layout.edit_toxin_dialog, editToxinDialog);
        EditText updateToxinName = view.findViewById(R.id.updateToxinName);
        EditText updateToxinDesc = view.findViewById(R.id.updateToxinDesc);
        AutoCompleteTextView updateToxinLevel = view.findViewById(R.id.updateToxinLevel);
        EditText updateToxinEffects = view.findViewById(R.id.updateToxinEffects);
        Button updateToxinBtn = view.findViewById(R.id.updateToxinBtn);

        levelItemSelected = toxinConsumptionLevel;

        // set the values of editText
        updateToxinName.setText(toxinName);
        updateToxinDesc.setText(toxinDesc);
        updateToxinLevel.setText(toxinConsumptionLevel);
        updateToxinEffects.setText(toxinEffects);

        updateToxinName.setEnabled(false);

        String[] level = {"Low", "Average", "High"};
        ArrayAdapter<String> levelAdapter = new ArrayAdapter<>(view.getContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, level);
        updateToxinLevel.setAdapter(levelAdapter);

        updateToxinLevel.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                levelItemSelected = parent.getItemAtPosition(position).toString();
                Toast.makeText(ManagePlantToxinsActivity.this, levelItemSelected, Toast.LENGTH_SHORT).show();
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(ManagePlantToxinsActivity.this);
        builder.setView(view);
        final AlertDialog alertDialog = builder.create();

        updateToxinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = updateToxinName.getText().toString().trim();
                String desc = updateToxinDesc.getText().toString().trim();
                String level = levelItemSelected;
                String effects = updateToxinEffects.getText().toString().trim();

                if (name.isEmpty()) {
                    updateToxinName.setError("Toxin name is empty");
                } else if (desc.isEmpty()) {
                    updateToxinDesc.setError("Toxin Description is empty");
                } else if (level == null) {
                    Toast.makeText(ManagePlantToxinsActivity.this, "No Toxin Level Selected", Toast.LENGTH_SHORT).show();
                } else if (effects.isEmpty()) {
                    updateToxinEffects.setError("Toxin Effects is empty");
                } else {
                    alertDialog.dismiss();

                    AlertDialog.Builder builder1 = new AlertDialog.Builder(ManagePlantToxinsActivity.this);
                    builder1.setView(R.layout.progress_bar_layout);
                    builder1.setCancelable(false);
                    AlertDialog dialog1 = builder1.create();
                    dialog1.show();

                    ToxinModel toxinModel = new ToxinModel(name, desc, level, effects);

                    Toast.makeText(ManagePlantToxinsActivity.this, "Updating Toxin", Toast.LENGTH_SHORT).show();
                    toxinRef.child(name).setValue(toxinModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                levelItemSelected = null;
                                toxinsArray.set(position, toxinModel);
                                Toast.makeText(ManagePlantToxinsActivity.this, "Update Successful", Toast.LENGTH_SHORT).show();
                                dialog1.dismiss();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ManagePlantToxinsActivity.this, "Error: " + e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }
    private void openDeleteDialog(ToxinsAdapter adapter, int position) {
        CardView deleteDialog = findViewById(R.id.deleteDialog);
        View view = LayoutInflater.from(ManagePlantToxinsActivity.this).inflate(R.layout.delete_confirmation_dialog, deleteDialog);
        TextView deleteDialogText = view.findViewById(R.id.deleteDialogText);
        TextView cancelDelete = view.findViewById(R.id.cancelDelete);
        TextView confirmDelete = view.findViewById(R.id.confirmDelete);

        AlertDialog.Builder builder = new AlertDialog.Builder(ManagePlantToxinsActivity.this);
        builder.setView(view);
        final AlertDialog alertDialog = builder.create();

        String toxinName = toxinsArray.get(position).getToxinName();
        String dialogText = "Are you sure you want to delete " + toxinName + "?";
        deleteDialogText.setText(dialogText);

        AlertDialog.Builder builder1 = new AlertDialog.Builder(ManagePlantToxinsActivity.this);
        builder1.setCancelable(false);
        builder1.setView(R.layout.delete_progress_bar);
        AlertDialog dialog = builder1.create();

        confirmDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();

                dialog.show();

                Toast.makeText(ManagePlantToxinsActivity.this, "Deleting " + toxinName, Toast.LENGTH_SHORT).show();
                toxinRef.child(toxinName).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        toxinsArray.remove(position);
                        adapter.notifyItemRemoved(position);
                        Toast.makeText(ManagePlantToxinsActivity.this, toxinName + " deleted", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ManagePlantToxinsActivity.this, "Delete error: " + e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
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
    public void fetchedFromAdminPage() {
        Intent intent = getIntent();
        toxinsArray = (ArrayList<ToxinModel>) intent.getSerializableExtra("toxinsArray");
    }
    private void searchLists(ToxinsAdapter adapter, String text) {
        ArrayList<ToxinModel> lists = new ArrayList<>();
        for (ToxinModel toxin: toxinsArray) {
            if (toxin.getToxinName().toLowerCase().contains(text)) {
                lists.add(toxin);
            }
        }
        adapter.searchLists(lists);
    }
}