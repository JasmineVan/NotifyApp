package com.example.project2;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import javax.security.auth.callback.Callback;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class UserFragment extends Fragment {
    private SharedPreferences sharedPreferences;
    private TextView logOutBtn, user_fragment_tvFullName,user_fragment_tvPhone, user_fragment_tvActive;
    private View view;
    private ProgressDialog dialog;
    private Button fragment_user_btnEdit, fragment_user_btnChangePassword;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_user, container, false);
        sharedPreferences = getActivity().getSharedPreferences("Login", Context.MODE_PRIVATE);
        dialog = new ProgressDialog(getActivity());
        logOutBtn = (TextView) view.findViewById(R.id.fragment_user_btnLogout);
        user_fragment_tvFullName = (TextView) view.findViewById(R.id.user_fragment_tvFullName);
        user_fragment_tvPhone = (TextView) view.findViewById(R.id.user_fragment_tvPhone);
        user_fragment_tvActive = (TextView) view.findViewById(R.id.user_fragment_tvActive);

        fragment_user_btnEdit = (Button) view.findViewById(R.id.fragment_user_btnEdit);
        fragment_user_btnChangePassword = (Button) view.findViewById(R.id.fragment_user_btnChangePassword);


        getUserInfo();

        logOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createAlertLogout();
            }
        });

        user_fragment_tvActive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), SendOTP.class);
                intent.putExtra("phoneNumber", user_fragment_tvPhone.getText().toString().trim());
                startActivity(intent);
                getActivity().finish();
            }
        });

        fragment_user_btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), EditProfile.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        fragment_user_btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ChangePassword.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        return view;
    }

    public void createAlertLogout(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Logout");
        builder.setMessage("Confirm to logout");
        builder.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SharedPreferences.Editor editSharedPreferences = sharedPreferences.edit();
                        editSharedPreferences.putString("accessToken", "");
                        editSharedPreferences.commit();

                        Intent intent = new Intent(getContext(), LoginPage.class);
                        startActivity(intent);
                        getActivity().finish();
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

                    getActivity().runOnUiThread(new Runnable(){
                        @Override
                        public void run(){
                            if(code == 200){
                                try {
                                    String phoneNumber = json.getString("phoneNumber");
                                    String username = json.getString("username");
                                    Boolean isActive = json.getBoolean("isActive");
                                    user_fragment_tvFullName.setText(username);
                                    user_fragment_tvPhone.setText(phoneNumber);
                                    if(!isActive){
                                        user_fragment_tvActive.setVisibility(View.VISIBLE);
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            else {
                                Toast.makeText(getContext(),"Get info failed", Toast.LENGTH_SHORT).show();
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