package com.example.project2;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class FavoriteFragment extends Fragment {

    private RecyclerView recyclerView;
    public List<Note> allNotes;
    private NoteAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager((this.getContext())));
        allNotes = new ArrayList<>();
        adapter = new NoteAdapter(this.getContext(), allNotes);
        //recyclerView setting
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext(), RecyclerView.VERTICAL, false));
        recyclerView.addItemDecoration(new DividerItemDecoration(this.getContext(), RecyclerView.VERTICAL));

        allNotes.add(new Note("1", "1", "Note Number 1", "abc def ghi", "Sunday 20-01-2022"));
        allNotes.add(new Note("2", "2", "Note Number 2", "abc def ghi", "Monday 21-02-2022"));
        allNotes.add(new Note("3", "3", "Note Number 3", "abc def ghi", "Tuesday 22-03-2022"));
        allNotes.add(new Note("4", "4", "Note Number 4", "abc def ghi", "Wednesday 23-04-2022"));
        allNotes.add(new Note("5", "5", "Note Number 5", "abc def ghi", "Thursday 24-05-2022"));
        allNotes.add(new Note("6", "6", "Note Number 6", "abc def ghi", "Friday 25-06-2022"));
        allNotes.add(new Note("7", "7", "Note Number 7", "abc def ghi", "Saturday 26-07-2022"));


        recyclerView.setAdapter(adapter);
        return view;    }
}