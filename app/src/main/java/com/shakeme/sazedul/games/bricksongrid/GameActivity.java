package com.shakeme.sazedul.games.bricksongrid;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.shakeme.sazedul.games.bricksongrid.util.GameAdapter;
import com.shakeme.sazedul.games.bricksongrid.util.GameUtils;


public class GameActivity extends Activity {

    private int gridState[] = new int[GameUtils.MAX_CELL];
    private GridView gameView;
    private GameAdapter gameAdapter;
    private GestureDetectorCompat mDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        gameView = (GridView) findViewById(R.id.gridView);
        gameAdapter = new GameAdapter(this);
        gameView.setAdapter(gameAdapter);
        mDetector = new GestureDetectorCompat(this, new GameGestureListener());
        gameView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return mDetector.onTouchEvent(event);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_game, menu);
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
                        gameView.pointToPosition(Math.round(e1.getX()-gameView.getColumnWidth()), Math.round(e2.getY()))
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
                }
            }
        }
    }
}