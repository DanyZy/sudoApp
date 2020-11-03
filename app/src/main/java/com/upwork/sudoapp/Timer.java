package com.upwork.sudoapp;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.widget.TextView;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;


public class Timer implements LifecycleObserver {
    private Handler handler;
    private Runnable runnable;
    private int elapsedTime;
    private Context context;
    private TextView timeText;
    private boolean flag;

    public Timer(Context context, int elapsedTime) {
        this.context = context;
        this.elapsedTime = elapsedTime;
        init();
    }
    public void start () {
        runnable.run();
    }
    public void stop () {
        handler.removeCallbacks(runnable);
    }
    private void init () {
        timeText = ((Activity) context).findViewById(R.id.txt_elapsed_time);
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                if (flag)
                    elapsedTime += 1;
                handler.postDelayed(runnable, 1000);
                timeText.setText(getElapsedTimeString());
            }
        };
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
    }

    private boolean isInForeground() {
        return ProcessLifecycleOwner.get().getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    private void onAppForegrounded() {
        flag = true;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    private void onAppBackgrounded() {
        flag = false;
    }

    public int getElapsedSeconds() {
        return elapsedTime;
    }

    public String getElapsedTimeString () {
        int seconds = elapsedTime % 60;
        int minutes = elapsedTime / 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
    static public String getTimeFormat (int totalSeconds) {
        int hours = totalSeconds / 3600;
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        if(hours > 0) {
            return String.format("%02d:%02d:%02d", hours, minutes % 60, seconds);
        }
        return String.format("%02d:%02d", minutes, seconds);
    }
}