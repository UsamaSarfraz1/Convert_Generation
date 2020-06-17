package com.cgitsoft.convertgeneration.models;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Point;
import android.view.Display;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.cgitsoft.convertgeneration.activities.QrActivity;

import java.util.Objects;

public class Utils {
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
    public static void openActivity(Context context,Class tClass){
        Intent intent = new Intent(context, tClass);
        context.startActivity(intent);
    }
    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        if(inputMethodManager != null){
            inputMethodManager.hideSoftInputFromWindow(
                    activity.getCurrentFocus().getWindowToken(), 0);
        }

    }
    public static void setIsAdmin(Context context,boolean isAdmin){
        SharedPreferences preferences = context.getApplicationContext().getSharedPreferences("isAdmin",0);
        Editor editor = preferences.edit();
        editor.putBoolean("role",isAdmin);
        editor.apply();
    }
    public static boolean isAdmin(Context context){
        SharedPreferences preferences = context.getApplicationContext().getSharedPreferences("isAdmin",0);
        return preferences.getBoolean("role",false);
    }

    public static void showAlertDialog(Context context,String title,String Message){
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(Message)
                .setPositiveButton("OK", (dialogInterface, i) ->  {
                        dialogInterface.dismiss();
                }).show();

    }

    public static void setConnected(Context context,boolean isConnected){
        SharedPreferences preferences = context.getApplicationContext().getSharedPreferences("isConnected",0);
        Editor editor = preferences.edit();
        editor.putBoolean("connection",isConnected);
        editor.apply();
    }

    public static boolean isConnected(Context context){
        SharedPreferences preferences = context.getApplicationContext().getSharedPreferences("isConnected",0);
        return preferences.getBoolean("connection",true);
    }

    public static void setDenied(Context context,boolean isDenied){
        SharedPreferences preferences = context.getApplicationContext().getSharedPreferences("isDenied",0);
        Editor editor = preferences.edit();
        editor.putBoolean("Deny",isDenied);
        editor.apply();
    }

    public static boolean isDenied(Context context){
        SharedPreferences preferences = context.getApplicationContext().getSharedPreferences("isDenied",0);
        boolean check = preferences.getBoolean("Deny",false);
        return check;
    }

}
