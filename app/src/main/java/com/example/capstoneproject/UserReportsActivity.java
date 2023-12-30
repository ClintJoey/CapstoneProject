package com.example.capstoneproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.capstoneproject.adapters.UserReportsAdapter;
import com.example.capstoneproject.models.ReportModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class UserReportsActivity extends AppCompatActivity {
    private RecyclerView userReportRec;
    private Spinner monthSpinner, yearSpinner;
    private UserReportsAdapter userReportsAdapter;
    private List<ReportModel> userReports;
    private ArrayList<String> monthsArray = new ArrayList<>();
    private ArrayList<String> yearsArray = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_reports);

        Date date = new Date();
        SimpleDateFormat currentMonth = new SimpleDateFormat("MM");
        SimpleDateFormat currentYear = new SimpleDateFormat("Y");
        String month = currentMonth.format(date);
        String year = currentYear.format(date);

        userReportRec = findViewById(R.id.userReportRec);
        monthSpinner = findViewById(R.id.monthSpinner);
        yearSpinner = findViewById(R.id.yearSpinner);

        getUserReportKeys();

        // reverse the elements of the array
        Collections.reverse(userReports);

        for (ReportModel report: userReports) {
            Log.d("demo", report.toString());
        }

        populateMonthArray();
        setSpinnerItems(monthSpinner, monthsArray);
        monthSpinner.setSelection(Integer.parseInt(month) - 1);

        if (!userReports.isEmpty()) {
            for (ReportModel report: userReports) {
                String yearItem = report.getDate().substring(6,10);
                if (!yearsArray.contains(yearItem)) {
                    yearsArray.add(yearItem);
                }
            }
            setSpinnerItems(yearSpinner, yearsArray);
            yearSpinner.setSelection(yearsArray.indexOf(year));

            userReportRec.setLayoutManager(new LinearLayoutManager(UserReportsActivity.this));
            userReportsAdapter = new UserReportsAdapter(UserReportsActivity.this, userReports);
            userReportRec.setAdapter(userReportsAdapter);
            userReportsAdapter.notifyDataSetChanged();
        }

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
                if (!userReports.isEmpty()) {
                    filterData(userReportsAdapter, monthFilter, yearSpinner.getSelectedItem().toString());
                }
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
                if (!userReports.isEmpty()) {
                    filterData(userReportsAdapter, monthFilter, yearSpinner.getSelectedItem().toString());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
    public void getUserReportKeys() {
        Intent intent = getIntent();
        // get array of objects intent
        userReports = (List<ReportModel>) intent.getSerializableExtra("userReports");
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
    private void filterData(UserReportsAdapter adapter, String month, String year) {
        ArrayList<ReportModel> lists = new ArrayList<>();
        for (ReportModel report: userReports) {
            if (report.getDate().substring(0,2).equals(month) && report.getDate().substring(6,10).equals(year)) {
                lists.add(report);
            }
        }
        adapter.filterLists(lists);
    }
}