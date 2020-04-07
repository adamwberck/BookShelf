package com.temple.edu.bookshelf;

import android.content.Context;
import android.os.AsyncTask;

import java.util.List;


public class DownloadTask extends AsyncTask<String,Void,Void> {
    public HandlesBooks booksHandler;
    private List<Book> books;

    public DownloadTask(Context context) {
        booksHandler = (HandlesBooks) context;
    }

    @Override
    protected Void doInBackground(String... strings) {
        books = new BookFetcher().fetch(strings[0]);
        return null;
    }

    @Override
    protected void onPostExecute (Void v){
        booksHandler.setBookShelf(books);
        books = null;
    }

    public interface HandlesBooks{
        void setBookShelf(List<Book> books);
    }
}

