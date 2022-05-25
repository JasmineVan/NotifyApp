package com.example.project2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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

public class EditProfile extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private EditText textEditProfileUsername;
    private EditText textEditProfilePhone;
    private TextView editError;
    private TextView editSuccess;
    private Button btnEditProfileSave;
    private ProgressDialog dialog;
    private String phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        sharedPreferences = getSharedPreferences("Login", Context.MODE_PRIVATE);
        dialog = new ProgressDialog(EditProfile.this);
        textEditProfileUsername = findViewById(R.id.textEditProfileUsername);
        textEditProfilePhone = findViewById(R.id.textEditProfilePhone);
        editError = findViewById(R.id.editError);
        editSuccess = findViewById(R.id.editSuccess);
        btnEditProfileSave = findViewById(R.id.btnEditProfileSave);
        getUserInfo();

        btnEditProfileSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = checkEditProfile();
                if(msg.equals("")){
                    if(!textEditProfilePhone.getText().toString().trim().equals(phoneNumber)){
                        createAlertEditProfile("Your new phone number must active again, confirm change ?");
                    }
                    else{
                        createAlertEditProfile("Apply change");
                    }
                } else{
                    setResultMessage(msg, true);
                }

            }
        });
    }

    public void getUserInfo(){
        loading(true);
        String accessToken = sharedPreferences.getString("accessToken", "");
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url("https://note-app-lake.vercel.app/users/profile").header("Authorization", "Bear " + accessToken).build();
        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.d("onFailure", e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                loading(false);
                try {
                    String responseData = response.body().string();
                    JSONObject json = new JSONObject(responseData);
                    int code = response.code();

                    runOnUiThread(new Runnable(){
                        @Override
                        public void run(){
                            if(code == 200){
                                try {
                                    String oldPhoneNumber = json.getString("phoneNumber");
                                    String oldUsername = json.getString("username");
                                    textEditProfileUsername.setText(oldUsername);
                                    textEditProfilePhone.setText(oldPhoneNumber);
                                    phoneNumber = oldPhoneNumber;
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            else {
                                Toast.makeText(EditProfile.this,"Get info failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } catch (JSONException e) {
                    Log.d("onResponse", e.getMessage());
                }
            }
        });
    }

    public void editProfileAPI() {
        loading(true);
        OkHttpClient client = new OkHttpClient();
        String accessToken = sharedPreferences.getString("accessToken", "");
        String createStudentURL = "https://note-app-lake.vercel.app/users/updateInfo";
        RequestBody formBody = new FormBody.Builder()
                .add("username", textEditProfileUsername.getText().toString().trim())
                .add("phoneNumber", textEditProfilePhone.getText().toString().trim())
                .build();
        Request request = new Request.Builder()
                .url(createStudentURL)
                .put(formBody)
                .header("Authorization", "Bear " + accessToken)
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
                    loading(false);
                    String responseData = response.body().string();
                    int code = response.code();
                    JSONObject json = new JSONObject(responseData);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (code == 200) { //If request success, set error invisible
                                try {
                                    phoneNumber = textEditProfilePhone.getText().toString().trim();
                                    String success = json.getString("success");
                                    setResultMessage(success, false);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } else { // If request fail, set the error return
                                try {
                                    String error = json.getString("error");
                                    setResultMessage(error, true);
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

    public String checkEditProfile(){
        String msg = "";
        if(textEditProfileUsername.getText().toString().trim().equals("")){
            msg = "Missing username";
        }
        if(textEditProfilePhone.getText().toString().trim().length() != 10){
            msg = "Phone number invalid";
        }
        return msg;
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

    public void createAlertEditProfile(String msg){
        AlertDialog.Builder builder = new AlertDialog.Builder(EditProfile.this);
        builder.setTitle("Confirm Update");
        builder.setMessage(msg);
        builder.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        editProfileAPI();
                    }
                });
        builder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void setResultMessage(String msg, boolean isError){
        if(isError){
            editSuccess.setVisibility(View.INVISIBLE);
            editError.setVisibility(View.VISIBLE);
            editError.setText(msg);
        }else{
            editError.setVisibility(View.INVISIBLE);
            editSuccess.setVisibility(View.VISIBLE);
            editSuccess.setText(msg);
        }
    }
}