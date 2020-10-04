package com.emoji.eatogether;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
//        final TextView emailTextView = findViewById(R.id.email);
//        final TextView nameTextView = findViewById(R.id.name);
//        final TextView surnameTextView = findViewById(R.id.surname);
//        final TextView birthdayTextView = findViewById(R.id.birthday);
//        final TextView descriptionTextView = findViewById(R.id.description);
//
//        FirebaseUser user = mAuth.getCurrentUser();
//        if (user == null) {
//            Intent intent = new Intent(getApplication(), LoginActivity.class);
//            startActivity(intent);
//        }
//        emailTextView.setText(user.getEmail());
//        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());
//        userRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                User userData = snapshot.getValue(User.class);
//                nameTextView.setText(userData.name);
//                surnameTextView.setText(userData.surname);
//                birthdayTextView.setText(userData.birthday);
//                descriptionTextView.setText(userData.description);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
    }
}