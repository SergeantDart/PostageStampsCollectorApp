package com.example.postagestampscollectorapp.Database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.postagestampscollectorapp.Data.User;

import java.util.List;

@Dao
public interface UserDao {

    @Insert
    public void addUser(User user);

    @Query("SELECT * FROM Users")
    public List<User>  getAllUsers();

    @Query("SELECT COUNT(id) FROM Users WHERE username=:username AND password=:password")
    public int searchIfAccountExists(String username, String password);

    @Query("SELECT id FROM Users WHERE username=:username AND password=:password")
    public int getUserId(String username, String password);

    @Query("SELECT id FROM Users WHERE username=:username")
    public int getSearchedUserId(String username);


}

