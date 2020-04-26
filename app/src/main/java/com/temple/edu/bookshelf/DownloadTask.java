package com.temple.edu.bookshelf;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.util.List;


public class DownloadTask extends AsyncTask<String,Void,Void> {
    private static final String TAG = "DownTask";
    public HandlesBooks booksHandler;
    private List<Book> books;
    private boolean isComplete;

    public DownloadTask(Context context,boolean isComplete) {
        booksHandler = (HandlesBooks) context;
        Log.i(TAG,"start "+this.hashCode());
        this.isComplete = isComplete;
    }

    @Override
    protected Void doInBackground(String... strings) {
        books = new BookFetcher().fetch(strings[0]);
        return null;
    }

    @Override
    protected void onPostExecute (Void v){
        Log.i(TAG,"end "+this.hashCode());
        booksHandler.setBookShelf(books,isComplete);
        books = null;
    }

    public interface HandlesBooks{
        void setBookShelf(List<Book> books,boolean isCompleteList);
    }
}

