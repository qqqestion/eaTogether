package com.emoji.eatogether.db;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class Party extends DatabaseModel {
    public static String DB_PREFIX = "parties";

    public Party() {
        super(null);
    }

    public Party(DatabaseReference partyRef) {
        super(partyRef);
    }

    public void addUser(String userID) {
        modelRef.child("userArray").child(userID).setValue(true);
    }

    public void addTitle(String title) {
        modelRef.child("title").setValue(title);
    }

    public void addDescription(String description) {
        modelRef.child("description").setValue(description);
    }

    public void addTime(LocalDateTime time) {
        modelRef.child("time").setValue(time);
    }

    @Override
    public void save() {
    }

    @Override
    public String toString() {
        return modelRef.getKey();
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();

        return result;
    }

    public String getTime() {
        return "Hello";
    }
}