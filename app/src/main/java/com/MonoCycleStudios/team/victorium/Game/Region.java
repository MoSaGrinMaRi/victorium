package com.MonoCycleStudios.team.victorium.Game;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.MonoCycleStudios.team.victorium.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

public class Region implements Serializable{

    private static final long serialVersionUID = 666634L;

    public int id = -1;
    public int mapID = -1;
    public boolean isBase = false;
    public Player owner;
//    private int cost;
//    private AndroidImageMap rgBorder;     // now sure about it
    private ArrayList<Region> neighbourhoods = new ArrayList<>();
    private String[] neighbourhoodsID;
    private transient Bitmap pawnBitmap;
    private transient Bitmap baseBitmap;
    public boolean isActive = true;
    public boolean isInteractive = true;

    public Region(int id, int mapID, boolean isBase, Player owner /*, int cost, AndroidImageMap rgBorder*/){
        this.id = id;
        this.mapID = mapID;
        this.isBase = isBase;
        this.owner = owner;
//        this.cost = cost
//        this.rgBorder = rgBorder;
    }

    public void update(Player newOwner/*, int newCost*/){
        this.owner = newOwner;
        setBitmaps();
        //this.cost = newCost;
    }
    public Bitmap getPawn(){
        return pawnBitmap;
    }
    public Bitmap getBase(){
        return baseBitmap;
    }

    private void setPawnBitmap(Character ch){
        int resource = -1;
        switch (ch.getColor()){
            case RED:
                resource = R.drawable.man_red;
                break;
            case BLUE:
                resource = R.drawable.wizard_blue;
                break;
            case ORANGE:
                resource = R.drawable.warrior_orange;
                break;
            case GREEN:
                resource = R.drawable.man_green;
                break;
            case BLACK:
                resource = R.drawable.wizard_dark;
                break;
            case PURPLE:
                resource = R.drawable.warrior_purple;
                break;

        }
        pawnBitmap = BitmapFactory.decodeResource(Game.getInstance().getApplicationContext().getResources(), resource);
        pawnBitmap = Bitmap.createScaledBitmap(pawnBitmap, pawnBitmap.getWidth() / 24, pawnBitmap.getHeight() / 24, true);
    }
    private void setBaseBitmap(Character ch){
        int resource = -1;
        switch (ch.getColor()){
            case RED:
                resource = R.drawable.castle_b;
                break;
            case BLUE:
                resource = R.drawable.castle_dst1_b;
                break;
            case ORANGE:
                resource = R.drawable.castle_dst3_b;
                break;
            case GREEN:
                resource = R.drawable.castle_b;
                break;
            case BLACK:
                resource = R.drawable.castle_dst1_b;
                break;
            case PURPLE:
                resource = R.drawable.castle_dst3_b;
                break;

        }
        this.baseBitmap = BitmapFactory.decodeResource(Game.getInstance().getApplicationContext().getResources(), resource);
        this.baseBitmap = Bitmap.createScaledBitmap(baseBitmap, baseBitmap.getWidth() / 32, baseBitmap.getHeight() / 32, true);
    }
    public void setBitmaps(){
        if(owner != null)
            if(isBase)
                setBaseBitmap(this.owner.getPlayerCharacter());
            else
                setPawnBitmap(this.owner.getPlayerCharacter());
    }

    public void setNeighbourhoods(Region[] neighbourhoods) {
        this.neighbourhoods.clear();
        this.neighbourhoods.addAll(Arrays.asList(neighbourhoods));
    }
    public void setNeighbourhoodsID(String[] neighbourhoodsID){this.neighbourhoodsID = neighbourhoodsID;}
    public ArrayList<Region> getNeighbourhoods(){
        return this.neighbourhoods;
    }

    public void computeNeighbourhoods(ArrayList<Region> allRegion){
        for(String s : neighbourhoodsID){
            neighbourhoods.add(
                    allRegion.get(Integer.parseInt(s))
            );
        }
    }

    @Override
    public boolean equals(Object obj)
    {
        boolean sameObj = false;

        if (obj != null && obj instanceof Region)
            if((sameObj = this.id == ((Region)obj).id ) || (sameObj = this.mapID == ((Region)obj).mapID))
                if(this.owner != null && ((Region)obj).owner != null)
                    sameObj = this.owner.getPlayerID() == ((Region)obj).owner.getPlayerID();

        return sameObj;
    }

    public int hashCode() {
        return java.util.Objects.hashCode(id);
    }
}