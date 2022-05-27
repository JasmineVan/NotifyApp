package com.example.project2;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class OTPRecover extends AppCompatActivity {

    private EditText otpRe1, otpRe2, otpRe3, otpRe4, otpRe5, otpRe6;
    private Button btnOTPReVerify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otprecover);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        otpRe1 = findViewById(R.id.otpRe1);
        otpRe2 = findViewById(R.id.otpRe2);
        otpRe3 = findViewById(R.id.otpRe3);
        otpRe4 = findViewById(R.id.otpRe4);
        otpRe5 = findViewById(R.id.otpRe5);
        otpRe6 = findViewById(R.id.otpRe6);
        btnOTPReVerify = findViewById(R.id.btnOTPReVerify);

        setupOTPInput();
        btnOTPReVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OTPRecover.this, PasswordRecover.class);
                startActivity(intent);
            }
        });
    }

    private void setupOTPInput(){
        otpRe1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (otpRe2.getText().toString().trim().isEmpty() && !otpRe1.getText().toString().trim().isEmpty()){
                    otpRe2.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        otpRe2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (otpRe3.getText().toString().trim().isEmpty() && !otpRe2.getText().toString().trim().isEmpty()){
                    otpRe3.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        otpRe3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (otpRe4.getText().toString().trim().isEmpty() && !otpRe3.getText().toString().trim().isEmpty()){
                    otpRe4.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        otpRe4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (otpRe5.getText().toString().trim().isEmpty() && !otpRe4.getText().toString().trim().isEmpty()){
                    otpRe5.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        otpRe5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (otpRe6.getText().toString().trim().isEmpty() && !otpRe5.getText().toString().trim().isEmpty()){
                    otpRe6.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

}