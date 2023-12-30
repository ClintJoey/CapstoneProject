package com.example.capstoneproject;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class AboutUsActivity extends AppCompatActivity {
    ImageView back;
    CardView cjProfile, yanieProfile, sherwinProfile, farmOwnerProfile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        cjProfile = findViewById(R.id.cjProfile);
        yanieProfile = findViewById(R.id.yanieProfile);
        sherwinProfile = findViewById(R.id.sherwinProfile);
        farmOwnerProfile = findViewById(R.id.farmOwnerProfile);

        back = findViewById(R.id.back);

        cjProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openProfile(R.drawable.clint, "Clint Joey P. LLosala", "Bachelor of Science in Information Technology", "09630195200",
                        "clintjoey.llosala@cbsua.edu.ph", "100008380362622");
            }
        });

        yanieProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openProfile(R.drawable.jocel, "Jocel B. Garcia", "Bachelor of Science in Information Technology", "09489308245",
                        "jocel.garcia@cbsua.edu.ph", "yane.garcia.1428?mibextid=ZbWKwL");
            }
        });

        sherwinProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openProfile(R.drawable.sherwin, "Sherwin G. Talastasin", "Bachelor of Science in Information Technology", "09562263062",
                        "sherwin.talastasin@cbsua.edu.ph", "100007309518321");
            }
        });

        farmOwnerProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openProfile(R.drawable.farm_owner, "Henry Lasat", "Goat Farm Owner", "09123456789",
                        "link", "link");
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    private void openProfile(int image, String fullName, String role, String phoneNumber, String googleLink, String fbId) {
        LinearLayout profileCard = findViewById(R.id.profileCard);
        View view = LayoutInflater.from(AboutUsActivity.this).inflate(R.layout.dev_profile_dialog, profileCard);
        ImageView profile = view.findViewById(R.id.profileImg);
        TextView name = view.findViewById(R.id.fullName);
        TextView data = view.findViewById(R.id.metadata);
        TextView phoneNum = view.findViewById(R.id.profilePhoneNumber);
        CardView gmail = view.findViewById(R.id.googleLink);
        CardView fb = view.findViewById(R.id.fbLink);

        // set UI values
        String imagePath = "android.resource://" + getPackageName() + "/" + image;
        profile.setImageURI(Uri.parse(imagePath));
        name.setText(fullName);
        data.setText(role);
        phoneNum.setText(phoneNumber);

        if (googleLink == "link" && fbId == "link") {
            gmail.setVisibility(View.GONE);
            fb.setVisibility(View.GONE);
        }

        gmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGmailApp(googleLink);
            }
        });

        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFbApp(fbId);
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(AboutUsActivity.this);
        builder.setView(view);
        AlertDialog dialog = builder.create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        dialog.show();
    }
    private void openGmailApp(String recipient) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:" + recipient));
        startActivity(intent);
    }
    private void openFbApp(String fbId) {
        try {
            String fbPackage = "com.facebook.katana";
            Uri profileUri = Uri.parse("fb://profile/" + fbId);

            Intent intent = new Intent(Intent.ACTION_VIEW, profileUri);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void openFbWeb() {

    }

}