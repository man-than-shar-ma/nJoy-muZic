package com.manthansharma.njoymuzic;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class PlayerActivity extends AppCompatActivity {

    ImageButton btnPlay, btnNext, btnPrev;
    TextView txtSng, txtStart, txtStop;
    SeekBar skBar;
    ImageView imgView;

    String sName;
    public static final String EXTRA_NAME = "song_name";
    static MediaPlayer mediaPlayer;
    int pos;
    ArrayList<File> mySongs;

    Thread updateSeekbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        btnPlay = findViewById(R.id.btnPlay);
        btnNext = findViewById(R.id.btnNext);
        btnPrev = findViewById(R.id.btnPrev);

        txtSng = findViewById(R.id.txtSng);
        txtStart = findViewById(R.id.txtStart);
        txtStop = findViewById(R.id.txtStop);

        imgView = findViewById(R.id.imgView);

        skBar = findViewById(R.id.skBar);

        if(mediaPlayer != null){
            mediaPlayer.stop();
            mediaPlayer.release();
        }

        Intent i = getIntent();

        mySongs = (ArrayList) i.getParcelableArrayListExtra("songs");
        sName = i.getStringExtra("songName");
        pos = i.getIntExtra("pos", 0);

        txtSng.setSelected(true);
        Uri uri = Uri.parse(mySongs.get(pos).toString());
        sName = mySongs.get(pos).getName();
        txtSng.setText(sName);
        mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
        mediaPlayer.start();

        updateSeekbar = new Thread(){
            @Override
            public void run() {
                int totalDuration = mediaPlayer.getDuration();
                int currentPosition = 0;
                while (currentPosition < totalDuration){
                    try{
                        sleep(500);
                        currentPosition = mediaPlayer.getCurrentPosition();
                        skBar.setProgress(currentPosition);

                    }
                    catch(InterruptedException | IllegalStateException e){
                        e.printStackTrace();
                    }
                }
            }
        };

        skBar.setMax(mediaPlayer.getDuration());
        updateSeekbar.start();
        skBar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.pr), PorterDuff.Mode.MULTIPLY);
        skBar.getThumb().setColorFilter(getResources().getColor(R.color.pr), PorterDuff.Mode.SRC_IN);

        skBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
            }
        });

        String endTime = createTime(mediaPlayer.getDuration());
        txtStop.setText(endTime);
        final Handler handler = new Handler();
        final int delay = 1000;

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String currentTime = createTime(mediaPlayer.getCurrentPosition());
                txtStart.setText(currentTime);
                handler.postDelayed(this, delay);
            }
        }, delay);


        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mediaPlayer.isPlaying()){
                    btnPlay.setBackgroundResource(R.drawable.ic_play);
                    mediaPlayer.pause();
                }
                else{
                    btnPlay.setBackgroundResource(R.drawable.ic_pause);
                    mediaPlayer.start();
                }
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
                mediaPlayer.release();
                pos = ((pos + 1) % mySongs.size());
                Uri u = Uri.parse(mySongs.get(pos).toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext(), u);
                sName = mySongs.get(pos).getName();
                txtSng.setText(sName);
                mediaPlayer.start();
                btnPlay.setBackgroundResource(R.drawable.ic_pause);
                startAnimation(imgView, 360f);

            }
        });

        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
                mediaPlayer.release();
                pos = ((pos - 1) < 0)? (mySongs.size() - 1): (pos - 1);
                Uri u = Uri.parse(mySongs.get(pos).toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext(), u);
                sName = mySongs.get(pos).getName();
                txtSng.setText(sName);
                mediaPlayer.start();
                btnPlay.setBackgroundResource(R.drawable.ic_pause);
                startAnimation(imgView, -360f);
            }
        });

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                btnNext.performClick();
            }
        });
    }

    public void startAnimation(View view, float rot){
        ObjectAnimator animator = ObjectAnimator.ofFloat(imgView,"rotation",0f,rot);
        animator.setDuration(1000);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animator);
        animatorSet.start();
    }

    public String createTime(int duration){
        String time ="";
        int min = duration/1000/60;
        int sec = duration/1000%60;
        time += min + ":";
        if(sec<10){
            time += "0";
        }
        time += sec;
        return time;
    }
}