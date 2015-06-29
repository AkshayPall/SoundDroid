package com.example.akshaypall.sounddroid.com.example.akshaypall.sounddroid.soundcloud;

import android.util.Log;

import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Akshay Pall on 15/05/2015.
 */
public class Track {
    private static final String TAG = "TAG";

    @SerializedName("title")
    private String mTitle;

    @SerializedName("stream_url")
    private String mTrackURL;

    @SerializedName("id")
    private int mID;

    @SerializedName("artwork_url")
    private String artworkURL;

    @SerializedName("permalink_url")
    private String mLink;

    public String getmLink() {
        return mLink;
    }

    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String string) {
        mTitle = string;
    }

    public String getmTrackURL() {
        return mTrackURL;
    }

    public int getmID() {
        return mID;
    }

    public String getArtworkURL() { //DON'T USE! the getAvatarURL method returns the correct size (this gives large)
        return artworkURL;
    }

    public String getAvatarURL() {
        String avatarURL = artworkURL;
        if (avatarURL != null) {
            avatarURL = avatarURL.replace("large", "tiny");
        }
        return avatarURL;
    }

    public static Track parse(JSONObject jsonObject) {
        Track t = new Track();
        try {
            t.setmTitle(jsonObject.getString("title"));
        } catch (JSONException e){
            Log.d(TAG, "JSONException is "+e);
        }
        return t;
    }
}
