package com.cgitsoft.convertgeneration;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.androidstudy.networkmanager.Monitor;
import com.androidstudy.networkmanager.Tovuti;
import com.cgitsoft.convertgeneration.activities.LoginActivity;
import com.cgitsoft.convertgeneration.dialogs.InternetAlertDialog;
import com.cgitsoft.convertgeneration.models.SharedPref;
import com.cgitsoft.convertgeneration.models.Utils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.sql.Time;

import static com.cgitsoft.convertgeneration.Constants.PERMISSION_CAMERA;

public class SplashActivity extends AppCompatActivity {

    public static String title= "Internet Connection";
    public static String ON_message= "Internet Connected!!!";
    public static String OFF_message= "Not Connected!!!";
    ConnectivityManager connectivityManager;
    @RequiresApi(api = Build.VERSION_CODES.M)
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
        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        Network network = connectivityManager.getActiveNetwork();
        if (network == null) {
            Utils.setConnected(this, false);
        }else {
            NetworkCapabilities networkCapabilities=connectivityManager.getNetworkCapabilities(network);
            boolean isConnected = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED);
            Utils.setConnected(this,isConnected);
        }
        NetworkRequest.Builder builder=new NetworkRequest.Builder();
        if (connectivityManager!=null){
            connectivityManager.registerNetworkCallback(builder.build(),new ConnectivityManager.NetworkCallback(){
                @Override
                public void onAvailable(@NonNull Network network) {
                    super.onAvailable(network);
                    Utils.setConnected(SplashActivity.this,true);
                }

                @Override
                public void onLost(@NonNull Network network) {
                    super.onLost(network);
                    Utils.setConnected(SplashActivity.this,false);
                }

                @Override
                public void onUnavailable() {
                    super.onUnavailable();
                    Utils.setConnected(SplashActivity.this,false);
                }
            });
        }

    }
    private void checkPermission() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED){
            openScreen();
        }else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.CAMERA) ||
                ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_PHONE_STATE)) {
                new AlertDialog.Builder(this).
                        setTitle("Permission needed")
                        .setMessage("Camera  permission needed for this app to work. Grant permission")
                        .setNegativeButton("No",((dialog, which) -> {
                            dialog.dismiss();
                            finish();
                        }))
                        .setPositiveButton("Ok",((dialog, which) ->
                                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA,Manifest.permission.READ_PHONE_STATE},PERMISSION_CAMERA)))
                        .show();
            }else {
                if (!Utils.isDenied(this)){
                    ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA,Manifest.permission.READ_PHONE_STATE},PERMISSION_CAMERA);
                }else {
                    new AlertDialog.Builder(this).
                            setTitle("Permission needed")
                            .setMessage("You have to grant permission from Settings")
                            .setNegativeButton("No",((dialog, which) -> {
                                dialog.dismiss();
                                finish();
                            }))
                            .setPositiveButton("Ok",((dialog, which) -> {
                                startActivity(new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:com.cgitsoft.convertgeneration")));
                                finish();
                            }))
                            .show();
                }

            }
        }
    }
    @SuppressLint("MissingPermission")
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "All permission Granted!!!", Toast.LENGTH_SHORT).show();
                Utils.setDenied(this,false);
                openScreen();
            }else {
                Utils.setDenied(this,true);
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

    public boolean checkInternet(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            Network network = cm.getActiveNetwork();
            if (network == null) {
                Utils.setConnected(this, false);
                return false;
            }else {
                NetworkCapabilities networkCapabilities=cm.getNetworkCapabilities(network);
                boolean isConnected = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED);
                Utils.setConnected(this,isConnected);
                return isConnected;
            }
        }
        return true;
    }

}
