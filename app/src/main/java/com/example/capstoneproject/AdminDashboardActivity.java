package com.example.capstoneproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.capstoneproject.models.BeneficialPlantModel;
import com.example.capstoneproject.models.HarmfulPlantModel;
import com.example.capstoneproject.models.NutrientModel;
import com.example.capstoneproject.models.ReportModel;
import com.example.capstoneproject.models.ToxinModel;
import com.example.capstoneproject.models.UserAccountModel;
import com.example.capstoneproject.singleton.UserDataManager;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class AdminDashboardActivity extends AppCompatActivity {
    private Button viewUsers, viewReports;
    private TextView adminName, employeeCount, managerCount, adminCount, harmfulPlantsCount, plantToxinsCount, viewHarmfulPlants, viewBeneficialPlants, viewPlantToxins, viewPlantNutrients,
            gmelinaCount, balinghoyCount, ipilCount, hagunoyCount, talahibCount, monggoCount, pataniCount, beneficialPlantsCount, nutrientsCount;
    private Spinner plantSpinner, monthSpinner, yearSpinner;
    private ArrayList<String> monthsArray = new ArrayList<>();
    private ArrayList<String> yearsArray = new ArrayList<>();
    private LinearLayout adminOnly;
    private LineChart reportChart;
    private ArrayList<Entry> gmelinaLine = new ArrayList<>();
    private ArrayList<Entry> balinghoyLine = new ArrayList<>();
    private ArrayList<Entry> ipilLine = new ArrayList<>();
    private ArrayList<Entry> hagunoyLine = new ArrayList<>();
    private ArrayList<Entry> talahibLine = new ArrayList<>();
    private ArrayList<Entry> monggoLine = new ArrayList<>();
    private ArrayList<Entry> pataniLine = new ArrayList<>();
    private ArrayList<ILineDataSet> dataSets = new ArrayList<>();
    FirebaseDatabase database;
    DatabaseReference userRef, reportRef, harmfulPlantsRef, toxinRef, beneficialPlantsRef, nutrientsRef;
    private String role;
    final private ArrayList<UserAccountModel> usersArray = new ArrayList<>();
    final private ArrayList<ReportModel> reportsArray = new ArrayList<>();
    final private ArrayList<BeneficialPlantModel> beneficialPlantsArray = new ArrayList<>();
    final private ArrayList<NutrientModel> nutrientsArray = new ArrayList<>();
    private int gmelina = 0;
    private int balinghoy = 0;
    private int ipil = 0;
    private int hagunoy = 0;
    private int talahib = 0;
    private int monggo = 0;
    private int patani = 0;
    private List<ReportModel> combinedReports;
    private List<ReportModel> sortedReports;
    private List<ReportModel> filteredReports;
    final private ArrayList<ToxinModel> toxinsArray = new ArrayList<>();
    final private ArrayList<HarmfulPlantModel> harmfulPlantsArray = new ArrayList<>();
    private boolean isNightMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        isNightMode = isNightMode(this);

        Date date = new Date();
        SimpleDateFormat currentMonth = new SimpleDateFormat("MM");
        SimpleDateFormat currentYear = new SimpleDateFormat("Y");
        String month = currentMonth.format(date);
        String year = currentYear.format(date);

        database = FirebaseDatabase.getInstance();
        userRef = database.getReference("testUsers2");
        reportRef = database.getReference("testReport");
        harmfulPlantsRef = database.getReference("Harmful Plants");
        toxinRef = database.getReference("Toxins");
        beneficialPlantsRef = database.getReference("Beneficial Plants");
        nutrientsRef = database.getReference("Nutrients");

        adminName = findViewById(R.id.adminName);

        employeeCount = findViewById(R.id.employeeCount);
        managerCount = findViewById(R.id.managerCount);
        adminCount = findViewById(R.id.adminCount);

        gmelinaCount = findViewById(R.id.gmelinaCount);
        balinghoyCount = findViewById(R.id.balinghoyCount);
        hagunoyCount = findViewById(R.id.hagunoyCount);
        ipilCount = findViewById(R.id.ipilCount);
        talahibCount = findViewById(R.id.talahibCount);
        monggoCount = findViewById(R.id.monggoCount);
        pataniCount = findViewById(R.id.pataniCount);

        beneficialPlantsCount = findViewById(R.id.beneficialPlantsCount);
        nutrientsCount = findViewById(R.id.nutrientsCount);

        plantSpinner = findViewById(R.id.plantSpinner);
        monthSpinner = findViewById(R.id.monthSpinner);
        yearSpinner = findViewById(R.id.yearSpinner);

        reportChart = findViewById(R.id.reportChart);

        harmfulPlantsCount = findViewById(R.id.harmfulPlantsCount);
        plantToxinsCount = findViewById(R.id.plantToxinsCount);

        viewUsers = findViewById(R.id.viewUsers);
        viewReports = findViewById(R.id.viewReports);
        viewBeneficialPlants = findViewById(R.id.viewBeneficialPlants);
        viewHarmfulPlants = findViewById(R.id.viewHarmfulPlants);
        viewPlantToxins = findViewById(R.id.viewPlantToxins);
        viewPlantNutrients = findViewById(R.id.viewPlantNutrients);

        adminOnly = findViewById(R.id.adminOnly);

        setAdminName();

        fetchFromDB(month, year);

        viewUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminDashboardActivity.this, ManageUsersActivity.class);
                intent.putExtra("usersArray", usersArray);
                startActivity(intent);
            }
        });

        viewReports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminDashboardActivity.this, ManageReportsActivity.class);
                intent.putExtra("usersArray", usersArray);
                intent.putExtra("reportsArray", reportsArray);
                startActivity(intent);
            }
        });

        plantSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String plantFilter = parent.getItemAtPosition(position).toString();

                if (plantFilter.equals("All")) {
                    showAllLine();
                }
                if (plantFilter.equals("Gmelina")) {
                    dataSets.clear();

                    LineDataSet lineDataSet = new LineDataSet(gmelinaLine, "Gmelina");
                    addPlantLine(lineDataSet, ContextCompat.getColor(AdminDashboardActivity.this, R.color.lavenderColor));

                    dataSets.add(lineDataSet);
                }
                if (plantFilter.equals("Balinghoy")) {
                    dataSets.clear();

                    LineDataSet lineDataSet = new LineDataSet(balinghoyLine, "Balinghoy");
                    addPlantLine(lineDataSet, ContextCompat.getColor(AdminDashboardActivity.this, R.color.blueColor));

                    dataSets.add(lineDataSet);
                }
                if (plantFilter.equals("Hagunoy")) {
                    dataSets.clear();

                    LineDataSet lineDataSet = new LineDataSet(hagunoyLine, "Hagunoy");
                    addPlantLine(lineDataSet, ContextCompat.getColor(AdminDashboardActivity.this, R.color.midGreen));

                    dataSets.add(lineDataSet);
                }
                if (plantFilter.equals("Monggo")) {
                    dataSets.clear();

                    LineDataSet lineDataSet = new LineDataSet(monggoLine, "Monggo");
                    addPlantLine(lineDataSet, ContextCompat.getColor(AdminDashboardActivity.this, R.color.orangeColor));

                    dataSets.add(lineDataSet);
                }
                if (plantFilter.equals("Talahib")) {
                    dataSets.clear();

                    LineDataSet lineDataSet = new LineDataSet(talahibLine, "Talahib");
                    addPlantLine(lineDataSet, ContextCompat.getColor(AdminDashboardActivity.this, R.color.dangerColor));

                    dataSets.add(lineDataSet);
                }
                if (plantFilter.equals("Ipil-ipil")) {
                    dataSets.clear();

                    LineDataSet lineDataSet = new LineDataSet(ipilLine, "Ipil-ipil");
                    addPlantLine(lineDataSet, ContextCompat.getColor(AdminDashboardActivity.this, R.color.brownColor));

                    dataSets.add(lineDataSet);
                }
                if (plantFilter.equals("Patani")) {
                    dataSets.clear();

                    LineDataSet lineDataSet = new LineDataSet(pataniLine, "Patani");
                    addPlantLine(lineDataSet, ContextCompat.getColor(AdminDashboardActivity.this, R.color.pureBlue));

                    dataSets.add(lineDataSet);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        monthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int monthPos = position + 1;
                String monthFilter;
                if (monthPos < 10) {
                    monthFilter = "0" + monthPos;
                } else {
                    monthFilter = String.valueOf(monthPos);
                }
                filterReports(monthFilter, year);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        yearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int monthPos = monthSpinner.getSelectedItemPosition() + 1;
                String monthFilter;
                if (monthPos < 10) {
                    monthFilter = "0" + monthPos;
                } else {
                    monthFilter = String.valueOf(monthPos);
                }
                filterReports(monthFilter, yearSpinner.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        viewHarmfulPlants.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminDashboardActivity.this, ManageHarmfulPlantsActivity.class);
                intent.putExtra("toxinsArray", toxinsArray);
                intent.putExtra("harmfulPlantsArray", harmfulPlantsArray);
                startActivity(intent);
            }
        });

        viewBeneficialPlants.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminDashboardActivity.this, ManageBeneficialPlantsActivity.class);
                intent.putExtra("beneficialPlantsArray", beneficialPlantsArray);
                intent.putExtra("nutrientsArray", nutrientsArray);
                startActivity(intent);
            }
        });

        viewPlantToxins.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminDashboardActivity.this, ManagePlantToxinsActivity.class);
                intent.putExtra("toxinsArray", toxinsArray);
                startActivity(intent);
            }
        });

        viewPlantNutrients.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminDashboardActivity.this, ManagePlantNutrientsActivity.class);
                intent.putExtra("nutrientsArray", nutrientsArray);
                startActivity(intent);
            }
        });
    }
    public void setAdminName() {
        Intent intent = getIntent();
        String firstname = intent.getStringExtra("firstname");
        role = intent.getStringExtra("role");
        String greeting = role + " " + firstname;
        adminName.setText(greeting);

        if (!role.equals("Admin")) {
            adminOnly.setVisibility(View.GONE);
        }
    }
    public boolean isNightMode(Context context) {
        int nightModeFlags = context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        return nightModeFlags == Configuration.UI_MODE_NIGHT_YES;
    }
    private void fetchFromDB(String month, String year) {
        AlertDialog.Builder builder = new AlertDialog.Builder(AdminDashboardActivity.this);
        builder.setCancelable(false);
        builder.setView(R.layout.loading_progress_bar);
        AlertDialog dialog = builder.create();
        dialog.show();

        if (role.equals("Manager")) {
            getUsersFromDB();
            getReportsFromDB(month, year);
            dialog.dismiss();
        } else {
            getUsersFromDB();
            getReportsFromDB(month, year);
            getHarmfulPlantsFromDB();
            getToxinsFromDB();
            getBeneficialPlantsFromDB();
            getNutrientsFromDB();
            dialog.dismiss();
        }
    }
    private void getUsersFromDB() {
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usersArray.clear();
                UserDataManager.getInstance().clearUsers();
                int employee = 0;
                int manager = 0;
                int admin = 0;

                for (DataSnapshot data: snapshot.getChildren()) {
                    UserAccountModel user = data.getValue(UserAccountModel.class);
                    usersArray.add(user);
                    UserDataManager.getInstance().addUser(user);
                }
                for (UserAccountModel user: usersArray) {
                    if (user.role.equals("Employee")) {
                        employee += 1;
                    }
                    if (user.role.equals("Manager")) {
                        manager += 1;
                    }
                    if (user.role.equals("Admin")) {
                        admin += 1;
                    }
                    employeeCount.setText(String.valueOf(employee));
                    managerCount.setText(String.valueOf(manager));
                    adminCount.setText(String.valueOf(admin));
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AdminDashboardActivity.this, "Error: " + error.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void getReportsFromDB(String month, String year) {
        reportRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    reportsArray.clear();
                    for (DataSnapshot data: snapshot.getChildren()) {
                        String reportId = data.child("reportId").getValue(String.class);
                        String userUid = data.child("userUid").getValue(String.class);
                        int gmelina = data.child("Gmelina").getValue(Integer.class);
                        int balinghoy = data.child("Balinghoy").getValue(Integer.class);
                        int ipil = data.child("Ipil-ipil").getValue(Integer.class);
                        int hagunoy = data.child("Hagunoy").getValue(Integer.class);
                        int talahib = data.child("Talahib").getValue(Integer.class);
                        int monggo = data.child("Monggo").getValue(Integer.class);
                        int patani = data.child("Patani").getValue(Integer.class);
                        String date = data.child("date").getValue(String.class);
                        String time = data.child("time").getValue(String.class);

                        ReportModel reportModel = new ReportModel(reportId, userUid, date, time, gmelina, balinghoy, ipil, hagunoy, talahib, monggo, patani);
                        reportsArray.add(reportModel);
                    }
                    populatePlantCount();

                    populateMonthArray();
                    setSpinnerItems(monthSpinner, monthsArray);
                    monthSpinner.setSelection(Integer.parseInt(month) - 1);

                    for (ReportModel report: reportsArray) {
                        String yearItem = report.getDate().substring(6,10);
                        if (!yearsArray.contains(yearItem)) {
                            yearsArray.add(yearItem);
                        }
                    }
                    setSpinnerItems(yearSpinner, yearsArray);
                    yearSpinner.setSelection(yearsArray.indexOf(year));

                    combineAndSortReports();
                    filterReports(month, year);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AdminDashboardActivity.this, "Error fetching reports: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void populateMonthArray() {
        monthsArray.add("Jan");
        monthsArray.add("Feb");
        monthsArray.add("Mar");
        monthsArray.add("Apr");
        monthsArray.add("May");
        monthsArray.add("Jun");
        monthsArray.add("Jul");
        monthsArray.add("Aug");
        monthsArray.add("Sep");
        monthsArray.add("Oct");
        monthsArray.add("Nov");
        monthsArray.add("Dec");
    }
    private void setSpinnerItems(Spinner spinner, ArrayList<String> array) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_selectable_list_item, array);
        spinner.setAdapter(adapter);
    }
    private void addPlantLine(LineDataSet lineDataSet, int color) {
        dataSets.clear();

        lineDataSet.setLineWidth(5);
        lineDataSet.setColor(color);
        if (isNightMode) {
            lineDataSet.setValueTextColor(Color.WHITE);
        }
        lineDataSet.setValueTextSize(15);

        dataSets.add(lineDataSet);

        LineData data = new LineData(dataSets);
        reportChart.setData(data);
        reportChart.invalidate();

        reportChart.setVisibleXRangeMaximum(6);
    }
    private void combineAndSortReports() {
        combinedReports = reportsArray.stream()
                .collect(Collectors.toMap(ReportModel::getDate, report -> report, (report1, report2) -> {
                    report1.combineVal(report2.getGmelina(), report2.getBalinghoy(), report2.getIpil_ipil(), report2.getHagunoy(),
                            report2.getTalahib(), report2.getMonggo(), report2.getPatani());
                    return  report1;
                })).values().stream().collect(Collectors.toList());

        sortedReports = combinedReports.stream()
                .sorted(Comparator.comparing(ReportModel::getDate))
                .collect(Collectors.toList());
    }
    private void filterReports(String month, String year) {
        if (!sortedReports.isEmpty()) {
            filteredReports = sortedReports.stream()
                    .filter(report -> report.getDate().substring(0, 2).equals(month) && report.getDate().substring(6, 10).equals(year))
                    .collect(Collectors.toList());

            if (!filteredReports.isEmpty()) {
                int lastDayReport = Integer.parseInt(filteredReports.get(filteredReports.size() - 1).getDate().substring(3, 5));
                ArrayList<Integer> reportDays = new ArrayList<>();
                for (ReportModel report: filteredReports) {
                    int day = Integer.parseInt(report.getDate().substring(3, 5));
                    reportDays.add(day);
                }

                gmelinaLine.clear();
                balinghoyLine.clear();
                hagunoyLine.clear();
                monggoLine.clear();
                talahibLine.clear();
                ipilLine.clear();
                pataniLine.clear();

                int day;
                int j = 0;
                for (int i = 0; i < lastDayReport; i++) {
                    day = i + 1;
                    if (!reportDays.contains(day)) {
                        gmelinaLine.add(new Entry(day, 0));
                        balinghoyLine.add(new Entry(day, 0));
                        hagunoyLine.add(new Entry(day, 0));
                        monggoLine.add(new Entry(day, 0));
                        talahibLine.add(new Entry(day, 0));
                        ipilLine.add(new Entry(day, 0));
                        pataniLine.add(new Entry(day, 0));
                    } else {
                        int x = Integer.parseInt(filteredReports.get(j).getDate().substring(3, 5));

                        int plant1 = filteredReports.get(j).getGmelina();
                        int plant2 = filteredReports.get(j).getBalinghoy();
                        int plant3 = filteredReports.get(j).getHagunoy();
                        int plant4 = filteredReports.get(j).getMonggo();
                        int plant5 = filteredReports.get(j).getTalahib();
                        int plant6 = filteredReports.get(j).getIpil_ipil();
                        int plant7 = filteredReports.get(j).getPatani();

                        // populate the plant line arrays
                        gmelinaLine.add(new Entry(x, plant1));
                        balinghoyLine.add(new Entry(x, plant2));
                        hagunoyLine.add(new Entry(x, plant3));
                        monggoLine.add(new Entry(x, plant4));
                        talahibLine.add(new Entry(x, plant5));
                        ipilLine.add(new Entry(x, plant6));
                        pataniLine.add(new Entry(x, plant7));
                        j += 1;

                        showLineChart();
                    }
                }
            } else {
                LineData lineData = new LineData();
                reportChart.setData(lineData);
                reportChart.invalidate();
            }
        }
    }
    private void showLineChart() {
        reportChart.setDrawGridBackground(false);

        Description description = new Description();
        description.setText("");
        reportChart.setDescription(description);

        showAllLine();

        Legend legend = reportChart.getLegend();
        legend.setEnabled(false);

        XAxis xAxis = reportChart.getXAxis();
        YAxis yAxisLeft = reportChart.getAxisLeft();
        YAxis yAxisRight = reportChart.getAxisRight();

        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        if (isNightMode) {
            xAxis.setTextColor(Color.WHITE);
            yAxisLeft.setTextColor(Color.WHITE);
        }

        yAxisRight.setEnabled(false);
    }
    private void showAllLine() {
        dataSets.clear();

        LineDataSet lineDataSet1 = new LineDataSet(gmelinaLine, "Gmelina");
        lineDataSet1.setLineWidth(3);
        lineDataSet1.setColor(ContextCompat.getColor(this, R.color.lavenderColor));

        LineDataSet lineDataSet2 = new LineDataSet(balinghoyLine, "Balinghoy");
        lineDataSet2.setLineWidth(3);
        lineDataSet2.setColor(ContextCompat.getColor(this, R.color.blueColor));

        LineDataSet lineDataSet3 = new LineDataSet(hagunoyLine, "Hagunoy");
        lineDataSet3.setLineWidth(3);
        lineDataSet3.setColor(ContextCompat.getColor(this, R.color.midGreen));

        LineDataSet lineDataSet4 = new LineDataSet(monggoLine, "Monggo");
        lineDataSet4.setLineWidth(3);
        lineDataSet4.setColor(ContextCompat.getColor(this, R.color.orangeColor));

        LineDataSet lineDataSet5 = new LineDataSet(talahibLine, "Talahib");
        lineDataSet5.setLineWidth(3);
        lineDataSet5.setColor(ContextCompat.getColor(this, R.color.dangerColor));

        LineDataSet lineDataSet6 = new LineDataSet(ipilLine, "Ipil-ipil");
        lineDataSet6.setLineWidth(3);
        lineDataSet6.setColor(ContextCompat.getColor(this, R.color.brownColor));

        LineDataSet lineDataSet7 = new LineDataSet(pataniLine, "Patani");
        lineDataSet7.setLineWidth(3);
        lineDataSet7.setColor(ContextCompat.getColor(this, R.color.pureBlue));

        if (isNightMode) {
            lineDataSet1.setValueTextColor(Color.WHITE);
            lineDataSet2.setValueTextColor(Color.WHITE);
            lineDataSet3.setValueTextColor(Color.WHITE);
            lineDataSet4.setValueTextColor(Color.WHITE);
            lineDataSet5.setValueTextColor(Color.WHITE);
            lineDataSet6.setValueTextColor(Color.WHITE);
            lineDataSet7.setValueTextColor(Color.WHITE);
        }

        dataSets.add(lineDataSet1);
        dataSets.add(lineDataSet2);
        dataSets.add(lineDataSet3);
        dataSets.add(lineDataSet4);
        dataSets.add(lineDataSet5);
        dataSets.add(lineDataSet6);
        dataSets.add(lineDataSet7);

        LineData data = new LineData(dataSets);
        reportChart.clear();
        reportChart.setData(data);
        reportChart.invalidate();

        reportChart.setVisibleXRangeMaximum(6);
    }
    private void populatePlantCount() {
        gmelinaCount.setText("0");
        balinghoyCount.setText("0");
        ipilCount.setText("0");
        hagunoyCount.setText("0");
        talahibCount.setText("0");
        monggoCount.setText("0");
        pataniCount.setText("0");

        if (!reportsArray.isEmpty()) {
            for (ReportModel report: reportsArray) {
                gmelina += report.Gmelina;
                balinghoy += report.Balinghoy;
                ipil += report.Ipil_ipil;
                hagunoy += report.Hagunoy;
                talahib += report.Talahib;
                monggo += report.Monggo;
                patani += report.Patani;
            }
            DecimalFormat decimalFormat = new DecimalFormat("#,###");
            gmelinaCount.setText(decimalFormat.format(gmelina));
            balinghoyCount.setText(decimalFormat.format(balinghoy));
            ipilCount.setText(decimalFormat.format(ipil));
            hagunoyCount.setText(decimalFormat.format(hagunoy));
            talahibCount.setText(decimalFormat.format(talahib));
            monggoCount.setText(decimalFormat.format(monggo));
            pataniCount.setText(decimalFormat.format(patani));
        }
    }
    private void getHarmfulPlantsFromDB() {
        harmfulPlantsCount.setText("0");

        harmfulPlantsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                harmfulPlantsArray.clear();
                for (DataSnapshot data: snapshot.getChildren()) {
                    HarmfulPlantModel harmfulPlantModel = data.getValue(HarmfulPlantModel.class);
                    harmfulPlantsArray.add(harmfulPlantModel);
                }
                if (!harmfulPlantsArray.isEmpty()) {
                    harmfulPlantsCount.setText(String.valueOf(harmfulPlantsArray.size()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AdminDashboardActivity.this, "Error: " + error.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void getToxinsFromDB() {
        plantToxinsCount.setText("0");

        toxinRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                toxinsArray.clear();
                for (DataSnapshot data: snapshot.getChildren()) {

                    String toxinName = data.child("toxinName").getValue(String.class);
                    String toxinDesc = data.child("toxinDesc").getValue(String.class);
                    String toxinConsumptionLevel = data.child("toxinConsumptionLevel").getValue(String.class);
                    String toxinEffects = data.child("toxinEffects").getValue(String.class);

                    toxinsArray.add(new ToxinModel(toxinName, toxinDesc, toxinConsumptionLevel, toxinEffects));
                }
                plantToxinsCount.setText(String.valueOf(toxinsArray.size()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AdminDashboardActivity.this, "Error fetching toxins: " + error.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void getBeneficialPlantsFromDB() {
        beneficialPlantsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                beneficialPlantsArray.clear();
                for (DataSnapshot data: snapshot.getChildren()) {
                    BeneficialPlantModel plantModel = data.getValue(BeneficialPlantModel.class);

                    beneficialPlantsArray.add(plantModel);
                }
                beneficialPlantsCount.setText(String.valueOf(beneficialPlantsArray.size()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AdminDashboardActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void getNutrientsFromDB() {
        nutrientsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                nutrientsArray.clear();
                for (DataSnapshot data: snapshot.getChildren()) {
                    NutrientModel nutrientModel = data.getValue(NutrientModel.class);
                    nutrientsArray.add(nutrientModel);
                }
                nutrientsCount.setText(String.valueOf(nutrientsArray.size()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AdminDashboardActivity.this, "Error: " + nutrientsArray.size(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}