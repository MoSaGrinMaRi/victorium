package com.MonoCycleStudios.team.victorium.Game;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.MonoCycleStudios.team.victorium.R;
import com.MonoCycleStudios.team.victorium.widget.ImageMap;

import java.util.Arrays;

public class Ground extends Fragment {
    ImageMap mImageMap;
    public static Region[] regions;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View viev = inflater.inflate(R.layout.fragment_ground, container, false);

        mImageMap = (ImageMap) viev.findViewById(R.id.map);
        mImageMap.setImageResource(R.drawable.filleduamapregion);

        mImageMap.addOnImageMapClickedHandler(new ImageMap.OnImageMapClickedHandler()
        {
            @Override
            public void onImageMapClicked(int id, ImageMap imageMap)
            {
                mImageMap.showBubble(id);
            }

            @Override
            public void onBubbleClicked(int id)
            {
                // react to info bubble for area being tapped
            }
        });

        return viev;
    }

    // need this?
    public static void setRegions(Region[] regions) {
        Ground.regions = regions;
    }

    // use ArrayList instead?
    public static void addRegion(Region newRegion){
        Region[] result = Arrays.copyOf(regions, regions.length + 1);
        result[result.length - 1] = newRegion;

        regions = result;

    }

    public static void updateRegion(int id, Player newOwner/*, int newCost*/){
        regions[id].update(newOwner/*, newCost*/);
    }
}
