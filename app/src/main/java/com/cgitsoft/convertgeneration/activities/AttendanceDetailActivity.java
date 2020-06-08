

package com.cgitsoft.convertgeneration.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.cgitsoft.convertgeneration.AttendanceModel.Details;
import com.cgitsoft.convertgeneration.R;
import com.cgitsoft.convertgeneration.adapters.AttendanceAdapter;
import com.cgitsoft.convertgeneration.models.Utils;
import com.wang.avi.AVLoadingIndicatorView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class AttendanceDetailActivity extends AppCompatActivity {

    private final static String TAG = AttendanceDetailActivity.class.getSimpleName();
    private ArrayList<Details> list;
    private AttendanceAdapter adapter;
    private AVLoadingIndicatorView progressBar;
    private TextView txtFrom,txtTo;
    private AttendanceViewModel viewModel;
    String User_Id,from,to;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_detail);
        init();
        viewModel=new ViewModelProvider(this).get(AttendanceViewModel.class);
        viewModel.init(progressBar,from,to,User_Id,Utils.isAdmin(this));
        viewModel.getCurrentData().observe(this, root -> {
                if(root != null && root.getDetails() != null){
                    Log.i(TAG,"observe");
                    list.clear();
                    list.addAll(root.getDetails());
                    adapter.notifyDataSetChanged();
                }
        });

        setUpRecyclerView(recyclerView);

    }
    private void init() {

        progressBar = findViewById(R.id.progressBar);
        txtFrom = findViewById(R.id.txt_from);
        txtTo = findViewById(R.id.txt_to);
        ImageButton imgbtnFrom = findViewById(R.id.imgbtn_from);
        ImageButton imgbtnTo = findViewById(R.id.imgbtn_to);
        ImageButton btnBack = findViewById(R.id.imgbtn_back);
        Button btnFilter = findViewById(R.id.btn_filterResult);
        btnBack.setOnClickListener(back -> finish());
        recyclerView = findViewById(R.id.recyclerView);
        User_Id= Utils.getSharedPref(this).getId();
        imgbtnFrom.setOnClickListener(from -> setDate(txtFrom));
        imgbtnTo.setOnClickListener(to -> setDate(txtTo));
        btnFilter.setOnClickListener(filter -> validateDates());
    }

    private void validateDates() {
        from = txtFrom.getText().toString().trim();
        to = txtTo.getText().toString().trim();
        if(from.isEmpty() || to.isEmpty()) {
            Toast.makeText(this, "Select date range please", Toast.LENGTH_SHORT).show();
            return;
        }

        if (Utils.isAdmin(this)){
            viewModel.getAdminDataByRange(progressBar,from,to,User_Id);
        }else {
            viewModel.getEmployeeDataByRange(progressBar,from,to,User_Id);
        }

    }

    private void setDate(TextView textView) {
        Calendar calendar = Calendar.getInstance();

        DatePickerDialog.OnDateSetListener listener = (view, year, month, dayOfMonth) -> {
            //2020-03-01
            String dateFormat="yyyy-MM-dd";
            calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);
            calendar.set(Calendar.MONTH,month);
            calendar.set(Calendar.YEAR,year);
            SimpleDateFormat format = new SimpleDateFormat(dateFormat, Locale.US);
            textView.setText(format.format(calendar.getTime()));
        };
        new DatePickerDialog(this,listener,calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void setUpRecyclerView(RecyclerView recyclerView) {
        list = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                DividerItemDecoration.HORIZONTAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
        adapter = new AttendanceAdapter(this,list);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
