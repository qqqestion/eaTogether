package com.zolax.testgooglemapsapi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.maps.GoogleMap;

import java.util.prefs.PreferencesFactory;

public class InputEventInformationActivity extends AppCompatActivity {
    EditText title;
    EditText sportType;
    EditText authorName;
    Button submit;
    GoogleMap map;
    SharedPreferences preferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_event_information);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        final SharedPreferences.Editor editor = preferences.edit();
        editor.clear().apply();
        submit = findViewById(R.id.submit);
        title = findViewById(R.id.title);
        sportType = findViewById(R.id.sport_type);
        authorName = findViewById(R.id.author_name);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putString("title",title.getText().toString());
                editor.putString("sportType",sportType.getText().toString());
                editor.putString("authorName",authorName.getText().toString());
                editor.commit();
                finish();
            }
        });
    }
}
