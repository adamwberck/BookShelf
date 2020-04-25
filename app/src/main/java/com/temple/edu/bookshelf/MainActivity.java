package com.temple.edu.bookshelf;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import edu.temple.audiobookplayer.AudiobookService;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements BookListFragment.HandlesBook,
        DownloadTask.HandlesBooks, SearchFragment.HandleSearchTerm, CoverTask.CoverHandler,
        BookDetailsFragment.HandlesPlay {
    public static final String SEARCH_URL = "https://kamorris.com/lab/abp/booksearch.php?search=";
    private static final String FILE_BOOKSHELF = "bookshelf";
    private static final String FILE_BOOK = "detailBook";
    private static final String FILE_TERM = "term";
    private static final String FILE_NOW_PLAYING = "nowPlaying";
    private static final String TAG = "MainAct";
    private List<Book> bookShelf = new ArrayList<>(10);
    private Book detailBook;
    private Book nowPlayingBook;
    private String search;
    private boolean hasTwoContainers;
    private BookListFragment bookListFragment;
    private BookDetailsFragment bookDetailsFragment;
    private SearchFragment searchFragment;
    private boolean mBound;
    private AudiobookService.MediaControlBinder binder;
    private boolean isBound;
    private TextView nowPlayingText;
    private ImageButton playPauseButton;
    private ImageButton stopButton;
    private SeekBar audioSeekbar;
    private ProgressHandler progressHandler;
    private int position;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        load(savedInstanceState);

        setContentView(R.layout.main_layout);

        //set up now playing
        nowPlayingText = findViewById(R.id.text_now_playing);
        playPauseButton = findViewById(R.id.button_play_pause);
        stopButton = findViewById(R.id.button_stop);
        audioSeekbar = findViewById(R.id.seekbar_audio);

        bookListFragment = BookListFragment.newInstance(bookShelf);
        bookDetailsFragment = BookDetailsFragment.newInstance(detailBook);

        searchFragment = SearchFragment.newInstance(search);
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction().replace(R.id.search_container,searchFragment).commit();

        hasTwoContainers = findViewById(R.id.only_container)==null;

        if(hasTwoContainers) {
            fm.beginTransaction().replace(R.id.one_container, bookListFragment).commit();
            fm.beginTransaction().replace(R.id.two_container, bookDetailsFragment).commit();
            if(detailBook !=null && bookDetailsFragment!=null){
                new CoverTask(this).execute(detailBook.getCoverURL());
            }
        }else{
            fm.beginTransaction().replace(R.id.only_container, bookListFragment).commit();
        }

    }

    @Override
    protected void onStart(){
        super.onStart();
        Intent intent = new Intent(this,AudiobookService.class);
        bindService(intent,connection,Context.BIND_AUTO_CREATE);

    }

    @Override
    protected void onStop(){
        super.onStop();
        unbindService(connection);
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
            oos.writeObject(detailBook);
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
        detailBook = bundle==null? null :(Book) bundle.getSerializable(FILE_BOOK);
        if(detailBook == null) {
            try {
                File file = new File(this.getFilesDir(), FILE_BOOK);
                FileInputStream fis = new FileInputStream(file);
                ObjectInputStream ois = new ObjectInputStream(fis);
                detailBook = (Book) ois.readObject();
            } catch (IOException | ClassNotFoundException ioe) {
                ioe.printStackTrace();
            }
        }
        nowPlayingBook = bundle==null? null :(Book) bundle.getSerializable(FILE_NOW_PLAYING);
        if(nowPlayingBook == null) {
            try {
                File file = new File(this.getFilesDir(), FILE_NOW_PLAYING);
                FileInputStream fis = new FileInputStream(file);
                ObjectInputStream ois = new ObjectInputStream(fis);
                nowPlayingBook = (Book) ois.readObject();
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
        this.detailBook = book;
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
        savedInstanceState.putSerializable(FILE_BOOK, detailBook);
        savedInstanceState.putString(FILE_TERM, search);
        save();
    }

    @Override
    public void handlePlay(Book book) {
        nowPlayingBook = book;
        binder.play(book.getId());
        updateNowPlaying(book);
    }

    private void updateNowPlaying(Book book) {
        if(book!=null){
            nowPlayingText.setText(getString(R.string.now_playing,book.getTitle()));
            Drawable icon = binder.isPlaying() ? getDrawable(R.drawable.ic_play_arrow_black_24dp) : getDrawable(R.drawable.ic_pause_black_24dp);
            playPauseButton.setImageDrawable(icon);
            nowPlayingText.invalidate();
            playPauseButton.invalidate();
        }
        else{
            nowPlayingText.setText("");
        }
    }


    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            binder = (AudiobookService.MediaControlBinder) service;
            binder.setProgressHandler(progressHandler);
            isBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            isBound = false;
        }
    };

    private static class ProgressHandler extends Handler {
        MainActivity activity;

        public ProgressHandler(Context activity) {
            super();
            this.activity = (MainActivity) activity;
        }

        @Override
        public void handleMessage(Message msg){
            activity.setPosition((Integer) msg.obj);
        }
    }

    private void setPosition(int position) {
        this.position = position;
        updateSeekbar();
    }

    private void updateSeekbar() {
        int progress = (int) ((position/nowPlayingBook.getDuration()) * 100.0);
        audioSeekbar.setProgress(progress,true);
    }
}
