package com.shakeme.sazedul.games.bricksongrid;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
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
    Context context;

    public GameAdapter (Context context) {
        this.context = context;
        list = new ArrayList<Integer>();

        for (int i=0; i<MAX_CELL; i++) {
            list.add(R.drawable.cell);
        }
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.cell_view, parent, false);
        return null;
    }
}
