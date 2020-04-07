package com.temple.edu.bookshelf;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class BookFetcher {
    private static final String TAG = "FETCH";

    public byte[] getUrlBytes(String urlSpec) throws IOException{
        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        try{
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();
            if(connection.getResponseCode() != HttpURLConnection.HTTP_OK){
                throw new IOException(connection.getResponseMessage() +
                        ": with" +
                        urlSpec);
            }

            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer)) > 0){
                out.write(buffer,0,bytesRead);
            }
            out.close();
            return out.toByteArray();
        }
        finally {
            connection.disconnect();
        }
    }

    public List<Book> fetch(String url) {
        List<Book> books = new ArrayList<>();
        try{
            String jsonString = getUrlString(url);
            JSONArray jsonArray = new JSONArray(jsonString);
            parseItems(books,jsonArray);
        }catch (JSONException je){
            Log.e(TAG,"json exception");
        }catch (IOException ioe){
            Log.e(TAG,"ioe exception");
        }
        return books;
    }

    private void parseItems(List<Book> books, JSONArray jsonArray) throws IOException,JSONException{
        for(int i=0;i<jsonArray.length();i++){
            JSONObject bookJsonObject = jsonArray.getJSONObject(i);
            Book book = new Book(bookJsonObject);
            books.add(book);
        }
    }

    private String getUrlString(String urlSpec) throws IOException{
        return new String(getUrlBytes(urlSpec));
    }
}
