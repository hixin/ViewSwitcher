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
    private Switcher switcher;
    private ArrayList<String> names;

/*    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main1);
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



