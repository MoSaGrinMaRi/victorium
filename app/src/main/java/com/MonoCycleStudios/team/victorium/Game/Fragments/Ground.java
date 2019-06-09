package com.MonoCycleStudios.team.victorium.Game.Fragments;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.MonoCycleStudios.team.victorium.Connection.Client;
import com.MonoCycleStudios.team.victorium.Game.Enums.GameFragments;
import com.MonoCycleStudios.team.victorium.Game.Fragments.GroundEvents.RegionMenu;
import com.MonoCycleStudios.team.victorium.Game.Game;
import com.MonoCycleStudios.team.victorium.Game.GameRule;
import com.MonoCycleStudios.team.victorium.Game.Player;
import com.MonoCycleStudios.team.victorium.Game.Region;
import com.MonoCycleStudios.team.victorium.R;
import com.MonoCycleStudios.team.victorium.widget.ImageMap;
import com.MonoCycleStudios.team.victorium.widget.Utils.MMSystem;

import java.util.ArrayList;

public class Ground extends Fragment {
    static ImageMap mImageMap;
    public static ArrayList<Region> regions = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_ground, container, false);

        mImageMap = (ImageMap) view.findViewById(R.id.map);
        setRegions(regions);
//        mImageMap.setImageResource(R.drawable.filleduamapregion_720_838);

        mImageMap.addOnImageMapClickedHandler(new ImageMap.OnImageMapClickedHandler()
        {
            @Override
            public void onImageMapClicked(int id, ImageMap imageMap)
            {
                if(GameRule.check(Client.iPlayer, "hit region", id)){
                    final RegionMenu rm = new RegionMenu();
                    final int regionInd = regions.lastIndexOf(new Region(-1,id,false,null,-1));
                    Region r = regions.get(regionInd);

//                    rm.setButtonText(GameRule.isFirstHalf ? "Capture" : "Attack");
                    rm.setLabelText(""+r.getCost());
                    rm.setOnClick(new View.OnClickListener(){
                        @Override
                        public void onClick (View v){
                            GameRule.check(Client.iPlayer,"hit bulb attack",regionInd);     // V ||
                            Game.getInstance().showFragment(GameFragments.GROUND_EVENT, rm.getFragment(), "remove", "all");
                        }
                    });
                    int x,y;
                    x = (int)(imageMap.mIdToArea.get(id).getOriginX() / imageMap.mIdToArea.get(id).getMagicMultiplier() / 2);
                    y = (int)(imageMap.mIdToArea.get(id).getOriginY() / imageMap.mIdToArea.get(id).getMagicMultiplier() / 2) + 60;
                    rm.setPosition(x,y);

                    MMSystem.out.println("567890- "+x + " " +y);
                    Game.getInstance().showFragment(GameFragments.GROUND_EVENT, rm, "remove", "all");
                    Game.getInstance().showFragment(GameFragments.GROUND_EVENT, rm, "add");
                }

//                    mImageMap.showBubble(id);
            }

            @Override
            public void onBubbleClicked(int id)
            {
                // V ||
//                GameRule.check(Client.iPlayer,"hit bulb attack",regions.lastIndexOf(new Region(-1,id,false,null,-1)));
            }

            @Override
            public void onImageMapMiss(ImageMap imageMap) {
//                Game.getInstance().showFragment(GameFragments.NONE, GameFragments.QUESTION);    // TEMP
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
        redrawMap();
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

        for(Region rg : regions){
            if(!regionsToReturn.contains(rg)){
                rg.isActive = rg.owner != null && rg.owner.equals(p);
                rg.isInteractive = false;
            }else{
                rg.isInteractive = true;
            }
        }
        redrawMap();
        return regionsToReturn;
    }

    public static void redrawMap(){
        mImageMap.invalidate();
    }

    public static void setRegions(ArrayList<Region> newRegions) {
        regions = newRegions;
        for(Region rg : regions) {
            rg.setBitmaps();
            rg.computeNeighbourhoods(regions);
        }
        redrawMap();
    }

    public static void updateRegion(int id, Player newOwner/*, int newCost*/){
        regions.get(id).update(newOwner/*, newCost*/);
        redrawMap();
    }

    public static int getRegionIDFromMapID(int mapID){
        return regions.indexOf(new Region(-1,mapID,false,null,-1));
    }

    public static int getMapIDFromRegionID(int regionID){
        return regions.get(regions.indexOf(new Region(regionID,-1,false,null,-1))).mapID;
    }

    public static String getRegionCoords(int rgID){
        return "" +
                ((int)(mImageMap.mIdToArea.get(rgID).getOriginX() / mImageMap.mIdToArea.get(rgID).getMagicMultiplier() / 2)) +
                ":;" +
                ((int)(mImageMap.mIdToArea.get(rgID).getOriginY() / mImageMap.mIdToArea.get(rgID).getMagicMultiplier() / 2));
    }
}
