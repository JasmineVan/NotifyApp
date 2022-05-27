package com.example.project2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OTP extends AppCompatActivity {

    private Button btnOTPVerify;
    private TextView textOTPResend, otpMinute, otpSecond;
    private EditText otp1, otp2, otp3, otp4, otp5, otp6;
    private String verifyId;
    private String phoneNumber;
    private Boolean isExpire = false;
    private CountDownTimer countDown;
    private SharedPreferences sharedPreferences;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        sharedPreferences = getSharedPreferences("Login", Context.MODE_PRIVATE);
        dialog = new ProgressDialog(OTP.this);
        btnOTPVerify = findViewById(R.id.btnOTPVerify);
        textOTPResend = findViewById(R.id.textOTPResend);
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
        resendCountDown();
        setupOTPInput();

        btnOTPVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isExpire){
                    sendOTP();
                }
                else{
                    Toast.makeText(OTP.this,"Code is expired, please enter resend", Toast.LENGTH_SHORT).show();
                }
            }
        });

        textOTPResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getOtp();
                resendCountDown();
                countDown();

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
            loading(true);
            PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(verifyId, code);
            FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            loading(false);
                            if(task.isSuccessful()){
                                activeUser();
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
                30,
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
                otpMinute.setText(minuteText);
                otpSecond.setText(secondText);
            }
            public void onFinish() {
                isExpire = true;
            }
        };
        countDown.start();
    }

    public void resendCountDown(){
        textOTPResend.setEnabled(false);
        new CountDownTimer(20000, 1000) {
            String second;
            public void onTick(long millisUntilFinished) {
                second = String.valueOf(millisUntilFinished / 1000);
                if(second.length() == 1){
                    second = "0"+second;
                }
                textOTPResend.setText(second+"s");
            }
            public void onFinish() {
                textOTPResend.setText("Resend");
                textOTPResend.setEnabled(true);
                isExpire = false;
            }
        }.start();
    }

    public void activeUser() {
        String accessToken = sharedPreferences.getString("accessToken", "");
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url("https://note-app-lake.vercel.app/users/active").header("Authorization", "Bear " + accessToken).build();
        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.d("onFailure", e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                try {
                    String responseData = response.body().string();
                    JSONObject json = new JSONObject(responseData);
                    int code = response.code();
                    runOnUiThread(new Runnable(){
                        @Override
                        public void run(){
                           if(code == 200){
                               Intent intent = new Intent(getApplicationContext(), Dashboard.class);
                               intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                               startActivity(intent);
                               finish();
                           }else{
                               Toast.makeText(OTP.this,"Active failed", Toast.LENGTH_SHORT).show();
                           }
                        }
                    });
                } catch (JSONException e) {
                    Log.d("onResponse", e.getMessage());
                }
            }
        });
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