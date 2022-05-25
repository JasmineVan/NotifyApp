package com.example.project2;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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


public class NewNoteFragment extends Fragment {
    private View view;
    private SharedPreferences sharedPreferences;
    private EditText newNoteFragmentTitle;
    private TextView newNoteFragmentLabel;
    private EditText newNoteFragmentContent;
    private TextView newNoteFragmentSave;
    private ProgressDialog dialog;
    private ArrayList<String> listLabel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_new_note, container, false);

        listLabel = new ArrayList<String>();
        dialog = new ProgressDialog(getActivity());
        sharedPreferences = getActivity().getSharedPreferences("Login", Context.MODE_PRIVATE);
        newNoteFragmentTitle = (EditText) view.findViewById(R.id.newNoteFragmentTitle);
        newNoteFragmentLabel = (TextView) view.findViewById(R.id.newNoteFragmentLabel);
        newNoteFragmentContent = (EditText) view.findViewById(R.id.newNoteFragmentContent);
        newNoteFragmentSave = (TextView) view.findViewById(R.id.newNoteFragmentSave);

        getUserLabel();

        newNoteFragmentLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAlertDialog();
            }
        });

        return view;
    }

    private void showAlertDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
        alertDialog.setTitle("AlertDialog");
        String[] arrayLabel = new String[listLabel.size()];

        for (int i = 0; i < listLabel.size(); i++){
            arrayLabel[i] = listLabel.get(i);
        }

        final ArrayList itemsSelected = new ArrayList();

        alertDialog.setMultiChoiceItems(arrayLabel, null, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                if (isChecked) {
                    itemsSelected.add(which);
                } else if (itemsSelected.contains(which)) {
                    itemsSelected.remove(Integer.valueOf(which));
                }
            }
        });
        AlertDialog alert = alertDialog.create();
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }

    public void getUserLabel(){
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
                        @Override
                        public void run(){
                            if(code == 200){
                                try {
                                    JSONArray label = json.getJSONArray("label");
                                    for(int i = 0; i < label.length(); i++){
                                        listLabel.add(label.getString(i));
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            else {
                                Toast.makeText(getContext(),"Get label failed", Toast.LENGTH_SHORT).show();
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

}