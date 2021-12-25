package com.explodeman.castles;

import com.explodeman.castles.models.Castle;

public interface IChangeFragment {
    void showCastleDetailFragment(Castle castle);
    void showMapsFragment();
    void showMapsFragment(Castle castle);
}
