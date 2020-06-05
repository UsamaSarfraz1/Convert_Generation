

package com.cgitsoft.convertgeneration.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.cgitsoft.convertgeneration.AttendanceModel.Details;
import com.cgitsoft.convertgeneration.AttendanceModel.Root;
import com.cgitsoft.convertgeneration.Dashboard;
import com.cgitsoft.convertgeneration.R;
import com.cgitsoft.convertgeneration.adapters.AttendanceAdapter;
import com.cgitsoft.convertgeneration.models.Utills;
import com.cgitsoft.convertgeneration.models.Utils;
import com.cgitsoft.convertgeneration.models.attendance.AttendanceDetail;
import com.cgitsoft.convertgeneration.models.attendance.AttendanceResponse;
import com.cgitsoft.convertgeneration.retrofit.CGITAPIs;
import com.cgitsoft.convertgeneration.retrofit.RetrofitService;
import com.wang.avi.AVLoadingIndicatorView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AttendanceDetailActivity extends AppCompatActivity {
    private ArrayList<Details> list;
    private AttendanceAdapter adapter;
    private AVLoadingIndicatorView progressBar;
    private TextView txtFrom,txtTo;
    private AttendanceViewModel viewModel;
    String User_Id,from,to;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_detail);

        viewModel = new ViewModelProvider(this).get(AttendanceViewModel.class);

        init();

        getDetail();
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
        RecyclerView recyclerView = findViewById(R.id.recyclerView);

        Utills.checkConnection(this,txtFrom);

        User_Id= Utils.getSharedPref(this).getId();

        imgbtnFrom.setOnClickListener(from -> setDate(txtFrom));
        imgbtnTo.setOnClickListener(to -> setDate(txtTo));
        btnFilter.setOnClickListener(filter -> validateDates());
        setUpRecyclerView(recyclerView);
    }

    private void validateDates() {
        from = txtFrom.getText().toString().trim();
        to = txtTo.getText().toString().trim();
        if(from.isEmpty() || to.isEmpty()){
            Toast.makeText(this, "Select date range please", Toast.LENGTH_SHORT).show();
            return;
        }
        viewModel.initAttendanceByRange(progressBar,from,to,User_Id,Utils.isAdmin(AttendanceDetailActivity.this));
        viewModel.getLiveAttendanceByRange().observe(this,response -> {
            if(response != null && response.getDetails() != null){
                list.clear();
                list.addAll(response.getDetails());
                adapter.notifyDataSetChanged();
            }
        });
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

    private void getDetail() {
        progressBar.setVisibility(View.VISIBLE);

        if (Utils.isAdmin(AttendanceDetailActivity.this)){
            new Thread(new Runnable() {
                @Override
                public void run() {

                    CGITAPIs api = RetrofitService.createService(CGITAPIs.class);
                    api.getAttendance("view_attendance").enqueue(new Callback<Root>() {
                        @Override
                        public void onResponse(Call<Root> call, Response<Root> response) {
                            if(response.isSuccessful()){
                                Root root = response.body();
                                if(root != null && root.getStatus().equals("200")){
                                    list.addAll(root.getDetails());
                                    adapter.notifyDataSetChanged();
                                }else {
                                    Log.i("My Errors::",root.getStatus());
                                }
                            }
                            progressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onFailure(Call<Root> call, Throwable t) {
                            new AlertDialog.Builder(AttendanceDetailActivity.this)
                                    .setTitle("Response")
                                    .setMessage(t.getMessage())
                                    .setPositiveButton("OK",((dialog, which) -> finish())).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    });


                }
            }).start();

        }else {
            new Thread(new Runnable() {
                @Override
                public void run() {

                    CGITAPIs api = RetrofitService.createService(CGITAPIs.class);
                    api.getAttendanceList(User_Id,from,to).enqueue(new Callback<Root>() {
                        @Override
                        public void onResponse(Call<Root> call, Response<Root> response) {
                            if(response.isSuccessful()){
                                Root root = response.body();
                                if(root != null && root.getStatus().equals("200")){
                                    list.addAll(root.getDetails());
                                    adapter.notifyDataSetChanged();
                                }else {
                                    Log.i("My Errors::",root.getStatus());
                                }
                            }
                            progressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onFailure(Call<Root> call, Throwable t) {
                            new AlertDialog.Builder(AttendanceDetailActivity.this)
                                    .setTitle("Response")
                                    .setMessage(t.getMessage())
                                    .setPositiveButton("OK",((dialog, which) -> finish())).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                }
            }).start();

        }


    }


    private class CallBackListener implements Callback<AttendanceResponse> {

        @Override
        public void onResponse(Call<AttendanceResponse> call, Response<AttendanceResponse> response) {

        }

        @Override
        public void onFailure(Call<AttendanceResponse> call, Throwable t) {

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Utills.checkConnection(this,txtFrom);
    }
}
