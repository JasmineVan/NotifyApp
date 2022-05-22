package com.example.project2;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

public class MainActivity extends AppCompatActivity {

    private Button btnLandingNext;
    private SharedPreferences sharedPreferences;
    private String accessToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        btnLandingNext = findViewById(R.id.btnLandingNext);
        sharedPreferences = getSharedPreferences("Login", MODE_PRIVATE);
        accessToken = sharedPreferences.getString("accessToken","");

        btnLandingNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                System.out.println(accessToken);
                if (accessToken.isEmpty()){
                    intent = new Intent(MainActivity.this, LoginPage.class);
                }else{
                    intent = new Intent(MainActivity.this, Dashboard.class);
                }
                startActivity(intent);
            }
        });
    }
}