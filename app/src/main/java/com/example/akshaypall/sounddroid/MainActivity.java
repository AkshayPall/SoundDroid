package com.example.akshaypall.sounddroid;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.akshaypall.sounddroid.com.example.akshaypall.sounddroid.soundcloud.SoundCloudService;
import com.example.akshaypall.sounddroid.com.example.akshaypall.sounddroid.soundcloud.Soundcloud;
import com.example.akshaypall.sounddroid.com.example.akshaypall.sounddroid.soundcloud.Track;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.text.SimpleDateFormat;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class MainActivity extends ActionBarActivity implements SearchView.OnQueryTextListener {

    private static final String TAG = "TAG";
    private TracksAdapter mAdapter;
    private List<Track> mTracks;
    private TextView mCurrentTrackTitle;
    private ImageView mCurrentTrackArtwork;
    private MediaPlayer mMediaPlayer;
    private ImageView mPlayerState;
    private List<Track> mPreviousList;
    TextView mSoundcloudLink;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                togglePlayPauseState();
            }
        });
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mPlayerState.setImageResource(R.drawable.ic_play);
            }
        });

        SoundCloudService service = Soundcloud.getService();
        service.getRecentSongs(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()), new Callback<List<Track>>() {
            @Override
            public void success(List<Track> tracks, Response response) {
                getTracks(tracks);
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });

        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.songs_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mTracks = new ArrayList<Track>();
        mAdapter = new TracksAdapter(mTracks, this);
        mAdapter.setmOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Track selectedTrack = mTracks.get(position);
                mCurrentTrackTitle.setText(selectedTrack.getmTitle());
                Picasso.with(MainActivity.this).load(selectedTrack.getArtworkURL()).into(mCurrentTrackArtwork);

                if (mMediaPlayer.isPlaying()) {
                    mMediaPlayer.stop();
                    mMediaPlayer.reset();
                }

                try {
                    mMediaPlayer.setDataSource(selectedTrack.getmTrackURL() + "?client_id=" + SoundCloudService.CLIENT_ID);
                    mMediaPlayer.prepareAsync();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                mSoundcloudLink.setText(selectedTrack.getmLink());

            }
        });
        recyclerView.setAdapter(mAdapter);

        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.player_toolbar);
        mCurrentTrackTitle = (TextView)findViewById(R.id.current_track_title);
        mCurrentTrackArtwork = (ImageView)findViewById(R.id.current_track_artwork);
        mPlayerState = (ImageView)toolbar.findViewById(R.id.player_state);
        mPlayerState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePlayPauseState();
            }
        });


        Button emailButton = (Button)findViewById(R.id.email_button);
        emailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = getApplicationContext();
                if (!mCurrentTrackTitle.getText().equals("")) {
                    Intent sendEmail = new Intent(Intent.ACTION_SEND);
                    sendEmail.setData(Uri.parse("mailto:"));
                    sendEmail.setType("text/plain");
                    sendEmail.putExtra(Intent.EXTRA_SUBJECT, "I want to share a track!");
                    sendEmail.putExtra(Intent.EXTRA_TEXT, "I really like this song I found on Soundcloud via my Soundroid App: "+mCurrentTrackTitle.getText()+"\nClick on the link below to listen!\n"+mSoundcloudLink.getText());
                    try {
                        releaseMediaPlayer();
                        startActivity(Intent.createChooser(sendEmail, "Send mail..."));
                        finish();
                        Toast emailSent = Toast.makeText(context, "Email sent", Toast.LENGTH_SHORT);
                    } catch (Exception e) {
                        Toast noEmailClient = Toast.makeText(context, "No email client installed", Toast.LENGTH_LONG);
                        noEmailClient.show();
                    }
                } else {
                    Toast noTrackPlayed = Toast.makeText(context, "Play a song!", Toast.LENGTH_SHORT);
                    noTrackPlayed.show();
                }
            }
        });

    }

    private void getTracks (List<Track> tracks) {
        mTracks.clear();
        mTracks.addAll(tracks);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseMediaPlayer();
    }

    private void releaseMediaPlayer() {
        if (mMediaPlayer != null) {
            if (mMediaPlayer.isPlaying()) mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    private void togglePlayPauseState() {
        if (mMediaPlayer.isPlaying()){
            mMediaPlayer.pause();
            mPlayerState.setImageResource(R.drawable.ic_play);
        } else {
            mMediaPlayer.start();
            mPlayerState.setImageResource(R.drawable.ic_pause);
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        Log.d(TAG, query);
        Soundcloud.getService().searchSongs(query, new Callback<List<Track>>() {
            @Override
            public void success(List<Track> tracks, Response response) {
                getTracks(tracks);
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        SearchView searchView = (SearchView)menu.findItem(R.id.action_search).getActionView();
        searchView.setOnQueryTextListener(this);
        MenuItemCompat.setOnActionExpandListener(menu.findItem(R.id.action_search), new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                mPreviousList = new ArrayList<Track>(mTracks);
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                getTracks(mPreviousList);
                return true;
            }
        });

        mSoundcloudLink = (TextView)menu.findItem(R.id.soundcloud_url).getActionView();
        mSoundcloudLink.setTextIsSelectable(true);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            return true;
        }

        if (id == R.id.soundcloud_url) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
