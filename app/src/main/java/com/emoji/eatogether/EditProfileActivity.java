package com.emoji.eatogether;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.emoji.eatogether.db.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EditProfileActivity extends AppCompatActivity {
    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private EditText birthdayEditText;
    private EditText emailEditText;
    private EditText descriptionEditText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        firstNameEditText = findViewById(R.id.first_name);
        lastNameEditText = findViewById(R.id.last_name);
        birthdayEditText = findViewById(R.id.birthday);
        emailEditText = findViewById(R.id.email);
        descriptionEditText = findViewById(R.id.description);

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
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        // TODO: getUid не советуют использовать в качестве ключа. Изменить
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference(User.DB_PREFIX).child(user.getUid());
    }

    private void onClickSignOut(View view) {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(getApplication(), WelcomeActivity.class);
        startActivity(intent);
    }
}
