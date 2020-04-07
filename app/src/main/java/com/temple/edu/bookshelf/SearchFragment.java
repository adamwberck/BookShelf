package com.temple.edu.bookshelf;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;


public class SearchFragment extends Fragment {
    private static final String ARG_SEARCH = "search";

    private String searchTerm;

    private HandleSearchTerm searchHandler;
    private EditText searchEdit;
    private ImageButton searchButton;

    public static SearchFragment newInstance(String term) {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putString(ARG_SEARCH, term);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            searchTerm = getArguments().getString(ARG_SEARCH);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.search_layout,container,false);
        searchEdit = view.findViewById(R.id.edit_search);
        searchEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchTerm = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        searchButton = view.findViewById(R.id.button_search);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchHandler.handleTerm(searchTerm);
            }
        });
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        searchHandler = (HandleSearchTerm) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        searchHandler = null;
    }
    public interface HandleSearchTerm {
        void handleTerm(String term);
    }
}
