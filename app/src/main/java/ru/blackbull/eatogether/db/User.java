package ru.blackbull.eatogether.db;

import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

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
    private Uri imageUri;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
        super((DatabaseReference) null);
    }

    // TODO: удалить этот конструктор, сейчас оставлен для активити регистрации
    public User(DatabaseReference modelRef) {
        super(modelRef);
    }

    public User(String userID) {
        super(userID, DB_PREFIX);
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

    public void addImage(Uri imageUri) {
        this.imageUri = imageUri;
    }

    @Override
    public void save() {
        modelRef.setValue(toMap());

        if (imageUri != null) {
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference ref = storage.getReference().child(FirebaseAuth.getInstance().getCurrentUser().getUid());
            UploadTask uploadTask = ref.putFile(imageUri);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(User.class.getName(), "Image upload failed");
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.d(User.class.getName(), "Image uploaded successfully");
                }
            });
        }
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