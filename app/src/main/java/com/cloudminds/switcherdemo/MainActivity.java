package com.cloudminds.switcherdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements Switcher.OnItemSelectedListener {
    private static final String TAG = "MainActivity";
    private LinearLayout layout;
    private Button scrollToBtn;
    private Button scrollByBtn;
    private ScrollerButton smoothScrollToBtn;
    private Switcher switcher;
    private ArrayList<String> names;

/*    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main1);
    }*/

    /*@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main0);
        layout = findViewById(R.id.layout);
        scrollToBtn = findViewById(R.id.scroll_to_btn);
        scrollByBtn = findViewById(R.id.scroll_by_btn);
        smoothScrollToBtn = findViewById(R.id.smooth_scroll_to_btn);
        scrollToBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout.scrollTo(-60, 0);
            }
        });

        smoothScrollToBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                smoothScrollToBtn.smoothScrollTo(-60, 0);
            }
        });

        scrollByBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick: " + layout.getScrollX());
                //layout.scrollBy(-60, 0);
            }
        });
    }*/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        switcher = findViewById(R.id.wheelview);
        switcher.setData(names);
        switcher.setOnItemSelectedListener(this);
    }

    private void init() {
        names = new ArrayList<>();
        for(int i = 1; i < 7; i++) {
            int temp = 1 << (i*3);
            names.add(String.valueOf(temp));
        }
    }
    @Override
    public void onItemChanged(int lastPos, int currentPos) {

    }

    @Override
    public void onItemSelected(Switcher view, int position) {
        Log.i(TAG, "onItemSelected: " + position);
    }
}



