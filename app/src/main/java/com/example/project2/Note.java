package com.example.project2;

import java.util.ArrayList;

public class Note {
    private String noteId;
    private String userId;
    private String title;
    private String label;
    private String content;
    private String noteMedia;
    private String mediaType;
    private String date;
    private Boolean isPassword;

    public Note (String noteId, String userId, String title, String content, String date){
        this.noteId = noteId;
        this.userId = userId;
        this.title = title;
        this.date = date;
        this.content = content;
        this.noteMedia = "";
        this.mediaType = "";
        this.isPassword = false;
    }

    public Note (String noteId, String userId, String title, String label, String content, String date, Boolean isPassword){
        this.noteId = noteId;
        this.userId = userId;
        this.title = title;
        this.label = label;
        this.date = date;
        this.content = content;
        this.isPassword = isPassword;
        this.noteMedia = "";
        this.mediaType = "";
    }

    public Note (String noteId, String title, String userId, String label, String content, String noteMedia, String mediaType, String date, Boolean isPassword){
        this.noteId = noteId;
        this.userId = userId;
        this.title = title;
        this.label = label;
        this.content = content;
        this.noteMedia = noteMedia;
        this.mediaType = mediaType;
        this.date = date;
        this.isPassword = isPassword;
    }

    public String getNoteId() {
        return noteId;
    }

    public void setNoteId(String noteId) {
        this.noteId = noteId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getNoteMedia() {
        return noteMedia;
    }

    public void setNoteMedia(String noteMedia) {
        this.noteMedia = noteMedia;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public Boolean getPassword() {
        return isPassword;
    }

    public void setPassword(Boolean password) {
        isPassword = password;
    }
}
