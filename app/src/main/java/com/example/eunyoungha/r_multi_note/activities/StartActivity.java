package com.example.eunyoungha.r_multi_note.activities;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;

import com.example.eunyoungha.r_multi_note.R;
import com.example.eunyoungha.r_multi_note.fragments.FragmentStartOne;
import com.example.eunyoungha.r_multi_note.fragments.FragmentStartThree;

public class StartActivity extends AppCompatActivity {

    private static int mTabNumber = 2;

    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_start);

        viewPager = (ViewPager) findViewById(R.id.viewPager);

        viewPager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                switch(position){
                    case 0:
                        return new FragmentStartOne();
                    case 1:
                        return new FragmentStartThree();
                }
                return new FragmentStartOne();
            }

            @Override
            public int getCount() {
                return mTabNumber;
            }
        });
//
//        Button button = new Button(this);
//        button.setText("start");
    }
}
