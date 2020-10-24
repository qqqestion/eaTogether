package com.emoji.eatogether;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.emoji.eatogether.db.User;
import com.emoji.eatogether.validators.UserValidator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditProfileActivity extends AppCompatActivity {
    private static final String TAG = "EditProfileActivity";
    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private EditText birthdayEditText;
    private EditText emailEditText;
    private EditText descriptionEditText;
    private FirebaseUser user;
    private DatabaseReference userRef;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        user = FirebaseAuth.getInstance().getCurrentUser();
        userRef = FirebaseDatabase.getInstance().getReference(User.DB_PREFIX).child(user.getUid());

        emailEditText = findViewById(R.id.email_input);
        emailEditText.setText(user.getEmail());

        firstNameEditText = findViewById(R.id.first_name_input);
        lastNameEditText = findViewById(R.id.last_name_input);
        birthdayEditText = findViewById(R.id.birthday_input);
        descriptionEditText = findViewById(R.id.description_input);

        ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User dataUser = snapshot.getValue(User.class);
                firstNameEditText.setText(dataUser.getFirstName());
                lastNameEditText.setText(dataUser.getLastName());
                birthdayEditText.setText(dataUser.getBirthday());
                descriptionEditText.setText(dataUser.getDescription());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "loadPost:onCancelled", error.toException());
            }
        };
        userRef.addListenerForSingleValueEvent(userListener);

        Button signOutBtn = findViewById(R.id.btn_sign_out);
        signOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickSignOut(view);
            }
        });

        ImageButton cancelBtn = findViewById(R.id.edit_profile_back_btn);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        ImageButton saveBtn = findViewById(R.id.edit_profile_save_btn);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickSave(view);
            }
        });
    }

    private void onClickSave(View view) {
        User dataUser = new User(userRef);
        dataUser.addFirstName(firstNameEditText.getText().toString());
        dataUser.addLastName(lastNameEditText.getText().toString());
        dataUser.addBirthday(birthdayEditText.getText().toString());
        dataUser.addDescription(descriptionEditText.getText().toString());
        UserValidator validator = new UserValidator();
        if (validator.isValid(dataUser)) {
            dataUser.save();
            Toast.makeText(getApplicationContext(), "Вы успешно обновили информацию о себе", Toast.LENGTH_SHORT).show();
        }
    }

    private void onClickSignOut(View view) {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(getApplication(), WelcomeActivity.class);
        startActivity(intent);
    }
}
