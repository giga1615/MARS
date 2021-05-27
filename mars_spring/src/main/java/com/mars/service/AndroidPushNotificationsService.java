package com.mars.service;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

import com.mars.pushhandle.HeaderRequestInterceptor;
import org.springframework.http.HttpEntity;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AndroidPushNotificationsService {

    private static final String firebase_server_key="AAAAZy-y5Uc:APA91bHJDIYgFaOyuKJxBwfg2EZ7vY4wEYRipCXl22TxRkSjTYgF7sM3Lp0l6-rRxobonWL0v8_9OhH26Ow0yubexWzshY5GXfFPNiQz0Q8xce-Q2FTtFiyKtx1lhUjxRYegiGdzuDaG";
    private static final String firebase_api_url="https://fcm.googleapis.com/fcm/send";

    @Async
    public CompletableFuture<String> send(HttpEntity<String> entity) {

        RestTemplate restTemplate = new RestTemplate();

        ArrayList<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();

        interceptors.add(new HeaderRequestInterceptor("Authorization",  "key=" + firebase_server_key));
        interceptors.add(new HeaderRequestInterceptor("Content-Type", "application/json"));
        restTemplate.setInterceptors(interceptors);

        String firebaseResponse = restTemplate.postForObject(firebase_api_url, entity, String.class);

        return CompletableFuture.completedFuture(firebaseResponse);
    }


}