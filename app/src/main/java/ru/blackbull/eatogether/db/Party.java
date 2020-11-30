package ru.blackbull.eatogether.db;

import androidx.annotation.Nullable;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class Party extends DatabaseModel {
    public static String DB_PREFIX = "parties";

    private static final String TITLE_PREFIX = "title";
    private static final String DESCRIPTION_PREFIX = "description";
    private static final String TIME_PREFIX = "time";
    private static final String USER_ARRAY_PREFIX = "userArray";

    private String title;
    private String description;
    private String time;
    private Map<String, Boolean> userArray;
    private String placeId;

    public Party() {
        this(null);
    }

    public Party(DatabaseReference partyRef) {
        super(partyRef);
        userArray = new HashMap<>();
    }

    public void addUser(String userID) {
        userArray.put(userID, true);
    }

    public void addTitle(String title) {
        this.title = title;
    }


    public void addDescription(String description) {
        this.description = description;
    }

    public void addTime(String time) {
        this.time = time;
    }

    @Override
    public void save() {
//        modelRef.setValue(toMap());
        modelRef.updateChildren(toMap());
    }

    @Override
    public String toString() {
        return "Party{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", time='" + time + '\'' +
                ", userArray=" + userArray +
                ", id='" + id + '\'' +
                '}';
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put(TIME_PREFIX, time);
        result.put(TITLE_PREFIX, title);
        result.put(DESCRIPTION_PREFIX, description);
        result.put(USER_ARRAY_PREFIX, userArray);
        result.put("placeId", placeId);

        return result;
    }

    public void fromMap(Map<String, Object> data) {
        title = (String) data.get(TITLE_PREFIX);
        description = (String) data.get(DESCRIPTION_PREFIX);
        time = (String) data.get(TIME_PREFIX);
        userArray = (Map<String, Boolean>) data.get(USER_ARRAY_PREFIX);
        placeId = (String) data.get("placeId");
    }

    public String getId() {
        return modelRef.getKey();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Map<String, Boolean> getUserArray() {
        return userArray;
    }

    public void setUserArray(Map<String, Boolean> userArray) {
        this.userArray = userArray;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof Party) {
            return getTitle().equals(((Party) obj).getTitle());
        }
        return false;
    }
}