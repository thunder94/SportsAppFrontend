package com.hfad.sportsapp;

import android.net.Uri;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@IgnoreExtraProperties
public class User implements Serializable {

    private String id;
    private String email;
    private String name;
    private String surname;
    private String photo = null;
    private double rating = 0;

    public User() {

    }

    public User(String id, String email, String name, String surname) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.surname = surname;
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getPhoto() {
        return photo;
    }

    public double getRating() {
        return rating;
    }
}
