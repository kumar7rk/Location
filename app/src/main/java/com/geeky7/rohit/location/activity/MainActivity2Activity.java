package com.geeky7.rohit.location.activity;

import android.app.Activity;
import android.os.Bundle;

import com.geeky7.rohit.location.R;
import com.roughike.bottombar.BottomBar;


public class MainActivity2Activity extends Activity {

    BottomBar bottomBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_activity2);

        bottomBar = BottomBar.attach(this,savedInstanceState);
        bottomBar.setItems(R.menu.bottombar_menu_three_items);
    }
}
