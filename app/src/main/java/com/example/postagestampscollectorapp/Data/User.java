package com.example.postagestampscollectorapp.Data;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Users")
public
class User {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    int userId;
    @ColumnInfo(name = "fullname")
    String fullName;
    @ColumnInfo(name="username")
    String username;
    @ColumnInfo(name="password")
    String password;
    @ColumnInfo(name="email")
    String email;
    @ColumnInfo(name="phone")
    String phone;
    @ColumnInfo(name="description")
    String shortBio;


    public User(String fullName, String username, String password, String email, String phone, String shortBio) {
        this.fullName = fullName;
        this.username = username;
        this.password = password;
        this.email = email;
        this.phone = phone;
        this.shortBio = shortBio;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getShortBio() {
        return shortBio;
    }

    public void setShortBio(String shortBio) {
        this.shortBio = shortBio;
    }
}
