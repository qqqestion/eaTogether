package ru.blackbull.eatogether.db;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public abstract class DatabaseModel {
    protected DatabaseReference modelRef;
    protected String id;

    public DatabaseModel(String id, String databasePrefix) {
        this.id = id;
        this.modelRef = FirebaseDatabase.getInstance().getReference(databasePrefix).child(id);
    }

    public DatabaseModel(DatabaseReference modelRef) {
        this.modelRef = modelRef;
    }

    public abstract void save();
}
