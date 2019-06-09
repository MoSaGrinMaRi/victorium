package com.MonoCycleStudios.team.victorium.Game;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.MonoCycleStudios.team.victorium.Connection.Client;
import com.MonoCycleStudios.team.victorium.Connection.Enums.CommandType;
import com.MonoCycleStudios.team.victorium.Connection.Lobby;
import com.MonoCycleStudios.team.victorium.Connection.MonoPackage;
import com.MonoCycleStudios.team.victorium.Game.Enums.GameCommandType;
import com.MonoCycleStudios.team.victorium.Game.Enums.PlayerState;
import com.MonoCycleStudios.team.victorium.Game.Fragments.Ground;
import com.MonoCycleStudios.team.victorium.R;
import com.MonoCycleStudios.team.victorium.widget.Utils.MMSystem;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

public class Region implements Serializable{

    private static final long serialVersionUID = 666634L;

    public int id = -1;
    public int mapID = -1;
    public boolean isBase = false;
    public boolean isBaseDestroyed = false;
    public int currentHP = -1;
    public Player owner;
    private int cost;

    private ArrayList<Region> neighbourhoods = new ArrayList<>();
    private String[] neighbourhoodsID;
    private transient Bitmap pawnBitmap;
    private transient Bitmap baseBitmap;
    public boolean isActive = true;
    public boolean isInteractive = true;

    public Region(int id, int mapID, boolean isBase, Player owner, int cost){
        this.id = id;
        this.mapID = mapID;
        this.isBase = isBase;
        this.currentHP = getMaxHP();
        this.owner = owner;
        this.cost = cost;

        if(owner != null)
            MMSystem.out.println("tyuio1 "+ owner + " " +owner.getPlayerState());
    }

    public void update(Player newOwner/*, int newCost*/){
        if(newOwner == null)
            --this.currentHP;
        else {
            if(isBase && this.owner != null && this.owner != newOwner) {
                isBaseDestroyed = true;
                owner.setPlayerState(PlayerState.DEFEAT);
                Lobby.getPlayersList().get(owner.getPlayerID()).setPlayerState(PlayerState.DEFEAT);

                int allCost = 0;

                for(int i = 0; i < Ground.regions.size(); i++){
                    if(Ground.regions.get(i).owner.equals(this.owner) && Ground.regions.get(i).id != this.id){
//                        Ground.regions.get(i).owner = newOwner;
//                        Game.getInstance().useGameServer("all", null,
//                                new MonoPackage(GameCommandType.PLAYER.getStr(), CommandType.GAMEDATA.getStr(),
//                                        new MonoPackage("score", ""+Ground.regions.get((int) param).getCost(), p)));
                        Client.iPlayer.getPlayerGame().commandProcess(
                                new MonoPackage(GameCommandType.REGIONS.getStr(), "",
                                        new MonoPackage("update", ""+Ground.regions.get(i).id, newOwner))
                        );

                        allCost += Ground.regions.get(i).getCost();

                    }
                }

                Client.iPlayer.getPlayerGame().commandProcess(
                        new MonoPackage(GameCommandType.PLAYER.getStr(), "",
                                new MonoPackage("score", "s"+0, this.owner))
                );
                Client.iPlayer.getPlayerGame().commandProcess(
                        new MonoPackage(GameCommandType.PLAYER.getStr(), "",
                                new MonoPackage("score", ""+allCost, newOwner))
                );

            }

            this.owner = newOwner;
            this.currentHP = getMaxHP();
        }
        setBitmaps();
        //this.cost = newCost;
    }

    public int getCost() {
        return cost;
    }
    public Bitmap getPawn(){
        return pawnBitmap;
    }
    public Bitmap getBase(){
        return baseBitmap;
    }
    private int getMaxHP(){
        if(isBase) {
            if(isBaseDestroyed)
                return 1;
            return 3; //  TEMP !!! GLOBAL_VAR 3 = BASE_MAX_HP
        }
        else
            return 1; //  TEMP !!! GLOBAL_VAR 1 = WARRIOR_MAX_HP
    }

    public void setIsBase(boolean isBase){
        this.isBase = isBase;
        this.currentHP = getMaxHP();
    }

    private void setPawnBitmap(Character ch){
        int frameWidth = 174/2; //522/6;
        int frameHeight = 274/2;//820/6;
        int frameCountX = 0;    //  MAX = 3; Only 3 different type
        int frameCountY = 0;    //  MAX = 2; Only 2 different color for each

        switch (ch.getColor()){
            case RED:
                frameCountX = 0;
                frameCountY = 0;
                break;
            case BLUE:
                frameCountX = 1;
                frameCountY = 0;
                break;
            case ORANGE:
                frameCountX = 2;
                frameCountY = 0;
                break;
            case GREEN:
                frameCountX = 0;
                frameCountY = 1;
                break;
            case BLACK:
                frameCountX = 1;
                frameCountY = 1;
                break;
            case PURPLE:
                frameCountX = 2;
                frameCountY = 1;
                break;

        }

        this.pawnBitmap = Bitmap.createBitmap(Game.pawnAtlas,
                frameWidth * frameCountX,
                frameHeight * frameCountY,
                frameWidth,
                frameHeight);
    }
    private void setBaseBitmap(Character ch){
        int frameWidth = 257/2; //1028/8;
        int frameHeight = 156/2;//640/8;
        int frameCountX = 0;    //  MAX = 4; Only 4 castle state
        int frameCountY = 0;    //  MAX = Lobby.MAX_PLAYERS; Only 6 players can play

        switch (ch.getColor()){
            case RED:
                frameCountY = 0;
                break;
            case BLUE:
                frameCountY = 1;
                break;
            case ORANGE:
                frameCountY = 2;
                break;
            case GREEN:
                frameCountY = 3;
                break;
            case BLACK:
                frameCountY = 4;
                break;
            case PURPLE:
                frameCountY = 5;
                break;
        }
        switch (isBaseDestroyed ? 0 : currentHP){
            case 3:
                frameCountX = 0;
                break;
            case 2:
                frameCountX = 1;
                break;
            case 1:
                frameCountX = 2;
                break;
            case 0:
                frameCountX = 3;
                break;
        }

        this.baseBitmap = Bitmap.createBitmap(Game.castleAtlas,
                frameWidth * frameCountX,
                frameHeight * frameCountY,
                frameWidth,
                frameHeight);
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
        MMSystem.out.println("neighbourhoodsID"+Arrays.toString(neighbourhoodsID));
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

    @Override
    public String toString() {
        return "Region{" +
                "id=" + id +
//                ", isBase=" + isBase +
//                ", owner=" + owner +
                '}';
    }
}