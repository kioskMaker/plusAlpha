package org.techtown.ebookbgm;

import android.media.MediaPlayer;

public class bgmPlay {

    MediaPlayer mediaPlayer;

    mediaPlayer = MediaPlayer.create(this, R.raw.music);
    mediaPlayer.start()


}
