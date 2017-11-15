package com.example.eunyoungha.r_multi_note.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.eunyoungha.r_multi_note.R;

/**
 * Created by eunyoung.ha on 2017/10/13.
 */

public class FragmentStartTwo extends Fragment{

    public FragmentStartTwo(){}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_start_two, container, false);

        return view;
    }
}
