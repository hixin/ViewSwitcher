package com.cloudminds.switcherdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private Switcher switcher;
    private ArrayList<String> names;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        switcher = findViewById(R.id.wheelview);
        switcher.setData(names);
    }

    private void init() {
        names = new ArrayList<>();
        for(int i = 1; i < 7; i++) {
            int temp = 1 << (i*3);
            names.add(String.valueOf(temp));
        }
    }
}
