package com.shakeme.sazedul.games.bricksongrid.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.shakeme.sazedul.games.bricksongrid.R;

import java.util.ArrayList;

/**
 * Created by Sazedul on 17-Dec-14.
 */

public class GameAdapter extends BaseAdapter {

    Context context;
    ArrayList<Integer> list;

    public GameAdapter (Context context) {
        this.context = context;
        list = new ArrayList<>();

        for (int i=0; i<GameUtils.MAX_CELL; i++) {
            list.add(R.drawable.cell);
        }
    }

    @Override
    public int getCount() {
        return GameUtils.MAX_CELL;
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setItem(int position, int imageId) {
        list.set(position, imageId);
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

        holder.cell.setImageResource(list.get(position));
        return row;
    }
}
