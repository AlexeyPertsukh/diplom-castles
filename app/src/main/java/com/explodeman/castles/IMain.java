package com.explodeman.castles;

import com.explodeman.castles.models.Castle;

import java.util.List;

public interface IMain {
    List<Castle> getCastles();

    void popBackStack();
    void setActionBarTittle(String tittle);
}
