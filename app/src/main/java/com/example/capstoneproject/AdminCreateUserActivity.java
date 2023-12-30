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
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.capstoneproject.models.UserAccountModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;

public class AdminCreateUserActivity extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference userRef;
    FirebaseAuth auth;
    StorageReference profilesRef;
    private ImageView createProfileImg;
    private EditText createFirstName, createMiddleName, createLastName, createAge, createPhoneNumber, createBarangay, createMunicipality,
        createProvince, createEmail, createPassword, createConfirmPassword;
    private RadioGroup radioSex;
    private AutoCompleteTextView userRoleATV;
    private Button createUserBtn;
    Uri uri;
    String profilePic = null;
    String sex = "Male";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_create_user);

        database = FirebaseDatabase.getInstance();
        userRef = database.getReference("testUsers2");
        auth = FirebaseAuth.getInstance();
        profilesRef = FirebaseStorage.getInstance().getReference("Profile Pictures");

        createProfileImg = findViewById(R.id.createProfileImg);
        createFirstName = findViewById(R.id.createFirstName);
        createMiddleName = findViewById(R.id.createMiddleName);
        createLastName = findViewById(R.id.createLastName);
        createAge = findViewById(R.id.createAge);
        createPhoneNumber = findViewById(R.id.createPhoneNumber);
        createBarangay = findViewById(R.id.createBarangay);
        createMunicipality = findViewById(R.id.createMunicipality);
        createProvince = findViewById(R.id.createProvince);
        createEmail = findViewById(R.id.createEmail);
        createPassword = findViewById(R.id.createPassword);
        createConfirmPassword = findViewById(R.id.createConfirmPassword);
        radioSex = findViewById(R.id.radioSex);
        userRoleATV = findViewById(R.id.userRoleATV);
        createUserBtn = findViewById(R.id.createUserBtn);

        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            uri = data.getData();
                            createProfileImg.setImageURI(uri);
                            profilePic = uri.toString();
                        } else {
                            Toast.makeText(AdminCreateUserActivity.this, "Profile picture is not set", Toast.LENGTH_SHORT).show();
                        }
                    }
        });

        createProfileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPicker = new Intent(Intent.ACTION_PICK);
                photoPicker.setType("image/*");
                activityResultLauncher.launch(photoPicker);
            }
        });

        radioSex.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.male) {
                    sex = "Male";
                }
                if (checkedId == R.id.female) {
                    sex = "Female";
                }
            }
        });

        String[] roles = {"Employee","Manager","Admin"};
        ArrayAdapter<String> atvAdapter = new ArrayAdapter<>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, roles);
        userRoleATV.setAdapter(atvAdapter);

        createUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // include profile pic
                String firstname = createFirstName.getText().toString().trim();
                String middlename = createMiddleName.getText().toString().trim();
                String lastname = createLastName.getText().toString().trim();
                String selectedSex = sex;
                String age = createAge.getText().toString().trim();
                String phoneNumber = createPhoneNumber.getText().toString().trim();
                String role = userRoleATV.getText().toString();
                String barangay = createBarangay.getText().toString().trim();
                String municipality = createMunicipality.getText().toString().trim();
                String province = createProvince.getText().toString().trim();
                String email = createEmail.getText().toString().trim();
                String password = createPassword.getText().toString().trim();
                String confirmPassword = createConfirmPassword.getText().toString().trim();
                ArrayList<String> reports = new ArrayList<>();
                reports.add("");

                // user input validation
                if (profilePic == null) {
                    Toast.makeText(AdminCreateUserActivity.this, "No Image Selected", Toast.LENGTH_SHORT).show();
                } else if (firstname.isEmpty()) {
                    createFirstName.setError("First name field is empty");
                    createFirstName.requestFocus();
                } else if (middlename.isEmpty()) {
                    createMiddleName.setError("Middle name field is empty");
                    createMiddleName.requestFocus();
                } else if (lastname.isEmpty()) {
                    createLastName.setError("Last name field is empty");
                    createLastName.requestFocus();
                } else if (age.isEmpty()) {
                    createAge.setError("Age field is empty");
                    createAge.requestFocus();
                } else if (role.isEmpty()) {
                    Toast.makeText(AdminCreateUserActivity.this, "Please specify the role of the user", Toast.LENGTH_SHORT).show();
                } else if (phoneNumber.isEmpty()) {
                    createPhoneNumber.setError("Phone number field is empty");
                    createPhoneNumber.requestFocus();
                } else if (!phoneNumber.startsWith("09") && phoneNumber.length() < 11) {
                    createPhoneNumber.setError("Invalid Number");
                    createPhoneNumber.requestFocus();
                } else if (barangay.isEmpty()) {
                    createBarangay.setError("Barangay field is empty");
                    createBarangay.requestFocus();
                } else if (municipality.isEmpty()) {
                    createMunicipality.setError("Municipality field is empty");
                    createMunicipality.requestFocus();
                } else if (province.isEmpty()) {
                    createProvince.setError("Province field is empty");
                    createProvince.requestFocus();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    createEmail.setError("Invalid email");
                    createEmail.requestFocus();
                } else if (password.isEmpty()) {
                    createPassword.setError("Password field is empty");
                    createPassword.requestFocus();
                } else if (!password.equals(confirmPassword)) {
                    createConfirmPassword.setError("Password does not match");
                    createConfirmPassword.requestFocus();
                } else {
                    StorageReference imageReference = profilesRef.child(System.currentTimeMillis() + "." + getFileExtension(uri));

                    AlertDialog.Builder builder = new AlertDialog.Builder(AdminCreateUserActivity.this);
                    builder.setCancelable(false);
                    builder.setView(R.layout.signup_progress_bar);
                    AlertDialog dialog = builder.create();
                    dialog.show();

                    imageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            imageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    // authenticate user using firebase auth and if successful, save user data to firebase realtime database
                                    auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()) {
                                                String userUid = auth.getCurrentUser().getUid();
                                                int parseAge = Integer.parseInt(age);

                                                UserAccountModel data = new UserAccountModel(userUid, firstname, middlename, lastname, selectedSex, parseAge, phoneNumber, barangay, municipality, province, email, password, role, uri.toString(), reports);
                                                userRef.child(userUid).setValue(data);

                                                Toast.makeText(AdminCreateUserActivity.this, "User Successfully created", Toast.LENGTH_SHORT).show();
                                                // signout the created user
                                                dialog.dismiss();
                                                startActivity(new Intent(AdminCreateUserActivity.this, ManageUsersActivity.class));
                                                finish();
                                            } else {
                                                Toast.makeText(AdminCreateUserActivity.this, "Signup Failed "+ task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            });
                        }
                    });
                }
            }
        });
    }
    private String getFileExtension(Uri fileUri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(fileUri));
    }
}