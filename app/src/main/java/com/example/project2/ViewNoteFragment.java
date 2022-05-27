package com.example.project2;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ViewNoteFragment extends Fragment {
    private View view;
    private TextView viewNoteFragmentTitle;
    private TextView viewNoteFragmentDate;
    private TextView viewNoteFragmentLabel;
    private TextView viewNoteFragmentContent;
    private ProgressDialog dialog;
    private SharedPreferences sharedPreferences;
    private String noteId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_view_note, container, false);

        sharedPreferences = getActivity().getSharedPreferences("Login", Context.MODE_PRIVATE);
        dialog = new ProgressDialog(getActivity());
        viewNoteFragmentTitle = (TextView) view.findViewById(R.id.viewNoteFragmentTitle);
        viewNoteFragmentDate = (TextView) view.findViewById(R.id.viewNoteFragmentDate);
        viewNoteFragmentLabel = (TextView) view.findViewById(R.id.viewNoteFragmentLabel);
        viewNoteFragmentContent = (TextView) view.findViewById(R.id.viewNoteFragmentContent);
        noteId = getActivity().getIntent().getStringExtra("noteId");
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            noteId = bundle.getString("noteId", "");
        }

        getNoteDetail();

        return view;
    }

    public void getNoteDetail(){
        loading(true);
        String accessToken = sharedPreferences.getString("accessToken", "");
        OkHttpClient client = new OkHttpClient();
        String createStudentURL = "https://note-app-lake.vercel.app/notes/detail/noteId/" + noteId;
        FormBody.Builder formBody = new FormBody.Builder();

        FormBody form =  formBody.build();
        Request request = new Request.Builder()
                .url(createStudentURL)
                .header("Authorization", "Bear " + accessToken)
                .post(form)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e("onFailure", e.getMessage());
            }
            @Override
            public void onResponse(Call call, final Response response)
                    throws IOException {
                loading(false);
                try {
                    String responseData = response.body().string();
                    int code = response.code();
                    JSONObject json = new JSONObject(responseData);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(code == 200){
                                System.out.println(json);
                                try {
                                    String title = json.getString("title");
                                    String content = json.getString("content");
                                    String createdAt = json.getString("createdAt");
                                    String date = formatDateFromString("yyyy-MM-dd", "dd-MM-yyyy", createdAt.substring(0,10));
                                    JSONArray jsonLabel = json.getJSONArray("label");
                                    String label = "Label: ";
                                    for(int j = 0; j < jsonLabel.length(); j++){
                                        label += jsonLabel.getString(j) ;
                                        if( j != jsonLabel.length() - 1){
                                            label += ", ";
                                        }
                                    }
                                    viewNoteFragmentTitle.setText(title);
                                    viewNoteFragmentContent.setText(content);
                                    viewNoteFragmentDate.setText("Created At: " + date);
                                    viewNoteFragmentLabel.setText(label);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            else {
                                Toast.makeText(getContext(),"Active failed", Toast.LENGTH_SHORT).show();
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

    public static String formatDateFromString(String inputFormat, String outputFormat, String inputDate){
        Date parsed = null;
        String outputDate = "";

        SimpleDateFormat df_input = new SimpleDateFormat(inputFormat, java.util.Locale.getDefault());
        SimpleDateFormat df_output = new SimpleDateFormat(outputFormat, java.util.Locale.getDefault());

        try {
            parsed = df_input.parse(inputDate);
            outputDate = df_output.format(parsed);

        } catch (ParseException e) {
            Log.e("Date","ParseException - dateFormat");
        }

        return outputDate;
    }

}