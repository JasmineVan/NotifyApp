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
import androidx.fragment.app.Fragment;

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
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EditNoteFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditNoteFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public EditNoteFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EditNoteFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EditNoteFragment newInstance(String param1, String param2) {
        EditNoteFragment fragment = new EditNoteFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    private SharedPreferences sharedPreferences;
    private ProgressDialog dialog;
    private ArrayList<String> listLabel;
    private ArrayList<Integer> labelSelected;
    private int font = 0;
    private Typeface typeface;
    private View view;
    private  TextView editNoteFragmentSave, editNoteFragmentLabel;
    private EditText editNoteFragmentTitle, editNoteFragmentContent;
    private String noteId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_edit_note, container, false);

        listLabel = new ArrayList<String>();
        dialog = new ProgressDialog(getActivity());
        sharedPreferences = getActivity().getSharedPreferences("Login", Context.MODE_PRIVATE);
        editNoteFragmentContent = (EditText) view.findViewById(R.id.editNoteFragmentContent);
        editNoteFragmentTitle = (EditText) view.findViewById(R.id.editNoteFragmentTitle);
        editNoteFragmentSave = (TextView) view.findViewById(R.id.editNoteFragmentSave);
        editNoteFragmentLabel = (TextView) view.findViewById(R.id.editNoteFragmentLabel);
        labelSelected = new ArrayList<Integer>();
        noteId = getActivity().getIntent().getStringExtra("noteId");
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            noteId = bundle.getString("noteId", "");
        }
        getUserLabel();
        getIsPassword();

        editNoteFragmentLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLabelDialog();
            }
        });

        editNoteFragmentSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = checkCreateNewNote();
                if(msg.equals("")){
                    updateNote();
                }
                else{
                    Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    public void updateNote(){
        loading(true);
        String accessToken = sharedPreferences.getString("accessToken", "");
        OkHttpClient client = new OkHttpClient();
        String createStudentURL = "https://note-app-lake.vercel.app/notes/update";
        FormBody.Builder formBody = new FormBody.Builder().add("noteId", noteId).add("title",editNoteFragmentTitle.getText().toString().trim()).add("content", editNoteFragmentContent.getText().toString().trim());
        if(labelSelected.size() > 0){
            for(int i = 0; i < labelSelected.size(); i++){
                formBody.add("label", listLabel.get(labelSelected.get(i)));
            }
        }
        FormBody form =  formBody.build();
        Request request = new Request.Builder()
                .url(createStudentURL)
                .header("Authorization", "Bear " + accessToken)
                .put(form)
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
                                Toast.makeText(getContext(),"Update note success", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Toast.makeText(getContext(),"Update note failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } catch (JSONException e) {
                    Log.d("onResponse", e.getMessage());
                }
            }
        });
    }

    public String checkCreateNewNote(){
        String msg = "";
        if(editNoteFragmentTitle.getText().toString().trim().equals("")){
            msg = "Missing title";
        }
        if(editNoteFragmentLabel.getText().toString().trim().equals("")){
            msg = "Missing label";
        }
        if(editNoteFragmentContent.getText().toString().trim().equals("")){
            msg = "Missing content";
        }
        return msg;
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
                                    JSONArray jsonLabel = json.getJSONArray("label");
                                    String label = "Label: ";
                                    for(int i = 0; i < jsonLabel.length(); i++){
                                        label += jsonLabel.getString(i) ;
                                        if( i != jsonLabel.length() - 1){
                                            label += ", ";
                                        }
                                        for(int j = 0; j < listLabel.size(); j++){
                                            if(listLabel.get(j).equals(jsonLabel.getString(i))){
                                                labelSelected.add(j);
                                            }
                                        }
                                    }
                                    int font = json.getInt("font");
                                    switch (font){
                                        case 1:
                                            typeface = getContext().getResources().getFont(R.font.arya);
                                        case 2:
                                            typeface = getContext().getResources().getFont(R.font.oleo_script);
                                    }
                                    editNoteFragmentTitle.setTypeface(typeface);
                                    editNoteFragmentLabel.setTypeface(typeface);
                                    editNoteFragmentContent.setTypeface(typeface);

                                    editNoteFragmentTitle.setText(title);
                                    editNoteFragmentContent.setText(content);
                                    editNoteFragmentLabel.setText(label);
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

    private void showLabelDialog() {
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

        alertDialog.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String label = "";
                        for(int index = 0; index < labelSelected.size(); index++){
                            label = label + listLabel.get(labelSelected.get(index)) ;
                            if( index != labelSelected.size() - 1){
                                label += ", ";
                            }
                        }
                        editNoteFragmentLabel.setText(label);
                    }
                });
        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
        AlertDialog alert = alertDialog.create();
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }

    public void getUserLabel(){
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
            dialog.setCanceledOnTouchOutside(false);
        }else{
            Log.e("s","complete");
            dialog.cancel();
        }
    }
}