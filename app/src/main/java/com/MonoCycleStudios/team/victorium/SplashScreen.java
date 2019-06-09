package com.MonoCycleStudios.team.victorium;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;


import com.MonoCycleStudios.team.victorium.Game.QuestionParser;
import com.MonoCycleStudios.team.victorium.widget.Utils.FontFamily;

import java.io.IOException;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class SplashScreen extends AppCompatActivity {

    private static int SPLASH_TIME_OUT = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        TextView tv = findViewById(R.id.txtMCS);
        tv.setTypeface(FontFamily.hammers_r);

        final GifImageView imgView = findViewById(R.id.imageView123);
        GifDrawable gifFromResource;
        try {

            gifFromResource = new GifDrawable( getResources(), R.drawable.loading_anim_1);

            gifFromResource.setSpeed(3.0f);
            imgView.setImageDrawable(gifFromResource);

        } catch (IOException e) {
            e.printStackTrace();
        }

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    imgView.setImageDrawable(new GifDrawable( getResources(), R.drawable.loading_anim_1_close));

                } catch (IOException e) {
                    e.printStackTrace();
                }

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        new AsyncRequest().execute();
                        dosmth();

                    }
                }, SPLASH_TIME_OUT-2000);


            }
        }, SPLASH_TIME_OUT);
    }

    void dosmth(){
        startActivity(new Intent(this, MainActivity.class));
        finish();

        overridePendingTransition(R.animator.slideout_bottom, R.animator.slidein_top);
    }



    private class AsyncRequest extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... arg) {
            QuestionParser.prepareQuestions(getApplicationContext());
            return "done";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }

}
