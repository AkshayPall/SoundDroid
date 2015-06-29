package com.example.akshaypall.sounddroid.com.example.akshaypall.sounddroid.soundcloud;

import retrofit.RestAdapter;

/**
 * Created by Akshay Pall on 16/05/2015.
 */
public class Soundcloud {
    private static final String API_URL = "http://api.soundcloud.com";

    private static final RestAdapter REST_ADAPTER = new RestAdapter.Builder()
            .setEndpoint("http://api.soundcloud.com")
            .build();

    private static final SoundCloudService SERVICE = REST_ADAPTER.create(SoundCloudService.class);

    public static SoundCloudService getService(){
        return SERVICE;
    }
}
