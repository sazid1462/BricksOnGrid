package com.shakeme.sazedul.games.bricksongrid;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.shakeme.sazedul.games.bricksongrid.util.GameAdapter;
import com.shakeme.sazedul.games.bricksongrid.util.GameUtils;


public class GameActivity extends Activity implements
        AudioManager.OnAudioFocusChangeListener,
        View.OnClickListener,
        Animation.AnimationListener{

    private int MAX_CELL;
    private int MAX_ROW;
    private int MAX_COL;

    private int gridState[];
    private GridView gameView;
    private GameAdapter gameAdapter;
    private GestureDetectorCompat mDetector;

    private SharedPreferences prefSettings;

    private String name;
    private int blockedCell;
    private int dim;
    private boolean musicEnabled;
    private boolean soundEnabled;
    private boolean aiEnabled;
    private boolean classicEnabled;

    private LinearLayout layoutYourScore;
    private LinearLayout layoutRivalScore;
    private LinearLayout layoutWinner;
    private LinearLayout layoutLoser;
    private TextView txtYourScore;
    private TextView txtRivalScore;
    private TextView txtRivalTurn;
    private TextView txtYourTurn;
    private ProgressBar progressAI;

    private boolean playerTurn;

    private MediaPlayer mpMainMenu;
    private MediaPlayer mpBrickPlayer;
    private MediaPlayer mpBrickRival;
    private AudioManager audioManager;

    Animation animFadein;
    Animation animFadeout;
    Animation animBlink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mpMainMenu = MediaPlayer.create(this, R.raw.game);
        mpBrickPlayer = MediaPlayer.create(this, R.raw.brick);
        mpBrickRival = MediaPlayer.create(this, R.raw.brick);
        mpMainMenu.setLooping(true);

        animFadein = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
        animFadeout = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out);
        animBlink = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.blink);

        animBlink.setAnimationListener(this);
        animFadeout.setAnimationListener(this);
        animFadein.setAnimationListener(this);

        prefSettings = getSharedPreferences(GameUtils.SHARED_PREF_SETTINGS, MODE_PRIVATE);
        // Get the settings from SharedPreferences
        name = prefSettings.getString(GameUtils.APP_TAG+GameUtils.NAME_TAG, GameUtils.DEFAULT_NAME);
        blockedCell = prefSettings.getInt(GameUtils.APP_TAG+GameUtils.BLOCKED_TILE_TAG, GameUtils.DEFAULT_BLOCKED);
        dim = prefSettings.getInt(GameUtils.APP_TAG+GameUtils.DIMENSION_TAG, GameUtils.DEFAULT_DIMENSION);
        musicEnabled = prefSettings.getBoolean(GameUtils.APP_TAG+GameUtils.MUSIC_TAG, GameUtils.DEFAULT_MUSIC);
        soundEnabled = prefSettings.getBoolean(GameUtils.APP_TAG+GameUtils.SOUND_TAG, GameUtils.DEFAULT_SOUND);
        aiEnabled = prefSettings.getBoolean(GameUtils.APP_TAG+GameUtils.AI_TAG, GameUtils.DEFAULT_AI);
        classicEnabled = prefSettings.getBoolean(GameUtils.APP_TAG+GameUtils.CLASSIC_TAG, GameUtils.DEFAULT_CLASSIC);

        startPlayback();

        GameUtils.MAX_CELL = dim*dim;
        GameUtils.MAX_ROW = dim;
        GameUtils.MAX_COL = dim;
        MAX_CELL = GameUtils.MAX_CELL;
        MAX_ROW = GameUtils.MAX_ROW;
        MAX_COL = GameUtils.MAX_COL;

        // Instantiate variables
        gameView = (GridView) findViewById(R.id.gridView);
        layoutWinner = (LinearLayout) findViewById(R.id.winner);
        layoutLoser = (LinearLayout) findViewById(R.id.loser);
        layoutYourScore = (LinearLayout) findViewById(R.id.your_score);
        layoutRivalScore = (LinearLayout) findViewById(R.id.rival_score);
        txtYourScore = (TextView) findViewById(R.id.txt_your_score);
        txtRivalScore = (TextView) findViewById(R.id.txt_rival_score);
        txtYourTurn = (TextView) findViewById(R.id.your_turn);
        txtRivalTurn = (TextView) findViewById(R.id.rival_turn);
        progressAI = (ProgressBar) findViewById(R.id.progress_ai);

        initializeGame();

        layoutWinner.setOnClickListener(this);
        layoutLoser.setOnClickListener(this);
        mDetector = new GestureDetectorCompat(this, new GameGestureListener());
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

    @Override
    public void onAnimationStart(Animation animation) {
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        // Take any action after completing the animation

        // check for fade out animation
        if (animation == animFadeout) {
            layoutWinner.setVisibility(View.GONE);
            layoutLoser.setVisibility(View.GONE);
            initializeGame();
        }
        else if (animation == animBlink) {
            gameView.setVisibility(View.GONE);
            if (playerTurn) {
                layoutLoser.setVisibility(View.VISIBLE);
                layoutLoser.startAnimation(animFadein);
            } else {
                layoutWinner.setVisibility(View.VISIBLE);
                layoutWinner.startAnimation(animFadein);
            }
        }
        else if (animation == animFadein) {
            if (!playerTurn) notifyAI();
        }
    }

    @Override
    public void onAnimationRepeat(Animation animation) {
    }

    private void initializeGame () {
        gameView.setVisibility(View.VISIBLE);
        gameView.startAnimation(animFadein);

        gridState = new int[MAX_CELL];
        gameAdapter = new GameAdapter(this);
        gameView.setAdapter(gameAdapter);
        gameView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return mDetector.onTouchEvent(event);
            }
        });
        playerTurn = ((Math.round(Math.random()*997))%2 == 1);
        gameView.setNumColumns(MAX_COL);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (classicEnabled) {
                    layoutYourScore.setVisibility(View.INVISIBLE);
                    layoutRivalScore.setVisibility(View.INVISIBLE);
                }
                showWhoseTurn();
            }
        });
        if (blockedCell != 0) {
            blockTheCells();
        }
    }

    private void showWhoseTurn() {
        if (playerTurn) {
            txtYourTurn.setVisibility(View.VISIBLE);
            txtRivalTurn.setVisibility(View.GONE);
        } else {
            txtYourTurn.setVisibility(View.GONE);
            txtRivalTurn.setVisibility(View.VISIBLE);
        }
    }

    private void notifyAI() {
        if (classicEnabled) {
            new ClassicAI().execute();
        } else {
            new NonClassicAI().execute();
        }
    }

    private void blockTheCells () {

        new AsyncTask<Void, Void, Void>() {
            int cnt = blockedCell;
            @Override
            protected Void doInBackground(Void... params) {
                while (cnt > 0) {
                    int cell = (int) (Math.round(Math.random()*997)%MAX_CELL);
                    while (gridState[cell] != GameUtils.BLANK) {
                        cell = (int) (Math.round(Math.random()*997)%MAX_CELL);
                    }
                    gridState[cell] = GameUtils.BLOCKED;
                    gameAdapter.setItem(cell, R.drawable.obstacle);
                    cnt--;
                }
                gameAdapter.notifyDataSetChanged();
                return null;
            }
        }.execute();
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

    @Override
    public void onClick(View v) {
        v.startAnimation(animFadeout);
    }

    /**
     * Created by Sazedul on 26-Dec-14.
     */
    private class GameGestureListener extends GestureDetector.SimpleOnGestureListener {

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
            if (playerTurn) {
                if (
                        e1.getX() - e2.getX() > gameView.getColumnWidth()
                                &&
                                Math.abs(e1.getY() - e2.getY()) < gameView.getColumnWidth()
                        ) {
                    onScrollLeft(
                            gameView.pointToPosition(Math.round(e1.getX()), Math.round(e1.getY())),
                            gameView.pointToPosition(Math.round(e1.getX() - gameView.getColumnWidth()), Math.round(e1.getY()))
                    );
                } else if (
                        e2.getX() - e1.getX() > gameView.getColumnWidth()
                                &&
                                Math.abs(e1.getY() - e2.getY()) < gameView.getColumnWidth()
                        ) {
                    onScrollRight(
                            gameView.pointToPosition(Math.round(e1.getX()), Math.round(e1.getY())),
                            gameView.pointToPosition(Math.round(e1.getX() + gameView.getColumnWidth()), Math.round(e1.getY()))
                    );
                } else if (
                        e2.getY() - e1.getY() > gameView.getColumnWidth()
                                &&
                                Math.abs(e1.getX() - e2.getX()) < gameView.getColumnWidth()
                        ) {
                    onScrollDown(
                            gameView.pointToPosition(Math.round(e1.getX()), Math.round(e1.getY())),
                            gameView.pointToPosition(Math.round(e1.getX()), Math.round(e1.getY() + gameView.getColumnWidth()))
                    );
                } else if (
                        e1.getY() - e2.getY() > gameView.getColumnWidth()
                                &&
                                Math.abs(e1.getX() - e2.getX()) < gameView.getColumnWidth()
                        ) {
                    onScrollUp(
                            gameView.pointToPosition(Math.round(e1.getX()), Math.round(e1.getY())),
                            gameView.pointToPosition(Math.round(e1.getX()), Math.round(e1.getY() - gameView.getColumnWidth()))
                    );
                }
                return true;
            }
            return false;
        }

        private void onScrollLeft(int pos1, int pos2) {
            Log.d(GameUtils.DEBUG_TAG, "onScroll: Left " + pos1+" "+pos2);
            if (pos1 != AdapterView.INVALID_POSITION && pos2 != AdapterView.INVALID_POSITION) {
                if (gridState[pos1]==GameUtils.BLANK && gridState[pos2]==GameUtils.BLANK) {
                    gameAdapter.setItem(pos1, R.drawable.cell_brick_right);
                    gameAdapter.setItem(pos2, R.drawable.cell_brick_left);
                    gridState[pos1] = gridState[pos2] = GameUtils.PLAYER;
                    gameAdapter.notifyDataSetChanged();
                    if (soundEnabled) mpBrickPlayer.start();
                    playerTurn = false;
                    showWhoseTurn();
                    notifyAI();
                }
            }
        }

        private void onScrollRight(int pos1, int pos2) {
            Log.d(GameUtils.DEBUG_TAG, "onScroll: Right " + pos1+" "+pos2);
            if (pos1 != AdapterView.INVALID_POSITION && pos2 != AdapterView.INVALID_POSITION) {
                if (gridState[pos1]==GameUtils.BLANK && gridState[pos2]==GameUtils.BLANK) {
                    gameAdapter.setItem(pos1, R.drawable.cell_brick_left);
                    gameAdapter.setItem(pos2, R.drawable.cell_brick_right);
                    gridState[pos1] = gridState[pos2] = GameUtils.PLAYER;
                    gameAdapter.notifyDataSetChanged();
                    if (soundEnabled) mpBrickPlayer.start();
                    playerTurn = false;
                    showWhoseTurn();
                    notifyAI();
                }
            }
        }

        private void onScrollUp(int pos1, int pos2) {
            Log.d(GameUtils.DEBUG_TAG, "onScroll: Up " + pos1+" "+pos2);
            if (pos1 != AdapterView.INVALID_POSITION && pos2 != AdapterView.INVALID_POSITION) {
                if (gridState[pos1]==GameUtils.BLANK && gridState[pos2]==GameUtils.BLANK) {
                    gameAdapter.setItem(pos1, R.drawable.cell_brick_bottom);
                    gameAdapter.setItem(pos2, R.drawable.cell_brick_top);
                    gridState[pos1] = gridState[pos2] = GameUtils.PLAYER;
                    gameAdapter.notifyDataSetChanged();
                    if (soundEnabled) mpBrickPlayer.start();
                    playerTurn = false;
                    showWhoseTurn();
                    notifyAI();
                }
            }
        }

        private void onScrollDown(int pos1, int pos2) {
            Log.d(GameUtils.DEBUG_TAG, "onScroll: Down " + pos1+" "+pos2);
            if (pos1 != AdapterView.INVALID_POSITION && pos2 != AdapterView.INVALID_POSITION) {
                if (gridState[pos1]==GameUtils.BLANK && gridState[pos2]==GameUtils.BLANK) {
                    gameAdapter.setItem(pos1, R.drawable.cell_brick_top);
                    gameAdapter.setItem(pos2, R.drawable.cell_brick_bottom);
                    gridState[pos1] = gridState[pos2] = GameUtils.PLAYER;
                    gameAdapter.notifyDataSetChanged();
                    if (soundEnabled) mpBrickPlayer.start();
                    playerTurn = false;
                    showWhoseTurn();
                    notifyAI();
                }
            }
        }
    }

    /**
     * Created by Sazedul on 28-Dec-14.
     */
    private class ClassicAI extends AsyncTask<Void, Void, Integer[]> {
        private int col[][] = new int[MAX_ROW][MAX_COL];
        private boolean isItMyTurn;
        private int cell;

        private void gridStateToColorArray() {
            for (int i=0; i<MAX_ROW; i++) {
                System.arraycopy(gridState, i * MAX_ROW, col[i], 0, MAX_COL);
            }
        }

        private int calculateHuristics(boolean myTurn, int parentValue, int d) {
//            Log.d(GameUtils.AI_THINKING_TAG, "I'm still thinking... "+myTurn+" "+parentValue);
            if (cell > 25) return 0;
            if (d==0) return 0;
            int curValue;
            int tempValue;

            if (myTurn) {

                curValue = -1;
                for (int i=0; i<MAX_ROW; i++) {
                    for (int j=0; j<MAX_COL; j++) {
                        if (col[i][j] == GameUtils.BLANK) {
                            if (j+1<MAX_COL && col[i][j+1]==GameUtils.BLANK) {

                                col[i][j] = col[i][j+1] = GameUtils.RIVAL;
                                tempValue = calculateHuristics(false, curValue, d-1);
                                col[i][j] = col[i][j+1] = GameUtils.BLANK;

                                if (curValue <= tempValue) {
                                    curValue = tempValue;
                                    if (curValue>=parentValue) return curValue;
                                }
                            }
                            if (i+1<MAX_ROW && col[i+1][j]==GameUtils.BLANK) {

                                col[i][j] = col[i+1][j] = GameUtils.RIVAL;
                                tempValue = calculateHuristics(false, curValue, d-1);
                                col[i][j] = col[i+1][j] = GameUtils.BLANK;

                                if (curValue <= tempValue) {
                                    curValue = tempValue;
                                    if (curValue>=parentValue) return curValue;
                                }
                            }
                        }
                    }
                }
                return curValue;
            } else {
                curValue = 1;
                for (int i=0; i<MAX_ROW; i++) {
                    for (int j=0; j<MAX_COL; j++) {
                        if (col[i][j] == GameUtils.BLANK) {
                            if (j+1<MAX_COL && col[i][j+1]==GameUtils.BLANK) {

                                col[i][j] = col[i][j+1] = GameUtils.PLAYER;
                                tempValue = calculateHuristics(true, curValue, d-1);
                                col[i][j] = col[i][j+1] = GameUtils.BLANK;

                                if (curValue >= tempValue) {
                                    curValue = tempValue;
                                    if (curValue<=parentValue) return curValue;
                                }
                            }
                            if (i+1<MAX_ROW && col[i+1][j]==GameUtils.BLANK) {

                                col[i][j] = col[i+1][j] = GameUtils.PLAYER;
                                tempValue = calculateHuristics(true, curValue, d-1);
                                col[i][j] = col[i+1][j] = GameUtils.BLANK;

                                if (curValue >= tempValue) {
                                    curValue = tempValue;
                                    if (curValue<=parentValue) return curValue;
                                }
                            }
                        }
                    }
                }
            }
            return curValue;
        }

        private int[] getWinningPosition() {
            int ret[] = new int[2];
            int curValue = -100000;
            int tempValue;
            cell = calculateAvailableCell();

            ret[0] = ret[1] = -1;
            for (int i=0; i<MAX_ROW; i++) {
                for (int j=0; j<MAX_COL; j++) {
                    if (col[i][j] == GameUtils.BLANK) {
                        if (j+1<MAX_COL && col[i][j+1]==GameUtils.BLANK) {

                            col[i][j] = col[i][j+1] = GameUtils.RIVAL;
                            tempValue = calculateHuristics(false, curValue, 8);
                            col[i][j] = col[i][j+1] = GameUtils.BLANK;

                            if (curValue <= tempValue) {
                                ret[0] = i*MAX_ROW + j;
                                ret[1] = i*MAX_ROW + j+1;
                                curValue = tempValue;
                                if (curValue==1) return ret;
                            }
                        }
                        if (i+1<MAX_ROW && col[i+1][j]==GameUtils.BLANK) {

                            col[i][j] = col[i+1][j] = GameUtils.RIVAL;
                            tempValue = calculateHuristics(false, curValue, 8);
                            col[i][j] = col[i+1][j] = GameUtils.BLANK;

                            if (curValue <= tempValue) {
                                ret[0] = i*MAX_ROW + j;
                                ret[1] = (i+1)*MAX_ROW + j;
                                curValue = tempValue;
                                if (curValue==1) return ret;
                            }
                        }
                    }
                }
            }
            if (curValue == 0) {
                int pos;
                while (true){
                    pos = (int) (Math.round(Math.random() * 997) % MAX_CELL);
                    while (gridState[pos] != GameUtils.BLANK) {
                        pos = (int) (Math.round(Math.random() * 997) % MAX_CELL);
                    }
                    if ((pos+1)%dim != 0) {
                        if (pos+1 < MAX_CELL && gridState[pos+1] == GameUtils.BLANK) return new int[]{pos, pos+1};
                    }
                    if (pos+MAX_COL < MAX_CELL && gridState[pos+MAX_COL] == GameUtils.BLANK) return new int[]{pos, pos+MAX_COL};
                }
            }
            return ret;
        }

        private int calculateAvailableCell() {
            int c = 0;
            for (int i=0; i<MAX_CELL; i++) {
                if (gridState[i]==GameUtils.BLANK) c++;
            }
            return c;
        }

        @Override
        protected void onPreExecute() {
            Log.d(GameUtils.AI_THINKING_TAG, "Wait buddy, let me think.");
            isItMyTurn = playerTurn;
            if (!isItMyTurn) progressAI.setVisibility(View.VISIBLE);
        }

        @Override
        protected Integer[] doInBackground(Void... params) {
            gridStateToColorArray();
            int tmp[] = getWinningPosition();
            return new Integer[]{tmp[0], tmp[1]};
        }

        @Override
        protected void onPostExecute(Integer[] integers) {
            Log.d(GameUtils.AI_THINKING_TAG, "I've finished my thinking.");
            progressAI.setVisibility(View.GONE);

            if (!isItMyTurn) {
                if (integers[0] == -1 || integers[1] == -1) {
                    Toast.makeText(GameActivity.this, "You Win!", Toast.LENGTH_LONG).show();
                    gameView.startAnimation(animBlink);
                } else {
                    if (integers[1] - integers[0] == 1) { // Horizontally adjacent tiles
                        gameAdapter.setItem(integers[0], R.drawable.rival_cell_brick_left);
                        gameAdapter.setItem(integers[1], R.drawable.rival_cell_brick_right);
                    } else { // Vertically adjacent tiles
                        gameAdapter.setItem(integers[0], R.drawable.rival_cell_brick_top);
                        gameAdapter.setItem(integers[1], R.drawable.rival_cell_brick_bottom);
                    }
                    gridState[integers[0]] = gridState[integers[1]] = GameUtils.RIVAL;
                    gameAdapter.notifyDataSetChanged();
                    if (soundEnabled) mpBrickRival.start();
                    playerTurn = true;
                    showWhoseTurn();
                    notifyAI();
                }
            } else {
                if (integers[0] == -1 || integers[1] == -1) {
                    Toast.makeText(GameActivity.this, "I Win!", Toast.LENGTH_LONG).show();
                    gameView.startAnimation(animBlink);
                }
            }
        }
    }

    /**
     * Created by Sazedul on 28-Dec-14.
     */
    private class NonClassicAI extends AsyncTask<Void, Void, Integer> {

        @Override
        protected Integer doInBackground(Void... params) {
            return null;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
        }
    }
}