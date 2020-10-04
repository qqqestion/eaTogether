package com.example.eatogether;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User {

    public String name;
    public String surname;
    public String birthday;
    public String description;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String name, String surname, String birthday, String description) {
        this.name = name;
        this.surname = surname;
        this.birthday = birthday;
        this.description = description;
    }
}

