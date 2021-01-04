package ru.blackbull.eatogether.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import ru.blackbull.eatogether.R;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private final String TAG = "LoginActivity: ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        System.out.println("Hello, World!");

        Button cancelBtn = findViewById(R.id.login_cancel_btn);
        Button loginBtn = findViewById(R.id.login_confirm_btn);
        mAuth = FirebaseAuth.getInstance();

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickCancel(view);
            }
        });
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickLogin(view);
            }
        });
    }

    public void onClickCancel(View view) {
        finish();
    }

    public void onClickLogin(View view) {
        EditText emailField = findViewById(R.id.login_email_field);
        EditText passwordField = findViewById(R.id.login_password_field);
        String email = emailField.getText().toString();
        String password = passwordField.getText().toString();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            Log.d(TAG, "signInWithEmail:success -> username: " + user.getEmail());
                            Intent intent = new Intent(getApplication(), ProfileActivity.class);
                            startActivity(intent);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.d(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(getApplication(), "Authentication failed. Try again",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}