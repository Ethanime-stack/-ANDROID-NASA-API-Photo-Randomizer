package com.example.finalandroidproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class DescriptionFragment extends Fragment {

    // Gonna just remove the description being a textview and turn it into a fragment so we meet that requirement
    private static final String ARG_TEXT = "description_text";
    private TextView textView;


    public static DescriptionFragment newInstance(String text) {
        DescriptionFragment fragment = new DescriptionFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TEXT, text);
        fragment.setArguments(args);
        return fragment;
    }


    // onCreateView to set the TextView
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_description, container, false);

        textView = view.findViewById(R.id.descriptionTextView);
        if (getArguments() != null) {
            String text = getArguments().getString(ARG_TEXT);
            textView.setText(text);
        }
        return view;
    }

    // Update Desc
    public void updateDescription(String newText) {
        if (textView != null) {
            textView.setText(newText);
        }
    }
}
