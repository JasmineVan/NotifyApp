package com.example.project2;

import android.content.Context;
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

public class TrashFragment extends Fragment {

    private View view;
    private NoteAdapter adapter;
    private RecyclerView recyclerView;
    public List<Note> trashNotes;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_trash, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager((this.getContext())));
        trashNotes = new ArrayList<>();
        trashNotes.add(new Note("1", "999", "Thuong Note", "test", "this is trash note",
                "01-01-2000",true, true, true, "" ));
        trashNotes.add(new Note("2", "888", "Thuong Note 2", "test", "this is trash note",
                "01-01-2000",true, true, true, "" ));
        adapter = new NoteAdapter(this.getContext(), trashNotes);

        //recyclerView setting
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext(), RecyclerView.VERTICAL, false));
        //recyclerView.addItemDecoration(new DividerItemDecoration(this.getContext(), RecyclerView.VERTICAL));
        recyclerView.setAdapter(adapter);

        return view;
    }
}