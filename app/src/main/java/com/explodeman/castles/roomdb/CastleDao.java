package com.explodeman.castles.roomdb;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.RawQuery;
import androidx.sqlite.db.SupportSQLiteQuery;

import com.explodeman.castles.models.Castle;

import java.util.List;

@Dao
public interface CastleDao {

    @RawQuery
    List<Castle> get(SupportSQLiteQuery query);

//    @Query("SELECT * FROM castle")
//    List<Castle> get();

    @Query("SELECT * FROM castle WHERE id = :id")
    Castle getById(long id);

//    @Query("SELECT TOP(1) castle WHERE name = ':name'")
//    Castle getByName(String name);

    @Insert
    void add(Castle... castle);

//    @Update
//    void update(Castle... castle);
//
//    @Delete
//    void remove(Castle... castle);

}
