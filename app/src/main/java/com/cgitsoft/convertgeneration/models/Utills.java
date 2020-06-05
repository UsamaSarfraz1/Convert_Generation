package com.cgitsoft.convertgeneration.models;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkRequest;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.cgitsoft.convertgeneration.models.login.UserDetail;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class Utills {
    public static void openDialog(FragmentManager manager, DialogFragment fragment){
        FragmentTransaction ft = manager.beginTransaction();
        Fragment prev = manager.findFragmentByTag("dialog");
        if(prev != null)
        {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        fragment.show(ft, "dialog");
    }
    public static void setSharedPref(Context context, SharedPref pref){
        SharedPreferences preferences = context.getSharedPreferences("loginStatus",0);
        Editor editor  = preferences.edit();
        editor.putBoolean("status",pref.isStatus());
        editor.putString("id",pref.getId());
        editor.apply();
    }

    public static SharedPref getSharedPref(Context context){
        SharedPreferences preferences = context.getSharedPreferences("loginStatus",0);
        boolean a = preferences.getBoolean("status",false);
        String id = preferences.getString("id",null);

        return new SharedPref(id,a);
    }

    public static void setProfileData(UserDetail userDetail,Context context){
        SharedPreferences preferences = context.getSharedPreferences("ProfileData",0);
        String profileObj = new Gson().toJson(userDetail);
        Editor editor  = preferences.edit();
        editor.putString("profile",profileObj);
        editor.apply();
    }

    public static UserDetail getProfileData(Context context){
        SharedPreferences preferences = context.getSharedPreferences("ProfileData",0);
        String profileObj = preferences.getString("profile",null);
        return new Gson().fromJson(profileObj, new TypeToken<UserDetail>(){}.getType());
    }

    public static void setDialogSize(Dialog dialog,double width){
        Window window = dialog.getWindow();
        Point size = new Point();

        // Store dimensions of the screen in `size`
        Display display = window.getWindowManager().getDefaultDisplay();
        display.getSize(size);

        // Set the width of the dialog proportional to 90% of the screen width
        window.setLayout((int) (size.x * width), WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
    }

    public static void checkConnection(Context context, View view){
        final ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkRequest.Builder builder=new NetworkRequest.Builder();
        if (connectivityManager!=null){
            connectivityManager.registerNetworkCallback(builder.build(),new ConnectivityManager.NetworkCallback(){
                @Override
                public void onAvailable(@NonNull Network network) {
                    super.onAvailable(network);
                    Snackbar.make(view,"Wifi Enabled", BaseTransientBottomBar.LENGTH_LONG).show();
                }

                @Override
                public void onLost(@NonNull Network network) {
                    super.onLost(network);
                    Snackbar.make(view,"Internet Not Connected", BaseTransientBottomBar.LENGTH_INDEFINITE).show();
                }
            });
        }
    }
}
