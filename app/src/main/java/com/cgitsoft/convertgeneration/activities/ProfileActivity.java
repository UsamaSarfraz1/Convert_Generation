package com.cgitsoft.convertgeneration.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.androidstudy.networkmanager.Monitor;
import com.androidstudy.networkmanager.Tovuti;
import com.bumptech.glide.Glide;
import com.cgitsoft.convertgeneration.R;
import com.cgitsoft.convertgeneration.models.UpdateProfile.Root;
import com.cgitsoft.convertgeneration.models.Utills;
import com.cgitsoft.convertgeneration.models.Utils;
import com.cgitsoft.convertgeneration.models.login.UserDetail;
import com.cgitsoft.convertgeneration.retrofit.CGITAPIs;
import com.cgitsoft.convertgeneration.retrofit.RetrofitService;
import com.wang.avi.AVLoadingIndicatorView;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ProfileActivity extends AppCompatActivity {



    public static String title= "Internet Connection";
    public static String ON_message= "Internet Connected!!!";
    public static String OFF_message= "Not Connected!!!";
    public static String IMAGE_URL="http://cgitsoft.com/emp/img/uploads/";
    EditText userName;
    EditText name;
    EditText email;
    EditText cnic;
    EditText address;
    EditText phoneNumber;
    EditText btnBack;
    Button updateBtn;
    CircleImageView profilePic;
    private AVLoadingIndicatorView progressBar;
    String imageUri;
    TextView editProfile;
    boolean isConnect=true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        init();
        setData();


    }

    public void init(){
        progressBar = findViewById(R.id.progressBar);
        userName = findViewById(R.id.userName);
        name=findViewById(R.id.emp_name);
        email=findViewById(R.id.emp_email);
        cnic=findViewById(R.id.emp_cnic);
        address=findViewById(R.id.emp_address);
        phoneNumber=findViewById(R.id.emp_phone);
        editProfile=findViewById(R.id.editProfile);
        TextView saveProfile=findViewById(R.id.saveProfile);
        ImageButton btnBack=findViewById(R.id.imgbtn_back);
        updateBtn=findViewById(R.id.updateBtn);
        profilePic=findViewById(R.id.profileImage);


        userName.setEnabled(false);
        name.setEnabled(false);
        email.setEnabled(false);
        cnic.setEnabled(false);
        address.setEnabled(false);
        phoneNumber.setEnabled(false);

        btnBack.setOnClickListener(v -> finish());
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utils.isConnected(ProfileActivity.this)) {
                    editProfile.setVisibility(View.GONE);
                    saveProfile.setVisibility(View.VISIBLE);
                    userName.setEnabled(true);
                    name.setEnabled(true);
                    email.setEnabled(true);
                    cnic.setEnabled(true);
                    address.setEnabled(true);
                    phoneNumber.setEnabled(true);
                    updateBtn.setEnabled(true);
                }else {
                    Utils.showAlertDialog(ProfileActivity.this,title,OFF_message);
                }
            }
        });

        saveProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editProfile.setVisibility(View.VISIBLE);
                saveProfile.setVisibility(View.GONE);
                updateProfile();
            }
        });

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateProfile();
            }
        });

    }


    public void setData(){
        UserDetail userDetail= Utills.getProfileData(this);
        userName.setText(userDetail.getUsername());
        name.setText(userDetail.getUser_fullname());
        email.setText(userDetail.getUser_email());
        cnic.setText(userDetail.getUser_cnic());
        address.setText(userDetail.getUser_address());
        phoneNumber.setText(userDetail.getUser_phone());
        imageUri=userDetail.getUser_pic();
        Glide.with(this).load(IMAGE_URL+imageUri).placeholder(R.drawable.no_image).into(profilePic);
    }


    public void updateProfile(){
        progressBar.setVisibility(View.VISIBLE);
        String emp_id= Utils.getSharedPref(this).getId();
        String namestr=name.getText().toString();
        String emailstr=email.getText().toString();
        String phonestr=phoneNumber.getText().toString();
        String addressstr=address.getText().toString();
        String cnicstr=cnic.getText().toString();
        String usernamestr=userName.getText().toString();

        UserDetail userDetail=new UserDetail(emp_id,usernamestr,emailstr,phonestr,namestr,addressstr,cnicstr,imageUri);

        CGITAPIs api = RetrofitService.createService(CGITAPIs.class);
        api.UpdateProfile(emp_id,namestr,emailstr,phonestr,addressstr,cnicstr,usernamestr).enqueue(new Callback<Root>() {
            @Override
            public void onResponse(Call<Root> call, Response<Root> response) {
                if(response.isSuccessful()){
                    com.cgitsoft.convertgeneration.models.UpdateProfile.Root root = response.body();
                    if(root != null && root.getCode().equals("200")){
                        progressBar.setVisibility(View.GONE);
                        Utills.setProfileData(userDetail,ProfileActivity.this);
                        Disable();
                        Toast.makeText(ProfileActivity.this, "Profile Updated!!!", Toast.LENGTH_SHORT).show();
                    }else {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(ProfileActivity.this, "something went wrong", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Root> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                new AlertDialog.Builder(ProfileActivity.this)
                        .setTitle("Response")
                        .setMessage(t.getMessage())
                        .setPositiveButton("OK",((dialog, which) -> finish())).show();
            }
        });
    }




    public void Disable(){
        userName.setEnabled(false);
        name.setEnabled(false);
        email.setEnabled(false);
        cnic.setEnabled(false);
        address.setEnabled(false);
        phoneNumber.setEnabled(false);
        updateBtn.setEnabled(false);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Tovuti.from(this).stop();
    }
}
