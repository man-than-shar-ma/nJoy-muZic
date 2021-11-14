package com.manthansharma.njoymuzic;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
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
                startAnimation(imgView);

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
                startAnimation(imgView);
            }
        });

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                btnNext.performClick();
            }
        });
    }

    public void startAnimation(View view){
        ObjectAnimator animator = ObjectAnimator.ofFloat(imgView,"rotation",0f,360f);
        animator.setDuration(1000);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animator);
        animatorSet.start();
    }


}