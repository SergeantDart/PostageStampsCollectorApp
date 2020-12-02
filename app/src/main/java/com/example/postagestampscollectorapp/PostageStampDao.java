package com.example.postagestampscollectorapp;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import static android.icu.text.MessagePattern.ArgType.SELECT;

@Dao
public interface PostageStampDao {

    @Insert
    void addPostageStamp(PostageStamp postageStampData);

    @Query("SELECT * FROM Stamps")
    List<PostageStamp>  getAllPostageStamps();

    @Query("SELECT * FROM Stamps WHERE collectionId = :id")
    List<PostageStamp> getCertainCollectionStamps(int id);

    @Query("SELECT * FROM Stamps WHERE id = :id")
    PostageStamp getPostageStampById(int id);

    @Query("UPDATE Stamps SET name=:name, year=:year, country=:country, description=:description WHERE id = :id ")
    void updateStampById(int id, String name, int year, String country, String description);

    @Query("DELETE FROM Stamps WHERE id=:id")
    void deleteStampById(int id);
}
