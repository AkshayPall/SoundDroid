package com.example.akshaypall.sounddroid.com.example.akshaypall.sounddroid.soundcloud;

import java.util.Date;
import java.util.List;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by Akshay Pall on 16/05/2015.
 */
public interface SoundCloudService {
    static final String CLIENT_ID = "9cfa090eb20607b2d256519bbf0e74ed";

    @GET("/tracks?client_id="+CLIENT_ID)
    public void searchSongs (@Query("q") String query, Callback<List<Track>> cb);

    @GET("/tracks?client_id="+CLIENT_ID)
    public void getRecentSongs (@Query("created_at[from]") String query, Callback<List<Track>> cb);
}
