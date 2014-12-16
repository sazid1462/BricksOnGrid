package com.shakeme.sazedul.games.bricksongrid;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;


public class MainMenuActivity extends Activity implements AudioManager.OnAudioFocusChangeListener {

    private MediaPlayer mpMainMenu;
    private AudioManager audioManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mpMainMenu = MediaPlayer.create(this, R.raw.welcome);
        mpMainMenu.setLooping(true);

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

        mpMainMenu.release();
    }

    public void showGameActivity (View view) {
        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
    }

    public void showSettingsActivity(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    public void showInstructionsActivity(View view) {
        Intent intent = new Intent(this, InstructionActivity.class);
        startActivity(intent);
    }

    public void showCreditsActivity(View view) {
        Intent intent = new Intent(this, CreditsActivity.class);
        startActivity(intent);
    }

    public void exitActivity(View view) {
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

    private void stopPlayback() {
        audioManager.abandonAudioFocus(this);
        // Stop playback
        mpMainMenu.pause();
    }

    private void pausePlayback() {
        mpMainMenu.pause();
    }

    private void resumePlayback() {
        mpMainMenu.start();
        mpMainMenu.setVolume(1f, 1f);
    }

    private void duckPlayback() {
        mpMainMenu.setVolume(0.5f, 0.5f);
    }
}