package com.temple.edu.bookshelf;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.graphics.Bitmap;
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
        DownloadTask.HandlesBooks, SearchFragment.HandleSearchTerm {
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
        load();

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

    @Override
    public void onStop(){
        super.onStop();
        save();
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

    private void load(){
        try {
            File file = new File(this.getFilesDir(), FILE_BOOKSHELF);
            FileInputStream fis = new FileInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(fis);
            bookShelf = (List<Book>) ois.readObject();
        }
        catch (IOException ioe){
            ioe.printStackTrace();
        }
        catch (ClassNotFoundException cnfe){
            cnfe.printStackTrace();
        }

        try {
            File file = new File(this.getFilesDir(), FILE_BOOK);
            FileInputStream fis = new FileInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(fis);
            book = (Book) ois.readObject();
        }
        catch (IOException ioe){
            ioe.printStackTrace();
        }
        catch (ClassNotFoundException cnfe){
            cnfe.printStackTrace();
        }
        try {
            File file = new File(this.getFilesDir(), FILE_TERM);
            FileInputStream fis = new FileInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(fis);
            search = (String) ois.readObject();
        }
        catch (IOException ioe){
            ioe.printStackTrace();
        }
        catch (ClassNotFoundException cnfe){
            cnfe.printStackTrace();
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
    }

    @Override
    public void handleTerm(String term) {
        search = term;
        searchForBooks(term);
    }
}
