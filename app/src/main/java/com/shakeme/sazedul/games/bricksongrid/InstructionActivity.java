package com.shakeme.sazedul.games.bricksongrid;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ScrollView;

import com.shakeme.sazedul.games.bricksongrid.util.GameUtils;


public class InstructionActivity extends Activity implements
        AudioManager.OnAudioFocusChangeListener, Animation.AnimationListener {

    private SharedPreferences prefSettings;
    private MediaPlayer mpMainMenu;
    private MediaPlayer mpButton;
    private AudioManager audioManager;
    private boolean music;
    private boolean sound;

    Animation animFadein;
    Animation animFadeout;
    ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instruction);

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mpMainMenu = MediaPlayer.create(this, R.raw.deep_forest);
        mpButton = MediaPlayer.create(this, R.raw.button);
        mpMainMenu.setLooping(true);
        prefSettings = getSharedPreferences(GameUtils.SHARED_PREF_SETTINGS, MODE_PRIVATE);
        scrollView = (ScrollView) findViewById(R.id.scrollView);

        animFadein = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
        animFadeout = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out);
        // set animation listener
        animFadeout.setAnimationListener(this);
        scrollView.startAnimation(animFadein);
    }

    @Override
    public void onAnimationStart(Animation animation) {
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        // Take any action after completing the animation

        // check for fade out animation
        if (animation == animFadeout) {
            scrollView.setVisibility(View.INVISIBLE);
            finish();
        }
    }

    @Override
    public void onAnimationRepeat(Animation animation) {
    }

    @Override
    protected void onPause() {
        super.onPause();

        pausePlayback();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSoundSettings();
        startPlayback();
    }

    @Override
    protected void onStop() {
        super.onStop();

        stopPlayback();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mpButton.release();
        mpMainMenu.release();
    }

    private void getSoundSettings() {
        music = prefSettings.getBoolean(GameUtils.APP_TAG + GameUtils.MUSIC_TAG, GameUtils.DEFAULT_MUSIC);
        sound = prefSettings.getBoolean(GameUtils.APP_TAG + GameUtils.SOUND_TAG, GameUtils.DEFAULT_SOUND);
    }
    public void exitActivity(View view) {
        if (sound) mpButton.start();
        scrollView.startAnimation(animFadeout);
        view.setClickable(false);
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
            // Pause playback
            pausePlayback();
        } if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
            // Lower the volume
            duckPlayback();
        } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
            // Resume playback
            startPlayback();
        } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
            stopPlayback();
        }
    }

    private void startPlayback() {
        if (music) {
            // Request audio focus for playback
            int result = audioManager.requestAudioFocus(this,
                    // Use the music stream.
                    AudioManager.STREAM_MUSIC,
                    // Request permanent focus.
                    AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK);

            if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                resumePlayback();
            }
        }
    }

    private void stopPlayback() {
        if (music) {
            audioManager.abandonAudioFocus(this);
            // Stop playback
            mpMainMenu.pause();
        }
    }

    private void pausePlayback() {
        if (music) {
            mpMainMenu.pause();
        }
    }

    private void resumePlayback() {
        if (music) {
            mpMainMenu.start();
            mpMainMenu.setVolume(1f, 1f);
        }
    }

    private void duckPlayback() {
        if (music) {
            mpMainMenu.setVolume(0.5f, 0.5f);
        }
    }
}
