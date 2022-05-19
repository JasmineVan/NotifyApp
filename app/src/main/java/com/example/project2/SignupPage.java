package com.example.project2;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SignupPage extends AppCompatActivity {

    private EditText textSignupUsername, textSignupPhone, textSignupPassword, textSignupPasswordConfirm;
    private Button btnSignup;
    private ImageView ivSignupBack;
    private TextView textSignupLoginAccount;
    public TextView signupError;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_page);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        textSignupUsername = findViewById(R.id.textSignupUsername);
        textSignupPhone = findViewById(R.id.textSignupPhone);
        textSignupPassword = findViewById(R.id.textSignupPassword);
        textSignupPasswordConfirm = findViewById(R.id.textSignupPasswordConfirm);

        btnSignup = findViewById(R.id.btnSignup);
        ivSignupBack = findViewById(R.id.ivSignupBack);
        textSignupLoginAccount = findViewById(R.id.textSignupLoginAccount);
        signupError = findViewById(R.id.signupError);
        signupError.setVisibility(View.INVISIBLE);

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = checkSignUp();
                if(!msg.equals("")){
                    signupError.setText(msg);
                    signupError.setVisibility(View.VISIBLE);
                }else{
                    signUpApi();
                }
            }
        });
        textSignupLoginAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(SignupPage.this, LoginPage.class);
                startActivity(intent1);
            }
        });
        ivSignupBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent2 = new Intent(SignupPage.this, MainActivity.class);
                startActivity(intent2);
            }
        });
        nextField();
    }

    public void signUpApi(){
        OkHttpClient client = new OkHttpClient();
        String createStudentURL = "https://note-app-lake.vercel.app/users/signUp";
        RequestBody formBody = new FormBody.Builder()
                .add("username", textSignupUsername.getText().toString().trim())
                .add("password", textSignupPassword.getText().toString().trim())
                .add("phoneNumber", textSignupPhone.getText().toString().trim())
                .build();
        Request request = new Request.Builder()
                .url(createStudentURL)
                .post(formBody)
                .build();
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
                            if(code == 200){ //If request success, set error invisible, save access token to local
                                try {
                                    signupError.setVisibility(View.INVISIBLE);
                                    String accessToken = json.getString("accessToken");
                                    sharedPreferences = getSharedPreferences("Login", MODE_PRIVATE);
                                    SharedPreferences.Editor editSharedPreferences = sharedPreferences.edit();
                                    editSharedPreferences.putString("accessToken", accessToken);
                                    editSharedPreferences.commit();

                                    Intent intent = new Intent(SignupPage.this, SendOTP.class);
                                    startActivity(intent);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            else { // If request fail, set the error return
                                try {
                                    String error = json.getString("error");
                                    signupError.setText(error);
                                    signupError.setVisibility(View.VISIBLE);
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

    public String checkSignUp(){ // Validate sign up
        String username = textSignupUsername.getText().toString().trim();
        String phoneNumber = textSignupPhone.getText().toString().trim();
        String password = textSignupPassword.getText().toString().trim();
        String passwordConfirm = textSignupPasswordConfirm.getText().toString().trim();
        String msg = "";

        if(username.equals("")){
            msg = "Missing username";
        }else if(phoneNumber.length() != 10) {
            msg = "Phone number invalid";
        }else if(password.length() < 6){
            msg = "Password invalid";
        } else if(!passwordConfirm.equals(password)){
            msg = "Password confirm invalid";
        }
        return msg;
    }

    private void nextField(){
        textSignupUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().isEmpty()){
                    textSignupPhone.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        textSignupPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().isEmpty()){
                    textSignupPassword.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        textSignupPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().isEmpty()){
                    textSignupPasswordConfirm.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }
}