package com.example.project2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OTPRecover extends AppCompatActivity {

    private EditText otpRe1, otpRe2, otpRe3, otpRe4, otpRe5, otpRe6;
    private Button btnOTPReVerify;
    private TextView otpReMinute, otpReSecond, textOTPReResend;
    private Boolean isExpire = false;
    private ProgressDialog dialog;
    private String verifyId;
    private CountDownTimer countDown;
    private String phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otprecover);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        dialog = new ProgressDialog(OTPRecover.this);
        otpRe1 = findViewById(R.id.otpRe1);
        otpRe2 = findViewById(R.id.otpRe2);
        otpRe3 = findViewById(R.id.otpRe3);
        otpRe4 = findViewById(R.id.otpRe4);
        otpRe5 = findViewById(R.id.otpRe5);
        otpRe6 = findViewById(R.id.otpRe6);
        otpReMinute = findViewById(R.id.otpReMinute);
        otpReSecond = findViewById(R.id.otpReSecond);
        textOTPReResend = findViewById(R.id.textOTPReResend);
        btnOTPReVerify = findViewById(R.id.btnOTPReVerify);
        verifyId = getIntent().getStringExtra("verification");
        phoneNumber = getIntent().getStringExtra("phoneNumber");

        countDown();
        resendCountDown();
        setupOTPInput();

        btnOTPReVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isExpire){
                    sendOTP();
                } else{
                    Toast.makeText(OTPRecover.this,"Code is expired, please enter resend", Toast.LENGTH_SHORT).show();
                }
            }
        });

        textOTPReResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getOtp();
                resendCountDown();
                countDown();
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

    public void getOtp(){
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+84" + phoneNumber.substring(1),
                30,
                TimeUnit.SECONDS,
                OTPRecover.this,
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks(){

                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        System.out.println("Ok2");
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        Toast.makeText(OTPRecover.this,e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        verifyId = verificationId;
                    }
                }
        );
    }

    public void sendOTP(){
        if(otpRe1.getText().toString().trim().isEmpty()
                || otpRe2.getText().toString().trim().isEmpty()
                || otpRe3.getText().toString().trim().isEmpty()
                || otpRe4.getText().toString().trim().isEmpty()
                || otpRe5.getText().toString().trim().isEmpty()
                || otpRe6.getText().toString().trim().isEmpty()){
            Toast.makeText(OTPRecover.this, "Please enter valid code", Toast.LENGTH_SHORT).show();
            return;
        }
        String code = otpRe1.getText().toString() + otpRe2.getText().toString() + otpRe3.getText().toString()
                + otpRe4.getText().toString() + otpRe5.getText().toString() + otpRe6.getText().toString();
        if(verifyId != null){
            loading(true);
            PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(verifyId, code);
            FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            loading(false);
                            if(task.isSuccessful()){
                                Intent intent = new Intent(OTPRecover.this, PasswordRecover.class);
                                intent.putExtra("phoneNumber", phoneNumber);
                                startActivity(intent);
                                finish();
                            }else{
                                Toast.makeText(OTPRecover.this,"The verification code is invalid", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                    });
        }
    }

    public void countDown(){
        if(countDown != null){
            countDown.cancel();
        }
        countDown = new CountDownTimer(120000, 1000) {
            long minute, second;
            String minuteText, secondText;
            public void onTick(long millisUntilFinished) {
                minute = millisUntilFinished / 60000;
                second = (millisUntilFinished - minute * 60000) / 1000;
                minuteText = String.valueOf(minute);
                secondText = String.valueOf(second);
                if(minuteText.length() == 1){
                    minuteText = "0" + minuteText;
                }
                if(secondText.length() == 1){
                    secondText = "0" + secondText;
                }
                otpReMinute.setText(minuteText);
                otpReSecond.setText(secondText);
            }
            public void onFinish() {
                isExpire = true;
            }
        };
        countDown.start();
    }

    public void resendCountDown(){
        textOTPReResend.setEnabled(false);
        new CountDownTimer(20000, 1000) {
            String second;
            public void onTick(long millisUntilFinished) {
                second = String.valueOf(millisUntilFinished / 1000);
                if(second.length() == 1){
                    second = "0"+second;
                }
                textOTPReResend.setText(second+"s");
            }
            public void onFinish() {
                textOTPReResend.setText("Resend");
                textOTPReResend.setEnabled(true);
                isExpire = false;
            }
        }.start();
    }

    public void loading(boolean isLoad){
        if(isLoad && !dialog.isShowing()){
            Log.e("s","wait");
            dialog.setMessage("Loading. Please wait...");
            dialog.show();
            dialog.setCanceledOnTouchOutside(false);
        }else{
            Log.e("s","complete");
            dialog.cancel();
        }
    }
}
