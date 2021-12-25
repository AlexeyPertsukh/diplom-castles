package com.explodeman.castles.roomdb;

import android.app.Application;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.explodeman.castles.models.Castle;

@Database(entities = {Castle.class},version = 21)
public abstract class CastleDb extends RoomDatabase {
    public abstract CastleDao castleDao();
}
