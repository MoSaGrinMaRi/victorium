package com.MonoCycleStudios.team.victorium.Game.Fragments;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.MonoCycleStudios.team.victorium.R;
import com.MonoCycleStudios.team.victorium.widget.MyCountDownTimer;

public class TimeDisplacer extends Fragment {

    TextView tv;
    boolean isCreated = false;
    boolean started = false;
    public MyCountDownTimer mcdt;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_timer, container, false);

        tv = (TextView) view.findViewById(R.id.timerText);
        isCreated = true;

        return view;
    }

    void updateTimerText(int milli, int sec, int min){
        if(isCreated)
            tv.setText(String.format("%02d : %02d : %03d".toLowerCase(), min, sec, milli));
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
        mcdt = new MyCountDownTimer(millisInFuture, countDownInterval);

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                mcdt.addOnUpdateHandler(new MyCountDownTimer.OnUpdateHandler(){

                    @Override
                    public void onTickUpdate(long currentMillis, int milli, int sec, int min) {

                        updateTimerText(milli, sec, min);

                    }

                    @Override
                    public void onFinish() {

                        updateTimerText(0, 0, 0);
                        started = false;
                    }
                });
                mcdt.start();
//                started = false;
            }
        });
    }

    public void setTimer(MyCountDownTimer mcdt){
        this.mcdt = mcdt;
    }

    public void stopTimer(){
        mcdt.cancel();
    }

}
