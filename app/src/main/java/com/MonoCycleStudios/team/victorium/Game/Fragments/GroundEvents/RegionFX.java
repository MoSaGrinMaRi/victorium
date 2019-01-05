package com.MonoCycleStudios.team.victorium.Game.Fragments.GroundEvents;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.MonoCycleStudios.team.victorium.Game.Enums.GameFragments;
import com.MonoCycleStudios.team.victorium.Game.Game;
import com.MonoCycleStudios.team.victorium.R;

import java.io.IOException;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class RegionFX extends Fragment implements GroundEvents {

    public void setPosition(int x, int y){
        _x = x;
        _y = y;
    }
    public void setFXType(int second){
        if(second == -1){
            animTime = 2000;
        }
    }

    public int _x, _y;
    public int _w, _h;
    int animTime = 1500;
    GifImageView gifImageView;
    FrameLayout flWrapper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        System.out.println("}{1}{}{{{{{{{}{}{}{{{}{}{}{}");
        View view = inflater.inflate(R.layout.fragment_ge_region_fx, container, false);

        flWrapper = (FrameLayout) view.findViewById(R.id.rfx_wrapper);
        gifImageView = (GifImageView) view.findViewById(R.id.regionFX_GifPlaceholder);
        flWrapper.setAlpha(0);
        view.post(new Runnable() {
            @Override
            public void run() {

            _w = flWrapper.getWidth();
            _h = flWrapper.getHeight();

            System.out.println("}{3}{}{{{{{{{}{}{}{{{}{}{}{}" + _w);

            try {

                GifDrawable gifFromResource = new GifDrawable( getResources(), R.drawable.explode_anim_0_tst);

                gifFromResource.setSpeed(3.0f);
                gifImageView.setImageDrawable(gifFromResource);

            } catch (IOException e) {
                e.printStackTrace();
            }

            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    Game.getInstance().showFragment(GameFragments.GROUND_EVENT, getFragment(), "remove", "this");
                }
            }, animTime);

            Game.getInstance().showFragment(GameFragments.GROUND_EVENT, getFragment(), "update");
            flWrapper.setAlpha(1);
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
//        if (txtLabel != null) {
//            tv.setText(txtLabel);
//        }
//        if (txtButton != null) {
//            btn.setText(txtButton);
//        }

        System.out.println("}{2}{}{{{{{{{}{}{}{{{}{}{}{}");
    }


    @Override
    public int getX() {
        return _x;
    }

    @Override
    public int getY() {
        return _y;
    }

    @Override
    public int getW() {
        return _w;
    }

    @Override
    public int getH() {
        return _h;
    }

    @Override
    public Fragment getFragment() {
        return this;
    }
}
