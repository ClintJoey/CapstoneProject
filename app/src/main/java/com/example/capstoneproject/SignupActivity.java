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

public class SignupActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private StorageReference storageReference;
    private EditText signupFirstName, signupMiddleName, signupLastName, signupAge, signupPhoneNumber, signupBarangay, signupMunicipality, signupProvince, signupEmail, signupPassword, signupConfirmPassword;
    private RadioGroup radioSex;
    private Button signupBtn;
    private ImageView signupProfileImg;
    public String role = "Employee";
    public String sex = "Male";
    Uri uri;
    String profilePic = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("testUsers2");
        storageReference = FirebaseStorage.getInstance().getReference("Profile Pictures");

        signupFirstName = findViewById(R.id.signupFirstName);
        signupMiddleName = findViewById(R.id.signupMiddleName);
        signupLastName = findViewById(R.id.signupLastName);
        radioSex = findViewById(R.id.radioSex);
        signupAge = findViewById(R.id.signupAge);
        signupPhoneNumber = findViewById(R.id.signupPhoneNumber);
        signupEmail = findViewById(R.id.signupEmail);
        signupPassword = findViewById(R.id.signupPassword);
        signupConfirmPassword = findViewById(R.id.signupConfirmPassword);
        signupBarangay = findViewById(R.id.signupBarangay);
        signupMunicipality = findViewById(R.id.signupMunicipality);
        signupProvince = findViewById(R.id.signupProvince);
        signupBtn = findViewById(R.id.signupBtn);
        signupProfileImg = findViewById(R.id.signupProfileImg);

        // profile picture changer
        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    uri = data.getData();
                    signupProfileImg.setImageURI(uri);
                    profilePic = uri.toString();
                } else {
                    Toast.makeText(SignupActivity.this, "Profile picture is not set", Toast.LENGTH_SHORT).show();
                }
            }
        });

        signupProfileImg.setOnClickListener(new View.OnClickListener() {
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

        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String firstName = signupFirstName.getText().toString().trim();
                String middleName = signupMiddleName.getText().toString().trim();
                String lastName = signupLastName.getText().toString().trim();
                String selectedSex = sex;
                String getAge = signupAge.getText().toString().trim();
                String phoneNumber = signupPhoneNumber.getText().toString();
                String barangay = signupBarangay.getText().toString().trim();
                String municipality = signupMunicipality.getText().toString().trim();
                String province = signupProvince.getText().toString().trim();
                String email = signupEmail.getText().toString().trim();
                String password = signupPassword.getText().toString().trim();
                String confirmPassword = signupConfirmPassword.getText().toString().trim();
                ArrayList<String> reports = new ArrayList<>();

                reports.add("");

                // user input validation
                if (profilePic == null) {
                    Toast.makeText(SignupActivity.this, "No Image Selected", Toast.LENGTH_SHORT).show();
                } else if (firstName.isEmpty()) {
                    signupFirstName.setError("First name field is empty");
                    signupFirstName.requestFocus();
                } else if (middleName.isEmpty()) {
                    signupMiddleName.setError("Middle name field is empty");
                    signupMiddleName.requestFocus();
                } else if (lastName.isEmpty()) {
                    signupLastName.setError("Last name field is empty");
                    signupLastName.requestFocus();
                } else if (getAge.isEmpty()) {
                    signupAge.setError("Age field is empty");
                    signupAge.requestFocus();
                } else if (phoneNumber.isEmpty()) {
                    signupPhoneNumber.setError("Phone number field is empty");
                    signupPhoneNumber.requestFocus();
                } else if (phoneNumber.length() < 11) {
                    signupPhoneNumber.setError("Phone number must be 11 digits");
                    signupPhoneNumber.requestFocus();
                } else if (barangay.isEmpty()) {
                    signupBarangay.setError("Barangay field is empty");
                    signupBarangay.requestFocus();
                } else if (municipality.isEmpty()) {
                    signupMunicipality.setError("Municipality field is empty");
                    signupMunicipality.requestFocus();
                } else if (province.isEmpty()) {
                    signupProvince.setError("Province field is empty");
                    signupProvince.requestFocus();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    signupEmail.setError("Invalid email");
                    signupEmail.requestFocus();
                } else if (password.isEmpty()) {
                    signupPassword.setError("Password field is empty");
                    signupPassword.requestFocus();
                } else if (!password.equals(confirmPassword)) {
                    signupConfirmPassword.setError("Password does not match");
                    signupConfirmPassword.requestFocus();
                } else {
                    StorageReference imageReference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(uri));

                    AlertDialog.Builder builder = new AlertDialog.Builder(SignupActivity.this);
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
                                                int age = Integer.parseInt(getAge);

                                                UserAccountModel data = new UserAccountModel(userUid, firstName, middleName, lastName, selectedSex, age, phoneNumber, barangay, municipality, province, email, password, role, uri.toString(), reports);
                                                reference.child(userUid).setValue(data);
                                                dialog.dismiss();

                                                Toast.makeText(SignupActivity.this, "Signup Successful", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                                                finish();
                                            } else {
                                                Toast.makeText(SignupActivity.this, "Signup Failed "+ task.getException().getMessage(), Toast.LENGTH_SHORT).show();
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