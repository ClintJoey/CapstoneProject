package com.example.capstoneproject;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.Manifest;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
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


public class ProfileFragment extends Fragment {
    FirebaseDatabase database;
    DatabaseReference userRef;
    FirebaseAuth auth;
    FirebaseStorage storage;
    ImageView profileImg, editBtn;
    TextView profileFirstname, profileMiddlename, profileLastname, profileSex, profileAge, profilePhoneNumber, profileBarangay,
            profileMunicipality, profileProvince, profileEmail, profilePassword, profileRole, profileReportsCount;
    String imageUrl, fName, mName, lName, sex, phoneNumber, barangay, municipality, province, email, password, role;
    int age;
    ArrayList<String> reports;
    private ActivityResultLauncher<String> requestPermissionLauncher;
    private ActivityResultLauncher<Intent> takePictureLauncher;
    private ActivityResultLauncher<Intent> pickImageLauncher;
    Uri uri;
    String profilePic = null;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        database = FirebaseDatabase.getInstance();
        userRef = database.getReference("testUsers2");
        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();

        profileImg = rootView.findViewById(R.id.profileImg);
        profileFirstname = rootView.findViewById(R.id.profileFirstname);
        profileMiddlename = rootView.findViewById(R.id.profileMiddlename);
        profileLastname = rootView.findViewById(R.id.profileLastname);
        profileSex = rootView.findViewById(R.id.profileSex);
        profileAge = rootView.findViewById(R.id.profileAge);
        profilePhoneNumber = rootView.findViewById(R.id.profilePhoneNumber);
        profileBarangay = rootView.findViewById(R.id.profileBarangay);
        profileMunicipality = rootView.findViewById(R.id.profileMunicipality);
        profileProvince = rootView.findViewById(R.id.profileProvince);
        profileEmail = rootView.findViewById(R.id.profileEmail);
        profilePassword = rootView.findViewById(R.id.profilePassword);
        profileRole = rootView.findViewById(R.id.profileRole);
        profileReportsCount = rootView.findViewById(R.id.profileReportsCount);

        editBtn = rootView.findViewById(R.id.editBtn);
        setProfile();

