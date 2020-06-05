package com.cgitsoft.convertgeneration.ui.view;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cgitsoft.convertgeneration.AttendanceModel.Details;
import com.cgitsoft.convertgeneration.AttendanceModel.Root;
import com.cgitsoft.convertgeneration.R;
import com.cgitsoft.convertgeneration.activities.AttendanceDetailActivity;
import com.cgitsoft.convertgeneration.activities.AttendanceViewModel;
import com.cgitsoft.convertgeneration.adapters.AttendanceAdapter;
import com.cgitsoft.convertgeneration.models.Utils;
import com.cgitsoft.convertgeneration.models.attendance.AttendanceResponse;
import com.cgitsoft.convertgeneration.retrofit.CGITAPIs;
import com.cgitsoft.convertgeneration.retrofit.RetrofitService;
import com.wang.avi.AVLoadingIndicatorView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewAttendanceFragment extends Fragment {

    private ArrayList<Details> list;
    private AttendanceAdapter adapter;
    private AVLoadingIndicatorView progressBar;
    private TextView txtFrom,txtTo;
    private AttendanceViewModel viewModel;
    String User_Id,from,to;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v;
        v=inflater.inflate(R.layout.fragment_view_attendance,container,false);
        progressBar = v.findViewById(R.id.progressBar);
        txtFrom = v.findViewById(R.id.txt_from);
        txtTo = v.findViewById(R.id.txt_to);
        ImageButton imgbtnFrom = v.findViewById(R.id.imgbtn_from);
        ImageButton imgbtnTo = v.findViewById(R.id.imgbtn_to);
        ImageButton btnBack = v.findViewById(R.id.imgbtn_back);
        Button btnFilter = v.findViewById(R.id.btn_filterResult);
        RecyclerView recyclerView = v.findViewById(R.id.recyclerView);

        User_Id= Utils.getSharedPref(getContext()).getId();

        imgbtnFrom.setOnClickListener(from -> setDate(txtFrom));
        imgbtnTo.setOnClickListener(to -> setDate(txtTo));
        btnFilter.setOnClickListener(filter -> validateDates());
        setUpRecyclerView(recyclerView);
        viewModel = new ViewModelProvider(this).get(AttendanceViewModel.class);

        getDetail();
        return v;
    }

    private void validateDates() {
        from = txtFrom.getText().toString().trim();
        to = txtTo.getText().toString().trim();
        if(from.isEmpty() || to.isEmpty()){
            Toast.makeText(getContext(), "Select date range please", Toast.LENGTH_SHORT).show();
            return;
        }
        viewModel.initAttendanceByRange(progressBar,from,to,User_Id,Utils.isAdmin(getContext()));
        viewModel.getLiveAttendanceByRange().observe(getActivity(),response -> {
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
        new DatePickerDialog(getContext(),listener,calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void setUpRecyclerView(RecyclerView recyclerView) {
        list = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                DividerItemDecoration.HORIZONTAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
        adapter = new AttendanceAdapter(getContext(),list);
        recyclerView.setAdapter(adapter);
    }

    private void getDetail() {
        progressBar.setVisibility(View.VISIBLE);

        if (Utils.isAdmin(getContext())) {
            new Thread(new Runnable() {
                @Override
                public void run() {

                    CGITAPIs api = RetrofitService.createService(CGITAPIs.class);
                    api.getAttendance("view_attendance").enqueue(new Callback<Root>() {
                        @Override
                        public void onResponse(Call<Root> call, Response<Root> response) {
                            if (response.isSuccessful()) {
                                Root root = response.body();
                                if (root != null && root.getStatus().equals("200")) {
                                    list.addAll(root.getDetails());
                                    adapter.notifyDataSetChanged();
                                } else {
                                    Log.i("My Errors::", root.getStatus());
                                }
                            }
                            progressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onFailure(Call<Root> call, Throwable t) {
                            new AlertDialog.Builder(getContext())
                                    .setTitle("Response")
                                    .setMessage(t.getMessage())
                                    .setPositiveButton("OK", ((dialog, which) -> getActivity().finish())).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    });


                }
            }).start();

        } else {
            new Thread(new Runnable() {
                @Override
                public void run() {

                    CGITAPIs api = RetrofitService.createService(CGITAPIs.class);
                    api.getAttendanceList(User_Id, from, to).enqueue(new Callback<Root>() {
                        @Override
                        public void onResponse(Call<Root> call, Response<Root> response) {
                            if (response.isSuccessful()) {
                                Root root = response.body();
                                if (root != null && root.getStatus().equals("200")) {
                                    list.addAll(root.getDetails());
                                    adapter.notifyDataSetChanged();
                                } else {
                                    Log.i("My Errors::", root.getStatus());
                                }
                            }
                            progressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onFailure(Call<Root> call, Throwable t) {
                            new AlertDialog.Builder(getContext())
                                    .setTitle("Response")
                                    .setMessage(t.getMessage())
                                    .setPositiveButton("OK", ((dialog, which) -> getActivity().finish())).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                }
            }).start();

        }

    }

}
