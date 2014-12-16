package com.shakeme.sazedul.games.bricksongrid;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.shakeme.sazedul.games.bricksongrid.util.SystemUiHider;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        Thread timer;

        timer = new Thread(){

            @Override
            public void run() {
                try {
                    sleep(5000);
                    Intent intent = new Intent(SplashActivity.this, MainMenuActivity.class);
                    startActivity(intent);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    finish();
                }
            }
        };
        timer.start();

    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
