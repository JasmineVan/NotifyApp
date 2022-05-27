package com.example.project2;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentViewHolder;

import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteHolder> {

    private Context context;
    private List<Note> notes;
    private ViewNoteFragment viewNoteFragment = new ViewNoteFragment();

    public NoteAdapter(Context context, List<Note> notes) {
        this.context = context;
        this.notes = notes;
    }

    @NonNull
    @Override
    public NoteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.homepage_items, parent, false);
        return new NoteHolder(view);    }

    @Override
    public void onBindViewHolder(@NonNull NoteHolder holder, int position) {
        Note note = notes.get(position);
        holder.title.setText(note.getTitle());
        holder.label.setText("Label: " + note.getLabel());
        holder.date.setText("Created at: " + note.getDate());

        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                Toast.makeText(context, " "+ note.getNoteId(), Toast.LENGTH_SHORT).show();
                Bundle data = new Bundle();
                data.putString("noteId", note.getNoteId());
                viewNoteFragment.setArguments(data);
                ((AppCompatActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.dashboard_container1, viewNoteFragment).commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }


    //Create new view holder
    class NoteHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView title, date, label;
        private ItemClickListener itemClickListener;
        private ImageView ivNoteItemMenu;

        public NoteHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.homepageNoteTitle);
            date = itemView.findViewById(R.id.homepageNoteDate);
            label = itemView.findViewById(R.id.homepageNoteLabel);
            ivNoteItemMenu = itemView.findViewById(R.id.ivNoteItemMenu);
            ivNoteItemMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
                    popupMenu.inflate(R.menu.recycler_item_menu);
                    popupMenu.show();
                }
            });

            itemView.setOnClickListener(this);
        }

        public void setItemClickListener(ItemClickListener itemClickListener)
        {
            this.itemClickListener = itemClickListener;
        }

        @Override
        public void onClick(View view) {
            itemClickListener.onClick(view, getAdapterPosition(), false);
        }
    }
    public interface ItemClickListener {
        void onClick(View view, int position,boolean isLongClick);
    }
}
