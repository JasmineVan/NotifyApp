package com.example.project2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class Dashboard extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private HomeFragment homeFragment = new HomeFragment();
    private SettingFragment settingFragment = new SettingFragment();
    private FavoriteFragment favoriteFragment = new FavoriteFragment();
    private UserFragment userFragment = new UserFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        bottomNavigationView = findViewById(R.id.dashboard_btmNav);
        getSupportFragmentManager().beginTransaction().replace(R.id.dashboard_container1, homeFragment).commit();

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.btm_nav_home:
                        getSupportFragmentManager().beginTransaction().replace(R.id.dashboard_container1, homeFragment).commit();
                        return true;
                    case R.id.btm_nav_favorite:
                        getSupportFragmentManager().beginTransaction().replace(R.id.dashboard_container1, favoriteFragment).commit();
                        return true;
                    case R.id.btm_nav_user:
                        getSupportFragmentManager().beginTransaction().replace(R.id.dashboard_container1, userFragment).commit();
                        return true;
                    case R.id.btm_nav_setting:
                        getSupportFragmentManager().beginTransaction().replace(R.id.dashboard_container1, settingFragment).commit();
                        return true;
                }
                return false;
            }
        });
    }
}