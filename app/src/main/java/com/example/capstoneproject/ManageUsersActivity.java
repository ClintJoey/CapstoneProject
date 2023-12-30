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
import com.example.capstoneproject.adapters.UserAdapter;
import com.example.capstoneproject.models.UserAccountModel;
import com.example.capstoneproject.singleton.UserDataManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class ManageUsersActivity extends AppCompatActivity {
    FirebaseDatabase database;
    DatabaseReference userRef;
    StorageReference profileRef;
    private ImageView back;
    private RecyclerView usersRec;
    private SearchView searchUsers;
    private FloatingActionButton addUserFB;
    private ArrayList<UserAccountModel> usersArray = UserDataManager.getInstance().getUsers();
    private UserAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_users);

        database = FirebaseDatabase.getInstance();
        userRef = database.getReference("testUsers2");
        profileRef = FirebaseStorage.getInstance().getReference();

        usersRec = findViewById(R.id.usersRec);
        searchUsers = findViewById(R.id.searchUsers);

        back = findViewById(R.id.back);
        addUserFB = findViewById(R.id.addUserFB);

        usersRec.setLayoutManager(new LinearLayoutManager(ManageUsersActivity.this));
        adapter = new UserAdapter(ManageUsersActivity.this, usersArray);
        usersRec.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        adapter.setOnItemClickListener(new UserAdapter.onUserListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(ManageUsersActivity.this, UserProfileActivity.class);
                intent.putExtra("userUid", usersArray.get(position).userUid);
                intent.putExtra("firstname", usersArray.get(position).firstname);
                intent.putExtra("middlename", usersArray.get(position).middlename);
                intent.putExtra("lastname", usersArray.get(position).lastname);
                intent.putExtra("sex", usersArray.get(position).sex);
                intent.putExtra("age", usersArray.get(position).age);
                intent.putExtra("phoneNumber", usersArray.get(position).phoneNumber);
                intent.putExtra("barangay", usersArray.get(position).barangay);
                intent.putExtra("municipality", usersArray.get(position).municipality);
                intent.putExtra("province", usersArray.get(position).province);
                intent.putExtra("email", usersArray.get(position).email);
                intent.putExtra("password", usersArray.get(position).password);
                intent.putExtra("role", usersArray.get(position).role);
                intent.putExtra("profileImg", usersArray.get(position).profileImg);
                intent.putStringArrayListExtra("reports", usersArray.get(position).reports);
                startActivity(intent);
            }

            @Override
            public void onDeleteClick(int position) {
                openDeleteDialog(position);
            }
        });

        searchUsers.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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

        addUserFB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManageUsersActivity.this, AdminCreateUserActivity.class);
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

    @Override
    protected void onResume() {
        super.onResume();

        adapter.notifyDataSetChanged();
    }

    private void openDeleteDialog(int position) {
        // pending create a delete dialog
        CardView deleteDialog = findViewById(R.id.deleteDialog);
        View view = LayoutInflater.from(ManageUsersActivity.this).inflate(R.layout.delete_confirmation_dialog, deleteDialog);
        TextView deleteDialogText = view.findViewById(R.id.deleteDialogText);
        TextView cancelDelete = view.findViewById(R.id.cancelDelete);
        TextView confirmDelete = view.findViewById(R.id.confirmDelete);

        AlertDialog.Builder builder = new AlertDialog.Builder(ManageUsersActivity.this);
        builder.setView(view);
        AlertDialog dialog = builder.create();

        String userName = usersArray.get(position).firstname;
        String userUid = usersArray.get(position).userUid;
        String dialogText = "Are you sure you want to delete " + userName + " ?";
        deleteDialogText.setText(dialogText);

        confirmDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userRef.child(userUid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            UserDataManager.getInstance().deleteUser(userUid);
                            dialog.dismiss();
                            adapter.notifyItemRemoved(position);
                            Toast.makeText(ManageUsersActivity.this, "User deleted", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ManageUsersActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
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
    private void searchLists(UserAdapter adapter, String text) {
        ArrayList<UserAccountModel> lists = new ArrayList<>();
        for (UserAccountModel user: usersArray) {
            if (user.firstname.toLowerCase().contains(text.toLowerCase())) {
                lists.add(user);
            }
        }
        adapter.searchLists(lists);
    }
}