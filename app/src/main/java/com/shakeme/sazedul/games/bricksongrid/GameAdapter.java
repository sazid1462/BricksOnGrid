package com.shakeme.sazedul.games.bricksongrid;

import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

/**
 * Created by Sazedul on 17-Dec-14.
 */

class GameAdapter extends BaseAdapter {

    private static final int MAX_CELL = 100;
    ArrayList<Integer> list;

    public GameAdapter (Context context) {
        list = new ArrayList<Integer>();

        for (int i=0; i<MAX_CELL; i++) {
            
        }
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }
}
