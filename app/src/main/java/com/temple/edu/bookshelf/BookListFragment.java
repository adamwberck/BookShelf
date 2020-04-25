package com.temple.edu.bookshelf;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class BookListFragment extends Fragment {
    private static final String ARG_LIST = "book_list";
    private static List<Book> books;
    private ListView listView;
    private HandlesBook bookHandler;
    private BookAdapter bookAdapter;

    public static BookListFragment newInstance(List<Book> books){
        BookListFragment fragment = new BookListFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_LIST, (Serializable) books);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            books = (List<Book>) getArguments().getSerializable(ARG_LIST);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        bookHandler = (HandlesBook) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        bookHandler = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        listView = null;
        bookAdapter = null;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.list_layout,container,false);
        listView = view.findViewById(R.id.list_books);
        bookAdapter = new BookAdapter(getContext());
        listView.setAdapter(bookAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Book item = (Book) parent.getItemAtPosition(position);
                bookHandler.handleBook(item);
            }
        });
        return view;
    }

    public void setBooks(List<Book> books) {
        this.books = books;
        Bundle args = new Bundle();
        args.putSerializable(ARG_LIST, (Serializable) books);
        setArguments(args);
        bookAdapter.notifyDataSetChanged();
    }

    private class BookAdapter extends BaseAdapter {
        public BookAdapter(Context context) {
            this.context = context;
        }

        Context context;
        @Override
        public int getCount() {
            return books!=null ? books.size() : 0;
        }

        @Override
        public Object getItem(int position) {
            return books.get(position);
        }

        @Override
        public long getItemId(int position) {
            return books.get(position).hashCode();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(context)
                        .inflate(R.layout.list_item, parent, false);
            }
            Book item = (Book) getItem(position);
            TextView textView = convertView.findViewById(R.id.text1);
            textView.setText(item.getTitle());
            textView = convertView.findViewById(R.id.text2);
            textView.setText(item.getAuthor());
            return convertView;
        }
    }

    public interface HandlesBook{
        void handleBook(Book book);
    }
}
