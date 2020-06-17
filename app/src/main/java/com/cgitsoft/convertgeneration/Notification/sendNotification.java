package com.cgitsoft.convertgeneration.Notification;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.cgitsoft.convertgeneration.interfaces.APIService;
import com.cgitsoft.convertgeneration.models.Utils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class sendNotification {

    public static void Notify(String message, String title, Context context, APIService apiService,String id){
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        tokens.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()){
                      String token=  dataSnapshot.child("token").getValue(String.class);
                        Data data = new Data(message,title,id);
                        Sender sender1 =new Sender(data,token);
                        apiService.sendNotification(sender1)
                                .enqueue(new Callback<MyResponse>() {
                                    @Override
                                    public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                        if (response.code() != 200){
                                            Utils.showAlertDialog(context,"Notificationn Error","something went wrong");
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<MyResponse> call, Throwable t) {
                                        Toast.makeText(context, "Notification failed", Toast.LENGTH_SHORT).show();
                                    }
                                });
                }else {
                    Utils.showAlertDialog(context,"Database Error","Token do not exist");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(context, "something went wrong!!!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
