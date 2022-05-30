package com.example.project2;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentViewHolder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteHolder> {

    private Context context;
    private List<Note> notes;
    private SharedPreferences sharedPreferences;
    private ProgressDialog dialog;
    private ViewNoteFragment viewNoteFragment = new ViewNoteFragment();
    private EditNoteFragment editNoteFragment = new EditNoteFragment();

    public NoteAdapter(Context context, List<Note> notes) {
        this.context = context;
        this.notes = notes;
        sharedPreferences = context.getSharedPreferences("Login", Context.MODE_PRIVATE);
        dialog = new ProgressDialog(context);
    }

    @NonNull
    @Override
    public NoteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.homepage_items, parent, false);
        return new NoteHolder(view);    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull NoteHolder holder, int position) {
        Note note = notes.get(position);
        holder.title.setText(note.getTitle());
        holder.label.setText("Label: " + note.getLabel());
        holder.date.setText("Created at: " + note.getDate());
        if(note.getPassword()){
            holder.ivNoteItemLock.setImageResource(R.drawable.ic_lock_close);
        }
        else{
            holder.ivNoteItemLock.setImageResource(R.drawable.ic_lock_open);
        }
        if(note.getPin()){
            holder.pin.setImageResource(R.drawable.ic_pin);
        }

        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                if(!note.getDelete()){
                    Bundle data = new Bundle();
                    data.putString("noteId", note.getNoteId());
                    viewNoteFragment.setArguments(data);
                    ((AppCompatActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.dashboard_container1, viewNoteFragment).commit();
                }
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
        ImageView ivNoteItemLock;
        ItemClickListener itemClickListener;
        ImageView ivNoteItemMenu, pin;


        public NoteHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.homepageNoteTitle);
            date = itemView.findViewById(R.id.homepageNoteDate);
            label = itemView.findViewById(R.id.homepageNoteLabel);
            ivNoteItemLock = itemView.findViewById(R.id.ivNoteItemLock);
            pin = itemView.findViewById(R.id.pin);

            ivNoteItemMenu = itemView.findViewById(R.id.ivNoteItemMenu);
            ivNoteItemMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Note note = notes.get(getAdapterPosition());
                    PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
                    popupMenu.inflate(R.menu.recycler_item_menu);
                    popupMenu.getMenu().findItem(R.id.recycler_item_changePassword).setVisible(false);

                    if(note.getPin()){
                        popupMenu.getMenu().findItem(R.id.recycler_item_pin).setVisible(false);
                    }else{
                        popupMenu.getMenu().findItem(R.id.recycler_item_unpin).setVisible(false);
                    }
                    if(note.getDelete()){
                        popupMenu.getMenu().findItem(R.id.recycler_item_pin).setVisible(false);
                        popupMenu.getMenu().findItem(R.id.recycler_item_unpin).setVisible(false);
                        popupMenu.getMenu().findItem(R.id.recycler_item_edit).setVisible(false);
                        popupMenu.getMenu().findItem(R.id.recycler_item_delete).setVisible(false);
                        popupMenu.getMenu().findItem(R.id.recycler_item_unlock).setVisible(false);
                        popupMenu.getMenu().findItem(R.id.recycler_item_lock).setVisible(false);
                    }else{
                        popupMenu.getMenu().findItem(R.id.recycler_item_restore).setVisible(false);
                        popupMenu.getMenu().findItem(R.id.recycler_item_super_delete).setVisible(false);
                    }
                    if(note.getPassword() && !note.getDelete()){
                        popupMenu.getMenu().findItem(R.id.recycler_item_changePassword).setVisible(true);
                        popupMenu.getMenu().findItem(R.id.recycler_item_lock).setVisible(false);
                    }else{
                        popupMenu.getMenu().findItem(R.id.recycler_item_unlock).setVisible(false);
                    }

                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            return menuItemClicked(menuItem);
                        }

                        private boolean menuItemClicked(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.recycler_item_pin:
                                    pinNote(note.getNoteId(), getAdapterPosition());
                                    break;
                                case R.id.recycler_item_unpin:
                                    unpinNote(note.getNoteId(), getAdapterPosition());
                                    break;
                                case R.id.recycler_item_edit:
                                    Bundle data = new Bundle();
                                    data.putString("noteId", note.getNoteId());
                                    editNoteFragment.setArguments(data);
                                    ((AppCompatActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.dashboard_container1, editNoteFragment).commit();
                                    break;
                                case R.id.recycler_item_lock:
                                    if(note.getNotePassword().equals("")){
                                        createNewNotePasswordAlert(note.getNoteId(), getAdapterPosition());
                                    }else{
                                        lockNote(note.getNoteId(), "", getAdapterPosition());
                                    }
                                    break;
                                case R.id.recycler_item_changePassword:
                                    createChangeNotePassAlert(note.getNoteId(), getAdapterPosition());
                                    break;
                                case R.id.recycler_item_unlock:
                                    createVerifyNotePasswordAlert(note.getNoteId(), getAdapterPosition());
                                    break;
                                case R.id.recycler_item_delete:
                                    deleteNote(note.getNoteId(), getAdapterPosition());
                                    break;
                                case R.id.recycler_item_restore:
                                    restoreNote(note.getNoteId(), getAdapterPosition());
                                    break;
                                case R.id.recycler_item_super_delete:
                                    superDeleteNote(note.getNoteId(), getAdapterPosition());
                            }
                            return true;
                        }
                    });
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

        public void pinNote(String noteId, int position) {
            loading(true);
            String accessToken = sharedPreferences.getString("accessToken", "");
            OkHttpClient client = new OkHttpClient();
            String createStudentURL = "https://note-app-lake.vercel.app/notes/pin";
            RequestBody formBody = new FormBody.Builder()
                    .add("noteId", noteId)
                    .add("isPin", "true")
                    .build();
            Request request = new Request.Builder()
                    .url(createStudentURL)
                    .header("Authorization", "Bear " + accessToken)
                    .put(formBody)
                    .build();

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
                        ((AppCompatActivity)context).runOnUiThread(new Runnable(){
                            @Override
                            public void run(){
                                if(code == 200){
                                    notes.remove(position);
                                    NoteAdapter.this.notifyItemRemoved(position);
                                    Toast.makeText( ((AppCompatActivity)context),"Set pin success", Toast.LENGTH_SHORT).show();

                                }else{
                                    Toast.makeText( ((AppCompatActivity)context),"Set pin failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } catch (JSONException e) {
                        Log.d("onResponse", e.getMessage());
                    }
                }
            });
        }

        public void unpinNote(String noteId, int position) {
            loading(true);
            String accessToken = sharedPreferences.getString("accessToken", "");
            OkHttpClient client = new OkHttpClient();
            String createStudentURL = "https://note-app-lake.vercel.app/notes/pin";
            RequestBody formBody = new FormBody.Builder()
                    .add("noteId", noteId)
                    .add("isPin", "false")
                    .build();
            Request request = new Request.Builder()
                    .url(createStudentURL)
                    .header("Authorization", "Bear " + accessToken)
                    .put(formBody)
                    .build();

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
                        ((AppCompatActivity)context).runOnUiThread(new Runnable(){
                            @Override
                            public void run(){
                                if(code == 200){
                                    notes.remove(position);
                                    NoteAdapter.this.notifyItemRemoved(position);
                                    Toast.makeText( ((AppCompatActivity)context),"Unpin success", Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(((AppCompatActivity)context),"Set unpin failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } catch (JSONException e) {
                        Log.d("onResponse", e.getMessage());
                    }
                }
            });
        }

        public void lockNote(String noteId, @NonNull String password, int position) {
            loading(true);
            String accessToken = sharedPreferences.getString("accessToken", "");
            OkHttpClient client = new OkHttpClient();
            String createStudentURL = "https://note-app-lake.vercel.app/notes/password/set";
            FormBody.Builder formBody = new FormBody.Builder().add("noteId", noteId);
            if(!password.equals("")){
                formBody.add("password", password);
            }
            FormBody form =  formBody.build();
            Request request = new Request.Builder()
                    .url(createStudentURL)
                    .header("Authorization", "Bear " + accessToken)
                    .put(form)
                    .build();
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
                        ((AppCompatActivity)context).runOnUiThread(new Runnable(){
                            @Override
                            public void run(){
                                if(code == 200){
                                    notes.get(position).setPassword(true);
                                    NoteAdapter.this.notifyItemChanged(position);
                                    Toast.makeText( ((AppCompatActivity)context),"Lock success", Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText( ((AppCompatActivity)context),"Set lock failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } catch (JSONException e) {
                        Log.d("onResponse", e.getMessage());
                    }
                }
            });
        }

        public void changeNotePassword(String noteId, String oldPassword, String newPassword, int position) {
            loading(true);
            String accessToken = sharedPreferences.getString("accessToken", "");
            OkHttpClient client = new OkHttpClient();
            String createStudentURL = "https://note-app-lake.vercel.app/notes/password/change";
            FormBody.Builder formBody = new FormBody.Builder().add("noteId", noteId).add("oldPassword", oldPassword).add("password", newPassword);

            FormBody form =  formBody.build();
            Request request = new Request.Builder()
                    .url(createStudentURL)
                    .header("Authorization", "Bear " + accessToken)
                    .put(form)
                    .build();
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
                        ((AppCompatActivity)context).runOnUiThread(new Runnable(){
                            @Override
                            public void run(){
                                if(code == 200){
                                    Toast.makeText( ((AppCompatActivity)context),"Change password success", Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText( ((AppCompatActivity)context),"Password invalid", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } catch (JSONException e) {
                        Log.d("onResponse", e.getMessage());
                    }
                }
            });
        }

        public void unlockNote(String noteId, String password, int position) {
            loading(true);
            String accessToken = sharedPreferences.getString("accessToken", "");
            OkHttpClient client = new OkHttpClient();
            String createStudentURL = "https://note-app-lake.vercel.app/notes/password/unset";
            FormBody.Builder formBody = new FormBody.Builder().add("noteId", noteId).add("password", password);
            FormBody form =  formBody.build();
            Request request = new Request.Builder()
                    .url(createStudentURL)
                    .header("Authorization", "Bear " + accessToken)
                    .put(form)
                    .build();
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
                        ((AppCompatActivity)context).runOnUiThread(new Runnable(){
                            @Override
                            public void run(){
                                if(code == 200){
                                    notes.get(position).setPassword(false);
                                    NoteAdapter.this.notifyItemChanged(position);
                                    Toast.makeText( ((AppCompatActivity)context),"Unlock success", Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText( ((AppCompatActivity)context),"Password invalid", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } catch (JSONException e) {
                        Log.d("onResponse", e.getMessage());
                    }
                }
            });
        }

        public void deleteNote(String noteId, int position){
            loading(true);
            String accessToken = sharedPreferences.getString("accessToken", "");
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url("https://note-app-lake.vercel.app/notes/delete/noteId/" + noteId).header("Authorization", "Bear " + accessToken).build();
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
                        ((AppCompatActivity)context).runOnUiThread(new Runnable(){
                            @Override
                            public void run(){
                                if(code == 200){
                                    notes.remove(position);
                                    NoteAdapter.this.notifyItemRemoved(position);
                                    Toast.makeText( ((AppCompatActivity)context),"Delete success", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    Toast.makeText(((AppCompatActivity)context),"Delete note failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } catch (JSONException e) {
                        Log.d("onResponse", e.getMessage());
                    }
                }
            });
        }

        public void restoreNote(String noteId, int position){
            loading(true);
            String accessToken = sharedPreferences.getString("accessToken", "");
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url("https://note-app-lake.vercel.app/notes/unDelete/noteId/" + noteId).header("Authorization", "Bear " + accessToken).build();
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
                        ((AppCompatActivity)context).runOnUiThread(new Runnable(){
                            @Override
                            public void run(){
                                if(code == 200){
                                    notes.remove(position);
                                    NoteAdapter.this.notifyItemRemoved(position);
                                    Toast.makeText( ((AppCompatActivity)context),"Restore success", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    Toast.makeText(((AppCompatActivity)context),"Restore note failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } catch (JSONException e) {
                        Log.d("onResponse", e.getMessage());
                    }
                }
            });
        }

        public void superDeleteNote(String noteId, int position){
            loading(true);
            String accessToken = sharedPreferences.getString("accessToken", "");
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url("https://note-app-lake.vercel.app/notes/delete/super/noteId/" + noteId).header("Authorization", "Bear " + accessToken).build();
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
                        ((AppCompatActivity)context).runOnUiThread(new Runnable(){
                            @Override
                            public void run(){
                                if(code == 200){
                                    notes.remove(position);
                                    NoteAdapter.this.notifyItemRemoved(position);
                                    Toast.makeText(((AppCompatActivity)context),"Delete success", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    Toast.makeText(((AppCompatActivity)context),"Delete note failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } catch (JSONException e) {
                        Log.d("onResponse", e.getMessage());
                    }
                }
            });
        }

        public void createNewNotePasswordAlert(String noteId, int position){
            AlertDialog builder;
            builder = new AlertDialog.Builder(context).create();
            builder.setCanceledOnTouchOutside(false);
            builder.setTitle("Set note password");

            final EditText newPassword = new EditText(context);
            newPassword.setHint("Enter new password");
            final EditText confirmNewPassword = new EditText(context);
            confirmNewPassword.setHint("Enter confirm password");

            newPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            LinearLayout lp = new LinearLayout(context);
            lp.setOrientation(LinearLayout.VERTICAL);
            lp.addView(newPassword);
            lp.addView(confirmNewPassword);
            builder.setView(lp);

            builder.setButton("Ok",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            if(newPassword.getText().toString().trim().length() < 6){
                                Toast.makeText(context, "Password invalid", Toast.LENGTH_SHORT).show();
                            }else if(!newPassword.getText().toString().trim().equals(confirmNewPassword.getText().toString().trim())){
                                Toast.makeText(context, "Password confirm invalid", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                lockNote(noteId, newPassword.getText().toString().trim(), position);
                            }
                        }
                    });
            builder.show();
        }

        public void createVerifyNotePasswordAlert(String noteId, int position){
            AlertDialog builder;
            builder = new AlertDialog.Builder(context).create();
            builder.setCanceledOnTouchOutside(false);
            builder.setTitle("Verify");

            final EditText password = new EditText(context);
            password.setHint("Enter note password");

            password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            LinearLayout lp = new LinearLayout(context);
            lp.setOrientation(LinearLayout.VERTICAL);
            lp.addView(password);
            builder.setView(lp);

            builder.setButton("Ok",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            if(password.getText().toString().trim().equals("")){
                                Toast.makeText(context, "Password invalid", Toast.LENGTH_SHORT).show();
                            } else {
                                unlockNote(noteId, password.getText().toString().trim(), position);
                            }
                        }
                    });
            builder.show();
        }

        public void createChangeNotePassAlert(String noteId, int position){
            AlertDialog builder;
            builder = new AlertDialog.Builder(context).create();
            builder.setCanceledOnTouchOutside(false);
            builder.setTitle("Change Password");

            final EditText oldPassword = new EditText(context);
            oldPassword.setHint("Enter note password");
            final EditText newPassword = new EditText(context);
            newPassword.setHint("Enter new password");
            final EditText confirm = new EditText(context);
            confirm.setHint("Confirm new password");

            oldPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            LinearLayout lp = new LinearLayout(context);
            lp.setOrientation(LinearLayout.VERTICAL);
            lp.addView(oldPassword);
            lp.addView(newPassword);
            lp.addView(confirm);
            builder.setView(lp);

            builder.setButton("Ok",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            if(newPassword.getText().toString().trim().length() < 6){
                                Toast.makeText(context, "New password invalid", Toast.LENGTH_SHORT).show();
                            } else if (!confirm.getText().toString().trim().equals(newPassword.getText().toString().trim())) {
                                Toast.makeText(context, "Password confirm invalid", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                changeNotePassword(noteId, oldPassword.getText().toString().trim(), newPassword.getText().toString().trim(), position);
                            }
                        }
                    });
            builder.show();
        }
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

    public interface ItemClickListener {
        void onClick(View view, int position,boolean isLongClick);
    }
}