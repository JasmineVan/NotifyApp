package com.example.project2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

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
    public TextView sendOtpError;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_otp);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        FirebaseApp.initializeApp(this);

        textSendOtpPhone = findViewById(R.id.textSendOtpPhone);
        ivSendOtpBack = findViewById(R.id.ivSendOtpBack);
        textSendOtpSkip = findViewById(R.id.textSendOtpSkip);
        sendOtpError = findViewById(R.id.sendOtpError);
        btnSendOtp = findViewById(R.id.btnSendOtp);
        btnSendOtpChangePhone = findViewById(R.id.btnSendOtpChangePhone);
        sendOtpError.setVisibility(View.INVISIBLE);

        sharedPreferences = this.getSharedPreferences("Login", MODE_PRIVATE);
        String accessToken = sharedPreferences.getString("accessToken", "defaultToken");

        getPhoneNumber(accessToken);
        btnSendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println(textSendOtpPhone.getText().toString().substring(1));
                getOtp();
            }
        });
        textSendOtpSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(SendOTP.this, Dashboard.class);
                startActivity(intent1);
            }
        });
        ivSendOtpBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent2 = new Intent(SendOTP.this, SignupPage.class);
                startActivity(intent2);
            }
        });
        btnSendOtpChangePhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent3 = new Intent(SendOTP.this, SignupPage.class);
                startActivity(intent3);
            }
        });
    }

    public void getOtp(){
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+84" + textSendOtpPhone.getText().toString().substring(1),
                60,
                TimeUnit.SECONDS,
                SendOTP.this,
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks(){

                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        System.out.println("Ok2");
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        System.out.println(e.getMessage());
                        Toast.makeText(SendOTP.this,e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        Intent intent = new Intent(SendOTP.this, OTP.class);
                        intent.putExtra("mobile", textSendOtpPhone.getText().toString());
                        intent.putExtra("verification", verificationId);
                        startActivity(intent);
                    }
                }
        );
    }

    public void getPhoneNumber(String accessToken){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url("https://note-app-lake.vercel.app/users/profile").header("Authorization", "Bear " + accessToken).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.d("onFailure", e.getMessage());
            }
            @Override
            public void onResponse(Call call, final Response response)
                    throws IOException {
                try {
                    String responseData = response.body().string();
                    int code = response.code();
                    JSONObject json = new JSONObject(responseData);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(code == 200){
                                try {
                                    String phoneNumber = json.getString("phoneNumber");
                                    textSendOtpPhone.setText(phoneNumber);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            else {
                                try {
                                    String error = json.getString("error");
                                    Toast.makeText(SendOTP.this, error,Toast.LENGTH_SHORT).show();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                } catch (JSONException e) {
                    Log.d("onResponse", e.getMessage());
                }
            }
        });
    }
}

