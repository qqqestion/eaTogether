package com.example.eatogether;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
//
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.auth.AuthResult;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mUsersRef;
    private final String TAG = "RegisterActivity: ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Button registerBtn = findViewById(R.id.register_confirm);
        Button cancelBtn = findViewById(R.id.register_cancel);
        mAuth = FirebaseAuth.getInstance();
        mUsersRef = FirebaseDatabase.getInstance().getReference().child("users");

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickRegister(view);
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickCancel(view);
            }
        });
    }

    public void onClickRegister(View view) {
        List<Integer> requiredFields = new ArrayList<>(Arrays.asList(
                R.id.register_email,
                R.id.register_password,
                R.id.register_password_confirmation,
                R.id.register_name,
                R.id.register_surname,
                R.id.register_birthday,
                R.id.register_description)
        );
        final Map<Integer, String> fields = new HashMap<>();

        boolean isOkay = true;
        for (int i = 0; i < requiredFields.size(); i++) {
            EditText field = findViewById(requiredFields.get(i));
            String value = field.getText().toString();
            fields.put(requiredFields.get(i), value);
            if (value.equals("")) {
                field.setError("Required");
                isOkay = false;
            }
        }
        if (!isOkay) {
            return;
        }

        if (!fields.get(R.id.register_password).equals(fields.get(R.id.register_password_confirmation))) {
            Toast.makeText(getApplication(), "Password and password confirmation must be the same", Toast.LENGTH_SHORT).show();
            return;
        }
        mAuth.createUserWithEmailAndPassword(fields.get(R.id.register_email), fields.get(R.id.register_password))
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            Intent intent = new Intent(getApplication(), ProfileActivity.class);
                            FirebaseUser user = mAuth.getCurrentUser();
                            DatabaseReference userRef = mUsersRef.child(user.getUid());
                            userRef.child("name").setValue(fields.get(R.id.register_name));
                            userRef.child("surname").setValue(fields.get(R.id.register_surname));
                            userRef.child("birthday").setValue(fields.get(R.id.register_birthday));
                            userRef.child("description").setValue(fields.get(R.id.register_description));
                            startActivity(intent);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.d(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(getApplication(), "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void onClickCancel(View view) {
        finish();
    }

    protected static boolean isDateValid(String sDate) {
//        return Pattern.matches("[0123456789]{1,2}\.", sDate);
        return true;
    }
}