package com.MonoCycleStudios.team.victorium.widget.Utils;

import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;

public class FontFamily extends AppCompatActivity{
    {
        raleway_m = Typeface.createFromAsset(getAssets(), "font/Raleway-Medium.ttf");
        raleway_r = Typeface.createFromAsset(getAssets(), "font/Raleway-Regular.ttf");
        raleway_sb = Typeface.createFromAsset(getAssets(), "font/Raleway-SemiBold.ttf");
        raleway_b = Typeface.createFromAsset(getAssets(), "font/Raleway-Bold.ttf");
        raleway_eb = Typeface.createFromAsset(getAssets(), "font/Raleway-ExtraBold.ttf");
        raleway_bl = Typeface.createFromAsset(getAssets(), "font/PT_Sans-Web-Bold.ttf");

    }

    public static Typeface
            raleway_m,
            raleway_r,
            raleway_sb,
            raleway_b,
            raleway_eb,
            raleway_bl;

}
