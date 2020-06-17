package com.cgitsoft.convertgeneration.interfaces;

import com.cgitsoft.convertgeneration.Notification.MyResponse;
import com.cgitsoft.convertgeneration.Notification.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAA0OtDu2I:APA91bHUlA1zDFtjZMxEglBKgF01OFF03se_BwT6aWPGj1L5ycRKrCWKQJRhW74IRcrLKnU4lJBZ51qBLCOvMOBYUPVg_c6BOsLmS4ghCtbURLL7COzgNQ-IpjYu3jasj6jq0OFFno1X"
            }
    )
    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
