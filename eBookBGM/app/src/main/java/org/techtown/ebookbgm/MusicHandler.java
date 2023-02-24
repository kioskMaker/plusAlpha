package org.techtown.ebookbgm;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class MusicHandler {
    static MediaPlayer currentPlayer;
    static MediaPlayer auxPlayer;
    static Context context;
    static boolean isStop = true;

    public MusicHandler(Context context, MediaPlayer currentPlayer, MediaPlayer auxPlayer) {
        this.currentPlayer = currentPlayer;
        this.auxPlayer = auxPlayer;
        this.context = context;


    }



    public static void crossFade(String activeEmotion, int NUM) throws IOException {
        Log.d("Mypager", activeEmotion);
        if(activeEmotion != "null"){
            auxPlayer = new MediaPlayer();
            AssetFileDescriptor afd = null;
            afd = context.getAssets().openFd("musics/"+activeEmotion+"/"+activeEmotion+NUM+".mp3");
            auxPlayer.setDataSource(afd);
            auxPlayer.prepare();
            MusicHandler.fadeOut(currentPlayer, 2000);
            MusicHandler.fadeIn(auxPlayer, 2000);
            currentPlayer = auxPlayer;
            auxPlayer = null;
            isStop = false;
        }
        else if (activeEmotion.equals("null") && !isStop) {
            isStop = true;
            MusicHandler.fadeOut(currentPlayer, 2000);
            currentPlayer = new MediaPlayer();
        }
        else if(activeEmotion.equals("null") && isStop){
            return;
        }

        currentPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                try {
                    if(activeEmotion.equals("null")) return;
                    Log.d("Mypager", "onCompletion");
                    int i = NUM+1;
                    if(context.getAssets().list("musics/"+activeEmotion).length == i) i = 0;
                    crossFade(activeEmotion, i);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public static void fadeOut(final MediaPlayer _player, final int duration) {
        final Handler h = new Handler();
        h.postDelayed(new Runnable() {
            private float time = duration;
            private float volume = 0.0f;

            @Override
            public void run() {
                if (!_player.isPlaying())
                    _player.start();
                // can call h again after work!
                time -= 100;
                volume = (1f * time) / duration;
                _player.setVolume(volume, volume);
                if (time > 0)
                    h.postDelayed(this, 100);
                else {
                    _player.stop();
                    _player.release();
                }
            }
        }, 100); // 1 second delay (takes millis)


    }

    public static void fadeIn(final MediaPlayer _player, final int duration) {
        final Handler h = new Handler();
        h.postDelayed(new Runnable() {
            private float time = 0.0f;
            private float volume = 0.0f;

            @Override
            public void run() {
                if (!_player.isPlaying())
                    _player.start();
                // can call h again after work!
                time += 100;
                volume = (1f * time) / duration;
                _player.setVolume(volume, volume);
                if (time < duration)
                    h.postDelayed(this, 100);
            }
        }, 100); // 1 second delay (takes millis)

    }

    public void reset(){
        currentPlayer.release();
    }
}