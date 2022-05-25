package com.example.project2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SendOTP extends AppCompatActivity {

    private TextView textSendOtpPhone;
    private ImageView ivSendOtpBack;
    private Button btnSendOtp, btnSendOtpChangePhone;
    private TextView textSendOtpSkip;
    private TextView sendOtpError;
    private String phoneNumber;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_otp);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        FirebaseApp.initializeApp(this);

        dialog = new ProgressDialog(SendOTP.this);
        textSendOtpPhone = findViewById(R.id.textSendOtpPhone);
        ivSendOtpBack = findViewById(R.id.ivSendOtpBack);
        textSendOtpSkip = findViewById(R.id.textSendOtpSkip);
        sendOtpError = findViewById(R.id.sendOtpError);
        btnSendOtp = findViewById(R.id.btnSendOtp);
        btnSendOtpChangePhone = findViewById(R.id.btnSendOtpChangePhone);
        ivSendOtpBack.setVisibility(View.INVISIBLE);

        phoneNumber = getIntent().getStringExtra("phoneNumber");
        textSendOtpPhone.setText(phoneNumber);

        sendOtpError.setVisibility(View.INVISIBLE);

        btnSendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loading(true);
                getOtp();
            }
        });
        textSendOtpSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(SendOTP.this, Dashboard.class);
                startActivity(intent1);
                finish();
            }
        });
        ivSendOtpBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent2 = new Intent(SendOTP.this, SignupPage.class);
                startActivity(intent2);
            }
        });
        btnSendOtpChangePhone.setVisibility(View.INVISIBLE);
    }

    public void getOtp(){
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+84" + textSendOtpPhone.getText().toString().substring(1),
                30,
                TimeUnit.SECONDS,
                SendOTP.this,
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        loading(false);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        loading(false);
                        Toast.makeText(SendOTP.this,e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        loading(false);
                        Intent intent = new Intent(SendOTP.this, OTP.class);
                        intent.putExtra("mobile", textSendOtpPhone.getText().toString());
                        intent.putExtra("verification", verificationId);
                        intent.putExtra("phoneNumber", phoneNumber);
                        startActivity(intent);
                        finish();
                    }
                }
        );
    }

    public void loading(boolean isLoad){
        if(isLoad && !dialog.isShowing()){
            Log.e("s","wait");
            dialog.setMessage("Loading. Please wait...");
            dialog.show();
        }else{
            Log.e("s","complete");
            dialog.cancel();
        }
    }
}

