package com.emoji.eatogether;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.emoji.eatogether.db.Party;
import com.emoji.eatogether.db.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PartyListActivity extends AppCompatActivity {
    private String TAG = PartyListActivity.class.toString();
    private List<Party> userPartyList;
    private List<Party> allPartyList;
    private DatabaseReference partyRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_party_list);
        ListView userPartyListView = findViewById(R.id.user_party_list_view);
        ListView allPartyListView = findViewById(R.id.all_party_list_view);
        userPartyList = new ArrayList<>();
        allPartyList = new ArrayList<>();
        partyRef = FirebaseDatabase.getInstance().getReference(Party.DB_PREFIX);

        final ArrayAdapter<Party> userPartyAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                userPartyList
        );
        final ArrayAdapter<Party> allPartyAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                allPartyList

        );
        userPartyListView.setAdapter(userPartyAdapter);
        allPartyListView.setAdapter(allPartyAdapter);
        partyRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Party party = new Party(snapshot.getRef());
                party.fromMap((Map<String, Object>) snapshot.getValue());

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (party.getUserArray().containsKey(user.getUid())) {
                    userPartyList.add(party);
                    userPartyAdapter.notifyDataSetChanged();
                }
                allPartyList.add(party);
                allPartyAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Party party = new Party(snapshot.getRef());
                party.fromMap((Map<String, Object>) snapshot.getValue());
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (party.getUserArray().containsKey(user.getUid()) && !userPartyList.contains(party)) {
                    userPartyList.add(party);
                } else if (!party.getUserArray().containsKey(user.getUid()) && userPartyList.contains(party)) {
                    userPartyList.remove(party);
                }
                userPartyAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Party party = new Party(snapshot.getRef());
                party.fromMap((Map<String, Object>) snapshot.getValue());

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (party.getUserArray().containsKey(user.getUid()) && userPartyList.contains(party)) {
                    userPartyList.remove(party);
                    userPartyAdapter.notifyDataSetChanged();
                }
                allPartyList.remove(party);
                allPartyAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "Error occurred", error.toException());
            }
        });
//        Party newParty = new Party(partyRef.push());
//        newParty.addTitle("Великий поход в McDonald's");
//        newParty.addDescription("Соберемся и вкусно поедим");
//        newParty.addTime("2020-10-10");
//        newParty.save();
        userPartyListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(), PartyInfoActivity.class);
                intent.putExtra(PartyInfoActivity.EXTRA_PARTYID, userPartyList.get((int) l).getKey());
                startActivity(intent);
            }
        });
        allPartyListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(), PartyInfoActivity.class);
                intent.putExtra(PartyInfoActivity.EXTRA_PARTYID, allPartyList.get((int) l).getKey());
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        String email = "test.user@email.com";
        String password = "test1234";
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.signInWithEmailAndPassword(email, password);
    }
}