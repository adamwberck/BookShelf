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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class BookListFragment extends Fragment {
    List<Map<String, String>> books;
    private ListView listView;
    private HandlesBook bookHandler;

    public static BookListFragment newInstance(List<Map<String, String>> books){
        BookListFragment fragment = new BookListFragment();
        fragment.books = books;
        return fragment;
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
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.list_layout,container,false);
        listView = view.findViewById(R.id.list_books);
        listView.setAdapter(new BookAdapter(getContext()));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Map item = (Map) parent.getItemAtPosition(position);
                bookHandler.handleBook(item);
            }
        });
        return view;
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
            Map<String,String> item = (Map) getItem(position);
            TextView textView = convertView.findViewById(R.id.text1);
            textView.setText(item.get("title"));
            return convertView;
        }
    }

    public interface HandlesBook{
        void handleBook(Map<String,String> book);
    }
}
