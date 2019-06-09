package com.MonoCycleStudios.team.victorium.Game.Fragments.GroundEvents;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.MonoCycleStudios.team.victorium.Game.Enums.GameFragments;
import com.MonoCycleStudios.team.victorium.Game.Game;
import com.MonoCycleStudios.team.victorium.R;
import com.MonoCycleStudios.team.victorium.widget.Utils.MMSystem;

import java.lang.reflect.Field;

public class RegionMenu extends Fragment implements GroundEvents {

    public void setLabelText(String strToNotify) {
        txtLabel = strToNotify;
    }
    public void setButtonText(String strToNotify) {
        txtButton = strToNotify;
    }
    public void setPosition(int x, int y){
        _x = x;
        _y = y;
    }
    public void setOnClick(View.OnClickListener OCL){
        btnOCL = OCL;
    }

    String txtLabel;
    String txtButton;
    TextView tv;
    Button btn;
    View.OnClickListener btnOCL = null;

    Button arrow;
    LinearLayout llm;

    public int _x, _y;
    public int _w, _h;
    FrameLayout flWrapper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        MMSystem.out.println("}{1}{}{{{{{{{}{}{}{{{}{}{}{}");
        View view = inflater.inflate(R.layout.fragment_ge_region_menu, container, false);

        tv = view.findViewById(R.id.tvCost);
        btn = view.findViewById(R.id.btnAction);

        arrow = view.findViewById(R.id.arrow);
        llm = view.findViewById(R.id.llMenu);

        flWrapper = view.findViewById(R.id.rm_wrapper);
        flWrapper.setAlpha(0);
        view.post(new Runnable() {
            @Override
            public void run() {

                _w = flWrapper.getWidth();
                _h = flWrapper.getHeight();

                MMSystem.out.println("}{3}{}{{{{{{{}{}{}{{{}{}{}{}" + _w);

                Game.getInstance().showFragment(GameFragments.GROUND_EVENT, getFragment(), "update");
                flWrapper.setAlpha(1);
            }
        });

        if(btnOCL == null)
            btn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick (View v){
                    Game.getInstance().showFragment(GameFragments.GROUND_EVENT, getFragment(), "remove", "this");
                }
            });
        else
            btn.setOnClickListener(btnOCL);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        _w = flWrapper.getWidth();
        _h = flWrapper.getHeight();

        MMSystem.out.println("}{3}{}{{{{{{{}{}{}{{{}{}{}{}" + _w);

//        Game.getInstance().showFragment(GameFragments.GROUND_EVENT, getFragment(), "update");
//        flWrapper.setAlpha(1);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (txtLabel != null) {
            tv.setText(txtLabel);
        }
        if (txtButton != null) {
            btn.setText(txtButton);
        }

        MMSystem.out.println("}{2}{}{{{{{{{}{}{}{{{}{}{}{}");
//        Game.getInstance().showFragment(GameFragments.GROUND_EVENT, this, "update");

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();

        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);

        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public void flipMenu(){
        arrow.setTranslationY(8);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.TOP;
        params.gravity += Gravity.CENTER_HORIZONTAL;
        params.width = 30;
        params.height = 30;
        arrow.setLayoutParams(params);

        llm.setGravity(Gravity.BOTTOM);
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