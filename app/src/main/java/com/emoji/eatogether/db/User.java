package com.emoji.eatogether.db;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.Date;

@IgnoreExtraProperties
public class User extends DatabaseModel {
    public static String DB_PREFIX = "users";

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
        super(null);
    }

    public User(DatabaseReference modelRef) {
        super(modelRef);
    }

    public void addEmail(String email) {
        modelRef.child("email").setValue(email);
    }

    public void addFirstName(String firstName) {
        modelRef.child("firstName").setValue(firstName);
    }

    public void addLastName(String lastName) {
        modelRef.child("lastName").setValue(lastName);
    }

    public void addBirthday(Date birthday) {
        modelRef.child("birthday").setValue(birthday);
    }

    public void addDescription(String description) {
        modelRef.child("description").setValue(description);
    }

    @Override
    public void save() {

    }

    @Override
    public String toString() {
        return modelRef.getKey();
    }
}