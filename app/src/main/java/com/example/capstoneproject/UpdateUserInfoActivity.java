package com.example.capstoneproject;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.capstoneproject.models.UserAccountModel;
import com.example.capstoneproject.singleton.UserDataManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class UpdateUserInfoActivity extends AppCompatActivity {
    FirebaseDatabase database;
    DatabaseReference userRef;
    StorageReference profilePicRef;
    private ImageView updateProfileImg, back;
    private EditText updateFirstName, updateMiddleName, updateLastName, updateAge, updateBarangay, updateMunicipality, updateProvince;
    private RadioGroup radioSex;
    private RadioButton male, female;
    private AutoCompleteTextView userRoleATV;
    private Button updateUserBtn;
    private UserAccountModel user;
    Uri imageUri;
    String updatedPic = null;
    String sex;
    String selectedRole = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_user_info);

        database = FirebaseDatabase.getInstance();
        userRef = database.getReference("testUsers2");
        profilePicRef = FirebaseStorage.getInstance().getReference("Profile Pictures");

        updateProfileImg = findViewById(R.id.updateProfileImg);
        updateFirstName = findViewById(R.id.updateFirstName);
        updateMiddleName = findViewById(R.id.updateMiddleName);
        updateLastName = findViewById(R.id.updateLastName);
        radioSex = findViewById(R.id.radioSex);
        male = findViewById(R.id.male);
        female = findViewById(R.id.female);
        updateAge = findViewById(R.id.updateAge);
        userRoleATV = findViewById(R.id.userRoleATV);
        updateBarangay = findViewById(R.id.updateBarangay);
        updateMunicipality = findViewById(R.id.updateMunicipality);
        updateProvince = findViewById(R.id.updateProvince);
        updateUserBtn = findViewById(R.id.updateUserBtn);
        back = findViewById(R.id.back);

        fetchIntent();

        // image picker
        ActivityResultLauncher<Intent> imagePicker = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        imageUri = data.getData();
                        updateProfileImg.setImageURI(imageUri);
                        updatedPic = imageUri.toString();
                    }
                });

        updateProfileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                imagePicker.launch(intent);
            }
        });

        if (sex.equals("Male")) {
            male.setChecked(true);
            female.setChecked(false);
        } else {
            male.setChecked(false);
            female.setChecked(true);
        }

        userRoleATV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedRole = parent.getItemAtPosition(position).toString();
            }
        });

        updateUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // include profile pic
                String firstname = updateFirstName.getText().toString().trim();
                String middlename = updateMiddleName.getText().toString().trim();
                String lastname = updateLastName.getText().toString().trim();
                // include sex
                int age = Integer.parseInt(updateAge.getText().toString().trim());
                // include role selected
                String barangay = updateBarangay.getText().toString().trim();
                String municipality = updateMunicipality.getText().toString().trim();
                String province = updateProvince.getText().toString().trim();

                if (firstname.isEmpty()) {
                    updateFirstName.setError("First name field is empty");
                    updateFirstName.requestFocus();
                } else if (middlename.isEmpty()) {
                    updateMiddleName.setError("Middle name field is empty");
                    updateMiddleName.requestFocus();
                } else if (lastname.isEmpty()) {
                    updateLastName.setError("Last name field is empty");
                    updateLastName.requestFocus();
                } else if (updateAge.getText().toString().trim().isEmpty()) {
                    updateAge.setError("Age field is empty");
                    updateAge.requestFocus();
                } else if (selectedRole == null || user.role.isEmpty()) {
                    Toast.makeText(UpdateUserInfoActivity.this, "Please select a role", Toast.LENGTH_SHORT).show();
                } else if (barangay.isEmpty()) {
                    updateBarangay.setError("Barangay field is empty");
                    updateBarangay.requestFocus();
                } else if (municipality.isEmpty()) {
                    updateMunicipality.setError("Municipality field is empty");
                    updateMunicipality.requestFocus();
                } else if (province.isEmpty()) {
                    updateProvince.setError("Province field is empty");
                    updateProvince.requestFocus();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(UpdateUserInfoActivity.this);
                    builder.setCancelable(false);
                    builder.setView(R.layout.progress_bar_layout);
                    AlertDialog dialog = builder.create();
                    dialog.show();

                    if (updatedPic != null) {
                        StorageReference imageRef = profilePicRef.child(System.currentTimeMillis() + "." + getFileExtension(imageUri));

                        imageRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        UserAccountModel updatedUser = new UserAccountModel(user.userUid, firstname, middlename, lastname, sex, age,
                                                user.phoneNumber, barangay, municipality, province, user.email, user.password, selectedRole, uri.toString(), user.reports);

                                        userRef.child(user.userUid).setValue(updatedUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                UserDataManager.getInstance().updateUserProfile(updatedUser);
                                                dialog.dismiss();
                                                Toast.makeText(UpdateUserInfoActivity.this, "User's profile updated", Toast.LENGTH_SHORT).show();
                                                finish();
                                            }
                                        });
                                    }
                                });
                            }
                        });
                    } else {
                        UserAccountModel updatedUser = new UserAccountModel(user.userUid, firstname, middlename, lastname, sex, age,
                                user.phoneNumber, barangay, municipality, province, user.email, user.password, selectedRole, user.profileImg, user.reports);

                        userRef.child(user.userUid).setValue(updatedUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                UserDataManager.getInstance().updateUserProfile(updatedUser);
                                dialog.dismiss();
                                Toast.makeText(UpdateUserInfoActivity.this, "User's profile updated", Toast.LENGTH_SHORT).show();
                                finish();
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

    private void fetchIntent() {
        Intent intent = getIntent();
        user = (UserAccountModel) intent.getSerializableExtra("user");

        Glide.with(UpdateUserInfoActivity.this).load(user.profileImg).into(updateProfileImg);
        updateFirstName.setText(user.firstname);
        updateMiddleName.setText(user.middlename);
        updateLastName.setText(user.lastname);
        sex = user.sex;
        updateAge.setText(String.valueOf(user.age));

        String[] roles = {"Employee", "Manager", "Admin"};
        selectedRole = user.role;
        userRoleATV.setText(selectedRole);
        ArrayAdapter rolesAdapter = new ArrayAdapter(UpdateUserInfoActivity.this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, roles);
        userRoleATV.setAdapter(rolesAdapter);

        updateBarangay.setText(user.barangay);
        updateMunicipality.setText(user.municipality);
        updateProvince.setText(user.province);
    }
    private String getFileExtension(Uri uri) {
        ContentResolver resolver = this.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(resolver.getType(uri));
    }
}