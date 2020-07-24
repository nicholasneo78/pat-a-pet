package com.example.chatapp.Fragments;

import com.example.chatapp.Notification.MyResponse;
import com.example.chatapp.Notification.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * API key for Notifications
 * @author Aleem
 * @author Viet
 * @author Riki
 * @author Aavan
 * @author Nicholas
 * @version 1.0
 * @since 1.0
 */
public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAA2I7zqQk:APA91bGhsIWeVIteA-0ln-uVHNbWf0oTHw8hrkuBqYiAFroKw8e_TKL2lRKs4xPMl80Zplttt46bCqgc_OgWLPSbFF8qHJKfqNh4jfkxdEaF7J4I6qcUI-ht5L1rHH52u2az1Ht7f5dW"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
