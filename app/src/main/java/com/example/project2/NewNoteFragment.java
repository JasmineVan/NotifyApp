package com.example.project2;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.dynamic.IFragmentWrapper;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.BitSet;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
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
    private ArrayList<Integer> labelSelected;
    private ViewNoteFragment viewNoteFragment = new ViewNoteFragment();
    private Typeface typeface;
    private int font = 0;
    private ImageView ivNewNotePic;
    private ImageView newNotePlaceHolder, ivNewNoteNotification;

    private static final int REQUEST_CODE_STORAGE_PERMISSION = 200;
    private static final int REQUEST_CODE_SELECT_IMAGE = 201;


    @RequiresApi(api = Build.VERSION_CODES.O)
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
        ivNewNoteNotification = view.findViewById(R.id.ivNewNoteNotification);
        labelSelected = new ArrayList<Integer>();
        ivNewNotePic = view.findViewById(R.id.ivNewNotePic);
        newNotePlaceHolder = view.findViewById(R.id.newNotePlaceHolder);

        getUserLabel();
        getUserFont();

        newNoteFragmentLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLabelDialog();
            }
        });

        newNoteFragmentSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = checkCreateNewNote();
                if(msg.equals("")){
                    createNewNote();
                }
                else{
                    Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
                }
            }
        });
        ivNewNotePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(getActivity(), new String[]{
                            Manifest.permission.READ_EXTERNAL_STORAGE
                    }, REQUEST_CODE_STORAGE_PERMISSION);
                }else {
                    selectImage();
                }
            }
        });

        return view;
    }

    // jasmine - select image handle
    private void selectImage(){
        //Toast.makeText(getContext(), "Permission Granted", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (intent.resolveActivity(getActivity().getPackageManager()) != null){
            startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE);
        }
    }
    // jasmine - request permission
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_STORAGE_PERMISSION && grantResults.length > 0){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                selectImage();
            }else {
                Toast.makeText(getContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
    // jasmine - get image from intent
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SELECT_IMAGE && resultCode == getActivity().RESULT_OK){
            if (data != null){
                Uri selectedImageUri = data.getData();
                if (selectedImageUri != null){
                    try {
                        InputStream inputStream = getContext().getContentResolver().openInputStream(selectedImageUri);
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        newNotePlaceHolder.setImageBitmap(bitmap);
                        newNotePlaceHolder.setVisibility(View.VISIBLE);
                    }catch (Exception exception){
                        Toast.makeText(getContext(), exception.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
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
                        newNoteFragmentLabel.setText(label);
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

    public String checkCreateNewNote(){
        String msg = "";
        if(newNoteFragmentTitle.getText().toString().trim().equals("")){
            msg = "Missing title";
        }
        if(newNoteFragmentLabel.getText().toString().trim().equals("")){
            msg = "Missing label";
        }
        if(newNoteFragmentContent.getText().toString().trim().equals("")){
            msg = "Missing content";
        }
        return msg;
    }

    public void createNewNote(){
        loading(true);
        String accessToken = sharedPreferences.getString("accessToken", "");
        OkHttpClient client = new OkHttpClient();
        String createStudentURL = "https://note-app-lake.vercel.app/notes/create";
        FormBody.Builder formBody = new FormBody.Builder().add("title",newNoteFragmentTitle.getText().toString().trim()).add("content", newNoteFragmentContent.getText().toString().trim()).add("font", String.valueOf(font));
        if(labelSelected.size() > 0){
            for(int i = 0; i < labelSelected.size(); i++){
                formBody.add("label", listLabel.get(labelSelected.get(i)));
            }
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
                        @Override
                        public void run() {
                            if(code == 200){
                                try {
                                    String noteId = json.getString("noteId");
                                    Bundle data = new Bundle();
                                    data.putString("noteId", noteId);
                                    viewNoteFragment.setArguments(data);
                                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.dashboard_container1, viewNoteFragment).commit();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            else {
                                Toast.makeText(getContext(),"Create note failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } catch (JSONException e) {
                    Log.d("onResponse", e.getMessage());
                }
            }
        });
    }

    public void getUserFont(){
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
                        @RequiresApi(api = Build.VERSION_CODES.O)
                        @Override
                        public void run(){
                            if(code == 200){
                                try {
                                    font = json.getInt("font");

                                    switch (font){
                                        case 1:
                                            typeface = getContext().getResources().getFont(R.font.arya);
                                        case 2:
                                            typeface = getContext().getResources().getFont(R.font.oleo_script);
                                    }
                                    newNoteFragmentTitle.setTypeface(typeface);
                                    newNoteFragmentLabel.setTypeface(typeface);
                                    newNoteFragmentContent.setTypeface(typeface);
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
            dialog.setCanceledOnTouchOutside(false);
        }else{
            Log.e("s","complete");
            dialog.cancel();
        }
    }

}