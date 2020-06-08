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
import com.cgitsoft.convertgeneration.models.Utills;
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

    private final static String TAG = ViewAttendanceFragment.class.getSimpleName();
    private ArrayList<Details> list;
    private AttendanceAdapter adapter;
    private AVLoadingIndicatorView progressBar;
    private TextView txtFrom,txtTo;
    private AttendanceViewModel viewModel;
    String User_Id,from,to;
    RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v;
        v=inflater.inflate(R.layout.fragment_view_attendance,container,false);
        viewModel = new ViewModelProvider(this).get(AttendanceViewModel.class);
        init(v);
        viewModel=new ViewModelProvider(this).get(AttendanceViewModel.class);
        viewModel.init(progressBar,from,to,User_Id,Utils.isAdmin(getContext()));
        viewModel.getCurrentData().observe(getActivity(), root -> {
            if(root != null && root.getDetails() != null){
                Log.i(TAG,"observe");
                list.clear();
                list.addAll(root.getDetails());
                adapter.notifyDataSetChanged();
            }
        });

        setUpRecyclerView(recyclerView);
        return v;
    }

    private void init(View view) {

        progressBar = view.findViewById(R.id.progressBar);
        txtFrom = view.findViewById(R.id.txt_from);
        txtTo = view.findViewById(R.id.txt_to);
        ImageButton imgbtnFrom = view.findViewById(R.id.imgbtn_from);
        ImageButton imgbtnTo = view.findViewById(R.id.imgbtn_to);
        Button btnFilter = view.findViewById(R.id.btn_filterResult);
        recyclerView = view.findViewById(R.id.recyclerView);
        User_Id= Utils.getSharedPref(getContext()).getId();
        imgbtnFrom.setOnClickListener(from -> setDate(txtFrom));
        imgbtnTo.setOnClickListener(to -> setDate(txtTo));
        btnFilter.setOnClickListener(filter -> validateDates());
    }

    private void validateDates() {
        from = txtFrom.getText().toString().trim();
        to = txtTo.getText().toString().trim();
        if(from.isEmpty() || to.isEmpty()) {
            Toast.makeText(getContext(), "Select date range please", Toast.LENGTH_SHORT).show();
            return;
        }

        if (Utils.isAdmin(getContext())){
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
}
