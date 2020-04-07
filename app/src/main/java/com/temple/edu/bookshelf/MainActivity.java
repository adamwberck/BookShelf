package com.temple.edu.bookshelf;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.graphics.Bitmap;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements BookListFragment.HandlesBook,
        DownloadTask.HandlesBooks, SearchFragment.HandleSearchTerm {
    public static final String SEARCH_URL = "https://kamorris.com/lab/abp/booksearch.php?search=";
    List<Book> bookShelf = new ArrayList<>(10);
    private boolean hasTwoContainers;
    private BookListFragment bookListFragment;
    private BookDetailsFragment bookDetailsFragment;
    private SearchFragment searchFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_layout);


        bookListFragment = BookListFragment.newInstance(bookShelf);
        bookDetailsFragment = BookDetailsFragment.newInstance(null);
        searchFragment = SearchFragment.newInstance("");
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction().replace(R.id.search_container,searchFragment).commit();




        searchForBooks("");

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
    public void handleBook(Book book) {
        if(!hasTwoContainers){
            FragmentManager fm = getSupportFragmentManager();
            fm.beginTransaction().replace(R.id.only_container, bookDetailsFragment)
                    .addToBackStack(null).commit();
        }
        bookDetailsFragment.displayBook(book);
    }

    @Override
    public void handleTerm(String term) {
        searchForBooks(term);
    }
}
