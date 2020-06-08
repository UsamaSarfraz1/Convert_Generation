package com.cgitsoft.convertgeneration.activities;

import android.util.Log;
import android.view.View;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.cgitsoft.convertgeneration.AttendanceModel.Root;
import com.cgitsoft.convertgeneration.retrofit.CGITAPIs;
import com.cgitsoft.convertgeneration.retrofit.RetrofitService;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AttendanceViewModel extends ViewModel {

    private final static String TAG = AttendanceViewModel.class.getSimpleName();
    private MutableLiveData<Root> arrayListMutableLiveData;
    private com.cgitsoft.convertgeneration.repositiories.retrofitRepository retrofitRepository;
    private int i=0;

    public void init(AVLoadingIndicatorView progressBar, String from, String to, String UserId,boolean isAdmin){

        Log.i(TAG, String.valueOf(i));
        if (arrayListMutableLiveData != null){
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        retrofitRepository = retrofitRepository.getInstance();
        arrayListMutableLiveData = retrofitRepository.getAttendanceByRange(progressBar,from,to,UserId,isAdmin);
        Log.i(TAG,"viewModel init()!!!");
    }



    public void getEmployeeDataByRange(AVLoadingIndicatorView progressBar, String from, String to, String UserId){
        progressBar.setVisibility(View.VISIBLE);
        Log.i(TAG,"viewModel getNewData()");
        CGITAPIs api = RetrofitService.createService(CGITAPIs.class);
        api.getAttendanceList(UserId,from,to).enqueue(new Callback<Root>() {
            @Override
            public void onResponse(Call<Root> call, Response<Root> response) {
                if(response.isSuccessful()){
                    progressBar.setVisibility(View.GONE);
                    Root response1 = response.body();
                    Log.i(TAG,"on post");
                    arrayListMutableLiveData.postValue(response1);
                }
            }

            @Override
            public void onFailure(Call<Root> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Log.d(TAG, "Response Failed -> "+ Objects.requireNonNull(t.getMessage()));
            }});
    }

    public void getAdminDataByRange(AVLoadingIndicatorView progressBar, String from, String to, String UserId){
        progressBar.setVisibility(View.VISIBLE);
        Log.i(TAG,"viewModel getNewData()");
        CGITAPIs api = RetrofitService.createService(CGITAPIs.class);
        api.getAttendanceByRange("view_attendance",from,to).enqueue(new Callback<Root>() {
            @Override
            public void onResponse(Call<Root> call, Response<Root> response) {
                if(response.isSuccessful()){
                    progressBar.setVisibility(View.GONE);
                    Root response1 = response.body();
                    arrayListMutableLiveData.postValue(response1);
                }
            }

            @Override
            public void onFailure(Call<Root> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Log.d(TAG, "Response Failed -> "+Objects.requireNonNull(t.getMessage()));
            }
        });
    }
    public LiveData<Root> getCurrentData(){
        return arrayListMutableLiveData;
    }

}
