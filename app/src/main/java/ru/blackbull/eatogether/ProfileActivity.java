package ru.blackbull.eatogether;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {
    private String[] titles;
    private DrawerLayout drawerLayout;
    private ListView drawerList;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mAuth = FirebaseAuth.getInstance();
        titles = getResources().getStringArray(R.array.titles);

        drawerLayout = findViewById(R.id.profile_drawer_layout);
        drawerList = findViewById(R.id.profile_drawer);
        drawerList.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, titles));
        drawerList.setOnItemClickListener(this);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.profile_fragment_container, new ProfileFragment());
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            Intent intent = new Intent(getApplication(), WelcomeActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.profile_settings:
                Intent intent = new Intent(getApplication(), EditProfileActivity.class);
                startActivity(intent);
                break;
            case R.id.profile_menu_btn:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(drawerList)) {
            drawerLayout.closeDrawer(drawerList);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//        Fragment fragment = new ProfileFragment();
//        FragmentTransaction
        drawerLayout.closeDrawer(drawerList);
    }
}