package com.example.project2;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SettingFragment extends Fragment {

    private View view;
    private TextView settingFonts, settingDelete;
    private SharedPreferences sharedPreferences;
    private int font = 0;
    private int deleteTime = 0;
    private int deleteTimeTemp = 0;
    private ProgressDialog dialog;
    private String[] fonts = {"Default", "Arya", "Oleo Script"};
    private String[] deleteTimes = {"30s", "1 days", "7 days"};
    private ArrayList<Integer> labelSelected;
    private ArrayList<String> listLabel;
    private Button btnDeleteLabel, btnEditLabel, btnAddLabel, btnSettingLanguage;
    private Button fragment_setting_btnSave;
    private  int lb = 0;

    private Button btnSettingSounds;
    public static final int REQUEST_RING_TONES = 999;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_setting, container, false);
        dialog = new ProgressDialog(getActivity());
        settingFonts = view.findViewById(R.id.settingFonts);
        settingDelete = view.findViewById(R.id.settingDelete);
        btnSettingSounds = view.findViewById(R.id.btnSettingSounds);
        btnSettingLanguage = view.findViewById(R.id.btnSettingLanguage);
        sharedPreferences = getActivity().getSharedPreferences("Login", Context.MODE_PRIVATE);
        listLabel = new ArrayList<String>();
        btnDeleteLabel = view.findViewById(R.id.btnDeleteLabel);
        btnEditLabel = view.findViewById(R.id.btnEditLabel);
        btnAddLabel = view.findViewById(R.id.btnAddLabel);
        fragment_setting_btnSave = view.findViewById(R.id.fragment_setting_btnSave);

        getUserSettings();

        //font settings
        settingFonts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(getContext())
                        .setSingleChoiceItems(fonts, font, null)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                font = ((AlertDialog)dialog).getListView().getCheckedItemPosition();
                                settingFonts.setText(fonts[font]);
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        });

        //Label

        btnAddLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog builder;
                builder = new AlertDialog.Builder(getContext()).create();
                builder.setCanceledOnTouchOutside(false);
                builder.setTitle("Add Label");
                final EditText input = new EditText(getContext());
                input.setHint("Enter label");
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                input.setLayoutParams(lp);
                builder.setView(input);

                builder.setButton("Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if(!input.getText().toString().trim().equals("")){
                                    listLabel.add(input.getText().toString().trim());
                                    Toast.makeText(getContext(), "Add label success", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                builder.show();
            }
        });

        btnEditLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listLabel.size() > 0){
                    String[] lbs = new String[listLabel.size()];
                    for(int i = 0; i < listLabel.size(); i++){
                        lbs[i] = listLabel.get(i);
                    }
                    new AlertDialog.Builder(getContext())
                            .setSingleChoiceItems(lbs, lb, null)
                            .setPositiveButton("Edit", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    lb = ((AlertDialog)dialog).getListView().getCheckedItemPosition();
                                    editLabel(lb);
                                    dialog.dismiss();
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    dialog.dismiss();
                                }
                            })
                            .show();
                    }else{
                    Toast.makeText(getContext(), "No label to edit", Toast.LENGTH_SHORT).show();
                }
                }
        });

        btnDeleteLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLabelDeleteDialog();
            }
        });

        //delete settings
        settingDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(getContext())
                        .setSingleChoiceItems(deleteTimes, deleteTime, null)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                deleteTime = ((AlertDialog)dialog).getListView().getCheckedItemPosition();
                                settingDelete.setText(deleteTimes[deleteTime]);
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        });
        //setting sound
        btnSettingSounds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent to select Ringtone.
                final Uri currentTone=
                        RingtoneManager.getActualDefaultRingtoneUri(getContext(),
                                RingtoneManager.TYPE_ALARM);
                Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_RINGTONE);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Tone");
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, currentTone);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true);
                startActivityForResult(intent, REQUEST_RING_TONES);
            }
        });

        fragment_setting_btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = checkSettings();
                if(msg.equals("")){
                    if(deleteTime != deleteTimeTemp){
                        updateTimeDelete();
                    }
                    saveSetting();
                }
                else{
                    Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    public void updateTimeDelete(){
        int deleteNoteTime = 0;
        switch (deleteTime){
            case 0:
                deleteNoteTime = 30000;
                break;
            case 1:
                deleteNoteTime = 86400000;
                break;
            case 2:
                deleteNoteTime = 604800000;
                break;
        }
        OkHttpClient client = new OkHttpClient();
        String accessToken = sharedPreferences.getString("accessToken", "");
        String createStudentURL = "https://note-app-lake.vercel.app/users/updateDelete";
        FormBody.Builder formBody = new FormBody.Builder().add("deleteNoteTime",String.valueOf(deleteNoteTime));
        FormBody form =  formBody.build();
        Request request = new Request.Builder()
                .url(createStudentURL)
                .header("Authorization", "Bear " + accessToken)
                .put(form)
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
                    getActivity().runOnUiThread(new Runnable(){
                        @Override
                        public void run(){
                            if(code == 200){
                                Toast.makeText(getContext(),"Update delete time success", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(getContext(),"Update delete time failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } catch (JSONException e) {
                    Log.d("onResponse", e.getMessage());
                }
            }
        });
    }

    public String checkSettings(){
        String msg = "";
        if(listLabel.size() <= 0){
            msg = "Missing label";
        }
        return msg;
    }

    public void saveSetting(){
        loading(true);
        OkHttpClient client = new OkHttpClient();
        String accessToken = sharedPreferences.getString("accessToken", "");
        String createStudentURL = "https://note-app-lake.vercel.app/users/updateSettings";
        FormBody.Builder formBody = new FormBody.Builder().add("font",String.valueOf(font));
        for(int i = 0; i < listLabel.size(); i++){
            formBody.add("label", listLabel.get(i));
        }
        FormBody form =  formBody.build();
        Request request = new Request.Builder()
                .url(createStudentURL)
                .header("Authorization", "Bear " + accessToken)
                .put(form)
                .build();

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
                                Toast.makeText(getContext(),"Update settings success", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(getContext(),"Update settings failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } catch (JSONException e) {
                    Log.d("onResponse", e.getMessage());
                }
            }
        });
    }

     @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_RING_TONES && resultCode == getActivity().RESULT_OK){
            Uri uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
        }
    }

    private void showLabelDeleteDialog() {
        labelSelected = new ArrayList<Integer>();
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
        alertDialog.setTitle("Label");
        String[] arrayLabel = new String[listLabel.size()];
        boolean[] isCheckArr = new boolean[listLabel.size()];

        for (int i = 0; i < listLabel.size(); i++){
            arrayLabel[i] = listLabel.get(i);
            isCheckArr[i] = false;
        }

        for(int i = 0; i < labelSelected.size(); i++){
            isCheckArr[labelSelected.get(i)] = true;
        }

        alertDialog.setMultiChoiceItems(arrayLabel, isCheckArr, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                if (isChecked) {
                    labelSelected.add(which);
                } else if (labelSelected.contains(which)) {
                    labelSelected.remove(Integer.valueOf(which));
                }
            }
        });

        alertDialog.setPositiveButton("Delete",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Collections.sort(labelSelected);
                        Collections.reverse(labelSelected);
                        for(int index = 0; index < labelSelected.size(); index++){
                            listLabel.remove(listLabel.get(labelSelected.get(index)));
                        }
                        Toast.makeText(getContext(), "Delete label success", Toast.LENGTH_SHORT).show();
                    }
                });

        AlertDialog alert = alertDialog.create();
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }

    public void editLabel(int position){
        AlertDialog builder;
        builder = new AlertDialog.Builder(getContext()).create();
        builder.setCanceledOnTouchOutside(false);
        builder.setTitle("Edit label");
        final EditText input = new EditText(getContext());
        input.setText(listLabel.get(position));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        builder.setView(input);

        builder.setButton("Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        listLabel.set(position, input.getText().toString().trim());
                        Toast.makeText(getContext(), "Edit label success", Toast.LENGTH_SHORT).show();
                    }
                });
        builder.show();

    }

    public void getUserSettings(){
        loading(true);
        String accessToken = sharedPreferences.getString("accessToken", "");
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url("https://note-app-lake.vercel.app/users/settings").header("Authorization", "Bear " + accessToken).build();
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
                        @RequiresApi(api = Build.VERSION_CODES.O)
                        @Override
                        public void run(){
                            if(code == 200){
                                try {
                                    font = json.getInt("font");
                                    int deleteNoteTime = json.getInt("deleteNoteTime");
                                    JSONArray labels = json.getJSONArray("label");
                                    switch (deleteNoteTime){
                                        case 30000:
                                            deleteTime = 0;
                                            deleteTimeTemp = 0;
                                            break;
                                        case 86400000:
                                            deleteTime = 1;
                                            deleteTimeTemp = 1;
                                            break;
                                        case 604800000:
                                            deleteTime = 2;
                                            deleteTimeTemp = 2;
                                            break;
                                    }
                                    for(int i = 0; i < labels.length(); i++){
                                        listLabel.add(labels.getString(i));
                                    }
                                    settingFonts.setText(fonts[font]);
                                    settingDelete.setText(deleteTimes[deleteTime]);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            else {
                                Toast.makeText(getContext(),"Get font failed", Toast.LENGTH_SHORT).show();
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