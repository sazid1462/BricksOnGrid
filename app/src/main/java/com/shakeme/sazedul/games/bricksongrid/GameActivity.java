package com.shakeme.sazedul.games.bricksongrid;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shakeme.sazedul.games.bricksongrid.util.GameAdapter;
import com.shakeme.sazedul.games.bricksongrid.util.GameUtils;


public class GameActivity extends Activity implements AudioManager.OnAudioFocusChangeListener{

    private int gridState[] = new int[GameUtils.MAX_CELL];
    private GridView gameView;
    private GameAdapter gameAdapter;
    private GestureDetectorCompat mDetector;

    private SharedPreferences prefSettings;

    private String name;
    private int blockedCell;
    private boolean musicEnabled;
    private boolean soundEnabled;
    private boolean aiEnabled;
    private boolean classicEnabled;

    private LinearLayout layoutYourScore;
    private LinearLayout layoutRivalScore;
    private TextView txtYourScore;
    private TextView txtRivalScore;
    private TextView txtRivalTurn;
    private TextView txtYourTurn;

    private boolean playerTurn;

    private MediaPlayer mpMainMenu;
    private MediaPlayer mpBrickPlayer;
    private MediaPlayer mpBrickRival;
    private AudioManager audioManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mpMainMenu = MediaPlayer.create(this, R.raw.game);
        mpBrickPlayer = MediaPlayer.create(this, R.raw.brick);
        mpBrickRival = MediaPlayer.create(this, R.raw.brick);
        mpMainMenu.setLooping(true);

        startPlayback();

        // Instantiate variables
        gameView = (GridView) findViewById(R.id.gridView);
        gameAdapter = new GameAdapter(this);
        gameView.setAdapter(gameAdapter);
        prefSettings = getSharedPreferences(GameUtils.SHARED_PREF_SETTINGS, MODE_PRIVATE);
        layoutYourScore = (LinearLayout) findViewById(R.id.your_score);
        layoutRivalScore = (LinearLayout) findViewById(R.id.rival_score);
        txtYourScore = (TextView) findViewById(R.id.txt_your_score);
        txtRivalScore = (TextView) findViewById(R.id.txt_rival_score);
        txtYourTurn = (TextView) findViewById(R.id.your_turn);
        txtRivalTurn = (TextView) findViewById(R.id.rival_turn);
        playerTurn = ((Math.round(Math.random()*997))%2 == 1);

        // Get the settings from SharedPreferences
        name = prefSettings.getString(GameUtils.APP_TAG+GameUtils.NAME_TAG, GameUtils.DEFAULT_NAME);
        blockedCell = prefSettings.getInt(GameUtils.APP_TAG+GameUtils.BLOCKED_TILE_TAG, GameUtils.DEFAULT_BLOCKED);
        musicEnabled = prefSettings.getBoolean(GameUtils.APP_TAG+GameUtils.MUSIC_TAG, GameUtils.DEFAULT_MUSIC);
        soundEnabled = prefSettings.getBoolean(GameUtils.APP_TAG+GameUtils.SOUND_TAG, GameUtils.DEFAULT_SOUND);
        aiEnabled = prefSettings.getBoolean(GameUtils.APP_TAG+GameUtils.AI_TAG, GameUtils.DEFAULT_AI);
        classicEnabled = prefSettings.getBoolean(GameUtils.APP_TAG+GameUtils.CLASSIC_TAG, GameUtils.DEFAULT_CLASSIC);

        initializeGame();

        mDetector = new GestureDetectorCompat(this, new GameGestureListener());
        gameView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return mDetector.onTouchEvent(event);
            }
        });
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
        mpBrickRival.release();
        mpBrickPlayer.release();
        mpMainMenu.release();
    }

    private void initializeGame () {
        if (classicEnabled) {
            layoutYourScore.setVisibility(View.INVISIBLE);
            layoutRivalScore.setVisibility(View.INVISIBLE);
        }
        if (playerTurn) {
            txtYourTurn.setVisibility(View.VISIBLE);
            txtRivalTurn.setVisibility(View.GONE);
        } else {
            txtYourTurn.setVisibility(View.GONE);
            txtRivalTurn.setVisibility(View.VISIBLE);
        }
        if (blockedCell != 0) {
            blockTheCells();
        }
    }

    private void blockTheCells () {
        int cnt = blockedCell;

        while (cnt > 0) {
            int cell = (int) (Math.round(Math.random()*997)%GameUtils.MAX_CELL);
            while (gridState[cell] != 0) {
                cell = (int) (Math.round(Math.random()*997)%GameUtils.MAX_CELL);
            }
            gridState[cell] = 2;
            gameAdapter.setItem(cell, R.drawable.obstacle);
            cnt--;
        }
        gameAdapter.notifyDataSetChanged();
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
        if (musicEnabled) {
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
        if (musicEnabled) {
            audioManager.abandonAudioFocus(this);
            // Stop playback
            mpMainMenu.pause();
        }
    }

    private void pausePlayback() {
        if (musicEnabled) {
            mpMainMenu.pause();
        }
    }

    private void resumePlayback() {
        if (musicEnabled) {
            mpMainMenu.start();
            mpMainMenu.setVolume(1f, 1f);
        }
    }

    private void duckPlayback() {
        if (musicEnabled) {
            mpMainMenu.setVolume(0.5f, 0.5f);
        }
    }

    /**
     * Created by Sazedul on 26-Dec-14.
     */
    public class GameGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent event) {
            Log.d(GameUtils.DEBUG_TAG, "onDown: " + event.toString());
            Log.d(GameUtils.DEBUG_TAG, "Width "+gameView.getColumnWidth());
            return true;
        }

