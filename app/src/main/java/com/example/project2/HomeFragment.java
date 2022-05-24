package com.example.project2;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    public List<Note> pinNotes;
    private NoteAdapter adapter;
    private SharedPreferences sharedPreferences;
    private View view;
    private ProgressDialog dialog;
    private SearchView homeFragmentSearch;
    private ArrayList<String> labelFilter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        homeFragmentSearch = view.findViewById(R.id.homeFragmentSearch);
        recyclerView.setLayoutManager(new LinearLayoutManager((this.getContext())));
        pinNotes = new ArrayList<>();
        adapter = new NoteAdapter(this.getContext(), pinNotes);

        //recyclerView setting
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext(), RecyclerView.VERTICAL, false));
        recyclerView.addItemDecoration(new DividerItemDecoration(this.getContext(), RecyclerView.VERTICAL));
        sharedPreferences = getActivity().getSharedPreferences("Login", Context.MODE_PRIVATE);

        recyclerView.setAdapter(adapter);

        labelFilter = new ArrayList<String>();
        GetPinNote("",labelFilter);

        homeFragmentSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String search) {
                GetPinNote(search, labelFilter);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String search) {
                return true;
            }
        });

        return view;
    }

    public void setPinNotes(JSONArray jsonArray){
        try {
            pinNotes.clear();
            JSONObject note;
            String noteId, userId, title ,content, createdAt, date;
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
                createdAt = note.getString("createdAt");
                date = formatDateFromString("yyyy-MM-dd", "dd-MM-yyyy", createdAt.substring(0,10));
                for(int j = 0; j < jsonLabel.length(); j++){
                    label += jsonLabel.getString(j) ;
                    if( j != jsonLabel.length() - 1){
                        label += ", ";
                    }
                }
                pinNotes.add(new Note(noteId, userId, title, label, content, date));
            }

            recyclerView.setAdapter(adapter);
        } catch (JSONException e) {
            Log.e("setPin error", e.toString());
        }
    }

    public void GetPinNote(String search, ArrayList<String> label){
        loading(true);
        String accessToken = sharedPreferences.getString("accessToken", "");
        OkHttpClient client = new OkHttpClient();
        String createStudentURL = "https://note-app-lake.vercel.app/notes/query";
        FormBody.Builder formBody = new FormBody.Builder().add("search",search).add("isPin", "true");
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
                                    setPinNotes(data);
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
        if(isLoad){
            Log.e("s","wait");
            dialog = ProgressDialog.show(getContext(), "",
                    "Loading. Please wait...", true);
        }else{
            Log.e("s","complete");
            dialog.dismiss();
        }
    }
}