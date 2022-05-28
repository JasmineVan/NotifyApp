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

public class PasswordRecover extends AppCompatActivity {
    private EditText textNewPasswordRe, textPasswordConfirmRe;
    private Button btnPassReSave;
    private ProgressDialog dialog;
    private String phoneNumber;
    private TextView cvError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_recover);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        textNewPasswordRe = findViewById(R.id.textNewPasswordRe);
        textPasswordConfirmRe = findViewById(R.id.textPasswordConfirmRe);
        btnPassReSave = findViewById(R.id.btnPassReSave);
        cvError = findViewById(R.id.cvError);
        dialog = new ProgressDialog(PasswordRecover.this);
        phoneNumber = getIntent().getStringExtra("phoneNumber");
        System.out.println(phoneNumber);

        btnPassReSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = checkRecovery();
                if(msg.equals("")){
                    forgetPasswordAPI();
                }else{
                    cvError.setVisibility(View.VISIBLE);
                    cvError.setText(msg);
                }
            }
        });
    }

    public String checkRecovery(){
        String msg = "";
        if(textNewPasswordRe.getText().toString().trim().length() < 6){
            msg = "New password invalid";
        }
        if(!textPasswordConfirmRe.getText().toString().trim().equals(textNewPasswordRe.getText().toString().trim())){
            msg = "Password confirm invalid";
        }
        return msg;
    }

    public void forgetPasswordAPI() {
        OkHttpClient client = new OkHttpClient();
        String createStudentURL = "https://note-app-lake.vercel.app/users/forgetPassword";
        RequestBody formBody = new FormBody.Builder()
                .add("phoneNumber", phoneNumber)
                .add("newPassword", textNewPasswordRe.getText().toString().trim())
                .build();
        Request request = new Request.Builder()
                .url(createStudentURL)
                .put(formBody)
                .build();

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
                                cvError.setVisibility(View.INVISIBLE);
                                Intent intent = new Intent(PasswordRecover.this, LoginPage.class);
                                startActivity(intent);
                                finish();
                            }else{
                                Toast.makeText(PasswordRecover.this,"Recovery failed", Toast.LENGTH_SHORT).show();
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