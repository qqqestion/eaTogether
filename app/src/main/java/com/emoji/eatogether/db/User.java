package com.emoji.eatogether.db;

import androidx.annotation.Nullable;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class User extends DatabaseModel {
    public static String DB_PREFIX = "users";

    private static final String FIRST_NAME_PREFIX = "firstName";
    private static final String LAST_NAME_PREFIX = "lastName";
    private static final String BIRTHDAY_PREFIX = "birthday";
    private static final String DESCRIPTION_PREFIX = "description";

    private String firstName;
    private String lastName;
    private String birthday;
    private String description;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
        this(null);
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
        return firstName + ' ' + lastName;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();

        result.put(FIRST_NAME_PREFIX, firstName);
        result.put(LAST_NAME_PREFIX, lastName);
        result.put(BIRTHDAY_PREFIX, birthday);
        result.put(DESCRIPTION_PREFIX, description);

        return result;
    }

    public void fromMap(Map<String, Object> data) {
        firstName = (String) data.get(FIRST_NAME_PREFIX);
        lastName = (String) data.get(LAST_NAME_PREFIX);
        birthday = (String) data.get(BIRTHDAY_PREFIX);
        description = (String) data.get(DESCRIPTION_PREFIX);
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}