package com.temple.edu.bookshelf;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    Map<String,String> book;
    private TextView titleText;
    private TextView authorText;

    public static BookDetailsFragment newInstance(Map<String,String> book) {
        BookDetailsFragment fragment = new BookDetailsFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_BOOK, (Serializable) book);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            book = (Map<String, String>) getArguments().getSerializable(ARG_BOOK);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.details_layout,container,false);
        titleText = view.findViewById(R.id.text_title);
        authorText = view.findViewById(R.id.text_author);
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

    public void displayBook(Map<String, String> book) {
        this.book = book;
        if(book!=null && titleText!=null && authorText!=null ) {
            titleText.setText(book.get("title"));
            authorText.setText(book.get("author"));
        }
    }
}
