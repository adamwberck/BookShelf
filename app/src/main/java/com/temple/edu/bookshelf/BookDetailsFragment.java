package com.temple.edu.bookshelf;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
            Book book = (Book) getArguments().getSerializable(ARG_BOOK);
            if(book!=null) {
                this.book = book;
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.details_layout,container,false);
        titleText = view.findViewById(R.id.text_title);
        authorText = view.findViewById(R.id.text_author);
        coverImage = view.findViewById(R.id.image_cover);
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
            if (titleText != null && authorText != null) {
                titleText.setText(book.getTitle());
                authorText.setText(book.getTitle());
                coverImage.setImageBitmap(book.getCover());
            }
        }
    }

}
