package com.cgitsoft.convertgeneration.repositiories;

import android.util.Log;
import android.view.View;

import androidx.lifecycle.MutableLiveData;

import com.cgitsoft.convertgeneration.AttendanceModel.Root;
import com.cgitsoft.convertgeneration.retrofit.CGITAPIs;
import com.cgitsoft.convertgeneration.retrofit.RetrofitService;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class retrofitRepository {

    private final static String TAG = retrofitRepository.class.getSimpleName();
    private static retrofitRepository instance;
    public static retrofitRepository getInstance(){
        if(instance == null){
            instance = new retrofitRepository();
        }
        return instance;
    }

    public MutableLiveData<Root> getAttendanceByRange(AVLoadingIndicatorView view,
                                                             String from,String to,String User_Id,
                                                             boolean isAdmin){
        if (isAdmin){
            return DataForAdmin(view,from,to,User_Id);
        }else {
            return DataForEmployee(view,from,to,User_Id);
        }
    }

    private MutableLiveData<Root> DataForAdmin(AVLoadingIndicatorView view,
                                              String from,String to,String User_Id){
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
    }

    public MutableLiveData<Root> DataForEmployee(AVLoadingIndicatorView view,
                                                 String from,String to,String User_Id){
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
