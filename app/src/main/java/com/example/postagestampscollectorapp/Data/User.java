package com.example.postagestampscollectorapp.Data;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Random;

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


    @Ignore
    public User() {
        Random rand = new Random();
        this.userId = ( rand.nextInt(999999) + rand.nextInt(10) - rand.nextInt(10) ) * ( rand.nextInt(5) + 1);
        this.fullName = "N/A";
        this.username = "N/A";
        this.password = "N/A";
        this.email = "N/A";
        this.phone = "N/A";
        this.shortBio = "N/A";
    }
    public User(String fullName, String username, String password, String email, String phone, String shortBio) {
        Random rand = new Random();
        this.userId = ( rand.nextInt(999999) + username.length() - password.length() ) * ( rand.nextInt(5) + 1);
        this.fullName = fullName;
        this.username = username;
        this.password = password;
        this.email = email;
        this.phone = phone;
        this.shortBio = shortBio;
    }

    @Ignore
    public User(int userId, String fullName, String username, String password, String email, String phone, String shortBio) {
        this.userId = userId;
        this.fullName = fullName;
        this.username = username;
        this.password = password;
        this.email = email;
        this.phone = phone;
        this.shortBio = shortBio;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", fullName='" + fullName + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", shortBio='" + shortBio + '\'' +
                '}';
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
