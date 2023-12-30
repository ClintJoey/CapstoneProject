package com.example.capstoneproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;

import com.example.capstoneproject.adapters.AdminUserReportAdapter;
import com.example.capstoneproject.models.ReportModel;
import com.example.capstoneproject.models.UserAccountModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class ManageReportsActivity extends AppCompatActivity {
    private ImageView back;
    private RecyclerView userReportsRec;
    private Spinner monthSpinner, yearSpinner;
    private ArrayList<UserAccountModel> usersArray;
    private ArrayList<ReportModel> reportsArray;
    private ArrayList<String> monthsArray = new ArrayList<>();
    private ArrayList<String> yearArray = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_reports);

        Date date = new Date();
        SimpleDateFormat currentMonth = new SimpleDateFormat("MM");
        SimpleDateFormat currentYear = new SimpleDateFormat("Y");
        String month = currentMonth.format(date);
        String year = currentYear.format(date);

        back = findViewById(R.id.back);
        userReportsRec = findViewById(R.id.userReportsRec);
        monthSpinner = findViewById(R.id.monthSpinner);
        yearSpinner = findViewById(R.id.yearSpinner);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        fetchIntents();

        userReportsRec.setLayoutManager(new LinearLayoutManager(this));
        AdminUserReportAdapter adapter = new AdminUserReportAdapter(this, usersArray, reportsArray);
        userReportsRec.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        populateMonthArray();
        setSpinnerItems(monthSpinner, monthsArray);
        monthSpinner.setSelection(Integer.parseInt(month) - 1);

        for (ReportModel report: reportsArray) {
            String yearItem = report.getDate().substring(6,10);
            if (!yearArray.contains(yearItem)) {
                yearArray.add(yearItem);
            }
        }
        setSpinnerItems(yearSpinner, yearArray);
        yearSpinner.setSelection(yearArray.indexOf(year));

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
                filterData(adapter, monthFilter, yearSpinner.getSelectedItem().toString());
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
                filterData(adapter, monthFilter, yearSpinner.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
    private void fetchIntents() {
        Intent intent = getIntent();
        usersArray =  (ArrayList<UserAccountModel>) intent.getSerializableExtra("usersArray");
        reportsArray =  (ArrayList<ReportModel>) intent.getSerializableExtra("reportsArray");
        Collections.reverse(reportsArray);
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
    private void filterData(AdminUserReportAdapter adapter, String month, String year) {
        ArrayList<ReportModel> lists = new ArrayList<>();
        for (ReportModel report: reportsArray) {
            if (report.getDate().substring(0,2).equals(month) && report.getDate().substring(6,10).equals(year)) {
                lists.add(report);
            }
        }
        adapter.filterLists(lists);
    }
}