package com.explodeman.castles.roomdb;


import android.app.Application;

import androidx.room.Room;

public class App extends Application {
    public static final String DATABASE_NAME = "CastlesDatabase.db";
    private static final String ASSETS_DATABASE = "database/CastlesDatabase_db.db";

    public static App instance;
    private CastleDb castleDb;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        castleDb = Room.databaseBuilder(this, CastleDb.class, DATABASE_NAME)
                .allowMainThreadQueries()
                .createFromAsset(ASSETS_DATABASE)
                .fallbackToDestructiveMigration()
                .build();
    }

    public void

    createFromAsset() {
        castleDb = Room.databaseBuilder(this, CastleDb.class, DATABASE_NAME)
                .allowMainThreadQueries()
                .createFromAsset(ASSETS_DATABASE)
                .build();
    }

    public static App getInstance() {
        return instance;
    }

    public CastleDb getCastleDb() {
        return castleDb;
    }


}
