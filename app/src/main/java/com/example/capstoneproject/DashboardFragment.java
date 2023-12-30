package com.example.capstoneproject;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.capstoneproject.models.ReportModel;
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

public class DashboardFragment extends Fragment {
    private Button viewUserReportsBtn;
    private TextView gmelinaCount, balinghoyCount, hagunoyCount, monggoCount, talahibCount, ipilCount, pataniCount;
    private Spinner plantSpinner, monthSpinner, yearSpinner;
    private ArrayList<String> fetchedReportKeys;
    FirebaseDatabase database;
    DatabaseReference userRef, reportRef;
    private LineChart userReportChart;
    private ArrayList<Entry> gmelinaLine = new ArrayList<>();
    private ArrayList<Entry> balinghoyLine = new ArrayList<>();
    private ArrayList<Entry> ipilLine = new ArrayList<>();
    private ArrayList<Entry> hagunoyLine = new ArrayList<>();
    private ArrayList<Entry> talahibLine = new ArrayList<>();
    private ArrayList<Entry> monggoLine = new ArrayList<>();
    private ArrayList<Entry> pataniLine = new ArrayList<>();
    private ArrayList<ILineDataSet> dataSets = new ArrayList<>();
    final private ArrayList<ReportModel> reportArr = new ArrayList<>();
    private ArrayList<ReportModel> passableReports = new ArrayList<>();
    private List<ReportModel> combinedReports;
    private List<ReportModel> sortedReports;
    private List<ReportModel> filteredReports;
    private int gmelina = 0;
    private int balinghoy = 0;
    private int ipil = 0;
    private int hagunoy = 0;
    private int talahib = 0;
    private int monggo = 0;
    private int patani = 0;
    private ArrayList<String> monthsArray = new ArrayList<>();
    private ArrayList<String> yearArray = new ArrayList<>();
    private boolean isNightMode;
    private String selectedMonth, selectedYear;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_dashboard, container, false);

        database = FirebaseDatabase.getInstance();
        userRef = database.getReference("testUsers2");
        reportRef = database.getReference("testReport");

        Date date = new Date();
        SimpleDateFormat currentMonth = new SimpleDateFormat("MM");
        SimpleDateFormat currentYear = new SimpleDateFormat("Y");
        String month = currentMonth.format(date);
        String year = currentYear.format(date);

        gmelinaCount = rootView.findViewById(R.id.gmelinaCount);
        balinghoyCount = rootView.findViewById(R.id.balinghoyCount);
        hagunoyCount = rootView.findViewById(R.id.hagunoyCount);
        monggoCount = rootView.findViewById(R.id.monggoCount);
        talahibCount = rootView.findViewById(R.id.talahibCount);
        ipilCount = rootView.findViewById(R.id.ipilCount);
        pataniCount = rootView.findViewById(R.id.pataniCount);

        viewUserReportsBtn = rootView.findViewById(R.id.viewUserReportsBtn);

        plantSpinner = rootView.findViewById(R.id.plantSpinner);
        monthSpinner = rootView.findViewById(R.id.monthSpinner);
        yearSpinner = rootView.findViewById(R.id.yearSpinner);

        userReportChart = rootView.findViewById(R.id.userReportChart);

        populateMonthArray();
        isNightMode = isNightMode(rootView.getContext());

        getReportsFromDB(rootView, month, year);

        viewUserReportsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), UserReportsActivity.class);
                intent.putExtra("userReports", passableReports);
                startActivity(intent);
            }
        });

        plantSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String plantFilter = parent.getItemAtPosition(position).toString();

                if (plantFilter.equals("All")) {
                    showAllLine(rootView);
                }
                if (plantFilter.equals("Gmelina")) {
                    dataSets.clear();

                    LineDataSet lineDataSet = new LineDataSet(gmelinaLine, "Gmelina");
                    addPlantLine(lineDataSet, ContextCompat.getColor(rootView.getContext(), R.color.lavenderColor));

                    dataSets.add(lineDataSet);
                }
                if (plantFilter.equals("Balinghoy")) {
                    dataSets.clear();

                    LineDataSet lineDataSet = new LineDataSet(balinghoyLine, "Balinghoy");
                    addPlantLine(lineDataSet, ContextCompat.getColor(rootView.getContext(), R.color.blueColor));

                    dataSets.add(lineDataSet);
                }
                if (plantFilter.equals("Hagunoy")) {
                    dataSets.clear();

                    LineDataSet lineDataSet = new LineDataSet(hagunoyLine, "Hagunoy");
                    addPlantLine(lineDataSet, ContextCompat.getColor(rootView.getContext(), R.color.midGreen));

                    dataSets.add(lineDataSet);
                }
                if (plantFilter.equals("Monggo")) {
                    dataSets.clear();

                    LineDataSet lineDataSet = new LineDataSet(monggoLine, "Monggo");
                    addPlantLine(lineDataSet, ContextCompat.getColor(rootView.getContext(), R.color.orangeColor));

                    dataSets.add(lineDataSet);
                }
                if (plantFilter.equals("Talahib")) {
                    dataSets.clear();

                    LineDataSet lineDataSet = new LineDataSet(talahibLine, "Talahib");
                    addPlantLine(lineDataSet, ContextCompat.getColor(rootView.getContext(), R.color.dangerColor));

                    dataSets.add(lineDataSet);
                }
                if (plantFilter.equals("Ipil-ipil")) {
                    dataSets.clear();

                    LineDataSet lineDataSet = new LineDataSet(ipilLine, "Ipil-ipil");
                    addPlantLine(lineDataSet, ContextCompat.getColor(rootView.getContext(), R.color.brownColor));

                    dataSets.add(lineDataSet);
                }
                if (plantFilter.equals("Patani")) {
                    dataSets.clear();

                    LineDataSet lineDataSet = new LineDataSet(pataniLine, "Patani");
                    addPlantLine(lineDataSet, ContextCompat.getColor(rootView.getContext(), R.color.pureBlue));

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
                filterChart(rootView, monthFilter, year);
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
                filterChart(rootView, monthFilter, yearSpinner.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return rootView;
    }
    public void getUserReportsKey() {
        Bundle bundle = this.getArguments();
        fetchedReportKeys = bundle.getStringArrayList("reports");
    }
    public void getReportsFromDB(View rootView, String month, String year) {
        AlertDialog.Builder builder = new AlertDialog.Builder(rootView.getContext());
        builder.setCancelable(false);
        builder.setView(R.layout.loading_progress_bar);
        AlertDialog dialog = builder.create();
        dialog.show();

        getUserReportsKey();

        if (fetchedReportKeys.size() == 1) {
            dialog.dismiss();
        } else {
            for (int i = 1; i < fetchedReportKeys.size(); i++) {
                reportRef.child(fetchedReportKeys.get(i)).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            String reportId = snapshot.child("reportId").getValue(String.class);
                            String userUid = snapshot.child("userUid").getValue(String.class);
                            int gmelina = snapshot.child("Gmelina").getValue(Integer.class);
                            int balinghoy = snapshot.child("Balinghoy").getValue(Integer.class);
                            int ipil = snapshot.child("Ipil-ipil").getValue(Integer.class);
                            int hagunoy = snapshot.child("Hagunoy").getValue(Integer.class);
                            int talahib = snapshot.child("Talahib").getValue(Integer.class);
                            int monggo = snapshot.child("Monggo").getValue(Integer.class);
                            int patani = snapshot.child("Patani").getValue(Integer.class);
                            String date = snapshot.child("date").getValue(String.class);
                            String time = snapshot.child("time").getValue(String.class);

                            ReportModel reportModel = new ReportModel(reportId, userUid, date, time, gmelina, balinghoy, ipil, hagunoy, talahib, monggo, patani);
                            reportArr.add(reportModel);

                            String yearSpinnerItem = date.substring(6, 10);
                            if (!yearArray.contains(yearSpinnerItem)) {
                                yearArray.add(yearSpinnerItem);
                            }
                        }
                        if (fetchedReportKeys.size() == (reportArr.size()) + 1) {
                            setSpinnerItems(monthSpinner, rootView, month, monthsArray);
                            monthSpinner.setSelection(Integer.parseInt(month) - 1);

                            setSpinnerItems(yearSpinner, rootView, year, yearArray);
                            yearSpinner.setSelection(yearArray.indexOf(year));

                            populatePlantCount();

                            combineAndSortReports();

                            filterChart(rootView, month, year);

                            dialog.dismiss();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        dialog.dismiss();
                        Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }
    public boolean isNightMode(Context context) {
        int nightModeFlags = context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        return nightModeFlags == Configuration.UI_MODE_NIGHT_YES;
    }
    public void populatePlantCount() {
        for (ReportModel report: reportArr) {
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
    private void combineAndSortReports() {
        combinedReports = reportArr.stream()
                .collect(Collectors.toMap(ReportModel::getDate, report -> report, (report1, report2) -> {
                    report1.combineVal(report2.getGmelina(), report2.getBalinghoy(), report2.getIpil_ipil(), report2.getHagunoy(),
                            report2.getTalahib(), report2.getMonggo(), report2.getPatani());
                    return  report1;
                })).values().stream().collect(Collectors.toList());

        sortedReports = combinedReports.stream()
                .sorted(Comparator.comparing(ReportModel::getDate))
                .collect(Collectors.toList());

        passableReports.addAll(sortedReports);
    }
    private void showLineChart(View rootView) {
        userReportChart.setDrawGridBackground(false);

        Description description = new Description();
        description.setText("");
        userReportChart.setDescription(description);

        showAllLine(rootView);

        Legend legend = userReportChart.getLegend();
        legend.setEnabled(false);

        XAxis xAxis = userReportChart.getXAxis();
        YAxis yAxisLeft = userReportChart.getAxisLeft();
        YAxis yAxisRight = userReportChart.getAxisRight();

        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        if (isNightMode) {
            xAxis.setTextColor(Color.WHITE);
            yAxisLeft.setTextColor(Color.WHITE);
        }

        yAxisRight.setEnabled(false);
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
        userReportChart.setData(data);
        userReportChart.invalidate();

        userReportChart.setVisibleXRangeMaximum(6);
    }
    private void showAllLine(View rootView) {
        dataSets.clear();

        LineDataSet lineDataSet1 = new LineDataSet(gmelinaLine, "Gmelina");
        lineDataSet1.setLineWidth(3);
        lineDataSet1.setColor(ContextCompat.getColor(rootView.getContext(), R.color.lavenderColor));

        LineDataSet lineDataSet2 = new LineDataSet(balinghoyLine, "Balinghoy");
        lineDataSet2.setLineWidth(3);
        lineDataSet2.setColor(ContextCompat.getColor(rootView.getContext(), R.color.blueColor));

        LineDataSet lineDataSet3 = new LineDataSet(hagunoyLine, "Hagunoy");
        lineDataSet3.setLineWidth(3);
        lineDataSet3.setColor(ContextCompat.getColor(rootView.getContext(), R.color.midGreen));

        LineDataSet lineDataSet4 = new LineDataSet(monggoLine, "Monggo");
        lineDataSet4.setLineWidth(3);
        lineDataSet4.setColor(ContextCompat.getColor(rootView.getContext(), R.color.orangeColor));

        LineDataSet lineDataSet5 = new LineDataSet(talahibLine, "Talahib");
        lineDataSet5.setLineWidth(3);
        lineDataSet5.setColor(ContextCompat.getColor(rootView.getContext(), R.color.dangerColor));

        LineDataSet lineDataSet6 = new LineDataSet(ipilLine, "Ipil-ipil");
        lineDataSet6.setLineWidth(3);
        lineDataSet6.setColor(ContextCompat.getColor(rootView.getContext(), R.color.brownColor));

        LineDataSet lineDataSet7 = new LineDataSet(pataniLine, "Patani");
        lineDataSet7.setLineWidth(3);
        lineDataSet7.setColor(ContextCompat.getColor(rootView.getContext(), R.color.pureBlue));

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
        userReportChart.clear();
        userReportChart.setData(data);
        userReportChart.invalidate();

        userReportChart.setVisibleXRangeMaximum(6);
    }
    private void setSpinnerItems(Spinner spinner, View rootView, String date, ArrayList<String> array) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(rootView.getContext(), android.R.layout.simple_selectable_list_item, array);
        spinner.setAdapter(adapter);
        int itemPos = array.indexOf(date);
        spinner.setSelection(itemPos);
    }
    private void filterChart(View rootView, String providedMonth, String providedYear) {
        if (!sortedReports.isEmpty()) {
            selectedMonth = providedMonth;
            selectedYear = providedYear;

            filteredReports = sortedReports.stream()
                    .filter(report -> report.getDate().substring(0, 2).equals(selectedMonth) && report.getDate().substring(6, 10).equals(selectedYear))
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

                        showLineChart(rootView);
                    }
                }
            } else {
                LineData lineData = new LineData();
                userReportChart.setData(lineData);
                userReportChart.invalidate();
            }
        } else {
            Toast.makeText(rootView.getContext(), "Empty", Toast.LENGTH_SHORT).show();
        }
    }
}