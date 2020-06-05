package com.cgitsoft.convertgeneration.activities;

import android.view.View;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.cgitsoft.convertgeneration.AttendanceModel.Root;
import com.cgitsoft.convertgeneration.repositiories.RetrofitRepository;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;

public class AttendanceViewModel extends ViewModel {
    private MutableLiveData<Root> arrayListMutableLiveData;

    public void initAttendanceByRange(AVLoadingIndicatorView progressBar, String from, String to, String UserId,boolean isAdmin){
        progressBar.setVisibility(View.VISIBLE);
        arrayListMutableLiveData = RetrofitRepository.getAttendanceByRange(progressBar,from,to,UserId,isAdmin);
    }
    public LiveData<Root> getLiveAttendanceByRange(){return arrayListMutableLiveData;}
}
