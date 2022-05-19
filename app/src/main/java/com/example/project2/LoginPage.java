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
import android.widget.Toast;

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

public class LoginPage extends AppCompatActivity {

    private Button btnLogin;
    private EditText textLoginPhoneNumber, textLoginPassword;
    private TextView textForgetPass, textCreateAccount;
    private ImageView ivLoginBack;
    public TextView loginError;
    private SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        textLoginPhoneNumber = findViewById(R.id.textLoginPhoneNumber);
        textLoginPassword = findViewById(R.id.textLoginPassword);

        btnLogin = findViewById(R.id.btnLogin);
        ivLoginBack = findViewById(R.id.ivLoginBack);
        textCreateAccount = findViewById(R.id.textCreateAccount);
        loginError = findViewById(R.id.loginError);
        loginError.setVisibility(View.INVISIBLE);

        textCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginPage.this, SignupPage.class);
                startActivity(intent);
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = checkSignIn();
                if(!msg.equals("")){
                    Toast.makeText(LoginPage.this,msg,Toast.LENGTH_SHORT).show();
                }else{
                    signInApi();
                }
            }
        });
        ivLoginBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent2 = new Intent(LoginPage.this, MainActivity.class);
                startActivity(intent2);
            }
        });
        nextField();
    }

    public void signInApi() {
        OkHttpClient client = new OkHttpClient();
        String createStudentURL = "https://note-app-lake.vercel.app/users/signIn";
        RequestBody formBody = new FormBody.Builder()
                .add("phoneNumber", textLoginPhoneNumber.getText().toString().trim())
                .add("password", textLoginPassword.getText().toString().trim())
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
                            if (code == 200) { //If request success, set error invisible, save access token to local
                                try {
                                    String accessToken = json.getString("accessToken");
                                    sharedPreferences = getSharedPreferences("Login", MODE_PRIVATE);
                                    SharedPreferences.Editor editSharedPreferences = sharedPreferences.edit();
                                    editSharedPreferences.putString("accessToken", accessToken);
                                    editSharedPreferences.commit();

                                    Intent intent1 = new Intent(LoginPage.this, Dashboard.class);
                                    startActivity(intent1);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } else { // If request fail, set the error return
                                try {
                                    String error = json.getString("error");
                                    Toast.makeText(LoginPage.this, error, Toast.LENGTH_SHORT).show();
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

    public String checkSignIn(){
        String phoneNumber = textLoginPhoneNumber.getText().toString().trim();
        String password = textLoginPassword.getText().toString().trim();
        String msg = "";

        if(phoneNumber.length() != 10) {
            msg = "Phone number invalid";
        }else if(password.length() < 6){
            msg = "Password invalid";
        }
        return msg;
    }

    private void nextField(){
        textLoginPhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().isEmpty()){
                    textLoginPassword.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }
}