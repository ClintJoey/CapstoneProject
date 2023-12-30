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

import com.example.capstoneproject.adapters.HarmfulPlantAdapter;
import com.example.capstoneproject.models.HarmfulPlantModel;
import com.example.capstoneproject.models.ToxinModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ManageHarmfulPlantsActivity extends AppCompatActivity {
    FirebaseDatabase database;
    DatabaseReference harmfulPlantsRef;
    private ImageView back;
    private RecyclerView harmfulPlantsRec;
    private SearchView searchView;
    private FloatingActionButton addHarmfulPlantFB;
    private ArrayList<ToxinModel> toxinsArray = new ArrayList<>();
    private ArrayList<HarmfulPlantModel> harmfulPlantsArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_harmful_plants);

        database = FirebaseDatabase.getInstance();
        harmfulPlantsRef = database.getReference("Harmful Plants");

        harmfulPlantsRec = findViewById(R.id.harmfulPlantsRec);
        searchView = findViewById(R.id.searchView);

        back = findViewById(R.id.back);
        addHarmfulPlantFB = findViewById(R.id.addHarmfulPlantFB);

        fetchedToxins();

        // setup harmful plants recycler view
        harmfulPlantsRec.setLayoutManager(new LinearLayoutManager(this));
        HarmfulPlantAdapter adapter = new HarmfulPlantAdapter(this, harmfulPlantsArray);
        harmfulPlantsRec.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        adapter.setOnItemClickListener(new HarmfulPlantAdapter.onHarmfulPlantListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(ManageHarmfulPlantsActivity.this, AdminHarmfulPlantInfoActivity.class);
                intent.putExtra("plantName", harmfulPlantsArray.get(position).getPlantName());
                intent.putExtra("harmfulPlantsArray", harmfulPlantsArray);
                startActivity(intent);
            }

            @Override
            public void onDeleteClick(int position) {
                openDeleteDialog(adapter, position);
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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
        addHarmfulPlantFB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManageHarmfulPlantsActivity.this, AddHarmfulPlantActivity.class);
                intent.putExtra("toxinsArray", toxinsArray);
                startActivity(intent);
            }
        });
    }
    private void openDeleteDialog(HarmfulPlantAdapter adapter, int position) {
        // pending create a delete dialog
        CardView deleteDialog = findViewById(R.id.deleteDialog);
        View view = LayoutInflater.from(ManageHarmfulPlantsActivity.this).inflate(R.layout.delete_confirmation_dialog, deleteDialog);
        TextView deleteDialogText = view.findViewById(R.id.deleteDialogText);
        TextView cancelDelete = view.findViewById(R.id.cancelDelete);
        TextView confirmDelete = view.findViewById(R.id.confirmDelete);

        AlertDialog.Builder builder = new AlertDialog.Builder(ManageHarmfulPlantsActivity.this);
        builder.setView(view);
        AlertDialog dialog = builder.create();

        String plantName = harmfulPlantsArray.get(position).getPlantName();
        String dialogText = "Are you sure you want to delete " + plantName + "?";
        deleteDialogText.setText(dialogText);

        confirmDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ManageHarmfulPlantsActivity.this, "Deleting " + plantName, Toast.LENGTH_SHORT).show();
                harmfulPlantsRef.child(plantName).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        harmfulPlantsArray.remove(position);
                        adapter.notifyItemRemoved(position);
                        dialog.dismiss();
                        Toast.makeText(ManageHarmfulPlantsActivity.this, plantName + " deleted", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ManageHarmfulPlantsActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
    private void fetchedToxins() {
        Intent intent = getIntent();
        toxinsArray = (ArrayList<ToxinModel>) intent.getSerializableExtra("toxinsArray");
        harmfulPlantsArray = (ArrayList<HarmfulPlantModel>) intent.getSerializableExtra("harmfulPlantsArray");
    }
    private void searchLists(HarmfulPlantAdapter adapter, String text) {
        ArrayList<HarmfulPlantModel> lists = new ArrayList<>();
        for (HarmfulPlantModel plant: harmfulPlantsArray) {
            if (plant.getPlantName().toLowerCase().contains(text.toLowerCase())) {
                lists.add(plant);
            }
        }
        adapter.searchLists(lists);
    }
}