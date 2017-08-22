package com.MonoCycleStudios.team.victorium.Game.Fragments;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.MonoCycleStudios.team.victorium.Connection.Client;
import com.MonoCycleStudios.team.victorium.Game.Enums.GameFragments;
import com.MonoCycleStudios.team.victorium.Game.Game;
import com.MonoCycleStudios.team.victorium.Game.GameRule;
import com.MonoCycleStudios.team.victorium.Game.Player;
import com.MonoCycleStudios.team.victorium.Game.Region;
import com.MonoCycleStudios.team.victorium.R;
import com.MonoCycleStudios.team.victorium.widget.ImageMap;

import java.util.ArrayList;

public class Ground extends Fragment {
    static ImageMap mImageMap;
    public static ArrayList<Region> regions = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_ground, container, false);

        mImageMap = (ImageMap) view.findViewById(R.id.map);
//        mImageMap.setImageResource(R.drawable.filleduamapregion_720_838);

        mImageMap.addOnImageMapClickedHandler(new ImageMap.OnImageMapClickedHandler()
        {
            @Override
            public void onImageMapClicked(int id, ImageMap imageMap)
            {
                if(GameRule.check(Client.iPlayer, "hit region", id))
                    mImageMap.showBubble(id);
            }

            @Override
            public void onBubbleClicked(int id)
            {
                GameRule.check(Client.iPlayer,"hit bulb attack",regions.lastIndexOf(new Region(-1,id,false,null)));
            }

            @Override
            public void onImageMapMiss(ImageMap imageMap) {
                Game.getInstance().showFragment(GameFragments.NONE, GameFragments.QUESTION);    // TEMP
            }
        });

        return view;
    }

    public static ArrayList<Region> getAllCapturableRegions(Player p){
        ArrayList<Region> regionsToReturn = new ArrayList<>();

        ArrayList<Region> regionsFree = new ArrayList<>();
        for(Region rg : regions){
            if(rg.owner == null){
                regionsFree.add(rg);
            }
        }

        ArrayList<Region> regionsPlayer = new ArrayList<>();
        for(Region rg : regions){
            if(rg.owner != null && rg.owner.equals(p)){
                regionsPlayer.add(rg);
            }
        }

        for(Region rg : regionsFree){
            for(Region prg : regionsPlayer)
                if(prg.getNeighbourhoods().contains(rg)){
                    regionsToReturn.add(rg);
                    break;
                }
        }
        if(regionsToReturn.isEmpty()){
            regionsToReturn.addAll(regionsFree);
        }

        for(Region rg : regions){
            if(!regionsToReturn.contains(rg)){
                rg.isActive = rg.owner != null && rg.owner.equals(p);
                rg.isInteractive = false;
            }else{
                rg.isInteractive = true;
            }
        }
        mImageMap.invalidate();
        return regionsToReturn;
    }

    public static ArrayList<Region> getAllAttackableRegions(Player p){
        ArrayList<Region> regionsToReturn = new ArrayList<>();

//        for(Region rg : regions){
//            if(rg.owner != null && !rg.owner.equals(p)){
//                regionsToReturn.add(rg);
//            }
//        }

        ArrayList<Region> regionsPlayer = new ArrayList<>();
        for(Region rg : regions){
            if(rg.owner != null && rg.owner.equals(p)){
                regionsPlayer.add(rg);
            }
        }

        for(Region rg : regions){
            for(Region prg : regionsPlayer)
                if(prg.getNeighbourhoods().contains(rg)){
                    if(rg.owner != null && !rg.owner.equals(p)) {
                        regionsToReturn.add(rg);
                        break;
                    }
                }
        }

        return regionsToReturn;
    }

    public static void setRegions(ArrayList<Region> newRegions) {
        regions = newRegions;
        for(Region rg : regions) {
            rg.setBitmaps();
            rg.computeNeighbourhoods(regions);
        }
        mImageMap.invalidate();
    }

    public static void updateRegion(int id, Player newOwner/*, int newCost*/){
        regions.get(id).update(newOwner/*, newCost*/);
        mImageMap.invalidate();
    }
}
