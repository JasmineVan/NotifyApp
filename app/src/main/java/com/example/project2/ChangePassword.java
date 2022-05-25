package com.example.project2;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class ChangePassword extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private EditText textCPOldPassword;
    private EditText textCPNewPassword;
    private EditText textCPPasswordConfirm;
    private TextView cpError;
    private TextView cpSuccess;
    private Button btnCPSave;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        sharedPreferences = getSharedPreferences("Login", Context.MODE_PRIVATE);
        dialog = new ProgressDialog(ChangePassword.this);
        textCPOldPassword = findViewById(R.id.textCPOldPassword);
        textCPNewPassword = findViewById(R.id.textCPNewPassword);
        textCPPasswordConfirm = findViewById(R.id.textCPPasswordConfirm);
        cpError = findViewById(R.id.cpError);
        cpSuccess = findViewById(R.id.cpSuccess);
        btnCPSave = findViewById(R.id.btnCPSave);

        btnCPSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = checkChangeUserPassword();
                if(msg.equals("")){
                    createAlertChangeUserPassword("Confirm update password");
                }
                else{
                    setResultMessage(msg, true);
                }
            }
        });
    }

    public void changeUserPasswordAPI() {
        loading(true);
        OkHttpClient client = new OkHttpClient();
        String accessToken = sharedPreferences.getString("accessToken", "");
        String createStudentURL = "https://note-app-lake.vercel.app/users/updatePassword";
        RequestBody formBody = new FormBody.Builder()
                .add("oldPassword", textCPOldPassword.getText().toString().trim())
                .add("newPassword", textCPNewPassword.getText().toString().trim())
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

    public void setResultMessage(String msg, boolean isError){
        if(isError){
            cpSuccess.setVisibility(View.INVISIBLE);
            cpError.setVisibility(View.VISIBLE);
            cpError.setText(msg);
        }else{
            cpError.setVisibility(View.INVISIBLE);
            cpSuccess.setVisibility(View.VISIBLE);
            cpSuccess.setText(msg);
        }
    }

    public void createAlertChangeUserPassword(String msg){
        AlertDialog.Builder builder = new AlertDialog.Builder(ChangePassword.this);
        builder.setTitle("Confirm Change Password");
        builder.setMessage(msg);
        builder.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        changeUserPasswordAPI();
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

    public String checkChangeUserPassword(){
        String msg = "";
        if(textCPOldPassword.getText().toString().trim().equals("")){
            msg = "Missing old password";
        }
        if(textCPNewPassword.getText().toString().trim().length() < 6){
            msg = "New password invalid";
        }
        if(!textCPPasswordConfirm.getText().toString().trim().equals(textCPNewPassword.getText().toString().trim())){
            msg = "Password confirm invalid";
        }
        return msg;
    }
}