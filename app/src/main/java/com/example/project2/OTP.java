package com.example.project2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class OTP extends AppCompatActivity {

    private Button btnOTPVerify;
    private TextView textOTPResend, otpMinute, otpSecond;
    private ImageView ivOTPBack;
    private EditText otp1, otp2, otp3, otp4, otp5, otp6;
    private String verifyId;
    private String phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        btnOTPVerify = findViewById(R.id.btnOTPVerify);
        textOTPResend = findViewById(R.id.textOTPResend);
        ivOTPBack = findViewById(R.id.ivOTPBack);
        otpMinute = findViewById(R.id.otpMinute);
        otpSecond = findViewById(R.id.otpSecond);
        otp1 = findViewById(R.id.otp1);
        otp2 = findViewById(R.id.otp2);
        otp3 = findViewById(R.id.otp3);
        otp4 = findViewById(R.id.otp4);
        otp5 = findViewById(R.id.otp5);
        otp6 = findViewById(R.id.otp6);

        verifyId = getIntent().getStringExtra("verification");
        phoneNumber = getIntent().getStringExtra("phoneNumber");

        countDown();
        setupOTPInput();

        btnOTPVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendOTP();
            }
        });

        ivOTPBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(OTP.this, SendOTP.class);
                startActivity(intent1);
            }
        });

        textOTPResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getOtp();
                textOTPResend.setEnabled(false);
                new CountDownTimer(30000, 1000) {
                    public void onTick(long millisUntilFinished) {
                        textOTPResend.setText(String.valueOf(millisUntilFinished / 1000));
                    }
                    public void onFinish() {
                        textOTPResend.setText("Resend");
                        textOTPResend.setEnabled(false);
                    }
                }.start();
            }
        });
    }
    private void setupOTPInput(){
        otp1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (otp2.getText().toString().trim().isEmpty() && !otp1.getText().toString().trim().isEmpty()){
                    otp2.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        otp2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (otp3.getText().toString().trim().isEmpty() && !otp2.getText().toString().trim().isEmpty()){
                    otp3.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        otp3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (otp4.getText().toString().trim().isEmpty() && !otp3.getText().toString().trim().isEmpty()){
                    otp4.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        otp4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (otp5.getText().toString().trim().isEmpty() && !otp4.getText().toString().trim().isEmpty()){
                    otp5.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        otp5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (otp6.getText().toString().trim().isEmpty() && !otp5.getText().toString().trim().isEmpty()){
                    otp6.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    public void sendOTP(){
        if(otp1.getText().toString().trim().isEmpty()
                || otp2.getText().toString().trim().isEmpty()
                || otp3.getText().toString().trim().isEmpty()
                || otp4.getText().toString().trim().isEmpty()
                || otp5.getText().toString().trim().isEmpty()
                || otp6.getText().toString().trim().isEmpty()){
            Toast.makeText(OTP.this, "Please enter valid code", Toast.LENGTH_SHORT).show();
            return;
        }
        String code = otp1.getText().toString() + otp2.getText().toString() + otp3.getText().toString()
                + otp4.getText().toString() + otp5.getText().toString() + otp6.getText().toString();
        if(verifyId != null){
            PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(verifyId, code);
            FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }else{
                                Toast.makeText(OTP.this,"The verification code is invalid", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                    });
        }
    }

    public void getOtp(){
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+84" + phoneNumber.substring(1),
                60,
                TimeUnit.SECONDS,
                OTP.this,
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks(){

                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        System.out.println("Ok2");
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        Toast.makeText(OTP.this,e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        verifyId = verificationId;
                    }
                }
        );
    }

    public void countDown(){
        new CountDownTimer(90000, 1000) {
            public void onTick(long millisUntilFinished) {
                otpMinute.setText(String.valueOf(millisUntilFinished / 60000));
                otpSecond.setText(String.valueOf(millisUntilFinished / 1000));
            }
            public void onFinish() {
                System.out.println("Otp expired");
            }
        }.start();
    }
}