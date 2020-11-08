package com.emoji.eatogether;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.emoji.eatogether.db.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference mUsersRef;
    private Button nextBtn;
    private Button backBtn;
    private final String TAG = "RegisterActivity: ";
    private boolean isFirstStepCompleted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        nextBtn = findViewById(R.id.register_next_and_confirm);
        backBtn = findViewById(R.id.register_cancel_and_back);
        mAuth = FirebaseAuth.getInstance();
        mUsersRef = FirebaseDatabase.getInstance().getReference(User.DB_PREFIX);

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickNext(view);
            }
        });
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickCancel(view);
            }
        });
    }

    public void onClickNext(View view) {
        if (!isFirstStepCompleted) {
            onStepForward();
        } else {

        List<Integer> requiredFields = new ArrayList<>(Arrays.asList(
                R.id.register_email_field,
                R.id.register_password_field,
                R.id.register_password_confirmation_field,
                R.id.register_first_name_field,
                R.id.register_last_name_field,
                R.id.register_birthday_field,
                R.id.register_description_field
        ));
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

        if (!fields.get(R.id.register_password_field).equals(fields.get(R.id.register_password_confirmation_field))) {
            Toast.makeText(getApplication(), "Password and password confirmation must be the same", Toast.LENGTH_SHORT).show();
            return;
        }
        mAuth.createUserWithEmailAndPassword(fields.get(R.id.register_email_field), fields.get(R.id.register_password_field))
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            User userData = new User(mUsersRef.child(user.getUid()));
                            userData.setFirstName(fields.get(R.id.register_first_name_field));
                            userData.setLastName(fields.get(R.id.register_last_name_field));
                            userData.setBirthday(fields.get(R.id.register_birthday_field));
                            userData.setDescription(fields.get(R.id.register_description_field));
                            userData.save();
                            Intent intent = new Intent(getApplication(), ProfileActivity.class);
                            startActivity(intent);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.d(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(getApplication(), "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        }
    }

    public void onClickCancel(View view) {
        if (isFirstStepCompleted) {
            onStepBack();
        } else {
            finish();
        }
    }

    private void onStepForward() {
        LinearLayout registerFirstStep = findViewById(R.id.register_first_step);
        registerFirstStep.setVisibility(View.GONE);

        LinearLayout registerSecondStep = findViewById(R.id.register_second_step);
        registerSecondStep.setVisibility(View.VISIBLE);
        isFirstStepCompleted = true;

        nextBtn.setText(R.string.register);
        backBtn.setText(R.string.back);
    }

    private void onStepBack() {
        LinearLayout registerFirstStep = findViewById(R.id.register_first_step);
        registerFirstStep.setVisibility(View.VISIBLE);

        LinearLayout registerSecondStep = findViewById(R.id.register_second_step);
        registerSecondStep.setVisibility(View.GONE);
        isFirstStepCompleted = false;

        nextBtn.setText(R.string.next);
        backBtn.setText(R.string.cancel);
    }

    @Override
    public void onBackPressed() {
        if (isFirstStepCompleted) {
            onStepBack();
        } else {
            super.onBackPressed();
        }
    }
}