package com.temple.edu.bookshelf;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class BookDetailsFragment extends Fragment {
    private static final String ARG_BOOK = "book";
    Book book;
    private TextView titleText;
    private TextView authorText;
    private ImageView coverImage;
    private ImageButton detailPlayButton;
    private HandlesPlay playHandler;

    public static BookDetailsFragment newInstance(Book book) {
        BookDetailsFragment fragment = new BookDetailsFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_BOOK, book);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.book = (Book) getArguments().getSerializable(ARG_BOOK);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        playHandler = (HandlesPlay) context;
    }

    @Override
    public void onDetach(){
        super.onDetach();
        playHandler = null;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.details_layout,container,false);
        titleText = view.findViewById(R.id.text_title);
        authorText = view.findViewById(R.id.text_author);
        coverImage = view.findViewById(R.id.image_cover);
        detailPlayButton = view.findViewById(R.id.button_detail_play);
        detailPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(book!=null) {
                    playHandler.handlePlay(book);
                }
            }
        });
        displayBook(book);
        return view;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        book = null;
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
        titleText = null;
        authorText = null;
    }

    public void displayBook(Book book) {
        if(book!=null) {
            this.book = book;
            Bundle args = new Bundle();
            args.putSerializable(ARG_BOOK,book);
            setArguments(args);
            if (titleText != null && authorText != null) {
                titleText.setText(book.getTitle());
                authorText.setText(book.getAuthor());
            }
        }
    }

    public void setBitmap(Bitmap bitmap) {
        if(bitmap!=null && coverImage!=null) {
            coverImage.setImageBitmap(bitmap);
        }
    }

    public interface HandlesPlay{
        void handlePlay(Book book);
    }
}
