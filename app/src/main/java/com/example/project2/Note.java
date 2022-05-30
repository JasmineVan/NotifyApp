package com.example.project2;

import java.util.ArrayList;

public class Note {

    public static final int TYPE_LIST = 1;
    public static final int TYPE_GRID = 2;

    private int typeDisplay;
    private String noteId;
    private String userId;
    private String title;
    private String label;
    private String content;
    private String noteMedia;
    private String mediaType;
    private String date;
    private Boolean isPassword;
    private Boolean isPin;
    private Boolean isDelete;
    private String notePassword;

    public Note (String noteId, String userId, String title, String label, String content, String date, Boolean isPassword, Boolean isPin, Boolean isDelete, String notePassword){
        this.noteId = noteId;
        this.userId = userId;
        this.title = title;
        this.label = label;
        this.date = date;
        this.content = content;
        this.isPassword = isPassword;
        this.isPin = isPin;
        this.isDelete = isDelete;
        this.noteMedia = "";
        this.mediaType = "";
        this.notePassword = notePassword;
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

    public Boolean getPin() {
        return isPin;
    }

    public void setPin(Boolean pin) {
        isPin = pin;
    }

    public Boolean getDelete() {
        return isDelete;
    }

    public void setDelete(Boolean delete) {
        isDelete = delete;
    }

    public String getNotePassword() {
        return notePassword;
    }

    public void setNotePassword(String notePassword) {
        this.notePassword = notePassword;
    }

    public int getTypeDisplay() {
        return typeDisplay;
    }

    public void setTypeDisplay(int typeDisplay) {
        this.typeDisplay = typeDisplay;
    }
}
