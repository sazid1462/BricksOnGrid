package com.shakeme.sazedul.games.bricksongrid;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;

import com.shakeme.sazedul.games.bricksongrid.util.GameUtils;


public class SettingsActivity extends Activity implements AudioManager.OnAudioFocusChangeListener {

    private SharedPreferences prefSettings;

    private EditText txtName;
    private EditText txtBlocked;
    private Switch musicEnabled;
    private Switch soundEnabled;
    private Switch aiEnabled;
    private Switch classicEnabled;
    private MediaPlayer mpMainMenu;
    private MediaPlayer mpButton;
    private AudioManager audioManager;

    boolean music;
    boolean sound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mpMainMenu = MediaPlayer.create(this, R.raw.welcome);
        mpButton = MediaPlayer.create(this, R.raw.button);
        mpMainMenu.setLooping(true);

        startPlayback();

        prefSettings = getSharedPreferences(GameUtils.SHARED_PREF_SETTINGS, MODE_PRIVATE);
        txtName = (EditText) findViewById(R.id.txt_name);
        txtBlocked = (EditText) findViewById(R.id.txt_blocked_tile);
        musicEnabled = (Switch) findViewById(R.id.music_enabled);
        soundEnabled = (Switch) findViewById(R.id.sound_enabled);
        aiEnabled = (Switch) findViewById(R.id.ai_enabled);
        classicEnabled = (Switch) findViewById(R.id.classic_enabled);

        // Get the settings from SharedPreferences
        String name = prefSettings.getString(GameUtils.APP_TAG + GameUtils.NAME_TAG, GameUtils.DEFAULT_NAME);
        int blockedCell = prefSettings.getInt(GameUtils.APP_TAG + GameUtils.BLOCKED_TILE_TAG, GameUtils.DEFAULT_BLOCKED);
        music = prefSettings.getBoolean(GameUtils.APP_TAG + GameUtils.MUSIC_TAG, GameUtils.DEFAULT_MUSIC);
        sound = prefSettings.getBoolean(GameUtils.APP_TAG + GameUtils.SOUND_TAG, GameUtils.DEFAULT_SOUND);
        boolean ai = prefSettings.getBoolean(GameUtils.APP_TAG + GameUtils.AI_TAG, GameUtils.DEFAULT_AI);
        boolean classic = prefSettings.getBoolean(GameUtils.APP_TAG + GameUtils.CLASSIC_TAG, GameUtils.DEFAULT_CLASSIC);

        txtName.setText(name);
        txtBlocked.setText(blockedCell);
        musicEnabled.setChecked(music);
        soundEnabled.setChecked(sound);
        aiEnabled.setChecked(ai);
        classicEnabled.setChecked(classic);
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

    public void exitActivity(View view) {
        if (sound) mpButton.start();
        finish();
    }

    public void saveSettings(View view) {
        if (sound) mpButton.start();
        // Get the settings from UI
        String name = txtName.getText().toString();
        int blockedCell = Integer.parseInt(txtBlocked.getText().toString());
        boolean music = musicEnabled.isChecked();
        boolean sound = soundEnabled.isChecked();
        boolean ai = aiEnabled.isChecked();
        boolean classic = classicEnabled.isChecked();

        SharedPreferences.Editor editor = prefSettings.edit();

        editor.putString(GameUtils.APP_TAG+GameUtils.NAME_TAG, name);
        editor.putInt(GameUtils.APP_TAG + GameUtils.BLOCKED_TILE_TAG, blockedCell);
        editor.putBoolean(GameUtils.APP_TAG + GameUtils.MUSIC_TAG, music);
        editor.putBoolean(GameUtils.APP_TAG + GameUtils.SOUND_TAG, sound);
        editor.putBoolean(GameUtils.APP_TAG + GameUtils.AI_TAG, ai);
        editor.putBoolean(GameUtils.APP_TAG+GameUtils.CLASSIC_TAG, classic);
        editor.apply();

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
