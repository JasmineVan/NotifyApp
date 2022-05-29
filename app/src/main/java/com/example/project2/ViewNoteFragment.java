package com.example.project2;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ViewNoteFragment extends Fragment {
    private View view;
    private TextView viewNoteFragmentTitle, viewNoteFragmentDate, viewNoteFragmentLabel, viewNoteFragmentContent, viewNoteFragmentEdit;
    private ProgressDialog dialog;
    private SharedPreferences sharedPreferences;
    private String noteId;
    private Typeface typeface;
    private EditNoteFragment editNoteFragment = new EditNoteFragment();

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
        viewNoteFragmentEdit = (TextView) view.findViewById(R.id.viewNoteFragmentEdit);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            noteId = bundle.getString("noteId", "");
        }

        viewNoteFragmentEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle data = new Bundle();
                data.putString("noteId", noteId);
                editNoteFragment.setArguments(data);
                getFragmentManager().beginTransaction().replace(R.id.dashboard_container1, editNoteFragment).commit();
            }
        });

        getIsPassword();
        return view;
    }

    public void getNoteDetail(String password){
        loading(true);
        String accessToken = sharedPreferences.getString("accessToken", "");
        OkHttpClient client = new OkHttpClient();
        String createStudentURL = "https://note-app-lake.vercel.app/notes/detail/noteId/" + noteId;
        FormBody.Builder formBody = new FormBody.Builder();
        if(!password.equals("")){
            formBody.add("password",password);
        }
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
                        @RequiresApi(api = Build.VERSION_CODES.O)
                        @Override
                        public void run() {
                            if(code == 200){
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
                                    int font = json.getInt("font");
                                    switch (font){
                                        case 1:
                                            typeface = getContext().getResources().getFont(R.font.arya);
                                        case 2:
                                            typeface = getContext().getResources().getFont(R.font.oleo_script);
                                    }
                                    viewNoteFragmentDate.setTypeface(typeface);
                                    viewNoteFragmentTitle.setTypeface(typeface);
                                    viewNoteFragmentLabel.setTypeface(typeface);
                                    viewNoteFragmentContent.setTypeface(typeface);

                                    viewNoteFragmentTitle.setText(title);
                                    viewNoteFragmentContent.setText(content);
                                    viewNoteFragmentDate.setText("Created At: " + date);
                                    viewNoteFragmentLabel.setText(label);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            else {
                                Toast.makeText(getContext(),"Wrong Password", Toast.LENGTH_SHORT).show();
                                createVerifyPasswordAlert();
                            }
                        }
                    });
                } catch (JSONException e) {
                    Log.d("onResponse", e.getMessage());
                }
            }
        });
    }

    public void getIsPassword(){
        loading(true);
        String accessToken = sharedPreferences.getString("accessToken", "");
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url("https://note-app-lake.vercel.app/notes/isPassword/noteId/" + noteId).header("Authorization", "Bear " + accessToken).build();
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
                                    Boolean isPassword = json.getBoolean("isPassword");
                                    if(isPassword){
                                        createVerifyPasswordAlert();
                                    }
                                    else{
                                        getNoteDetail("");
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            else {
                                Toast.makeText(getContext(),"Get is password failed", Toast.LENGTH_SHORT).show();
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

    public void createVerifyPasswordAlert(){
        AlertDialog builder;
        builder = new AlertDialog.Builder(getContext()).create();
        builder.setCanceledOnTouchOutside(false);
        builder.setTitle("Enter Password");
        final EditText input = new EditText(getContext());
        input.setHint("Enter password");
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        builder.setView(input);

        builder.setButton("Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        getNoteDetail(input.getText().toString().trim());
                    }
                });
        builder.show();
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