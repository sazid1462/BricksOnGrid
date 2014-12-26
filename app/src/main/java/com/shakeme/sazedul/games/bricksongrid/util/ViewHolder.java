package com.shakeme.sazedul.games.bricksongrid.util;

import android.view.View;
import android.widget.ImageView;

import com.shakeme.sazedul.games.bricksongrid.R;

/**
 * Created by Sazedul on 17-Dec-14.
 */
public class ViewHolder {
    ImageView cell;
    public ViewHolder(View v) {
        cell = (ImageView) v.findViewById(R.id.imageView);
    }
}
