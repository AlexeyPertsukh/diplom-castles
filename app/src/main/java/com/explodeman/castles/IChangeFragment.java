package com.explodeman.castles;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import com.explodeman.castles.models.Castle;

public interface IChangeFragment {
    void showCastleDetailFragment(Castle castle);
    void showMapsFragment();
    void showMapsFragment(Castle castle);
    void showPictureFragment(Bitmap bitmap);
}
