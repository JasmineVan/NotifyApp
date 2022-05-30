package com.example.project2;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FavoriteFragment extends Fragment {

    private RecyclerView recyclerView;
    public List<Note> allNotes;
    private NoteAdapter adapter;
    private SharedPreferences sharedPreferences;
    private View view;
    private ProgressDialog dialog;
    private SearchView favoriteFragmentSearch;
    private ArrayList<String> labelFilter;
    private LinearLayout fragment_favorite_empty_holder;
    private TextView fragment_favorite_empty;
    private FloatingActionButton btnFavoriteFragmentAddNote;
    private ImageView favoriteFragmentFilter;
    private ArrayList<String> listLabel;
    private ArrayList<Integer> labelSelected;
    private NewNoteFragment newNoteFragment = new NewNoteFragment();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_favorite, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        listLabel = new ArrayList<String>();
        labelSelected = new ArrayList<Integer>();
        labelFilter = new ArrayList<String>();
        favoriteFragmentFilter = view.findViewById(R.id.favoriteFragmentFilter);
        fragment_favorite_empty_holder = view.findViewById(R.id.fragment_favorite_empty_holder);
        fragment_favorite_empty = view.findViewById(R.id.fragment_favorite_empty);
        btnFavoriteFragmentAddNote = view.findViewById(R.id.btnFavoriteFragmentAddNote);
        favoriteFragmentSearch= view.findViewById(R.id.favoriteFragmentSearch);
        recyclerView.setLayoutManager(new LinearLayoutManager((this.getContext())));
        allNotes = new ArrayList<>();
        adapter = new NoteAdapter(this.getContext(), allNotes);
        dialog = new ProgressDialog(getActivity());

        //recyclerView setting
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext(), RecyclerView.VERTICAL, false));
        //recyclerView.addItemDecoration(new DividerItemDecoration(this.getContext(), RecyclerView.VERTICAL));
        sharedPreferences = getActivity().getSharedPreferences("Login", Context.MODE_PRIVATE);

        recyclerView.setAdapter(adapter);

        GetNotes("",labelFilter);
        getUserLabel();

        favoriteFragmentFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLabelDialog();
            }
        });

        favoriteFragmentSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String search) {
                GetNotes(search, labelFilter);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String search) {
                return false;
            }
        });
        btnFavoriteFragmentAddNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().beginTransaction().replace(R.id.dashboard_container1, newNoteFragment).commit();
            }
        });

        return view;
    }

    public void setNotes(JSONArray jsonArray){
        try {
            if(jsonArray.length() == 0){
                fragment_favorite_empty_holder.setVisibility(View.VISIBLE);
                fragment_favorite_empty.setVisibility(View.VISIBLE);
            }
            allNotes.clear();
            JSONObject note;
            String noteId, userId, title ,content, createdAt, date, notePassword;
            JSONArray jsonLabel;
            Boolean isPassword, isPin, isDelete;
            String label;
            for(int i = 0; i < jsonArray.length(); i++){
                label = "";
                note = jsonArray.getJSONObject(i);
                noteId = note.getString("_id");
                userId = note.getString("userId");
                jsonLabel = note.getJSONArray("label");
                title = note.getString("title");
                content = note.getString("content");
                isPassword = note.getBoolean("isPassword");
                isPin = note.getBoolean("isPin");
                isDelete = note.getBoolean("isDelete");
                notePassword = note.getString("notePassword");
                createdAt = note.getString("createdAt");
                date = formatDateFromString("yyyy-MM-dd", "dd-MM-yyyy", createdAt.substring(0,10));
                for(int j = 0; j < jsonLabel.length(); j++){
                    label += jsonLabel.getString(j) ;
                    if( j != jsonLabel.length() - 1){
                        label += ", ";
                    }
                }
                allNotes.add(new Note(noteId, userId, title, label, content, date, isPassword, isPin, isDelete, notePassword));
            }

            recyclerView.setAdapter(adapter);
        } catch (JSONException e) {
            Log.e("setNote error", e.toString());
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

    public void GetNotes(String search, ArrayList<String> label){
        loading(true);
        String accessToken = sharedPreferences.getString("accessToken", "");
        OkHttpClient client = new OkHttpClient();
        String createStudentURL = "https://note-app-lake.vercel.app/notes/query";
        FormBody.Builder formBody = new FormBody.Builder().add("search",search);
        if(label.size() > 0){
            for(int i = 0; i < label.size(); i++){
                formBody.add("label", label.get(i));
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
                Log.d("onFailure", e.getMessage());
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
                                    JSONArray data =  json.getJSONArray("data");
                                    setNotes(data);
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
                        labelFilter.clear();
                        for(int index = 0; index < labelSelected.size(); index++){
                            labelFilter.add(listLabel.get(labelSelected.get(index)));
                        }
                        GetNotes("", labelFilter);
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