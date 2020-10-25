package com.emoji.eatogether;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.emoji.eatogether.db.Party;
import com.emoji.eatogether.db.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class PartyInfoActivity extends AppCompatActivity {
    public static final String EXTRA_PARTYID = "partyID";
    private FirebaseDatabase mDatabase;
    private List<String> userList;
    private ArrayAdapter<String> userArrayAdapter;
    private DatabaseReference partyRef;
    private Party party;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_party_info);
        mDatabase = FirebaseDatabase.getInstance();
        userList = new ArrayList<>();
        userArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, userList);
        ListView userListView = findViewById(R.id.user_array);
        userListView.setAdapter(userArrayAdapter);

        String partyID = getIntent().getExtras().getString(EXTRA_PARTYID);
        partyRef = mDatabase.getReference(Party.DB_PREFIX).child(partyID);
        party = new Party(partyRef);

        partyRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                party.fromMap((Map<String, Object>) snapshot.getValue());
                updateUI();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        Button enterPartyBtn = findViewById(R.id.enter_party_btn);
        enterPartyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickEnterParty(view);
            }
        });
    }

    private void onClickEnterParty(View view) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        party.addUser(user.getUid());
        party.save();
    }

    private void updateUI() {
        TextView titleTextView = findViewById(R.id.party_title);
        titleTextView.setText(party.getTitle());

        TextView descriptionTextView = findViewById(R.id.party_description);
        descriptionTextView.setText(party.getDescription());

        TextView timeTextView = findViewById(R.id.party_time);
        timeTextView.setText(party.getTime());

        final List<String> users = new ArrayList<>();
        userList.clear();
        for (String uid : party.getUserArray().keySet()) {
            DatabaseReference userRef = mDatabase.getReference(User.DB_PREFIX).child(uid);
            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User user = new User(snapshot.getRef());
                    user.fromMap((Map<String, Object>) snapshot.getValue());
                    userList.add(user.toString());
                    userArrayAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });
        }
    }
}