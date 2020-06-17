package com.cgitsoft.convertgeneration.Fragments;

import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.cgitsoft.convertgeneration.BaseFragment;
import com.cgitsoft.convertgeneration.R;
import com.cgitsoft.convertgeneration.activities.QrActivity;
import com.cgitsoft.convertgeneration.dialogs.CameraSelectorDialogFragment;
import com.cgitsoft.convertgeneration.dialogs.FormatSelectorDialogFragment;
import com.cgitsoft.convertgeneration.models.GeoLocationAttendance;
import com.cgitsoft.convertgeneration.models.MarkAttendanceResponse;
import com.cgitsoft.convertgeneration.models.SharedPref;
import com.cgitsoft.convertgeneration.models.Utils;
import com.cgitsoft.convertgeneration.retrofit.CGITAPIs;
import com.cgitsoft.convertgeneration.retrofit.RetrofitService;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.List;

import me.dm7.barcodescanner.zxing.ZXingScannerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.view.MenuItem.SHOW_AS_ACTION_NEVER;


public class QrFragment extends BaseFragment implements
        ZXingScannerView.ResultHandler, FormatSelectorDialogFragment.FormatSelectorDialogListener,
        CameraSelectorDialogFragment.CameraSelectorDialogListener {


    private static final String FLASH_SERIES = "FLASH_SERIES";
    private final static String TAG = QrActivity.class.getSimpleName();
    private static final String FLASH_STATE = "FLASH_STATE";
    private static final String AUTO_FOCUS_STATE = "AUTO_FOCUS_STATE";
    private static final String SELECTED_FORMATS = "SELECTED_FORMATS";
    private static final String CAMERA_ID = "CAMERA_ID";
    private static final String LAT = "lat";
    private static final String LNG = "lng";
    private ZXingScannerView mScannerView;
    private boolean mFlash;
    private boolean mAutoFocus;
    private ArrayList<Integer> mSelectedIndices;
    private int mCameraId = -1;
    private String lat,lng;
    ViewGroup contentFrame;
    Ringtone r;
    private AVLoadingIndicatorView progressBar;
    public static String title= "Internet Connection";
    public static String ON_message= "Internet Connected!!!";
    public static String OFF_message= "Not Connected!!!";
    boolean isConnect=true;

    public QrFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_qr, container, false);
        progressBar = view.findViewById(R.id.progressBar);
        Toast.makeText(getContext(), "qr fragment", Toast.LENGTH_SHORT).show();
        mFlash = false;
        mAutoFocus = true;
        mSelectedIndices = null;
        mCameraId = -1;
        if(!Utils.isAdmin(getContext())){
            lat = getArguments().getString("lat");
            lng = getArguments().getString("lng");
        }

        //check internet connectivity


        setupToolbar();

        contentFrame = (ViewGroup) view.findViewById(R.id.content_frame);
        mScannerView = new ZXingScannerView(getContext());
        setupFormats();
        contentFrame.addView(mScannerView);

        return view;
    }


    public void closeFormatsDialog() {
        closeDialog("format_selector");
    }

    public void closeDialog(String dialogName) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        DialogFragment fragment = (DialogFragment) fragmentManager.findFragmentByTag(dialogName);
        if(fragment != null) {
            fragment.dismiss();
        }
    }




    @Override
    public void handleResult(Result rawResult) {

        try {
            progressBar.setVisibility(View.VISIBLE);
            mScannerView.stopCamera();
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            r = RingtoneManager.getRingtone(getActivity().getApplicationContext(), notification);
            Log.d(TAG, rawResult.getText());
            String s = rawResult.getText();
            SharedPref sharedPref = Utils.getSharedPref(getActivity().getApplicationContext());
            CGITAPIs api = RetrofitService.createService(CGITAPIs.class);
            if (Utils.isConnected(getContext())){
                if(Utils.isAdmin(getContext())){
                    api.markAttendance("mark_attendance",s,sharedPref.getId()).enqueue(new Callback<MarkAttendanceResponse>() {
                        @Override
                        public void onResponse(@NonNull Call<MarkAttendanceResponse> call, Response<MarkAttendanceResponse> response) {
                            if(response.isSuccessful()){
                                progressBar.setVisibility(View.GONE);
                                r.play();
                                MarkAttendanceResponse attendanceResponse = response.body();
                                if(attendanceResponse != null){
                                    new AlertDialog.Builder(getContext())
                                            .setTitle("Response")
                                            .setMessage(attendanceResponse.getMessage())
                                            .setPositiveButton("OK",((dialog, which) -> getActivity().finish())).show();
                                }
                            }else {
                                progressBar.setVisibility(View.GONE);
                                new AlertDialog.Builder(getContext())
                                        .setTitle("Response")
                                        .setMessage("Check Your Internet Connection")
                                        .setPositiveButton("OK",((dialog, which) -> getActivity().finish())).show();
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<MarkAttendanceResponse> call, Throwable t) {
                            progressBar.setVisibility(View.GONE);
                            r.play();
                            new AlertDialog.Builder(getContext())
                                    .setTitle("Response")
                                    .setMessage(t.getMessage())
                                    .setPositiveButton("OK",((dialog, which) -> getActivity().finish())).show();
                        }
                    });
                }else {
                    if(s.equals("cgit")){
                        api.geoLocationAttendance(sharedPref.getId(),lat,lng).enqueue(new Callback<GeoLocationAttendance>() {
                            @Override
                            public void onResponse(Call<GeoLocationAttendance> call, Response<GeoLocationAttendance> response) {
                                if(response.isSuccessful()){
                                    progressBar.setVisibility(View.GONE);
                                    r.play();
                                    GeoLocationAttendance attendanceResponse = response.body();
                                    if(attendanceResponse != null){
                                        new AlertDialog.Builder(getContext())
                                                .setTitle("Response")
                                                .setMessage(attendanceResponse.getMessage())
                                                .setPositiveButton("OK",((dialog, which) -> getActivity().finish())).show();
                                    }else {
                                        progressBar.setVisibility(View.GONE);
                                        new AlertDialog.Builder(getContext())
                                                .setTitle("Response")
                                                .setMessage("Check Your Internet Connection")
                                                .setPositiveButton("OK",((dialog, which) -> getActivity().finish())).show();
                                    }
                                }else {
                                    progressBar.setVisibility(View.GONE);
                                    new AlertDialog.Builder(getContext())
                                            .setTitle("Response")
                                            .setMessage("Response failed")
                                            .setPositiveButton("OK",((dialog, which) -> getActivity().finish())).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<GeoLocationAttendance> call, Throwable t) {
                                progressBar.setVisibility(View.GONE);
                                r.play();
                                new AlertDialog.Builder(getContext())
                                        .setTitle("Response")
                                        .setMessage(t.getMessage())
                                        .setPositiveButton("OK",((dialog, which) -> getActivity().finish())).show();
                            }
                        });
                    }else {
                        progressBar.setVisibility(View.GONE);
                        r.play();
                        new AlertDialog.Builder(getContext())
                                .setTitle("Wrong QR Code")
                                .setMessage("Wrong QR Code -> "+s)
                                .setPositiveButton("OK",((dialog, which) -> getActivity().finish())).show();
                    }
                }
            }else {
                Utils.showAlertDialog(getContext(),title,OFF_message);
            }

        } catch (Exception e) {}


    }



    @Override
    public void onFormatsSaved(ArrayList<Integer> selectedIndices) {
        mSelectedIndices = selectedIndices;
        setupFormats();
    }

    @Override
    public void onCameraSelected(int cameraId) {
        mCameraId = cameraId;
        mScannerView.startCamera(mCameraId);
        mScannerView.setFlash(mFlash);
        mScannerView.setAutoFocus(mAutoFocus);
    }

    @Override
    public void onCancel() {
        mScannerView.startCamera();
    }

    public void setupFormats() {
        List<BarcodeFormat> formats = new ArrayList<BarcodeFormat>();
        if(mSelectedIndices == null || mSelectedIndices.isEmpty()) {

            mSelectedIndices = new ArrayList<Integer>();
            for(int i = 0; i < ZXingScannerView.ALL_FORMATS.size(); i++) {
                mSelectedIndices.add(i);
            }
        }

        for(int index : mSelectedIndices) {
            formats.add(ZXingScannerView.ALL_FORMATS.get(index));
        }
        if(mScannerView != null) {
            mScannerView.setFormats(formats);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
        closeFormatsDialog();
    }



//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuItem menuItem;
//
//        if(mFlash) {
//            menuItem = menu.add(Menu.NONE, R.id.menu_flash, 0, R.string.flash_on);
//        } else {
//            menuItem = menu.add(Menu.NONE, R.id.menu_flash, 0, R.string.flash_off);
//        }
//        menuItem.setShowAsAction(SHOW_AS_ACTION_NEVER);
//
//
//        if(mAutoFocus) {
//            menuItem = menu.add(Menu.NONE, R.id.menu_auto_focus, 0, R.string.auto_focus_on);
//        } else {
//            menuItem = menu.add(Menu.NONE, R.id.menu_auto_focus, 0, R.string.auto_focus_off);
//        }
//        menuItem.setShowAsAction(SHOW_AS_ACTION_NEVER);
//
//        menuItem = menu.add(Menu.NONE, R.id.menu_formats, 0, R.string.formats);
//        menuItem.setShowAsAction(SHOW_AS_ACTION_NEVER);
//
//        menuItem = menu.add(Menu.NONE, R.id.menu_camera_selector, 0, R.string.select_camera);
//        menuItem.setShowAsAction(SHOW_AS_ACTION_NEVER);
//
//        return super.onCreateOptionsMenu(menu);
//    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.menu_flash:
                mFlash = !mFlash;
                if(mFlash) {
                    item.setTitle(R.string.flash_on);
                } else {
                    item.setTitle(R.string.flash_off);
                }
                mScannerView.setFlash(mFlash);
                return true;
            case R.id.menu_auto_focus:
                mAutoFocus = !mAutoFocus;
                if(mAutoFocus) {
                    item.setTitle(R.string.auto_focus_on);
                } else {
                    item.setTitle(R.string.auto_focus_off);
                }
                mScannerView.setAutoFocus(mAutoFocus);
                return true;
            case R.id.menu_formats:
                DialogFragment fragment = FormatSelectorDialogFragment.newInstance(this, mSelectedIndices);
                fragment.show(getActivity().getSupportFragmentManager(), "format_selector");
                return true;
            case R.id.menu_camera_selector:
                mScannerView.stopCamera();
                DialogFragment cFragment = CameraSelectorDialogFragment.newInstance(this, mCameraId);
                cFragment.setCancelable(false);
                cFragment.show(getActivity().getSupportFragmentManager(), "camera_selector");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}