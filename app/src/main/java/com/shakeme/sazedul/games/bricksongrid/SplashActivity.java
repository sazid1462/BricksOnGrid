package com.shakeme.sazedul.games.bricksongrid;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.shakeme.sazedul.games.bricksongrid.util.GameUtils;

public class SplashActivity extends Activity {

    // Animation
    Animation animFadein;
    Animation animBlink;
    TextView txtWelcome;
    private SharedPreferences prefSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        prefSettings = getSharedPreferences(GameUtils.SHARED_PREF_SETTINGS, MODE_PRIVATE);
        // load the animation
        animFadein = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
        animBlink = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.welcome);
        txtWelcome = (TextView) findViewById(R.id.welcome);

        if (prefSettings.contains(GameUtils.APP_TAG+GameUtils.NAME_TAG)) {
            txtWelcome.setText("Welcome back "+prefSettings.getString(GameUtils.APP_TAG+GameUtils.NAME_TAG, GameUtils.DEFAULT_NAME));
        }

        txtWelcome.startAnimation(animBlink);

        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer.
             */

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                Intent intent = new Intent(SplashActivity.this, MainMenuActivity.class);
                startActivity(intent);

                // close this activity
                finish();
            }
        }, GameUtils.SPLASH_TIME_OUT);

    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
