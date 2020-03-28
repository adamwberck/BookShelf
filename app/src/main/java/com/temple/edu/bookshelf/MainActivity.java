package com.temple.edu.bookshelf;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.res.Configuration;
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
        addTestBooks();
        setContentView(R.layout.main_layout);
        bookListFragment = BookListFragment.newInstance(bookShelf);
        bookDetailsFragment = BookDetailsFragment.newInstance(null);

        hasTwoContainers = findViewById(R.id.only_container)==null;
        FragmentManager fm = getSupportFragmentManager();
        if(hasTwoContainers) {
            fm.beginTransaction().replace(R.id.one_container, bookListFragment).commit();
            fm.beginTransaction().replace(R.id.two_container, bookDetailsFragment).commit();
        }else{
            Fragment fragment = fm.findFragmentById(R.id.only_container);
            if (fragment != null) {
                fm.beginTransaction().remove(fragment).commit();
            }
            fm.beginTransaction().add(R.id.only_container, bookListFragment).commit();
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        bookDetailsFragment =null;
        bookListFragment = null;
    }

    @Override
    public void handleBook(Map<String, String> book) {
        if(!hasTwoContainers){
            FragmentManager fm = getSupportFragmentManager();
            fm.beginTransaction().replace(R.id.only_container, bookDetailsFragment)
                    .addToBackStack(null).commit();
        }
        bookDetailsFragment.displayBook(book);
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
