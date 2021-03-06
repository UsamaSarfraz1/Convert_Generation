package com.cgitsoft.convertgeneration;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.androidstudy.networkmanager.Monitor;
import com.androidstudy.networkmanager.Tovuti;
import com.bumptech.glide.Glide;
import com.cgitsoft.convertgeneration.AttendanceModel.Details;
import com.cgitsoft.convertgeneration.AttendanceModel.Root;
import com.cgitsoft.convertgeneration.activities.AttendanceDetailActivity;
import com.cgitsoft.convertgeneration.activities.LoginActivity;
import com.cgitsoft.convertgeneration.activities.ProfileActivity;
import com.cgitsoft.convertgeneration.activities.ResetPasswordActivity;
import com.cgitsoft.convertgeneration.dialogs.FormatSelectorDialogFragment;
import com.cgitsoft.convertgeneration.dialogs.LogoutDialog;
import com.cgitsoft.convertgeneration.models.AttendanceDetail;
import com.cgitsoft.convertgeneration.models.SharedPref;
import com.cgitsoft.convertgeneration.models.Utills;
import com.cgitsoft.convertgeneration.models.Utils;
import com.cgitsoft.convertgeneration.models.login.LoginResponse;
import com.cgitsoft.convertgeneration.models.login.UserDetail;
import com.cgitsoft.convertgeneration.retrofit.CGITAPIs;
import com.cgitsoft.convertgeneration.retrofit.RetrofitService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.onesignal.OneSignal;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.internal.Util;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Dashboard extends AppCompatActivity {

    DrawerLayout drawer;
    private AppBarConfiguration mAppBarConfiguration;
    public static Menu mymenu;
    public static String title= "Internet Connection";
    public static String ON_message= "Internet Connected!!!";
    public static String OFF_message= "Not Connected!!!";
    View navigationHeaderView;
    CircleImageView profileImage;
    TextView userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationHeaderView =navigationView.getHeaderView(0);
        userName=navigationHeaderView.findViewById(R.id.userNameLoggedin);
        profileImage=navigationHeaderView.findViewById(R.id.profileImage);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_logout,R.id.nav_profile,R.id.nav_view_attendance)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        

        updateDrawerHeaderData();
        }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.dashboard, menu);
        mymenu=menu;
        return true;
    }
    

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.action_veiwAttendance:
                Utils.openActivity(Dashboard.this,AttendanceDetailActivity.class);
                break;
            case R.id.action_Reset:
                Utils.openActivity(Dashboard.this, ResetPasswordActivity.class);
                break;
            case R.id.action_logout:
                DialogFragment dialogFragment=new LogoutDialog();
                dialogFragment.show(getSupportFragmentManager(),"logout");
                break;


        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }
    public void updateDrawerHeaderData(){
        UserDetail userDetail=Utills.getProfileData(this);
        String Name=userDetail.getUser_fullname();
        String imageUri=userDetail.getUser_pic();
        userName.setText(Name);
        Glide.with(this).load(ProfileActivity.IMAGE_URL+imageUri).placeholder(R.drawable.no_image).into(profileImage);
    }
}
