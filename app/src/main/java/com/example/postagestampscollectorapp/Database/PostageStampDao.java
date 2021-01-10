package com.example.postagestampscollectorapp.Database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.postagestampscollectorapp.Data.PostageStamp;

import java.util.List;

@Dao
public interface PostageStampDao {

    @Insert
    void addPostageStamp(PostageStamp postageStampData);

    @Query("SELECT * FROM Stamps s INNER JOIN Collections c ON s.collectionId = c.id WHERE c.userId = :userId")
    public List<PostageStamp>  getAllPostageStampsByUserId(int userId);

    @Query("SELECT * FROM Stamps WHERE collectionId = :id")
    public List<PostageStamp> getCertainCollectionStamps(int id);

    @Query("SELECT * FROM Stamps WHERE id = :id")
    public PostageStamp getPostageStampById(int id);

    @Query("UPDATE Stamps SET name = :name, year = :year, country = :country, description = :description WHERE id = :id ")
    public void updateStampById(int id, String name, int year, String country, String description);

    @Query("DELETE FROM Stamps WHERE id = :id")
    public void deleteStampById(int id);

    @Query("DELETE FROM Stamps")
    public void deleteAllStamps();

    @Query ("DELETE FROM Stamps WHERE collectionId = :collectionId")
    public void deleteStampsByCollectionId(int collectionId);
}
