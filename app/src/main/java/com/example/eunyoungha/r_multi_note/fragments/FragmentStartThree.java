package com.example.eunyoungha.r_multi_note.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.example.eunyoungha.r_multi_note.activities.LoginActivity;
import com.example.eunyoungha.r_multi_note.activities.MainActivity;
import com.example.eunyoungha.r_multi_note.R;

/**
 * Created by eunyoung.ha on 2017/10/13.
 */

public class FragmentStartThree extends Fragment{

    public FragmentStartThree(){}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_start_three,container,false);

        RelativeLayout goMainBtn = (RelativeLayout) view.findViewById(R.id.goMainButton);
        goMainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent for call a Activity from Fragment
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }
}
