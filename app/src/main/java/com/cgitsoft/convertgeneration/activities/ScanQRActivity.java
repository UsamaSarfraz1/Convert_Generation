package com.cgitsoft.convertgeneration.activities;



import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.widget.ImageButton;
import android.widget.TextView;

import com.cgitsoft.convertgeneration.R;
import com.cgitsoft.convertgeneration.models.GeoLocationAttendance;
import com.cgitsoft.convertgeneration.models.MarkAttendanceResponse;
import com.cgitsoft.convertgeneration.models.SharedPref;
import com.cgitsoft.convertgeneration.models.Utils;
import com.cgitsoft.convertgeneration.retrofit.CGITAPIs;
import com.cgitsoft.convertgeneration.retrofit.RetrofitService;
import com.google.android.gms.vision.barcode.Barcode;

import java.util.List;

import info.androidhive.barcode.BarcodeReader;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScanQRActivity extends AppCompatActivity implements BarcodeReader.BarcodeReaderListener {

    private final static String TAG = ScanQRActivity.class.getSimpleName();
    private BarcodeReader barcodeReader;
    private TextView title;
    private String lat,lng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_qr);

        title = findViewById(R.id.title);

        if(!Utils.isAdmin(this)){
            lat = getIntent().getStringExtra("lat");
            lng = getIntent().getStringExtra("lng");
        }

        // get the barcode reader instance
        barcodeReader = (BarcodeReader) getSupportFragmentManager().findFragmentById(R.id.barcode_scanner);
        assert barcodeReader != null;
        barcodeReader.setBeepSoundFile("shutter.mp3");

        ImageButton btnBack = findViewById(R.id.imgbtn_back);
        btnBack.setOnClickListener(back -> finish());
    }

    @Override
    public void onScanned(Barcode barcode) {
        barcodeReader.playBeep();
        barcodeReader.pauseScanning();
        Log.d(TAG, barcode.displayValue);
        String s = barcode.rawValue;
        SharedPref sharedPref = Utils.getSharedPref(getApplicationContext());
        CGITAPIs api = RetrofitService.createService(CGITAPIs.class);
        if(Utils.isAdmin(ScanQRActivity.this)){
            api.markAttendance("mark_attendance",s,sharedPref.getId()).enqueue(new apiResponse());
        }else {
            if(s.equals("cgit")){
                api.geoLocationAttendance(sharedPref.getId(),lat,lng).enqueue(new geoApiResponse());
            }else {
                new AlertDialog.Builder(ScanQRActivity.this)
                        .setTitle("Wrong QR Code")
                        .setMessage("Wrong QR Code -> "+s)
                        .setPositiveButton("OK",((dialog, which) -> finish())).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onScannedMultiple(List<Barcode> barcodes) {
        //Toast.makeText(this, barcodes.toString(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBitmapScanned(SparseArray<Barcode> sparseArray) {
        //Toast.makeText(this, sparseArray.toString(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onScanError(String errorMessage) {
        //Toast.makeText(this, "Error scanning code", Toast.LENGTH_SHORT).show();
        Log.e(TAG,"error -> "+errorMessage);
    }

    @Override
    public void onCameraPermissionDenied() {
        //Toast.makeText(this, "Error:Camera permission not granted", Toast.LENGTH_SHORT).show();
        Log.e(TAG,"Camera permission not granted");
    }
    private class apiResponse implements Callback<MarkAttendanceResponse> {

        @Override
        public void onResponse(@NonNull Call<MarkAttendanceResponse> call, Response<MarkAttendanceResponse> response) {
            if(response.isSuccessful()){
                MarkAttendanceResponse attendanceResponse = response.body();
                if(attendanceResponse != null){
                    new AlertDialog.Builder(ScanQRActivity.this)
                            .setTitle("Response")
                            .setMessage(attendanceResponse.getMessage())
                            .setPositiveButton("OK",((dialog, which) -> finish())).show();
                }
            }
        }

        @Override
        public void onFailure(@NonNull Call<MarkAttendanceResponse> call, Throwable t) {
            new AlertDialog.Builder(ScanQRActivity.this)
                    .setTitle("Response")
                    .setMessage(t.getMessage())
                    .setPositiveButton("OK",((dialog, which) -> finish())).show();
        }
    }
    private class geoApiResponse implements Callback<GeoLocationAttendance>{

        @Override
        public void onResponse(Call<GeoLocationAttendance> call, Response<GeoLocationAttendance> response) {
            if(response.isSuccessful()){
                GeoLocationAttendance attendanceResponse = response.body();
                if(attendanceResponse != null){
                    new AlertDialog.Builder(ScanQRActivity.this)
                            .setTitle("Response")
                            .setMessage(attendanceResponse.getMessage())
                            .setPositiveButton("OK",((dialog, which) -> finish())).show();
                }else {
                    new AlertDialog.Builder(ScanQRActivity.this)
                            .setTitle("Response")
                            .setMessage("Empty Response")
                            .setPositiveButton("OK",((dialog, which) -> finish())).show();
                }
            }else {
                new AlertDialog.Builder(ScanQRActivity.this)
                        .setTitle("Response")
                        .setMessage("Response failed")
                        .setPositiveButton("OK",((dialog, which) -> finish())).show();
            }
        }

        @Override
        public void onFailure(Call<GeoLocationAttendance> call, Throwable t) {
            new AlertDialog.Builder(ScanQRActivity.this)
                    .setTitle("Response")
                    .setMessage(t.getMessage())
                    .setPositiveButton("OK",((dialog, which) -> finish())).show();
        }
    }
}
