package com.example.project2;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
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

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SettingFragment extends Fragment {

    private View view;
    private Spinner languageSpinner;
    private TextView settingFonts, settingDelete;
    private SharedPreferences sharedPreferences;
    private int font = 0;
    private int deleteTime = 0;
    private ProgressDialog dialog;
    private String[] fonts = {"Default", "Arya", "Oleo Script"};
    private String[] deleteTimes = {"30s", "1 days", "7 days"};
    private ArrayList<Integer> labelSelected;
    private ArrayList<String> listLabel;
    private Button btnDeleteLabel, btnEditLabel, btnAddLabel;
    private  int lb = 0;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_setting, container, false);
        dialog = new ProgressDialog(getActivity());
        languageSpinner = view.findViewById(R.id.settingLanguages);
        settingFonts = view.findViewById(R.id.settingFonts);
        settingDelete = view.findViewById(R.id.settingDelete);
        sharedPreferences = getActivity().getSharedPreferences("Login", Context.MODE_PRIVATE);
        listLabel = new ArrayList<String>();
        btnDeleteLabel = view.findViewById(R.id.btnDeleteLabel);
        btnEditLabel = view.findViewById(R.id.btnEditLabel);


        getUserSettings();

        //language spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getContext(),
                R.array.languages, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        languageSpinner.setAdapter(adapter);

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

        btnEditLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] lbs = new String[listLabel.size()];
                for(int i = 0; i < listLabel.size(); i++){
                    lbs[i] = listLabel.get(i);
                }
                new AlertDialog.Builder(getContext())
                        .setSingleChoiceItems(lbs, lb, null)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                lb = ((AlertDialog)dialog).getListView().getCheckedItemPosition();
                                editLabel(lb);
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        });

        btnDeleteLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLabelDialog();
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

        return view;
    }

    private void showLabelDialog() {
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
                        for(int index = 0; index < labelSelected.size(); index++){
                            listLabel.remove(listLabel.get(labelSelected.get(index)));
                        }
                    }
                });

        alertDialog.setPositiveButton("Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

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
                                            break;
                                        case 86400000:
                                            deleteTime = 1;
                                            break;
                                        case 604800000:
                                            deleteTime = 2;
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