package com.geeky7.rohit.locations.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.geeky7.rohit.locations.R;
import com.geeky7.rohit.locations.ServiceEverthing.BackgroundService;


public class Monitoring extends Fragment implements View.OnClickListener{
    Button startServiceB,stopServiceB;
    boolean running;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.monitoring,container,false);;

        startServiceB = (Button)v.findViewById(R.id.startService);
        stopServiceB = (Button)v.findViewById(R.id.stopService);

        startServiceB.setOnClickListener(this);
        stopServiceB.setOnClickListener(this);

        startServiceB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!running)
                    startService();
            }
        });
        stopServiceB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent serviceIntent = new Intent(getActivity(), BackgroundService.class);
                getActivity().stopService(serviceIntent);
                running = false;
            }
        });
        return v;
    }

    @Override
    public void onClick(View v) {
    }
    private void startService() {
        Intent serviceIntent = new Intent(getActivity(), BackgroundService.class);
        getActivity().startService(serviceIntent);
    }
}
