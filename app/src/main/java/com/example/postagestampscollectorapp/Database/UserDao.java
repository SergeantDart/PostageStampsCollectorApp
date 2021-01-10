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

    @Query("SELECT * FROM Users WHERE id = :userId")
    public User getUserById(int userId);

    @Query("SELECT COUNT(id) FROM Users WHERE username = :username AND password = :password")
    public int searchIfAccountExists(String username, String password);

    @Query("SELECT id FROM Users WHERE username = :username AND password = :password")
    public int getUserId(String username, String password);

    @Query("SELECT id FROM Users WHERE username = :username")
    public int getSearchedUserId(String username);

    @Query("UPDATE Users SET fullname = :fullName, username = :username, password = :password, email= :email, phone= :telephone, description = :shortBio WHERE id = :userId")
    public void updateUser(int userId, String fullName, String username, String password, String email, String telephone, String shortBio);

    @Query("DELETE FROM Users")
    public void deleteAllUsers();


}

