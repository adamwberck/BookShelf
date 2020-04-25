package com.temple.edu.bookshelf;

import android.graphics.Bitmap;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class Book implements Serializable {
    private int id;
    private String title;
    private String author;
    private String coverURL;
    private int duration;


    public Book(int id, String title, String author, String coverURL, int duration) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.coverURL = coverURL;
    }

    public Book(JSONObject bookJsonObject) throws JSONException {
        id = bookJsonObject.getInt("book_id");
        title = bookJsonObject.getString("title");
        author = bookJsonObject.getString("author");
        coverURL = bookJsonObject.getString("cover_url");
    }


    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getCoverURL() {
        return coverURL;
    }
}
