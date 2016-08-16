package com.geeky7.rohit.location.ServiceEverthing;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.geeky7.rohit.location.R;

public class TwoButtonActivity extends Activity {

    Button startServiceB,stopServiceB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_two_button);
        startServiceB = (Button)findViewById(R.id.startService);
        stopServiceB = (Button)findViewById(R.id.stopService);
        startService();
        startServiceB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent serviceIntent = new Intent(TwoButtonActivity.this, BackgroundService.class);
                startService(serviceIntent);

            }
        });
        stopServiceB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent serviceIntent = new Intent(TwoButtonActivity.this, BackgroundService.class);
                stopService(serviceIntent);
            }
        });
    }
    private void startService() {
        Intent serviceIntent = new Intent(TwoButtonActivity.this, BackgroundService.class);
        startService(serviceIntent);
    }
}
