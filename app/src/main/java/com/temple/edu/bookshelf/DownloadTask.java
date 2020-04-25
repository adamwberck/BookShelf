package com.temple.edu.bookshelf;

import android.content.Context;
import android.os.AsyncTask;

import java.util.List;


public class DownloadTask extends AsyncTask<String,Void,Void> {
    public HandlesBooks booksHandler;
    private List<Book> books;
    private boolean isComplete;

    public DownloadTask(Context context,boolean isComplete) {
        booksHandler = (HandlesBooks) context;
        this.isComplete = isComplete;
    }

    @Override
    protected Void doInBackground(String... strings) {
        books = new BookFetcher().fetch(strings[0]);
        return null;
    }

    @Override
    protected void onPostExecute (Void v){
        booksHandler.setBookShelf(books,isComplete);
        books = null;
    }

    public interface HandlesBooks{
        void setBookShelf(List<Book> books,boolean isCompleteList);
    }
}

