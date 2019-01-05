package com.MonoCycleStudios.team.victorium.Game.Fragments;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.MonoCycleStudios.team.victorium.Game.Fragments.GroundEvents.GroundEvents;
import com.MonoCycleStudios.team.victorium.Game.Fragments.GroundEvents.RegionFX;
import com.MonoCycleStudios.team.victorium.Game.Fragments.GroundEvents.RegionMenu;
import com.MonoCycleStudios.team.victorium.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class GroundEvent extends Fragment {

//                RegionMenu rm = new RegionMenu();
//                rm.setButtonText("capture");
//                rm.setLabelText("3000? mb");
//                rm.setPosition(100,200);

//    FrameLayout fl;
//    GroundEvents fToAdd;
//    FragmentManager fm;
//    FragmentTransaction ft;

    public void addView(GroundEvents f, int s){  // Obj in future

//        fToAdd = f;

        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.relatLay, f.getFragment());
//        ft.replace(fl.getId(), f.getFragment(), "tag_event"+fl.getId());
        ft.commit();

        layers.add(f);
//        ft = fm.beginTransaction();
//
//            ft.setCustomAnimations(R.animator.fadein_scale_in, R.animator.fadeout_scale_out);
//        ft.replace(R.id.fragmentMMPlaceholder, fToAdd.getFragment());
//        ft.commit();


//
//        fm = getFragmentManager();
//        //
//
//
//        new Handler().post(new Runnable() {
//           @Override
//            public void run() {
//
//               ft = fm.beginTransaction();
////               FragmentTransaction ft = getFragmentManager().beginTransaction();
//
////               ft.attach(fToAdd.getFragment());
////               ft.setCustomAnimations(R.animator.fadein_scale_in, R.animator.fadeout_scale_out);
//               ft.add(R.id.relatLay, fToAdd.getFragment());
////        ft.replace(fl.getId(), f.getFragment(), "tag_event"+fl.getId());
//               ft.commit();
//
//        //            ft.setCustomAnimations(R.animator.fadein_scale_in, R.animator.fadeout_scale_out);
//
////                layers.put((FrameLayout) getView().findViewById(R.id.fragmentMMPlaceholder), fToAdd);
//           }
//        });

//        fl = new FrameLayout(this.getActivity());
//        new Handler().post(new Runnable() {
//            @Override
//            public void run() {
//
////                FrameLayout fl = new FrameLayout(this.getActivity());
//                fl.setId(View.generateViewId());
//                fl.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//                rl.addView(fl);
//
//                System.out.println("3456789  " + rl.getChildAt(0).getId() + fl.getId());
//
//                FragmentManager fm = getFragmentManager();
//                FragmentTransaction ft = fm.beginTransaction();
//        //
//        //            ft.setCustomAnimations(R.animator.fadein_scale_in, R.animator.fadeout_scale_out);
//                ft.replace(fl.getId(), f.getFragment(), "tag_event"+fl.getId());
//                ft.commit();
//
//                layers.put(fl, f);
//            }
//        });

//        if(s != -1)
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    removeView(f);
//                }
//            }, s);
    }

    public void updateView(GroundEvents f, String s){

        for(GroundEvents entry : layers){
            if(entry.equals(f)){
//                switch (entry.getClass().getCanonicalName()){
//                    case ((RegionMenu)null).getClass().getCanonicalName():{
//
//                    }break;
//                }

                int x = entry.getX();// - entry.getW()/2;
                int y = entry.getY();// - entry.getH();

                if (entry instanceof RegionMenu) {
                    x = entry.getX() - entry.getW() / 2;
                    y = entry.getY() - entry.getH();

                    if(y <= 0){
                        y += entry.getH();
                        ((RegionMenu)entry).flipMenu();
                    }
                }
                else if (entry instanceof RegionFX) {
                    x = entry.getX() - entry.getW() / 2;
                    y = entry.getY() - entry.getH() / 2;
                }

                RelativeLayout.LayoutParams vlp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                System.out.println("f.getX(), f.getY(): " + f.getX() + "  " + f.getY() + " | " + x + " " +y);
                vlp.setMargins(x, y, 0, 0);
                entry.getView().setLayoutParams(vlp);
            }
        }

//        for (Map.Entry entry: layers.entrySet()) {
//            FrameLayout key = (FrameLayout) entry.getKey();
//            GroundEvents value = (GroundEvents) entry.getValue();
//            if(value.equals(f)){
//                int x = value.getX() - value.getW()/2;
//                int y = value.getY() - value.getH();
//
//                if(y <= 0){
//                    y += value.getH();
//                    ((RegionMenu)value).flipMenu();
//                }
//
//                RelativeLayout.LayoutParams vlp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//                System.out.println("f.getX(), f.getY(): " + f.getX() + "  " + f.getY() + " | " + x + " " +y);
//                vlp.setMargins(x, y, 0, 0);
//                key.setLayoutParams(vlp);
//            }
//        }
//
//        FragmentManager fm = getFragmentManager();
//        FragmentTransaction ft = fm.beginTransaction();
//
//        ft.show(f.getFragment());
//        ft.commit();
//        for(int i = 0; i < layers.size(); i++) {
//            if(layers.get(i).getClass().equals(f.getClass())){
//
//                FrameLayout.LayoutParams vlp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//                vlp.setMargins(f.getX(), f.getY(), 0, 0);
//                rl.getChildAt(i).setLayoutParams(vlp);
//                //  smth;
////                break;
//            }
//        }
    }

    public void removeView(GroundEvents f, String s){
        List<GroundEvents> listToRemove = new LinkedList<>();

        for(GroundEvents entry : layers){
            if(s.equalsIgnoreCase("all")) {
                if (entry.getClass().equals(f.getClass()))
                    listToRemove.add(entry);  //  layers.remove(key);
            }
            else if(s.equalsIgnoreCase("this")) {
                if (entry.equals(f))
                    listToRemove.add(entry);  //  layers.remove(key);
            }
        }

//        for (Map.Entry entry : layers.entrySet()) {
//            FrameLayout key = (FrameLayout) entry.getKey();
//            GroundEvents value = (GroundEvents) entry.getValue();
//            if(s.equalsIgnoreCase("all"))
//                if(value.getClass().equals(f.getClass())){
//                    rl.removeView(key);
//                    listToRemove.add(key);  //  layers.remove(key);
//                }
//            else if(s.equalsIgnoreCase("this"))
//                if(value.equals(f)){
//                    rl.removeView(key);
//                    listToRemove.add(key);  //  layers.remove(key);
//                }
//        }
        for(GroundEvents entry : listToRemove) {
            rl.removeView(entry.getView());
            layers.remove(entry);
        }
//        for(GroundEvents tmp : layers){
//            if(tmp.getClass().equals(f.getClass())){
//                rl.removeView(tmp.getView());
//                layers.remove(tmp);
////                break;
//            }
//        }
    }
    RelativeLayout rl;
    FrameLayout flm;
    FrameLayout flf;
    View mView;
    ArrayList<GroundEvents> layers = new ArrayList<>();
