package com.temple.edu.bookshelf;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements BookListFragment.HandlesBook {
    List<Map<String,String>> bookShelf = new ArrayList<>(10);
    private boolean hasTwoContainers;
    private BookListFragment bookListFragment;
    private BookDetailsFragment bookDetailsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentManager fm = getSupportFragmentManager();
        addTestBooks();
        setContentView(R.layout.main_layout);

        bookListFragment = BookListFragment.newInstance(bookShelf);
        bookDetailsFragment = BookDetailsFragment.newInstance(null);

        hasTwoContainers = findViewById(R.id.only_container)==null;
        if(hasTwoContainers) {
            Fragment fragment = fm.findFragmentById(R.id.one_container);
            if (fragment == null) {
                fm.beginTransaction().add(R.id.one_container, bookListFragment).commit();
            }
            fragment = fm.findFragmentById(R.id.two_container);
            if (fragment == null) {
                fm.beginTransaction().add(R.id.two_container, bookDetailsFragment).commit();
            }
        }else{
            Fragment fragment = fm.findFragmentById(R.id.only_container);
            if (fragment == null) {
                fm.beginTransaction().add(R.id.only_container, bookListFragment).commit();
            }
        }
    }

    @Override
    public void handleBook(Map<String, String> book) {
        if(!hasTwoContainers){
            FragmentManager fm = getSupportFragmentManager();
            fm.beginTransaction().remove(bookListFragment).commit();
            fm.beginTransaction().add(R.id.only_container, bookDetailsFragment).commit();
        }
        bookDetailsFragment.displayBook(book);
    }

    @Override
    public void onBackPressed(){
        if(!hasTwoContainers){
            FragmentManager fm = getSupportFragmentManager();
            fm.beginTransaction().remove(bookDetailsFragment).commit();
            fm.beginTransaction().add(R.id.only_container, bookListFragment).commit();
        }
    }

    private class Book{
        private static final String TITLE = "title";
        private static final String AUTHOR = "author";
        Map<String,String> map = new HashMap<>(2);
        Book(String title,String author){
            map.put(TITLE,title);
            map.put(AUTHOR,author);
        }
    }

    private void addTestBooks(){
        Book book = new Book("Dog Soup","J. Lee");
        addBook(book);
        book = new Book("Cute Cats","Albert Johnson");
        addBook(book);
        book = new Book("Gorilla Games","James McGill");
        addBook(book);
        book = new Book("Ace Player","Lily Kim");
        addBook(book);
        book = new Book("Monkey Talk","Guy Smith");
        addBook(book);//5
        book = new Book("Hippo Food","R.A. Griffin");
        addBook(book);
        book = new Book("Parrot Speaker","Link Walsh");
        addBook(book);
        book = new Book("Work for Y O U","Vince Peterson");
        addBook(book);
        book = new Book("Don't Look Now","Alison Graham");
        addBook(book);
        book = new Book("Words are Wind","Madison McNickle");
        addBook(book);
    }

    private void addBook(Book book) {
        bookShelf.add(book.map);
    }
}
