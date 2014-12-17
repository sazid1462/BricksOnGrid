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

    private static final int MAX_CELL = 64;
    Context context;

    public GameAdapter (Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return MAX_CELL;
    }

    @Override
    public Object getItem(int position) {
        return R.drawable.cell;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder;

        if (row==null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.cell_view, parent, false);
            holder = new ViewHolder(row);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        holder.cell.setImageResource(R.drawable.cell);
        return row;
    }
}
