package com.shakeme.sazedul.games.bricksongrid;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;

import com.shakeme.sazedul.games.bricksongrid.util.GameUtils;


public class SettingsActivity extends Activity {

    private SharedPreferences prefSettings;

    private EditText txtName;
    private EditText txtBlocked;
    private Switch musicEnabled;
    private Switch soundEnabled;
    private Switch aiEnabled;
    private Switch classicEnabled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

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
        boolean music = prefSettings.getBoolean(GameUtils.APP_TAG + GameUtils.MUSIC_TAG, GameUtils.DEFAULT_MUSIC);
        boolean sound = prefSettings.getBoolean(GameUtils.APP_TAG + GameUtils.SOUND_TAG, GameUtils.DEFAULT_SOUND);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void exitActivity(View view) {
        finish();
    }

    public void saveSettings(View view) {
        // Get the settings from UI
        String name = txtName.getText().toString();
        int blockedCell = Integer.parseInt(txtBlocked.getText().toString());
        boolean music = musicEnabled.isChecked();
        boolean sound = soundEnabled.isChecked();
        boolean ai = aiEnabled.isChecked();
        boolean classic = classicEnabled.isChecked();

        SharedPreferences.Editor editor = prefSettings.edit();

        editor.putString(GameUtils.APP_TAG+GameUtils.NAME_TAG, name);
        editor.putInt(GameUtils.APP_TAG+GameUtils.BLOCKED_TILE_TAG, blockedCell);
        editor.putBoolean(GameUtils.APP_TAG+GameUtils.MUSIC_TAG, music);
        editor.putBoolean(GameUtils.APP_TAG+GameUtils.SOUND_TAG, sound);
        editor.putBoolean(GameUtils.APP_TAG+GameUtils.AI_TAG, ai);
        editor.putBoolean(GameUtils.APP_TAG+GameUtils.CLASSIC_TAG, classic);
        editor.apply();

        finish();
    }
}
