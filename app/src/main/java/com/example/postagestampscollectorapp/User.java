package com.example.postagestampscollectorapp;

public class User {

    int userId;
    String username;
    String password;
    String email;
    String telephone;
    String shortDesciption;

    public User(int userId, String username, String password, String email, String telephone, String shortDesciption) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.email = email;
        this.telephone = telephone;
        this.shortDesciption = shortDesciption;
    }
}
