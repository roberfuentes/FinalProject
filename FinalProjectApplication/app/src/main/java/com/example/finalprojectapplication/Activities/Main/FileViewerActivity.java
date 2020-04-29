package com.example.finalprojectapplication.Activities.Main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.finalprojectapplication.R;

import com.github.barteksc.pdfviewer.PDFView;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.squareup.picasso.Picasso;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class FileViewerActivity extends AppCompatActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener
{

    PDFView mPdfView;
    ImageView mImageView, mToggleAction;


    MediaPlayer mp;
    SeekBar mProgressAudio;
    TextView mTimeAudio;

    PlayerView mPlayerView;
    SimpleExoPlayer simpleExoPlayer;


    Toolbar toolbar;

    RelativeLayout mainLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fileviewer);

        mPdfView = findViewById(R.id.pdfView);

        mImageView = findViewById(R.id.imageView);

        mToggleAction = findViewById(R.id.toggleAction);
        mProgressAudio = findViewById(R.id.progressAudio);
        mTimeAudio = findViewById(R.id.timeAudio);

        mPlayerView = findViewById(R.id.exoPlayer);

        /*toolbar = findViewById(R.id.fileviewer_toolbar);
        setSupportActionBar(toolbar);*/

        mainLayout = findViewById(R.id.mainLayout);


        mToggleAction.setOnClickListener(this);
        mProgressAudio.setOnSeekBarChangeListener(this);


        if (getIntent() != null)
        {
            String type = getIntent().getStringExtra("type");
            String url = getIntent().getStringExtra("url");
            if (type != null && url != null)
            {
                typeAction(type, url);
            }
        } else
        {
            Toast.makeText(FileViewerActivity.this, "Couldn't retrieve the file", Toast.LENGTH_SHORT).show();
        }


    }

    private void typeAction(String type, String url)
    {
        switch (type)
        {
            case "image":
                readImage(url);
                break;
            case "pdf":
                readPdf(url);
                break;
            case "audio":
                readAudio(url);
                break;
            case "video":
                readVideo(url);
                break;
        }
    }

    private void readImage(String url)
    {
        //toolbar.setVisibility(View.VISIBLE);
        this.setTitle("Image");
        mImageView.setVisibility(View.VISIBLE);
        mainLayout.setBackgroundColor(Color.BLACK);
        Toast.makeText(FileViewerActivity.this, "Image", Toast.LENGTH_SHORT).show();
        //Picasso.with(FileViewerActivity.this).load(url).resize(1920, 1280).centerInside().into(mImageView);
        Picasso.with(FileViewerActivity.this).load(url).fit().centerInside().into(mImageView);


    }

    private void readPdf(String url)
    {

        this.setTitle("PDF");
        mPdfView.setVisibility(View.VISIBLE);
        new RetrievePDFStream().execute(url);


    }

    private void readAudio(String url)
    {
        //toolbar.setVisibility(View.VISIBLE);
        this.setTitle("Audio");

        Toast.makeText(FileViewerActivity.this, "Audio", Toast.LENGTH_SHORT).show();
        mp = new MediaPlayer();
        try
        {
            mp.setDataSource(url);
            mp.prepare();

            mImageView.setImageResource(R.drawable.ic_audio_outlined);
            mImageView.getLayoutParams().width = 1280;
            mImageView.getLayoutParams().height = 700;
            mToggleAction.setImageResource(R.drawable.ic_play_filled);


            mImageView.setVisibility(View.VISIBLE);
            mTimeAudio.setVisibility(View.VISIBLE);
            mProgressAudio.setVisibility(View.VISIBLE);
            mToggleAction.setVisibility(View.VISIBLE);


            int durationFile = mp.getDuration();
            mTimeAudio.setText("00:00");
            mProgressAudio.setMax(durationFile / 1000);


        } catch (Exception e)
        {
            e.printStackTrace();
        }


    }

    private void readVideo(String url)
    {
        getSupportActionBar().hide();
        Toast.makeText(FileViewerActivity.this, "Video", Toast.LENGTH_SHORT).show();
        mPlayerView.setVisibility(View.VISIBLE);
        simpleExoPlayer = new SimpleExoPlayer.Builder(FileViewerActivity.this).build();
        mPlayerView.setPlayer(simpleExoPlayer);

        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(FileViewerActivity.this, Util.getUserAgent(FileViewerActivity.this, "FinalProjectApplication"));

        MediaSource videoSource = new ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(Uri.parse(url));

        simpleExoPlayer.prepare(videoSource);

        simpleExoPlayer.setPlayWhenReady(true);
    }

    @Override
    public void onClick(View v)
    {

        switch (v.getId())
        {
            case R.id.toggleAction:
                playAction();
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
    {
        if (mp != null && fromUser)
        {
            mp.seekTo(progress * 1000);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar)
    {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar)
    {

    }

    private void playAction()
    {

        if (mp.isPlaying())
        {
            mp.pause();
            mToggleAction.setImageResource(R.drawable.ic_play_filled);
        } else
        {
            mp.start();
            mToggleAction.setImageResource(R.drawable.ic_pause_filled);

            final Handler mHandler = new Handler();
            this.runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {

                    mProgressAudio.setMax(mp.getDuration() / 1000);
                    int mCurrentPosition = mp.getCurrentPosition() / 1000;
                    int min = mCurrentPosition / 60;
                    int sec = mCurrentPosition - (min * 60);


                    String strMin = "";
                    String strSec = "";
                    mProgressAudio.setProgress(mCurrentPosition);
                    if (min < 10)
                    {
                        strMin = "0" + min;
                    } else
                    {
                        strMin = "" + min;
                    }
                    if (sec < 10)
                    {
                        strSec = "0" + sec;
                    } else
                    {
                        strSec = "" + sec;
                    }
                    mTimeAudio.setText(strMin + ":" + strSec);


                    mHandler.postDelayed(this, 1000);

                }
            });
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.viewer_toolbar, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.viewer_info_action:
                goToInfoActivity();
                break;
        }
        return false;
    }

    public void goToInfoActivity()
    {

        String fileRef = getIntent().getStringExtra("fileRef");
        if (fileRef != null)
        {
            Intent intent = new Intent(FileViewerActivity.this, InfoActivity.class);
            intent.putExtra("fileRef", fileRef);
            startActivity(intent);

        }

    }

    class RetrievePDFStream extends AsyncTask<String, Void, InputStream>
    {

        @Override
        protected InputStream doInBackground(String... strings)
        {
            InputStream inputStream = null;
            try
            {
                URL url = new URL(strings[0]);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                if (httpURLConnection.getResponseCode() == 200)
                {
                    inputStream = new BufferedInputStream(httpURLConnection.getInputStream());
                }
            } catch (Exception e)
            {
                e.printStackTrace();
            }

            return inputStream;
        }

        @Override
        protected void onPostExecute(InputStream inputStream)
        {
            mPdfView.fromStream(inputStream).load();
        }
    }
}
