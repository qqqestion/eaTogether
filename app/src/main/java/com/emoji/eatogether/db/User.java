package com.emoji.eatogether.db;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class User extends DatabaseModel {
    public static String DB_PREFIX = "users";

    private String firstName;
    private String lastName;
    private String birthday;
    private String description;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
        super(null);
    }

    public User(DatabaseReference modelRef) {
        super(modelRef);
    }

    public void addFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void addLastName(String lastName) {
        this.lastName = lastName;
    }

    public void addBirthday(String birthday) {
        this.birthday = birthday;
    }

    public void addDescription(String description) {
        this.description = description;
    }

    @Override
    public void save() {
        modelRef.setValue(toMap());
    }

    @Override
    public String toString() {
        return modelRef.getKey();
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();

        result.put("firstName", firstName);
        result.put("lastName", lastName);
        result.put("birthday", birthday);
        result.put("description", description);

        return result;
    }
}