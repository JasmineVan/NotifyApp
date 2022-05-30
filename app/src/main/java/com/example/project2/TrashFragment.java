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

public class TrashFragment extends Fragment {

    private View view;
    private NoteAdapter adapter;
    private RecyclerView recyclerView;
    public List<Note> trashNotes;
    private SharedPreferences sharedPreferences;
    private ProgressDialog dialog;
    private SearchView trashFragmentSearch;
    private TextView fragment_trash_empty;
    private LinearLayout fragment_trash_empty_holder;
    private ArrayList<String> labelFilter;
    private ImageView trashFragmentFilter;
    private ArrayList<String> listLabel;
    private ArrayList<Integer> labelSelected;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_trash, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        listLabel = new ArrayList<String>();
        labelSelected = new ArrayList<Integer>();
        labelFilter = new ArrayList<String>();
        trashFragmentSearch = view.findViewById(R.id.trashFragmentSearch);
        fragment_trash_empty = view.findViewById(R.id.fragment_trash_empty);
        fragment_trash_empty_holder = view.findViewById(R.id.fragment_trash_empty_holder);
        trashFragmentFilter = view.findViewById(R.id.trashFragmentFilter);
        trashNotes = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager((this.getContext())));
        dialog = new ProgressDialog(getActivity());
        adapter = new NoteAdapter(this.getContext(), trashNotes);
        sharedPreferences = getActivity().getSharedPreferences("Login", Context.MODE_PRIVATE);

        //recyclerView setting
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext(), RecyclerView.VERTICAL, false));
        //recyclerView.addItemDecoration(new DividerItemDecoration(this.getContext(), RecyclerView.VERTICAL));
        recyclerView.setAdapter(adapter);

        GetTrashNote();


        return view;
    }

    public void setTrashNotes(@NonNull JSONArray jsonArray){
        try {
            if(jsonArray.length() == 0){
                fragment_trash_empty_holder.setVisibility(View.VISIBLE);
                fragment_trash_empty.setVisibility(View.VISIBLE);
            }
            trashNotes.clear();
            JSONObject note;
            String noteId, userId, title ,content, createdAt, date, notePassword;
            Boolean isPassword, isPin, isDelete;
            JSONArray jsonLabel;
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
                trashNotes.add(new Note(noteId, userId, title, label, content, date, isPassword, isPin, isDelete, notePassword));
            }

            recyclerView.setAdapter(adapter);
        } catch (JSONException e) {
            Log.e("setPin error", e.toString());
        }
    }

    public void GetTrashNote(){
        loading(true);
        String accessToken = sharedPreferences.getString("accessToken", "");
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url("https://note-app-lake.vercel.app/notes/recycle").header("Authorization", "Bear " + accessToken).build();
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
                                    JSONArray data =  json.getJSONArray("data");
                                    setTrashNotes(data);
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