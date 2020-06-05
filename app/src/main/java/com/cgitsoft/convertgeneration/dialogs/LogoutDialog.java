package com.cgitsoft.convertgeneration.dialogs;


import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cgitsoft.convertgeneration.R;
import com.cgitsoft.convertgeneration.activities.LoginActivity;
import com.cgitsoft.convertgeneration.models.SharedPref;
import com.cgitsoft.convertgeneration.models.Utils;

public class LogoutDialog extends DialogFragment {

    private View v;
    private TextView btnLogout,btnCancel;
    public LogoutDialog() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.dialog_logout, container, false);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        init();

        return v;
    }
    private void init() {
        btnCancel = v.findViewById(R.id.btn_cancel);
        btnLogout = v.findViewById(R.id.btn_logout);

        btnCancel.setOnClickListener(cancel -> dismiss());
        btnLogout.setOnClickListener(logout -> {
            SharedPref pref = new SharedPref();
            Utils.setSharedPref(getContext().getApplicationContext(),pref);
            Intent intent = new Intent(getContext(), LoginActivity.class);
            getContext().startActivity(intent);
            getActivity().finishAffinity();
            dismiss();
        });
    }
    @Override
    public void onResume() {
       Utils.setDialogSize(getDialog(),0.80);

        // Call super onResume after sizing
        super.onResume();
    }
}
