package org.techtown.ebookbgm;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class MusicPlayerActivity extends AppCompatActivity {

    float volume = 0f;
    MediaPlayer mediaPlayer;
    Boolean isInit = false;
    int NUM = 1;
    String activeEmotion = "anger";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                NUM++;
                AssetFileDescriptor afd = null;
                try {
                    afd = getAssets().openFd("musics/"+activeEmotion+"/"+activeEmotion+NUM+".mp3");
                    mediaPlayer.setDataSource(afd);
                    mediaPlayer.prepare();
                    mediaPlayer.start();

                } catch (IOException e) {
                    Log.d("Mymusic", "onClick IOException");
                    e.printStackTrace();
                }
                startFadeIn();
                isInit = true;
            }
        });

        Button button_play = findViewById(R.id.button_play);
        button_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                play();
            }
        });
    }
    private void play(){
        if(isInit){
            NUM = 1;
            startFadeOut(activeEmotion, NUM);
        }
        else{
            AssetFileDescriptor afd = null;
            try {
                afd = getAssets().openFd("musics/"+activeEmotion+"/"+activeEmotion+NUM+".mp3");
                mediaPlayer.setDataSource(afd);
                mediaPlayer.prepare();
                mediaPlayer.start();

            } catch (IOException e) {
                Log.d("Mymusic", "onClick IOException");
                e.printStackTrace();
            }
            startFadeIn();
            isInit = true;
        }
    }

    private void startFadeIn(){
        volume = 0f;
        final int FADE_DURATION = 3000; //The duration of the fade
        //The amount of time between volume changes. The smaller this is, the smoother the fade
        final int FADE_INTERVAL = 250;
        final int MAX_VOLUME = 1; //The volume will increase from 0 to 1
        int numberOfSteps = FADE_DURATION/FADE_INTERVAL; //Calculate the number of fade steps
        //Calculate by how much the volume changes each step
        final float deltaVolume = MAX_VOLUME / (float)numberOfSteps;

        //Create a new Timer and Timer task to run the fading outside the main UI thread
        final Timer timer = new Timer(true);
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                fadeInStep(deltaVolume); //Do a fade step
                //Cancel and Purge the Timer if the desired volume has been reached
                if(volume>=1f){
                    timer.cancel();
                    timer.purge();
                }
            }
        };

        timer.schedule(timerTask,FADE_INTERVAL,FADE_INTERVAL);
    }

    private void fadeInStep(float deltaVolume){
        mediaPlayer.setVolume(volume, volume);
        volume += deltaVolume;

    }

    private void startFadeOut(String emotion, int num){
        volume = 1f;
        // The duration of the fade
        final int FADE_DURATION = 3000;

        // The amount of time between volume changes. The smaller this is, the smoother the fade
        final int FADE_INTERVAL = 250;

        // Calculate the number of fade steps
        int numberOfSteps = FADE_DURATION / FADE_INTERVAL;

        // Calculate by how much the volume changes each step
        final float deltaVolume = volume / numberOfSteps;

        // Create a new Timer and Timer task to run the fading outside the main UI thread
        final Timer timer = new Timer(true);
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {

                //Do a fade step
                fadeOutStep(deltaVolume);

                //Cancel and Purge the Timer if the desired volume has been reached
                if(volume <= 0f){
                    timer.cancel();
                    timer.purge();
                    resetPlayer(emotion, num);
                }
            }
        };

        timer.schedule(timerTask,FADE_INTERVAL,FADE_INTERVAL);
    }

    private void fadeOutStep(float deltaVolume){
        mediaPlayer.setVolume(volume, volume);
        volume -= deltaVolume;
    }

    // Release the player from memory
    private void resetPlayer(String emotion, int num){
        stopPlayer();
        if(emotion.equals("null")){
            isInit = false;
            return;
        }
        AssetFileDescriptor afd = null;
        try {
            afd = getAssets().openFd("musics/"+ emotion +"/" +emotion + num +".mp3");
            mediaPlayer.setDataSource(afd);
            mediaPlayer.prepare();
            mediaPlayer.start();

        } catch (IOException e) {
            Log.d("Mymusic", "onClick IOException");
            e.printStackTrace();
        }
        startFadeIn();
        isInit = true;

    }

    private void stopPlayer() {
        mediaPlayer.stop();
        mediaPlayer.reset();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.release();
    }
}