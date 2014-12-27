package com.shakeme.sazedul.games.bricksongrid;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;

import com.shakeme.sazedul.games.bricksongrid.util.GameUtils;


public class MainMenuActivity extends Activity implements AudioManager.OnAudioFocusChangeListener {

    private SharedPreferences prefSettings;
    private MediaPlayer mpMainMenu;
    private MediaPlayer mpButton;
    private AudioManager audioManager;
    private boolean music;
    private boolean sound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mpMainMenu = MediaPlayer.create(this, R.raw.welcome);
        mpButton = MediaPlayer.create(this, R.raw.button);
        mpMainMenu.setLooping(true);
        prefSettings = getSharedPreferences(GameUtils.SHARED_PREF_SETTINGS, MODE_PRIVATE);
        music = prefSettings.getBoolean(GameUtils.APP_TAG + GameUtils.MUSIC_TAG, GameUtils.DEFAULT_MUSIC);
        sound = prefSettings.getBoolean(GameUtils.APP_TAG + GameUtils.SOUND_TAG, GameUtils.DEFAULT_SOUND);

        startPlayback();
    }

    @Override
    protected void onPause() {
        super.onPause();

        pausePlayback();
    }

    @Override
    protected void onResume() {
        super.onResume();

        resumePlayback();
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

    public void showGameActivity (View view) {
        if (sound) mpButton.start();
        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
    }

    public void showSettingsActivity(View view) {
        if (sound) mpButton.start();
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    public void showInstructionsActivity(View view) {
        if (sound) mpButton.start();
        Intent intent = new Intent(this, InstructionActivity.class);
        startActivity(intent);
    }

    public void showCreditsActivity(View view) {
        if (sound) mpButton.start();
        Intent intent = new Intent(this, CreditsActivity.class);
        startActivity(intent);
    }

    public void exitActivity(View view) {
        if (sound) mpButton.start();
        finish();
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