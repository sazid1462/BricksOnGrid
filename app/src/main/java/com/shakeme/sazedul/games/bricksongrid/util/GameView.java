package com.shakeme.sazedul.games.bricksongrid.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.GridView;

/**
 * Created by Sazedul on 17-Dec-14.
 */
public class GameView extends GridView {

    private static final String DEBUG_TAG = "Gestures";
    private GestureDetectorCompat mDetector;

    public GameView(Context context) {
        super(context);
        mDetector = new GestureDetectorCompat(context, new GameGestureListener());
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mDetector = new GestureDetectorCompat(context, new GameGestureListener());
    }

    public GameView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mDetector = new GestureDetectorCompat(context, new GameGestureListener());
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public GameView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mDetector = new GestureDetectorCompat(context, new GameGestureListener());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth()); // Snap to width
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        mDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    /**
     * Created by Sazedul on 26-Dec-14.
     */
    public static class GameGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent event) {
            Log.d(DEBUG_TAG,"onDown: " + event.toString());
            return true;
        }

        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2,
                               float velocityX, float velocityY) {
            Log.d(DEBUG_TAG, "onFling: " + event1.toString() + event2.toString());
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            Log.d(DEBUG_TAG, "onScroll: " + e1.toString()+e2.toString());
            return true;
        }
    }
}
