package com.shakeme.sazedul.games.bricksongrid;

import android.app.Activity;
import android.content.Intent;
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
    TextView txtWaterMark;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        // load the animation
        animFadein = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
        txtWaterMark = (TextView) findViewById(R.id.water_mark);
        txtWaterMark.startAnimation(animFadein);

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