//        @Override
//        public boolean onFling(MotionEvent event1, MotionEvent event2,
//                               float velocityX, float velocityY) {
//            Log.d(DEBUG_TAG, "onFling: " + event1.toString() + event2.toString());
//            return true;
//        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
//            Log.d(DEBUG_TAG, "onScroll: " + e1.toString()+e2.toString());
            if (
                    e1.getX() - e2.getX() > gameView.getColumnWidth()
                            &&
                    Math.abs(e1.getY() - e2.getY()) < gameView.getColumnWidth()
               ) {
                onScrollLeft(
                        gameView.pointToPosition(Math.round(e1.getX()), Math.round(e1.getY())),
                        gameView.pointToPosition(Math.round(e1.getX()-gameView.getColumnWidth()), Math.round(e1.getY()))
                );
            }
            else if (
                        e2.getX() - e1.getX() > gameView.getColumnWidth()
                            &&
                        Math.abs(e1.getY() - e2.getY()) < gameView.getColumnWidth()
                    ) {
                    onScrollRight(
                            gameView.pointToPosition(Math.round(e1.getX()), Math.round(e1.getY())),
                            gameView.pointToPosition(Math.round(e1.getX()+gameView.getColumnWidth()), Math.round(e1.getY()))
                    );
            }
            else if (
                        e2.getY() - e1.getY() > gameView.getColumnWidth()
                            &&
                        Math.abs(e1.getX() - e2.getX()) < gameView.getColumnWidth()
                    ) {
                    onScrollDown(
                            gameView.pointToPosition(Math.round(e1.getX()), Math.round(e1.getY())),
                            gameView.pointToPosition(Math.round(e1.getX()), Math.round(e1.getY()+gameView.getColumnWidth()))
                    );
            }
            else if (
                        e1.getY() - e2.getY() > gameView.getColumnWidth()
                            &&
                        Math.abs(e1.getX() - e2.getX()) < gameView.getColumnWidth()
                    ) {
                    onScrollUp(
                            gameView.pointToPosition(Math.round(e1.getX()), Math.round(e1.getY())),
                            gameView.pointToPosition(Math.round(e1.getX()), Math.round(e1.getY()-gameView.getColumnWidth()))
                    );
            }
            return true;
        }

        private void onScrollLeft(int pos1, int pos2) {
            Log.d(GameUtils.DEBUG_TAG, "onScroll: Left " + pos1+" "+pos2);
            if (pos1 != AdapterView.INVALID_POSITION && pos2 != AdapterView.INVALID_POSITION) {
                if (gridState[pos1]==0 && gridState[pos2]==0) {
                    gameAdapter.setItem(pos1, R.drawable.cell_brick_right);
                    gameAdapter.setItem(pos2, R.drawable.cell_brick_left);
                    gridState[pos1] = gridState[pos2] = 1;
                    gameAdapter.notifyDataSetChanged();
                    if (soundEnabled) mpBrickPlayer.start();
                }
            }
        }

        private void onScrollRight(int pos1, int pos2) {
            Log.d(GameUtils.DEBUG_TAG, "onScroll: Right " + pos1+" "+pos2);
            if (pos1 != AdapterView.INVALID_POSITION && pos2 != AdapterView.INVALID_POSITION) {
                if (gridState[pos1]==0 && gridState[pos2]==0) {
                    gameAdapter.setItem(pos1, R.drawable.cell_brick_left);
                    gameAdapter.setItem(pos2, R.drawable.cell_brick_right);
                    gridState[pos1] = gridState[pos2] = 1;
                    gameAdapter.notifyDataSetChanged();
                    if (soundEnabled) mpBrickPlayer.start();
                }
            }
        }

        private void onScrollUp(int pos1, int pos2) {
            Log.d(GameUtils.DEBUG_TAG, "onScroll: Up " + pos1+" "+pos2);
            if (pos1 != AdapterView.INVALID_POSITION && pos2 != AdapterView.INVALID_POSITION) {
                if (gridState[pos1]==0 && gridState[pos2]==0) {
                    gameAdapter.setItem(pos1, R.drawable.cell_brick_bottom);
                    gameAdapter.setItem(pos2, R.drawable.cell_brick_top);
                    gridState[pos1] = gridState[pos2] = 1;
                    gameAdapter.notifyDataSetChanged();
                    if (soundEnabled) mpBrickPlayer.start();
                }
            }
        }

        private void onScrollDown(int pos1, int pos2) {
            Log.d(GameUtils.DEBUG_TAG, "onScroll: Down " + pos1+" "+pos2);
            if (pos1 != AdapterView.INVALID_POSITION && pos2 != AdapterView.INVALID_POSITION) {
                if (gridState[pos1]==0 && gridState[pos2]==0) {
                    gameAdapter.setItem(pos1, R.drawable.cell_brick_top);
                    gameAdapter.setItem(pos2, R.drawable.cell_brick_bottom);
                    gridState[pos1] = gridState[pos2] = 1;
                    gameAdapter.notifyDataSetChanged();
                    if (soundEnabled) mpBrickPlayer.start();
                }
            }
        }
    }
}