package com.MonoCycleStudios.team.victorium.Game.Fragments;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.MonoCycleStudios.team.victorium.Game.Fragments.GroundEvents.GroundEvents;
import com.MonoCycleStudios.team.victorium.Game.Fragments.GroundEvents.RegionMenu;
import com.MonoCycleStudios.team.victorium.R;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class GroundEvent extends Fragment {

//                RegionMenu rm = new RegionMenu();
//                rm.setButtonText("capture");
//                rm.setLabelText("3000? mb");
//                rm.setPosition(100,200);

    public void addView(final GroundEvents f, int s){  // Obj in future

        FrameLayout fl = new FrameLayout(this.getActivity());
        fl.setId(View.generateViewId());
        fl.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        rl.addView(fl);

        System.out.println("3456789  " + rl.getChildAt(0).getId() + fl.getId());

        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
//
//            ft.setCustomAnimations(R.animator.fadein_scale_in, R.animator.fadeout_scale_out);
        ft.replace(fl.getId(), f.getFragment(), "tag_event"+fl.getId());
        ft.commit();

        layers.put(fl, f);

//        if(s != -1)
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    removeView(f);
//                }
//            }, s);
    }

    public void updateView(GroundEvents f, String s){

        for (Map.Entry entry: layers.entrySet()) {
            FrameLayout key = (FrameLayout) entry.getKey();
            GroundEvents value = (GroundEvents) entry.getValue();
            if(value.equals(f)){
                int x = value.getX() - value.getW()/2;
                int y = value.getY() - value.getH();

                if(y <= 0){
                    y += value.getH();
                    ((RegionMenu)value).flipMenu();
                }

                RelativeLayout.LayoutParams vlp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                System.out.println("f.getX(), f.getY(): " + f.getX() + "  " + f.getY() + " | " + x + " " +y);
                vlp.setMargins(x, y, 0, 0);
                key.setLayoutParams(vlp);
            }
        }
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
        List<FrameLayout> listToRemove = new LinkedList<>();
        for (Map.Entry entry : layers.entrySet()) {
            FrameLayout key = (FrameLayout) entry.getKey();
            GroundEvents value = (GroundEvents) entry.getValue();
            if(s.equalsIgnoreCase("all"))
                if(value.getClass().equals(f.getClass())){
                    rl.removeView(key);
                    listToRemove.add(key);  //  layers.remove(key);
                }
            else if(s.equalsIgnoreCase("this"))
                if(value.equals(f)){
                    rl.removeView(key);
                    listToRemove.add(key);  //  layers.remove(key);
                }
        }
        for(FrameLayout entry : listToRemove) {
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
    Map<FrameLayout, GroundEvents> layers = new HashMap<>();
//    ArrayList<GroundEvents> layers = new ArrayList<>(); //  make hash map :     ID  : fragment

    public GroundEvent(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_ground_event, container, false);

        rl = (RelativeLayout) view.findViewById(R.id.relatLay);

        return view;
    }
}