//    Map<FrameLayout, GroundEvents> layers = new HashMap<>();
//    ArrayList<GroundEvents> layers = new ArrayList<>(); //  make hash map :     ID  : fragment

    public GroundEvent(){}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_ground_event, container, false);

        mView = view;
        rl = (RelativeLayout) view.findViewById(R.id.relatLay);
        flm = (FrameLayout) view.findViewById(R.id.fragmentMMPlaceholder);
        flf = (FrameLayout) view.findViewById(R.id.fragmentRegionFXPlaceholder);

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
//    public GroundEvent(Context context) {
//        super(context);
//
//        rl = (RelativeLayout) findViewById(R.id.relatLay);
//        flm = (FrameLayout) findViewById(R.id.fragmentMMPlaceholder);
//        flf = (FrameLayout) findViewById(R.id.fragmentRegionFXPlaceholder);
//
//    }

//    public View onTst(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//
//        View view = inflater.inflate(R.layout.fragment_ground_event, container, false);
//
//        rl = (RelativeLayout) view.findViewById(R.id.relatLay);
//        flm = (FrameLayout) view.findViewById(R.id.fragmentMMPlaceholder);
//        flf = (FrameLayout) view.findViewById(R.id.fragmentRegionFXPlaceholder);
//
//        return view;
//    }
}
