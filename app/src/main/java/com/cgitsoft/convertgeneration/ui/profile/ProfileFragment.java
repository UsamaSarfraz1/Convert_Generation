package com.cgitsoft.convertgeneration.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

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

import static com.cgitsoft.convertgeneration.activities.ProfileActivity.IMAGE_URL;

public class ProfileFragment extends Fragment {

    EditText userName;
    EditText name;
    EditText email;
    EditText cnic;
    EditText address;
    EditText phoneNumber;
    Button updateBtn;
    CircleImageView profilePic;
    private AVLoadingIndicatorView progressBar;
    String imageUri;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v;
        v=inflater.inflate(R.layout.fragment_profile,container,false);
        progressBar = v.findViewById(R.id.progressBar);
        userName = v.findViewById(R.id.userName);
        name= v.findViewById(R.id.emp_name);
        email= v.findViewById(R.id.emp_email);
        cnic= v.findViewById(R.id.emp_cnic);
        address= v.findViewById(R.id.emp_address);
        phoneNumber= v.findViewById(R.id.emp_phone);
        updateBtn= v.findViewById(R.id.updateBtn);
        profilePic= v.findViewById(R.id.profileImage);


        return v;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        updateEnablity(false);

        setHasOptionsMenu(true);
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateProfile();
            }
        });
        setData();
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem item=menu.findItem(R.id.edit_Profile);
        if(item!=null)
            item.setVisible(true);
    }


    public void setData(){
        UserDetail userDetail= Utills.getProfileData(getContext());
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
        String emp_id= Utils.getSharedPref(getContext()).getId();
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
                        Utills.setProfileData(userDetail, getContext());
                        updateEnablity(false);
                        Toast.makeText(getContext(), "Profile Updated!!!", Toast.LENGTH_SHORT).show();
                    }else {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "something went wrong", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Root> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                new AlertDialog.Builder(getContext())
                        .setTitle("Response")
                        .setMessage(t.getMessage())
                        .setPositiveButton("OK",((dialog, which) -> getActivity().finish())).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.edit_Profile:
                updateEnablity(true);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void updateEnablity(boolean update){
        userName.setEnabled(update);
        name.setEnabled(update);
        email.setEnabled(update);
        cnic.setEnabled(update);
        address.setEnabled(update);
        phoneNumber.setEnabled(update);
        updateBtn.setEnabled(update);

    }


}
