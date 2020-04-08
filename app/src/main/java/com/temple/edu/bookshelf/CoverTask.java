package com.temple.edu.bookshelf;

import android.graphics.Bitmap;
import android.os.AsyncTask;

public class CoverTask extends AsyncTask<String,Void,Void> {
    private final CoverHandler coverHandler;
    private Bitmap cover;

    public CoverTask(CoverHandler coverHandler) {
        this.coverHandler = coverHandler;
    }

    @Override
    protected Void doInBackground(String... strings) {
        cover = new BookFetcher().getImageBitmap(strings[0]);
        return null;
    }

    @Override
    protected void onPostExecute (Void v){
        coverHandler.handleCover(cover);
    }

    public interface CoverHandler {
        void handleCover(Bitmap cover);
    }
}
