package com.cgitsoft.convertgeneration.dialogs;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

public class InternetAlertDialog extends Activity {

    public static String title= "Internet Connection";
    public static String ON_message= "Internet Connected!!!";
    public static String OFF_message= "Not Connected!!!";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showAlertDialog();
    }

    public  void showAlertDialog(){
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(OFF_message)
                .setPositiveButton("OK",((dialog, which) -> finish())).show().setCancelable(false);
    }
}
