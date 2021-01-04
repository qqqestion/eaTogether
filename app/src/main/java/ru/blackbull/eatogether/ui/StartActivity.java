package ru.blackbull.eatogether.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import ru.blackbull.eatogether.R;
import ru.blackbull.eatogether.ui.viewmodels.PlaceViewModel;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        Button loginBtn = findViewById(R.id.profile_change_photo_btn);
        Button registerBtn = findViewById(R.id.welcome_register_btn);

        loginBtn.setOnClickListener(view -> {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        });
        registerBtn.setOnClickListener(view -> {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        });
    }
}