package com.temple.edu.bookshelf;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements BookListFragment.HandlesBook,
        DownloadTask.HandlesBooks, SearchFragment.HandleSearchTerm, CoverTask.CoverHandler {
    public static final String SEARCH_URL = "https://kamorris.com/lab/abp/booksearch.php?search=";
    private static final String FILE_BOOKSHELF = "bookshelf";
    private static final String FILE_BOOK = "book";
    private static final String FILE_TERM = "term";
    private static final String TAG = "MainAct";
    private List<Book> bookShelf = new ArrayList<>(10);
    private Book book;
    private String search;
    private boolean hasTwoContainers;
    private BookListFragment bookListFragment;
    private BookDetailsFragment bookDetailsFragment;
    private SearchFragment searchFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        load(savedInstanceState);

        setContentView(R.layout.main_layout);


        bookListFragment = BookListFragment.newInstance(bookShelf);
        bookDetailsFragment = BookDetailsFragment.newInstance(book);

        searchFragment = SearchFragment.newInstance(search);
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction().replace(R.id.search_container,searchFragment).commit();



        hasTwoContainers = findViewById(R.id.only_container)==null;

        if(hasTwoContainers) {
            fm.beginTransaction().replace(R.id.one_container, bookListFragment).commit();
            fm.beginTransaction().replace(R.id.two_container, bookDetailsFragment).commit();
            if(book!=null && bookDetailsFragment!=null){
                new CoverTask(this).execute(book.getCoverURL());
            }
        }else{
            fm.beginTransaction().replace(R.id.only_container, bookListFragment).commit();
        }

    }

    @Override
    public void setBookShelf(List<Book> books){
        bookShelf = books;
        bookListFragment.setBooks(books);
    }

    private void searchForBooks(String url) {
        DownloadTask task = new DownloadTask(this);
        task.execute(SEARCH_URL+url);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        bookDetailsFragment =null;
        bookListFragment = null;
    }

    private void save(){
        try {
            File file = new File(this.getFilesDir(),FILE_BOOKSHELF);
            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(bookShelf);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            File file = new File(this.getFilesDir(), FILE_BOOK);
            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(book);
        } catch (IOException e){
            e.printStackTrace();
        }
        try {
            File file = new File(this.getFilesDir(), FILE_TERM);
            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(search);
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private void load(Bundle bundle){
        bookShelf = bundle==null? null : (List<Book>) bundle.getSerializable(FILE_BOOKSHELF);
        if(bookShelf==null) {
            try {
                File file = new File(this.getFilesDir(), FILE_BOOKSHELF);
                FileInputStream fis = new FileInputStream(file);
                ObjectInputStream ois = new ObjectInputStream(fis);
                bookShelf = (List<Book>) ois.readObject();
            } catch (IOException | ClassNotFoundException ioe) {
                ioe.printStackTrace();
            }
        }
        book = bundle==null? null :(Book) bundle.getSerializable(FILE_BOOK);
        if(book == null) {
            try {
                File file = new File(this.getFilesDir(), FILE_BOOK);
                FileInputStream fis = new FileInputStream(file);
                ObjectInputStream ois = new ObjectInputStream(fis);
                book = (Book) ois.readObject();
            } catch (IOException | ClassNotFoundException ioe) {
                ioe.printStackTrace();
            }
        }
        search = bundle==null? null : bundle.getString(FILE_TERM,null);
        if(search==null){
            try {
                File file = new File(this.getFilesDir(), FILE_TERM);
                FileInputStream fis = new FileInputStream(file);
                ObjectInputStream ois = new ObjectInputStream(fis);
                search = (String) ois.readObject();
            } catch (IOException | ClassNotFoundException ioe) {
                ioe.printStackTrace();
            }
        }
    }

    @Override
    public void handleBook(Book book) {
        this.book = book;
        if(!hasTwoContainers){
            FragmentManager fm = getSupportFragmentManager();
            fm.beginTransaction().replace(R.id.only_container, bookDetailsFragment)
                    .addToBackStack(null).commit();
        }
        bookDetailsFragment.displayBook(book);
        bookDetailsFragment.setBitmap(null);
        new CoverTask(this).execute(book.getCoverURL());
    }

    @Override
    public void handleTerm(String term) {
        search = term;
        searchForBooks(term);
    }

    @Override
    public void handleCover(Bitmap cover) {
        if(bookDetailsFragment!=null)
            bookDetailsFragment.setBitmap(cover);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putSerializable(FILE_BOOKSHELF, (Serializable) bookShelf);
        savedInstanceState.putSerializable(FILE_BOOK, book);
        savedInstanceState.putString(FILE_TERM, search);
        save();
    }

}
