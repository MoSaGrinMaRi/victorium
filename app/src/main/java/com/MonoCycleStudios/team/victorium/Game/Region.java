package com.MonoCycleStudios.team.victorium.Game;

public class Region {
    private int id = -1;
    private boolean isBase = false;
    private Player owner;
//    private int cost;
//    private AndroidImageMap rgBorder;     // now sure about it
    private Region neighbourhoods[];

    Region(int id, boolean isBase, Player owner /*, int cost, AndroidImageMap rgBorder*/){
        this.id = id;
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
        this.neighbourhoods = neighbourhoods;
    }

    public Region[] getNeighbourhoods(){
        return this.neighbourhoods;
    }
}