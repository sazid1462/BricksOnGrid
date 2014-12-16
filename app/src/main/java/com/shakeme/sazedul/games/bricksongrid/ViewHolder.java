package com.shakeme.sazedul.games.bricksongrid;

import android.view.View;
import android.widget.ImageView;

/**
 * Created by Sazedul on 17-Dec-14.
 */
public class ViewHolder {
    ImageView cell;
    public ViewHolder(View v) {
        cell = (ImageView) v.findViewById(R.id.imageView);
    }
}