        if (profileEmail.getText().toString().length() > 28) {
            profileEmail.setTextSize(15);
        }

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openEditDialog(rootView);
            }
        });

        // Inflate the layout for this fragment
        return rootView;
    }
    public void setProfile() {
        Bundle bundle = this.getArguments();

        imageUrl = bundle.getString("profileImg");
        Glide.with(this).load(imageUrl).into(profileImg);

        fName = bundle.getString("firstname");
        mName = bundle.getString("middlename");
        lName = bundle.getString("lastname");
        sex = bundle.getString("sex");
        age = bundle.getInt("age");
        String setAge = age + " years old, ";
        phoneNumber = bundle.getString("phoneNumber");
        barangay = bundle.getString("barangay");
        municipality = bundle.getString("municipality");
        province = bundle.getString("province");
        email = bundle.getString("email");
        password = bundle.getString("password");
        role = bundle.getString("role");
        reports = bundle.getStringArrayList("reports");
        int reportsCount = reports.size() - 1;

        profileFirstname.setText(fName + " ");
        profileMiddlename.setText(mName.charAt(0)  + ". ");
        profileLastname.setText(lName);
        profileSex.setText(sex  + ", ");
        profileAge.setText(setAge);
        profilePhoneNumber.setText(phoneNumber);
        profileBarangay.setText(barangay  + " ");
        profileMunicipality.setText(municipality  + ", ");
        profileProvince.setText(province);
        profileEmail.setText(email);
        profilePassword.setText(password);
        profileRole.setText(role);
        profileReportsCount.setText(String.valueOf(reportsCount));
    }
    public void passDataToEditProfile() {
        Intent intent = new Intent(getActivity(), EditProfileActivity.class);
        String convertAge = String.valueOf(age);

        intent.putExtra("profileImg", imageUrl);
        intent.putExtra("firstname", fName);
        intent.putExtra("middlename", mName);
        intent.putExtra("lastname", lName);
        intent.putExtra("sex", sex);
        intent.putExtra("age", convertAge);
        intent.putExtra("barangay", barangay);
        intent.putExtra("municipality", municipality);
        intent.putExtra("province", province);

        startActivity(intent);
    }
    private void openEditDialog(View rootView) {
        CardView editProfileDialog = rootView.findViewById(R.id.editProfileDialog);
        View view = LayoutInflater.from(rootView.getContext()).inflate(R.layout.edit_profile_dialog, editProfileDialog);
        LinearLayout editProfileDetailsItem = view.findViewById(R.id.editProfileDetailsItem);
        LinearLayout editProfileNumberItem = view.findViewById(R.id.editProfileNumberItem);
        LinearLayout editEmailItem = view.findViewById(R.id.editEmailItem);
        LinearLayout editPasswordItem = view.findViewById(R.id.editPasswordItem);

        AlertDialog.Builder builder = new AlertDialog.Builder(rootView.getContext());
        builder.setView(view);
        final AlertDialog dialog = builder.create();

        editProfileDetailsItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                passDataToEditProfile();
            }
        });

        editProfileNumberItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                openEditPhoneNumberDialog(rootView);
            }
        });

        editEmailItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                openEditEmailDialog(rootView);
            }
        });

        editPasswordItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                openEditPasswordDialog(rootView);
            }
        });

        dialog.show();
    }
    private void openEditProfileImgDialog(View rootView) {
        CardView editProfileImgDialog = rootView.findViewById(R.id.editProfileImgDialog);
        View view = LayoutInflater.from(rootView.getContext()).inflate(R.layout.edit_profile_img_dialog, editProfileImgDialog);
        ImageView editProfileImg = view.findViewById(R.id.editProfileImg);
        Button updateProfileBtn = view.findViewById(R.id.updateProfilePicBtn);

        AlertDialog.Builder builder = new AlertDialog.Builder(rootView.getContext());
        builder.setView(view);
        AlertDialog dialog = builder.create();

//        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
//                new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
//                    @Override
//                    public void onActivityResult(ActivityResult result) {
//                        if (result.getResultCode() == Activity.RESULT_OK) {
//                            Intent data = result.getData();
//                            uri = data.getData();
//                            editProfileImg.setImageURI(uri);
//                            profilePic = uri.toString();
//                        } else {
//                            Toast.makeText(rootView.getContext(), "Profile picture not set", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                }
//        );

        editProfileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
//                Intent photoPicker = new Intent(Intent.ACTION_PICK);
//                photoPicker.setType("image/*");
//                activityResultLauncher.launch(photoPicker);
                requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
                    @Override
                    public void onActivityResult(Boolean isGranted) {
                        if (isGranted) {
                            // Permission granted, continue with the action (e.g., launch camera or gallery)
                        } else {
                            // Permission denied, handle accordingly
                        }
                    }
                });

            }
        });

        updateProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                // try this if it's working
                updateProfileImg(rootView);
            }
        });
        dialog.show();
    }
    private void updateProfileImg(View rootView) {
        StorageReference profileRef = storage.getReference("Profile Pictures");

        if (profilePic != null) {
            StorageReference imageRef = profileRef.child(System.currentTimeMillis() + "." + getFileExtension(uri, rootView));

            AlertDialog.Builder builder = new AlertDialog.Builder(rootView.getContext());
            builder.setCancelable(false);
            builder.setView(R.layout.progress_bar_layout);
            AlertDialog dialog = builder.create();
            dialog.show();

            imageRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String updatedProfileImg = uri.toString();
                            userRef.child("profileImg").setValue(updatedProfileImg);
                            Toast.makeText(rootView.getContext(), "Profile Picture update successful", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(rootView.getContext(), "Error downloading the image", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(rootView.getContext(), "Error updating profile picture", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(rootView.getContext(), "Profile Picture is not set", Toast.LENGTH_SHORT).show();
        }
    }
    private void openEditPhoneNumberDialog(View rootView) {
        CardView editProfileNumberDialog = rootView.findViewById(R.id.editProfileNumberDialog);
        View view = LayoutInflater.from(rootView.getContext()).inflate(R.layout.edit_profile_number_dialog, editProfileNumberDialog);
        EditText editPhoneNumber = view.findViewById(R.id.editPhoneNumber);
        Button updatePhoneNumberBtn = view.findViewById(R.id.updatePhoneNumberBtn);

        editPhoneNumber.setText(profilePhoneNumber.getText().toString());

        AlertDialog.Builder builder = new AlertDialog.Builder(rootView.getContext());
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

                    AlertDialog.Builder builder1 = new AlertDialog.Builder(rootView.getContext());
                    builder1.setCancelable(false);
                    builder1.setView(R.layout.progress_bar_layout);
                    AlertDialog dialog1 = builder1.create();
                    dialog1.show();

                    userRef.child(userUid).child("phoneNumber").setValue(phoneNumber).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                profilePhoneNumber.setText(phoneNumber);
                                dialog1.dismiss();
                                Toast.makeText(rootView.getContext(), "Phone Number updated", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            dialog1.dismiss();
                            Toast.makeText(rootView.getContext(), "Error updating phone number", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        dialog.show();
    }
    private void openEditEmailDialog(View rootView) {
        CardView editEmailDialog = rootView.findViewById(R.id.editEmailDialog);
        View view = LayoutInflater.from(rootView.getContext()).inflate(R.layout.edit_email_dialog, editEmailDialog);
        EditText editEmail = view.findViewById(R.id.editEmail);
        Button updateEmailBtn = view.findViewById(R.id.updateEmailBtn);

        editEmail.setText(profileEmail.getText().toString());

        AlertDialog.Builder builder = new AlertDialog.Builder(rootView.getContext());
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
                    String userUid = auth.getCurrentUser().getUid();

                    AlertDialog.Builder builder1 = new AlertDialog.Builder(rootView.getContext());
                    builder1.setCancelable(false);
                    builder1.setView(R.layout.progress_bar_layout);
                    AlertDialog dialog1 = builder1.create();
                    dialog1.show();

                    FirebaseUser user = auth.getCurrentUser();
                    AuthCredential credential = EmailAuthProvider.getCredential(profileEmail.getText().toString(),
                            profilePassword.getText().toString());

                    user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            user.updateEmail(newEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        userRef.child(userUid).child("email").setValue(newEmail);
                                        profileEmail.setText(newEmail);
                                        dialog1.dismiss();
                                        Toast.makeText(rootView.getContext(), "Email updated", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    dialog1.dismiss();
                                    Toast.makeText(rootView.getContext(), "Email is not updated", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            dialog1.dismiss();
                            Toast.makeText(rootView.getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }
        });

        dialog.show();
    }
    private void openEditPasswordDialog(View rootView) {
        CardView editPasswordDialog = rootView.findViewById(R.id.editPasswordDialog);
        View view = LayoutInflater.from(rootView.getContext()).inflate(R.layout.edit_password_dialog, editPasswordDialog);
        EditText editPassword = view.findViewById(R.id.editPassword);
        EditText editConfirmPassword = view.findViewById(R.id.editConfirmPassword);
        Button updatePasswordBtn = view.findViewById(R.id.updatePasswordBtn);

        editPassword.setText(profilePassword.getText().toString());
        editConfirmPassword.setText(profilePassword.getText().toString());

        AlertDialog.Builder builder = new AlertDialog.Builder(rootView.getContext());
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
                    String userUid = auth.getCurrentUser().getUid();

                    AlertDialog.Builder builder1 = new AlertDialog.Builder(rootView.getContext());
                    builder1.setCancelable(false);
                    builder1.setView(R.layout.progress_bar_layout);
                    AlertDialog dialog1 = builder1.create();
                    dialog1.show();

                    FirebaseUser user = auth.getCurrentUser();
                    AuthCredential credential = EmailAuthProvider.getCredential(profileEmail.getText().toString(),
                            profilePassword.getText().toString());

                    user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            user.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        userRef.child(userUid).child("password").setValue(newPassword);
                                        profilePassword.setText(newPassword);
                                        dialog1.dismiss();
                                        Toast.makeText(rootView.getContext(), "Password updated successfully", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    dialog1.dismiss();
                                    Toast.makeText(rootView.getContext(), "Error updating password", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            dialog1.dismiss();
                            Toast.makeText(rootView.getContext(), "Error updating password", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
        dialog.show();
    }
    private String getFileExtension(Uri fileUri, View rootView) {
        ContentResolver contentResolver = rootView.getContext().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(fileUri));
    }
    private void requestPermissions() {
        String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE};
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(requireContext(), permission) != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted, request it
                requestPermissionLauncher.launch(permission);
            }
        }
    }
    private void launchCamera() {
        requestPermissions();

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            takePictureLauncher.launch(takePictureIntent);
        }
    }
    private void launchGallery() {
        requestPermissions();

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Intent pickPhotoIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            pickImageLauncher.launch(pickPhotoIntent);
        }
    }

}