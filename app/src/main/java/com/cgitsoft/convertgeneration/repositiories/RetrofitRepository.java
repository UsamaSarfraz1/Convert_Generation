package com.cgitsoft.convertgeneration.repositiories;

import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.cgitsoft.convertgeneration.AttendanceModel.Root;
import com.cgitsoft.convertgeneration.Dashboard;
import com.cgitsoft.convertgeneration.models.Utils;
import com.cgitsoft.convertgeneration.models.attendance.AttendanceResponse;
import com.cgitsoft.convertgeneration.retrofit.CGITAPIs;
import com.cgitsoft.convertgeneration.retrofit.RetrofitService;
import com.wang.avi.AVLoadingIndicatorView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Objects;
import java.util.logging.Logger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RetrofitRepository {

    private final static String TAG = RetrofitRepository.class.getSimpleName();

    public static MutableLiveData<Root> getAttendanceByRange(AVLoadingIndicatorView view,
                                                                                      String from,String to,String User_Id,
                                                                                      boolean isAdmin){

        if (isAdmin){
            MutableLiveData<Root> attendanceResponse = new MutableLiveData<>();
            CGITAPIs api = RetrofitService.createService(CGITAPIs.class);
            api.getAttendanceByRange("view_attendance",from,to).enqueue(new Callback<Root>() {
                @Override
                public void onResponse(Call<Root> call, Response<Root> response) {
                    if(response.isSuccessful()){
                        ArrayList<Root> list = new ArrayList<>();
                        Root response1 = response.body();
                        attendanceResponse.setValue(response1);
                    }view.setVisibility(View.GONE);
                }

                @Override
                public void onFailure(Call<Root> call, Throwable t) {
                    view.setVisibility(View.GONE);
                    Log.d(TAG, "Response Failed -> "+Objects.requireNonNull(t.getMessage()));
                }
            });
            return attendanceResponse;

        }else {
            MutableLiveData<Root> attendanceResponse = new MutableLiveData<>();
            CGITAPIs api = RetrofitService.createService(CGITAPIs.class);
            api.getAttendanceList(User_Id,from,to).enqueue(new Callback<Root>() {
                @Override
                public void onResponse(Call<Root> call, Response<Root> response) {
                    if(response.isSuccessful()){
                        ArrayList<Root> list = new ArrayList<>();
                        Root response1 = response.body();
                        attendanceResponse.setValue(response1);
                    }view.setVisibility(View.GONE);
                }

                @Override
                public void onFailure(Call<Root> call, Throwable t) {
                    view.setVisibility(View.GONE);
                    Log.d(TAG, "Response Failed -> "+Objects.requireNonNull(t.getMessage()));
                }
            });
            return attendanceResponse;
        }
    }
}
