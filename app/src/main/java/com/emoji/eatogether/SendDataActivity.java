package com.emoji.eatogether;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SendDataActivity extends AppCompatActivity {

    private Button sendDataToFirebase;
    private EditText mInputUserName;
    private EditText mInputEmail;
    private DatabaseReference mRootRef;

    private String TAG = "--> DEBUG: ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_data);

        sendDataToFirebase = findViewById(R.id.add_value);
        mInputUserName = findViewById(R.id.input_username);
        mInputEmail = findViewById(R.id.input_email_address);

        mRootRef = FirebaseDatabase.getInstance().getReference();
        sendDataToFirebase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickSend(view);
            }
        });

//        mRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                String value = Objects.requireNonNull(snapshot.getValue()).toString();
//                Log.d("TAG", "Value is: " + value);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Log.w("TAG", "Failed to read value.", error.toException());
//            }
//        });


    }

    public void onClickSend(View view) {
        DatabaseReference mRefChild = mRootRef.push();
        String key = mRefChild.getKey();
        Log.d(TAG, "Key: " + key);
        User user = new User(mInputUserName.getText().toString(), mInputEmail.getText().toString());
        mRefChild.setValue(user);

        Intent intent = new Intent(this, ReceiveDataActivity.class);
        intent.putExtra(ReceiveDataActivity.OBJECT_KEY, key);
        startActivity(intent);
    }
}