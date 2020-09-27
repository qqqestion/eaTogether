package com.emoji.eatogether;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Entity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ReceiveDataActivity extends AppCompatActivity {
    private ArrayList<String> mUsernames;
    private DatabaseReference mRootRef;
    private ListView mListView;
    private Button mRegisterBtn;

    public final static String OBJECT_KEY = "objectKey";

    private String TAG = "--> DEBUG: ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive_data);

        mListView = findViewById(R.id.list_view);
        mUsernames = new ArrayList<>();
        mRegisterBtn = findViewById(R.id.register);
        mRootRef = FirebaseDatabase.getInstance().getReference();
//        registerManyUsers();

        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickRegister(view);
            }
        });

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mUsernames);

        mListView.setAdapter(arrayAdapter);
        mRootRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                User value = snapshot.getValue(User.class);
                Log.d(TAG, "Added value: " + value);
                if (value != null) {
                    mUsernames.add(value.toString());
                    arrayAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "Failed to load data");
            }
        });

    }

    public void onClickRegister(View view) {
        Intent intent = new Intent(this, SendDataActivity.class);
        startActivity(intent);
    }

    public void registerManyUsers() {
        String[] names = {
                "Liam",
                "Noah",
                "Oliver",
                "William",
                "Elijah",
                "James",
                "Benjamin",
                "Lucas",
                "Mason",
                "Ethan",
                "Alexander",
                "Henry",
                "Jacob",
                "Michael",
                "Daniel",
                "Logan",
                "Jackson",
                "Sebastian",
                "Jack",
                "Aiden"
        };
        for (int i = 0; i < 20; i++) {
            DatabaseReference mRefChild = mRootRef.push();
            String key = mRefChild.getKey();
            Log.d(TAG, "Key: " + key);
            User user = new User(names[i], names[i].toLowerCase() + "@email.com");
            mRefChild.setValue(user);
        }
    }
}