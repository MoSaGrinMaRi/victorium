package com.MonoCycleStudios.team.victorium.Game.Fragments;
import android.app.Fragment;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.MonoCycleStudios.team.victorium.Game.Game;
import com.MonoCycleStudios.team.victorium.R;

import java.util.Locale;

public class MyTimer extends Fragment {

    TextView tv;
    boolean isCreated = false;
    boolean started = false;
    MyCountDownTimer mcdt;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_timer, container, false);

        tv = (TextView) view.findViewById(R.id.timerText);
        isCreated = true;

        return view;
    }

    /**
     *  http://www.calculateme.com/Time/Milliseconds/ToMinutes.htm
     */
    public void setTimer(final long millisInFuture, final long countDownInterval){
        if(started){
            mcdt.cancel();
            started = false;
        }

        started = true;

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                mcdt = new MyCountDownTimer(millisInFuture, countDownInterval);
                mcdt.start();
                started = false;
            }
        });
    }

    public void stopTimer(){
        mcdt.cancel();
    }

    public class MyCountDownTimer extends CountDownTimer{

        public long currentMillis;

        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            currentMillis = millisUntilFinished;
            int milli = (int) (millisUntilFinished % 1000);
            int seconds = (int) (millisUntilFinished / 1000) % 60;
            int minutes = (int) (millisUntilFinished / 1000) / 60;
            if(isCreated)
                tv.setText(String.format("%02d : %02d : %03d".toLowerCase(), minutes, seconds, milli));
        }

        @Override
        public void onFinish() {
            if(isCreated)
                tv.setText(String.format("%02d : %02d : %03d".toLowerCase(),0,0,0));
            Game.getInstance().timeIsOver();    //  TODO: Make Handler to manage onFinish
            System.out.println("TIME IS OVER !!!");
        }
    }

}
