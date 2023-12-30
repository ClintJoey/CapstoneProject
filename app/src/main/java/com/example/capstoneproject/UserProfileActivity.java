package com.example.capstoneproject;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.capstoneproject.models.UserAccountModel;
import com.example.capstoneproject.singleton.UserDataManager;
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class UserProfileActivity extends AppCompatActivity implements Serializable {
    FirebaseAuth auth;
    FirebaseDatabase database;
    DatabaseReference userRef;
    StorageReference profileRef;
    private ImageView back, userProfileImg, editUserBtn;
    private TextView userToolBarName, userRole, userCompleteName, userAgeAndSex, userBaranggay, userMunicipality, userProvince, userPhoneNumber,
    userEmail, userPassword, userReportCount;
    String firstname;
    private ArrayList<String> userReportKeys = new ArrayList<>();
    private UserAccountModel user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        userRef = database.getReference("testUsers2");
        profileRef = FirebaseStorage.getInstance().getReference("Profile Pictures");

        userToolBarName = findViewById(R.id.userToolBarName);

        userProfileImg = findViewById(R.id.userProfileImg);
        userRole = findViewById(R.id.userRole);
        userCompleteName = findViewById(R.id.userCompleteName);
        userAgeAndSex = findViewById(R.id.userAgeAndSex);
        userBaranggay = findViewById(R.id.userBaranggay);
        userMunicipality = findViewById(R.id.userMunicipality);
        userProvince = findViewById(R.id.userProvince);
        userPhoneNumber = findViewById(R.id.userPhoneNumber);
        userEmail = findViewById(R.id.userEmail);
        userPassword = findViewById(R.id.userPassword);
        userReportCount = findViewById(R.id.userReportCount);

        back = findViewById(R.id.back);
        editUserBtn = findViewById(R.id.editUserBtn);

        fetchedUserData();

        userToolBarName.setText(firstname);

        editUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openEditDialog();
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

        UserAccountModel updatedUser = UserDataManager.getInstance().getUserById(user.userUid);

        if (user != null) {
            Glide.with(this).load(updatedUser.profileImg).into(userProfileImg);
            userRole.setText(updatedUser.role);

            String completeName = updatedUser.firstname + " " + updatedUser.middlename.charAt(0) + ". " + updatedUser.lastname;
            userCompleteName.setText(completeName);
            String ageAndSex = updatedUser.age + " years old, " + updatedUser.sex;
            userAgeAndSex.setText(ageAndSex);

            userBaranggay.setText(updatedUser.barangay);
            userMunicipality.setText(updatedUser.municipality);
            userProvince.setText(updatedUser.province);
        }
    }

    private void fetchedUserData() {
        Intent intent = getIntent();

        String userUid = intent.getStringExtra("userUid");
        firstname = intent.getStringExtra("firstname");
        String middlename = intent.getStringExtra("middlename");
        String lastname = intent.getStringExtra("lastname");
        String sex = intent.getStringExtra("sex");
        int age = intent.getIntExtra("age", 0);
        String phoneNumber = intent.getStringExtra("phoneNumber");
        String barangay = intent.getStringExtra("barangay");
        String municipality = intent.getStringExtra("municipality");
        String province = intent.getStringExtra("province");
        String email = intent.getStringExtra("email");
        String password = intent.getStringExtra("password");
        String role = intent.getStringExtra("role");
        String profileImg = intent.getStringExtra("profileImg");
        ArrayList<String> reports = intent.getStringArrayListExtra("reports");

        user = new UserAccountModel(userUid, firstname, middlename, lastname, sex, age, phoneNumber, barangay, municipality, province, email,
                password, role, profileImg, reports);

        userReportKeys = reports;
        Glide.with(this).load(profileImg).into(userProfileImg);
        userRole.setText(role);

        String completeName = firstname + " " + middlename.charAt(0) + ". " + lastname;
        userCompleteName.setText(completeName);
        String ageAndSex = age + " years old, " + sex;
        userAgeAndSex.setText(ageAndSex);
        userReportCount.setText(String.valueOf(userReportKeys.size() - 1));

        userBaranggay.setText(barangay);
        userMunicipality.setText(municipality);
        userProvince.setText(province);
        userPhoneNumber.setText(phoneNumber);
        userEmail.setText(email);
        userPassword.setText(password);
    }
    private void openEditDialog() {
        CardView editProfileDialog = findViewById(R.id.editProfileDialog);
        View view = LayoutInflater.from(UserProfileActivity.this).inflate(R.layout.edit_profile_dialog, editProfileDialog);
        LinearLayout editProfileDetailsItem = view.findViewById(R.id.editProfileDetailsItem);
        LinearLayout editProfileNumberItem = view.findViewById(R.id.editProfileNumberItem);
        LinearLayout editEmailItem = view.findViewById(R.id.editEmailItem);
        LinearLayout editPasswordItem = view.findViewById(R.id.editPasswordItem);

        AlertDialog.Builder builder = new AlertDialog.Builder(UserProfileActivity.this);
        builder.setView(view);
        final AlertDialog dialog = builder.create();

        editProfileDetailsItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                Intent intent = new Intent(view.getContext(), UpdateUserInfoActivity.class);
                intent.putExtra("user", user);
                startActivity(intent);
            }
        });

        editProfileNumberItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                openEditPhoneNumberDialog();
            }
        });

        editEmailItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                openEditEmailDialog();
            }
        });

        editPasswordItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                openEditPasswordDialog();
            }
        });

        dialog.show();
    }
    private void openEditPhoneNumberDialog() {
        CardView editProfileNumberDialog = findViewById(R.id.editProfileNumberDialog);
        View view = LayoutInflater.from(UserProfileActivity.this).inflate(R.layout.edit_profile_number_dialog, editProfileNumberDialog);
        EditText editPhoneNumber = view.findViewById(R.id.editPhoneNumber);
        Button updatePhoneNumberBtn = view.findViewById(R.id.updatePhoneNumberBtn);

        editPhoneNumber.setText(userPhoneNumber.getText().toString());

        AlertDialog.Builder builder = new AlertDialog.Builder(UserProfileActivity.this);
        builder.setView(view);
        AlertDialog dialog = builder.create();

        updatePhoneNumberBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = editPhoneNumber.getText().toString().trim();

                if (phoneNumber.isEmpty()) {
                    editPhoneNumber.setError("Phone number field is empty");
                    editPhoneNumber.requestFocus();
                } else if (phoneNumber.length() < 11) {
                    editPhoneNumber.setError("Phone number must be 11 digits");
                    editPhoneNumber.requestFocus();
                } else {
                    dialog.dismiss();

                    String userUid = auth.getCurrentUser().getUid();

                    AlertDialog.Builder builder1 = new AlertDialog.Builder(UserProfileActivity.this);
                    builder1.setCancelable(false);
                    builder1.setView(R.layout.progress_bar_layout);
                    AlertDialog dialog1 = builder1.create();
                    dialog1.show();

                    userRef.child(userUid).child("phoneNumber").setValue(phoneNumber).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                userPhoneNumber.setText(phoneNumber);
                                dialog1.dismiss();
                                Toast.makeText(UserProfileActivity.this, "Phone Number updated", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            dialog1.dismiss();
                            Toast.makeText(UserProfileActivity.this, "Error updating phone number", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        dialog.show();
    }
    private void openEditEmailDialog() {
        CardView editEmailDialog = findViewById(R.id.editEmailDialog);
        View view = LayoutInflater.from(UserProfileActivity.this).inflate(R.layout.edit_email_dialog, editEmailDialog);
        EditText editEmail = view.findViewById(R.id.editEmail);
        Button updateEmailBtn = view.findViewById(R.id.updateEmailBtn);

        editEmail.setText(userEmail.getText().toString());

        AlertDialog.Builder builder = new AlertDialog.Builder(UserProfileActivity.this);
        builder.setView(view);
        AlertDialog dialog = builder.create();

        updateEmailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newEmail = editEmail.getText().toString().trim();

                if (!Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()) {
                    editEmail.setError("Invalid email");
                    editEmail.requestFocus();
                } else {
                    dialog.dismiss();
                    String userUid = user.userUid;

                    AlertDialog.Builder builder1 = new AlertDialog.Builder(UserProfileActivity.this);
                    builder1.setCancelable(false);
                    builder1.setView(R.layout.progress_bar_layout);
                    AlertDialog dialog1 = builder1.create();
                    dialog1.show();

                    userRef.child(userUid).child("email").setValue(newEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                dialog1.dismiss();
                                userEmail.setText(newEmail);
                                Toast.makeText(UserProfileActivity.this, "Email updated successfully", Toast.LENGTH_SHORT).show();
                            } else {
                                dialog1.dismiss();
                                Toast.makeText(UserProfileActivity.this, "Updating email failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        dialog.show();
    }
    private void openEditPasswordDialog() {
        CardView editPasswordDialog = findViewById(R.id.editPasswordDialog);
        View view = LayoutInflater.from(UserProfileActivity.this).inflate(R.layout.edit_password_dialog, editPasswordDialog);
        EditText editPassword = view.findViewById(R.id.editPassword);
        EditText editConfirmPassword = view.findViewById(R.id.editConfirmPassword);
        Button updatePasswordBtn = view.findViewById(R.id.updatePasswordBtn);

        editPassword.setText(userPassword.getText().toString());
        editConfirmPassword.setText(userPassword.getText().toString());

        AlertDialog.Builder builder = new AlertDialog.Builder(UserProfileActivity.this);
        builder.setView(view);
        AlertDialog dialog = builder.create();

        updatePasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newPassword = editPassword.getText().toString().trim();
                String newConfirmPassword = editConfirmPassword.getText().toString().trim();

                if (newPassword.isEmpty()) {
                    editPassword.setError("Password field is empty");
                    editPassword.requestFocus();
                } else if (!newPassword.equals(newConfirmPassword)) {
                    editConfirmPassword.setError("Password does not match");
                    editConfirmPassword.requestFocus();
                } else {
                    dialog.dismiss();
                    String userUid = user.userUid;

                    AlertDialog.Builder builder1 = new AlertDialog.Builder(UserProfileActivity.this);
                    builder1.setCancelable(false);
                    builder1.setView(R.layout.progress_bar_layout);
                    AlertDialog dialog1 = builder1.create();
                    dialog1.show();

                    userRef.child(userUid).child("password").setValue(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                userPassword.setText(newPassword);
                                dialog1.dismiss();
                                Toast.makeText(UserProfileActivity.this, "User's password updated", Toast.LENGTH_SHORT).show();
                            } else {
                                dialog1.dismiss();
                                Toast.makeText(UserProfileActivity.this, "Password update failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
        dialog.show();
    }
}