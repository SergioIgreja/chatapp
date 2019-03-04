package com.example.chat.models;

import android.graphics.Bitmap;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Message {

    @SerializedName("index")
    @Expose
    private Integer index;
    @SerializedName("author")
    @Expose
    private Boolean author;
    @SerializedName("text")
    @Expose
    private String text;
    private Bitmap image;

    public Message(Integer index, Boolean author, String text) {
        this.setIndex(index);
        this.setAuthor(author);
        this.setText(text);
    }

    public Message(Integer index, Boolean author, Bitmap image) {
        this.setIndex(index);
        this.setAuthor(author);
        this.setImage(image);
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public Boolean getAuthor() {
        return author;
    }

    public void setAuthor(Boolean author) {
        this.author = author;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Bitmap getImage() { return image; };

    public void setImage(Bitmap image) { this.image = image; };
}