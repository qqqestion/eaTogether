package ru.blackbull.eatogether.db;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public abstract class DatabaseModel {
    protected DatabaseReference modelRef;
    protected String id;

    public DatabaseModel(String id, String databasePrefix) {
        this.id = id;
        this.modelRef = FirebaseDatabase.getInstance("https://eatogether-a8490.firebaseio.com/").getReference(databasePrefix).child(id);
    }

    public DatabaseModel(DatabaseReference modelRef) {
        this.modelRef = modelRef;
    }

    public abstract void save();

    public DatabaseReference getRef() {
        return modelRef;
    }
}
