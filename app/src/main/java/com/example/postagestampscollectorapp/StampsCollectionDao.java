package com.example.postagestampscollectorapp;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface StampsCollectionDao {

    @Insert
    public void addStampCollection(StampsCollection stampsCollection);

    @Query("SELECT * FROM Collections WHERE id = :collectionId AND userId = :userId")
    public StampsCollection getChosenStampCollection(int collectionId, int userId);

    @Query("SELECT * FROM Collections WHERE userId = :userId")
    public List<StampsCollection> getAllUserStampCollections(int userId);
}
