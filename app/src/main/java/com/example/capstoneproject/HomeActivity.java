package com.example.capstoneproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.capstoneproject.models.UserAccountModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import me.ibrahimsn.lib.OnItemSelectedListener;
import me.ibrahimsn.lib.SmoothBottomBar;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    FirebaseDatabase database;
    DatabaseReference reference;
    FirebaseAuth auth;
    DrawerLayout sidenavDrawer;
    SmoothBottomBar smoothBottomBar;
    String firstnameFromDB, middlenameFromDB, lastnameFromDB, sexFromDB, phoneNumberFromDB, barangayFromDB, municipalityFromDB,
    provinceFromDB, emailFromDB, passwordFromDB, roleFromDB, profileImgFromDB;
    ArrayList<String> reportsFromDB;
    FloatingActionButton goToAdmin;
    int ageFromDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        goToAdmin = findViewById(R.id.goToAdmin);

        auth = FirebaseAuth.getInstance();
        String key = auth.getCurrentUser().getUid();

        database = FirebaseDatabase.getInstance();
        reference = database.getReference("testUsers2").child(key);

        fetchedUserData();

        Toolbar homeToolBar = findViewById(R.id.homeToolbar);
        setSupportActionBar(homeToolBar);

        sidenavDrawer = findViewById(R.id.sidenavDrawer);
        NavigationView sideNavView = findViewById(R.id.sideNavView);
        sideNavView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, sidenavDrawer, homeToolBar, R.string.open_nav, R.string.close_nav);
        sidenavDrawer.addDrawerListener(toggle);
        toggle.syncState();

        smoothBottomBar = findViewById(R.id.bottomNav);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.homeNavFrameLayout, new ScannerFragment());
        fragmentTransaction.commit();

        smoothBottomBar.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public boolean onItemSelect(int i) {
                if (i == 0) {
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.homeNavFrameLayout, new ScannerFragment());
                    fragmentTransaction.commit();
                }
                if (i == 1) {
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.homeNavFrameLayout, new LibraryFragment());
                    fragmentTransaction.commit();
                }
                if (i == 2) {
                    passToDashboard();
                }
                if (i == 3) {
                    passToProfile();
                }
                return false;
            }
        });
        goToAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, AdminDashboardActivity.class);
                intent.putExtra("firstname", firstnameFromDB);
                intent.putExtra("role", roleFromDB);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.navAboutUs) {
            startActivity(new Intent(HomeActivity.this, AboutUsActivity.class));
        }
        if (item.getItemId() == R.id.navLogout) {
            auth.signOut();
            Toast.makeText(this, "Logout", Toast.LENGTH_SHORT).show();
            logout();
        }
        sidenavDrawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (sidenavDrawer.isDrawerOpen(GravityCompat.START)) {
            sidenavDrawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    public void fetchedUserData() {
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        builder.setCancelable(false);
        builder.setView(R.layout.loading_progress_bar);
        AlertDialog dialog = builder.create();
        dialog.show();

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserAccountModel userAccountModel = snapshot.getValue(UserAccountModel.class);
                if (snapshot.exists()) {
                    firstnameFromDB = userAccountModel.firstname;
                    middlenameFromDB = userAccountModel.middlename;
                    lastnameFromDB = userAccountModel.lastname;
                    sexFromDB = userAccountModel.sex;
                    ageFromDB = userAccountModel.age;
                    phoneNumberFromDB = userAccountModel.phoneNumber;
                    barangayFromDB = userAccountModel.barangay;
                    municipalityFromDB = userAccountModel.municipality;
                    provinceFromDB = userAccountModel.province;
                    emailFromDB = userAccountModel.email;
                    passwordFromDB = userAccountModel.password;
                    roleFromDB = userAccountModel.role;
                    profileImgFromDB = userAccountModel.profileImg;
                    reportsFromDB = userAccountModel.reports;

                    if (roleFromDB.equals("Manager") || roleFromDB.equals("Admin")) {
                        goToAdmin.setVisibility(View.VISIBLE);
                    }
                    dialog.dismiss();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                dialog.dismiss();
                Toast.makeText(HomeActivity.this, "Error: " + error.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void passToDashboard() {
        DashboardFragment dashboardFragment = new DashboardFragment();

        Bundle userData = new Bundle();
        userData.putStringArrayList("reports", reportsFromDB);

        dashboardFragment.setArguments(userData);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.homeNavFrameLayout, dashboardFragment).commit();
    }
    public void passToProfile() {
        ProfileFragment profileFragment = new ProfileFragment();

        Bundle userData = new Bundle();
        userData.putString("firstname", firstnameFromDB);
        userData.putString("middlename", middlenameFromDB);
        userData.putString("lastname", lastnameFromDB);
        userData.putString("sex", sexFromDB);
        userData.putInt("age", ageFromDB);
        userData.putString("phoneNumber", phoneNumberFromDB);
        userData.putString("barangay", barangayFromDB);
        userData.putString("municipality", municipalityFromDB);
        userData.putString("province", provinceFromDB);
        userData.putString("email", emailFromDB);
        userData.putString("password", passwordFromDB);
        userData.putString("role", roleFromDB);
        userData.putString("profileImg", profileImgFromDB);
        userData.putStringArrayList("reports", reportsFromDB);

        profileFragment.setArguments(userData);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.homeNavFrameLayout, profileFragment).commit();
    }
    public void logout() {
        Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}