package org.techtown.ebookbgm;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class MusicHandler {
    private MediaPlayer mediaPlayer;
    private MediaPlayer mediaPlayer_pre;
    private Context context;
    private int iVolume;
    final int FADE_DURATION = 3000; //The duration of the fade
    //The amount of time between volume changes. The smaller this is, the smoother the fade
    final int FADE_INTERVAL = 250;
    final int MAX_VOLUME = 1; //The volume will increase from 0 to 1
    int numberOfSteps = FADE_DURATION/FADE_INTERVAL; //Calculate the number of fade steps
    //Calculate by how much the volume changes each step
    final float deltaVolume = MAX_VOLUME / (float)numberOfSteps;

    float volume = 0;
    public MusicHandler(Context context) {
        this.context = context;
    }

    public void play(Uri audiofileUri) throws IOException {
        if(mediaPlayer.isPlaying()){
            startFadeOut(audiofileUri);
        }
        else {
            playSetting(audiofileUri);
            startFadeIn();
        }
    }

    private void playSetting(Uri audiofileUri) throws IOException {
        mediaPlayer.stop();
        mediaPlayer.reset();
        mediaPlayer.setDataSource(context,audiofileUri);
        mediaPlayer.prepare();
        mediaPlayer.start();
    }




    private void startFadeIn(){
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

    private void startFadeOut(Uri audiofileUri){
        //Create a new Timer and Timer task to run the fading outside the main UI thread
        final Timer timer = new Timer(true);
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                fadeOutStep(deltaVolume); //Do a fade step
                //Cancel and Purge the Timer if the desired volume has been reached
                if(volume<=0f){
                    timer.cancel();
                    timer.purge();
                    try {
                        playSetting(audiofileUri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    startFadeIn();
                }
            }
        };

        timer.schedule(timerTask,FADE_INTERVAL,FADE_INTERVAL);
    }

    private void fadeInStep(float deltaVolume){
        mediaPlayer.setVolume(volume, volume);
        volume += deltaVolume;

    }
    private void fadeOutStep(float deltaVolume){
        mediaPlayer.setVolume(volume, volume);
        volume -= deltaVolume;

    }
}