package com.example.project2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class ForgotPassword extends AppCompatActivity {

    private Button btnRecover;
    private EditText textRecoverPhone;
    private TextView sendOtpReError;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        dialog = new ProgressDialog(ForgotPassword.this);
        btnRecover = findViewById(R.id.btnRecover);
        sendOtpReError = findViewById(R.id.sendOtpReError);
        textRecoverPhone = findViewById(R.id.textRecoverPhone);

        btnRecover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(textRecoverPhone.getText().toString().trim().length() != 10){
                    sendOtpReError.setVisibility(View.VISIBLE);
                    sendOtpReError.setText("Phone number invalid");
                }
                else{
                    getOtp();
                }
            }
        });
    }

    public void getOtp(){
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+84" + textRecoverPhone.getText().toString().substring(1),
                30,
                TimeUnit.SECONDS,
                ForgotPassword.this,
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        loading(false);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        loading(false);
                        Toast.makeText(ForgotPassword.this,e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        loading(false);
                        Intent intent = new Intent(ForgotPassword.this, OTPRecover.class);
                        intent.putExtra("phoneNumber", textRecoverPhone.getText().toString());
                        intent.putExtra("verification", verificationId);
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
            dialog.setCanceledOnTouchOutside(false);
        }else{
            Log.e("s","complete");
            dialog.cancel();
        }
    }
}