package com.emoji.eatogether.db;

import com.google.firebase.database.DatabaseReference;

public abstract class DatabaseModel {
    public static String DB_PREFIX;
    protected DatabaseReference modelRef;

    public DatabaseModel(DatabaseReference modelRef) {
        this.modelRef = modelRef;
    }

    public abstract void save();
}
