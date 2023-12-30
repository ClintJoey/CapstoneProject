package com.example.capstoneproject;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;

public class EditProfileActivity extends AppCompatActivity {
    private EditText editFirstname, editMiddlename, editLastname, editAge, editBarangay, editMunicipality, editProvince;
    private RadioGroup editSex;
    private RadioButton maleBtn, femaleBtn;
    private Button editProfileBtn;
    private ImageView editProfileImg;
    private DatabaseReference databaseReference;
    private StorageReference profileRef;
    private FirebaseAuth auth;
    String firstname, middlename, lastname, sex, barangay, municipality, province;
    Uri uri;
    String updatedPic = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        auth = FirebaseAuth.getInstance();

        databaseReference = FirebaseDatabase.getInstance().getReference("testUsers2");
        profileRef = FirebaseStorage.getInstance().getReference("Profile Pictures");

        editProfileImg = findViewById(R.id.editProfileImg);
        editFirstname = findViewById(R.id.editFirstname);
        editMiddlename = findViewById(R.id.editMiddlename);
        editLastname = findViewById(R.id.editLastname);
        editSex = findViewById(R.id.editSex);
        maleBtn = findViewById(R.id.male);
        femaleBtn = findViewById(R.id.female);
        editAge = findViewById(R.id.editAge);
        editBarangay = findViewById(R.id.editBarangay);
        editMunicipality = findViewById(R.id.editMunicipality);
        editProvince = findViewById(R.id.editProvince);
        editProfileBtn = findViewById(R.id.editProfileBtn);

        fetchProfileIntent();

        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            uri = data.getData();
                            editProfileImg.setImageURI(uri);
                            updatedPic = uri.toString();
                        } else {
                            Toast.makeText(EditProfileActivity.this, "Profile picture is not set", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        editProfileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPicker = new Intent(Intent.ACTION_PICK);
                photoPicker.setType("image/*");
                activityResultLauncher.launch(photoPicker);
            }
        });

        editSex.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.male) {
                    sex = "Male";
                } else {
                    sex = "Female";
                }
            }
        });

        editProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile();
            }
        });
    }

    public void fetchProfileIntent() {
        Intent intent = getIntent();
        Glide.with(EditProfileActivity.this).load(intent.getStringExtra("profileImg")).into(editProfileImg);
        editFirstname.setText(intent.getStringExtra("firstname"));
        editMiddlename.setText(intent.getStringExtra("middlename"));
        editLastname.setText(intent.getStringExtra("lastname"));
        sex = intent.getStringExtra("sex");
        if (sex.equals("Male")) {
            maleBtn.setChecked(true);
            femaleBtn.setChecked(false);
        } else {
            maleBtn.setChecked(false);
            femaleBtn.setChecked(true);
        }
        editAge.setText(intent.getStringExtra("age"));
        editBarangay.setText(intent.getStringExtra("barangay"));
        editMunicipality.setText(intent.getStringExtra("municipality"));
        editProvince.setText(intent.getStringExtra("province"));
    }

    public void updateProfile() {
        String userUid = auth.getCurrentUser().getUid();
        firstname = editFirstname.getText().toString().trim();
        middlename = editMiddlename.getText().toString().trim();
        lastname = editLastname.getText().toString().trim();
        int age = Integer.parseInt(editAge.getText().toString().trim());
        barangay = editBarangay.getText().toString().trim();
        municipality = editMunicipality.getText().toString().trim();
        province = editProvince.getText().toString().trim();

        // user input validation
        if (firstname.isEmpty()) {
            editFirstname.setError("First name field is empty");
        } else if (middlename.isEmpty()) {
            editMiddlename.setError("Middle name field is empty");
        } else if (lastname.isEmpty()) {
            editLastname.setError("Last name field is empty");
        } else if (editAge.getText().toString().trim().isEmpty()) {
            editAge.setError("Age field is empty");
        } else if (barangay.isEmpty()) {
            editBarangay.setError("Barangay field is empty");
        } else if (municipality.isEmpty()) {
            editMunicipality.setError("Municipality field is empty");
        } else if (province.isEmpty()) {
            editProvince.setError("Province field is empty");
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(EditProfileActivity.this);
            builder.setCancelable(false);
            builder.setView(R.layout.progress_bar_layout);
            AlertDialog dialog = builder.create();
            dialog.show();

            if (updatedPic != null) {
                StorageReference imageRef = profileRef.child(System.currentTimeMillis() + "." + getFileExtension(uri));

                imageRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                databaseReference.child(userUid).child("profileImg").setValue(uri.toString());
                                databaseReference.child(userUid).child("firstname").setValue(firstname);
                                databaseReference.child(userUid).child("middlename").setValue(middlename);
                                databaseReference.child(userUid).child("lastname").setValue(lastname);
                                databaseReference.child(userUid).child("sex").setValue(sex);
                                databaseReference.child(userUid).child("age").setValue(age);
                                databaseReference.child(userUid).child("barangay").setValue(barangay);
                                databaseReference.child(userUid).child("municipality").setValue(municipality);
                                databaseReference.child(userUid).child("province").setValue(province);

                                dialog.dismiss();

                                Intent intent = new Intent(EditProfileActivity.this, HomeActivity.class);
                                startActivity(intent);
                                Toast.makeText(EditProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            } else {
                databaseReference.child(userUid).child("firstname").setValue(firstname);
                databaseReference.child(userUid).child("middlename").setValue(middlename);
                databaseReference.child(userUid).child("lastname").setValue(lastname);
                databaseReference.child(userUid).child("sex").setValue(sex);
                databaseReference.child(userUid).child("age").setValue(age);
                databaseReference.child(userUid).child("barangay").setValue(barangay);
                databaseReference.child(userUid).child("municipality").setValue(municipality);
                databaseReference.child(userUid).child("province").setValue(province);

                dialog.dismiss();

                Intent intent = new Intent(EditProfileActivity.this, HomeActivity.class);
                startActivity(intent);
                Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private String getFileExtension(Uri fileUri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(fileUri));
    }
}