package com.example.eunyoungha.r_multi_note;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

public class StartActivity extends AppCompatActivity {

    private static int mTabNumber = 3;

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
                        return new FragmentStartTwo();
                    case 2:
                        return new FragmentStartThree();
                    default:
                        return new FragmentStartOne();
                }
            }

            @Override
            public int getCount() {
                return mTabNumber;
            }
        });

        Button button = new Button(this);
        button.setText("start");
    }


}
