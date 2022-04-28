package com.example.squash;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class NavigationActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    FrameLayout container;
    Fragment homeFragment, filesFragment, profileFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(navigationItemSelectedListener);
        bottomNavigationView.setSelectedItemId(R.id.home);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, new HomeFragment())
                .commit();
    }

    private NavigationBarView.OnItemSelectedListener navigationItemSelectedListener = new NavigationBarView.OnItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;
            switch (item.getItemId()) {
                case R.id.home:
                    selectedFragment = new HomeFragment();
                    break;
                case R.id.files:
                    selectedFragment = new FilesFragment();
                    break;
                case R.id.profile:
                    selectedFragment = new ProfileFragment();
                    break;

            }
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, selectedFragment)
                    .commit();
            return true;
        }
    };
}
