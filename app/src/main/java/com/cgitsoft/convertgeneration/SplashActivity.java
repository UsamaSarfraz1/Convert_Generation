package com.cgitsoft.convertgeneration;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.androidstudy.networkmanager.Monitor;
import com.androidstudy.networkmanager.Tovuti;
import com.cgitsoft.convertgeneration.activities.LoginActivity;
import com.cgitsoft.convertgeneration.models.SharedPref;
import com.cgitsoft.convertgeneration.models.Utils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import static com.cgitsoft.convertgeneration.Constants.PERMISSION_CAMERA;

public class SplashActivity extends AppCompatActivity {

    public static String title= "Internet Connection";
    public static String ON_message= "Internet Connected!!!";
    public static String OFF_message= "Not Connected!!!";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        transparentToolbar();

        Handler handler = new Handler();
        handler.postDelayed(this::checkPermission,3000);

        FloatingActionButton btnLogin = findViewById(R.id.fab_go);
        btnLogin.setVisibility(View.GONE);
        btnLogin.setOnClickListener(login -> checkPermission());

        //check internet connectivity
        Tovuti.from(this).monitor(new Monitor.ConnectivityListener(){
            @Override
            public void onConnectivityChanged(int connectionType, boolean isConnected, boolean isFast){
                if (!isConnected){
                    Utils.showAlertDialog(SplashActivity.this,title,OFF_message);
                }
            }
        });
    }
    private void checkPermission() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
            openScreen();
        }else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                new AlertDialog.Builder(this).
                        setTitle("Permission needed")
                        .setMessage("Camera permission needed for this app to work. Grant permission")
                        .setNegativeButton("No",((dialog, which) -> {
                            dialog.dismiss();
                            finish();
                        }))
                        .setPositiveButton("Ok",((dialog, which) ->
                                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA},PERMISSION_CAMERA)))
                        .show();
            }else {
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA},PERMISSION_CAMERA);
            }
        }
    }
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openScreen();
            }
        }
    }
    private void openScreen() {
        SharedPref status = Utils.getSharedPref(getApplicationContext());
        if(status.isStatus()){
            Utils.openActivity(this,Dashboard.class);
            finish();
            //openScanActivity(Dashboard.class);
        }else {
            Utils.openActivity(this,LoginActivity.class);
            finish();
            //openScanActivity(LoginActivity.class);
        }
    }
    private void openScanActivity(Class tClass) {
        Intent intent = new Intent(this, tClass);
        startActivity(intent);
        finish();
    }
    private void transparentToolbar() {
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(this);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }

    private void setWindowFlag(Activity activity) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        winParams.flags &= ~WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        win.setAttributes(winParams);
    }
}
