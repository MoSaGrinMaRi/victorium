package com.MonoCycleStudios.team.victorium.Game;

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
        //this.cost = newCost;
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
            if((sameObj = this.id == ((Region)obj).id )|| (sameObj = this.mapID == ((Region)obj).mapID))
                if(this.owner != null && ((Region)obj).owner != null)
                    sameObj = this.owner.getPlayerID() == ((Region)obj).owner.getPlayerID();

        return sameObj;
    }

    public int hashCode() {
        return java.util.Objects.hashCode(id);
    }
}