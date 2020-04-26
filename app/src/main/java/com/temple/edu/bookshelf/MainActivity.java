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
import android.util.Log;
import android.view.View;
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
    private static final String FILE_BOOK   = "detailBook";
    private static final String FILE_TERM   = "term";
    private static final String FILE_NPBOOK = "npbook";
    private static final String TAG = "MainAct";
    private static final double TOP_VALUE = 100.0;
    private List<Book> bookShelf = new ArrayList<>(10);
    private List<Book> completeShelf = new ArrayList<>(10);
    private Book detailBook;
    private Book nowPlayingBook;
    private String search;
    private boolean hasTwoContainers;
    private BookListFragment bookListFragment;
    private BookDetailsFragment bookDetailsFragment;
    private SearchFragment searchFragment;
    private AudiobookService.MediaControlBinder binder;
    private boolean isBound;
    private TextView nowPlayingText;
    private ImageButton playPauseButton;
    private ImageButton stopButton;
    private SeekBar audioSeekbar;
    private ProgressHandler progressHandler = new ProgressHandler(this);
    private int position;
    private boolean isDrag = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        searchForBooks("",true);
        if(savedInstanceState!=null) {
            nowPlayingBook = (Book) savedInstanceState.getSerializable(FILE_NPBOOK);
            updateNowPlaying(false);
        }
        load(savedInstanceState);

        setContentView(R.layout.main_layout);

        //set up now playing
        nowPlayingText = findViewById(R.id.text_now_playing);
        playPauseButton = findViewById(R.id.button_play_pause);
        playPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isPlaying = binder.isPlaying();
                if(nowPlayingBook != null) {
                    binder.pause();
                }
                updateNowPlaying(!isPlaying);
            }
        });
        stopButton = findViewById(R.id.button_stop);
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binder.stop();
                //Intent intent = new Intent(MainActivity.this,AudiobookService.class);
                //stopService(intent);
                nowPlayingBook = null;
                audioSeekbar.setProgress(0);
                updateNowPlaying(false);
            }
        });
        audioSeekbar = findViewById(R.id.seekbar_audio);
        audioSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isDrag = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {//update thingy
                if(nowPlayingBook!=null) {
                    int progress = seekBar.getProgress();
                    position = (int) (((progress*1.0) / 100.0)*(nowPlayingBook.getDuration()*1.0));
                    binder.seekTo(position);
                    Log.i(TAG,"pos "+position + "dur" +nowPlayingBook.getDuration() );
                    //updateNowPlaying(true);
                }else{
                    seekBar.setProgress(0,true);
                }
                isDrag = false;
            }
        });

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
        Intent intent = new Intent(this,AudiobookService.class);
        startService(intent);
        bindService(intent,connection,BIND_AUTO_CREATE);
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
    public void setBookShelf(List<Book> books,boolean isCompleteList){
        if(!isCompleteList) {
            bookShelf = books;
            bookListFragment.setBooks(books);
        }else{
            completeShelf = books;
        }
    }

    private void searchForBooks(String url,boolean isCompleteList) {
        DownloadTask task = new DownloadTask(this,isCompleteList);
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
        searchForBooks(term,false);
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
        savedInstanceState.putSerializable(FILE_NPBOOK,nowPlayingBook);
        save();
    }

    @Override
    public void handlePlay(Book book) {
        nowPlayingBook = book;
        Intent intent = new Intent(this,AudiobookService.class);
        startService(intent);
        bindService(intent,connection,Context.BIND_AUTO_CREATE);
        binder.play(book.getId());
        audioSeekbar.setProgress(0);
        updateNowPlaying(true);
    }

    private void updateNowPlaying(boolean isPlaying) {
        Drawable icon =  nowPlayingBook==null || binder==null || !isPlaying ?
                getDrawable(R.drawable.ic_play_arrow_black_24dp) : getDrawable(R.drawable.ic_pause_black_24dp);
        if(nowPlayingBook!=null && nowPlayingText!=null){
            nowPlayingText.setText(getString(R.string.now_playing,nowPlayingBook.getTitle()));
            nowPlayingText.invalidate();
            playPauseButton.invalidate();
        }
        else if(nowPlayingText!=null){
            nowPlayingText.setText("");
        }
        if(playPauseButton!=null) {
            playPauseButton.setImageDrawable(icon);
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

        private ProgressHandler(Context activity) {
            super();
            this.activity = (MainActivity) activity;
        }

        @Override
        public void handleMessage(Message msg){
            AudiobookService.BookProgress obj = (AudiobookService.BookProgress) msg.obj;
            if(activity!=null && obj!=null) {
                if (activity.getNowPlaying() == null) {
                    Book book = activity.getBookFromId(obj.getBookId());
                    activity.setNowPlaying(book);
                    activity.updateNowPlaying(true);
                }
                if(!activity.isDrag()) {
                    activity.setPosition(obj.getProgress());
                    activity.updateNowPlaying(true);
                }
            }
        }
    }

    private boolean isDrag() {
        return isDrag;
    }

    private Book getNowPlaying() {
        return nowPlayingBook;
    }

    private Book getBookFromId(int bookId) {
        for(int i=0;i<completeShelf.size();i++){
            Book book = completeShelf.get(i);
            if(bookId==book.getId()){
                return book;
            }
        }
        return null;
    }

    private void setNowPlaying(Book book) {
        nowPlayingBook = book;
    }

    private void setPosition(int position) {
        this.position = position;
        updateSeekbar();
    }

    private void updateSeekbar() {
        int progress = (int) ( ((position*1.0)/(nowPlayingBook.getDuration()*1.0)) * 100.0);
        audioSeekbar.setProgress(progress,true);
    }
}
