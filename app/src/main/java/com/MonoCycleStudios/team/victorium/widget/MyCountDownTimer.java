package com.MonoCycleStudios.team.victorium.widget;

import android.os.CountDownTimer;

import com.MonoCycleStudios.team.victorium.widget.Utils.MMSystem;

import java.util.ArrayList;

public class MyCountDownTimer extends CountDownTimer {

    // click handler list
    ArrayList<OnUpdateHandler> mCallbackList;

    public interface OnUpdateHandler
    {
        void onTickUpdate(long currentMillis, int milli, int sec, int min);

        void onFinish();
    }

    public void addOnUpdateHandler( OnUpdateHandler h ) {
        if (h != null) {
            if (mCallbackList == null) {
                mCallbackList = new ArrayList<OnUpdateHandler>();
            }
            mCallbackList.add(h);
        }
    }

    private void onTickUpdate(int milli, int sec, int min){
        if(mCallbackList != null) {
            for (OnUpdateHandler h : mCallbackList){
                h.onTickUpdate(currentMillis, milli, sec, min);
            }
        }
    }

    private void onFinishTicking(){
        if(mCallbackList != null) {
            for (OnUpdateHandler h : mCallbackList){
                h.onFinish();
            }
        }
    }

    // Timer itself

    public long currentMillis;
    public long askedMillis;

    public MyCountDownTimer(long millisInFuture, long countDownInterval) {
        super(millisInFuture, countDownInterval);
        askedMillis = millisInFuture;
    }

    @Override
    public void onTick(long millisUntilFinished) {
        currentMillis = millisUntilFinished;

        int milli = getOnlyMillis(millisUntilFinished);
        int seconds = getOnlySeconds(millisUntilFinished);
        int minutes = getOnlyMinutes(millisUntilFinished);

        onTickUpdate(milli,seconds,minutes);
    }

    @Override
    public void onFinish() {
        onFinishTicking();
        MMSystem.out.println("TIME IS OVER !!!");
    }

    public static int getOnlyMillis(long millisUntilFinished){
        return (int)(millisUntilFinished % 1000);
    }

    public static int getOnlySeconds(long millisUntilFinished){
        return (int)(millisUntilFinished / 1000) % 60;
    }

    public static int getOnlyMinutes(long millisUntilFinished){
        return (int)(millisUntilFinished / 1000) / 60;
    }
}