package com.MonoCycleStudios.team.victorium.Game;

import com.MonoCycleStudios.team.victorium.Connection.Client;
import com.MonoCycleStudios.team.victorium.Connection.Enums.CommandType;
import com.MonoCycleStudios.team.victorium.Connection.MonoPackage;
import com.MonoCycleStudios.team.victorium.Game.Enums.GameCommandType;
import com.MonoCycleStudios.team.victorium.Game.Enums.GameFragments;
import com.MonoCycleStudios.team.victorium.Game.Fragments.Ground;

import java.util.ArrayList;
import java.util.Arrays;

public class GameRule {

    static boolean isFirstHalf = true;

    public static final String[] action = {
            "None",
            "hit region",
            "hit bulb attack",
            "attacked",
            "under attack",
            "spec battle",
            "chose answer",
            "win",
            "lose"
    };

    static ArrayList<Player> activePlayers = new ArrayList<>();

    private static int tick = 0;

    GameRule(Player p){
        activePlayers.add(p);
    }

    // click handler list
    static ArrayList<OnUpdateHandler> mCallbackList;

    public interface OnUpdateHandler
    {
        void onTickUpdate(int currentTick, boolean isFirstHalf);
    }

    public void addOnUpdateHandler( OnUpdateHandler h ) {
        if (h != null) {
            if (mCallbackList == null) {
                mCallbackList = new ArrayList<OnUpdateHandler>();
            }
            mCallbackList.add(h);
        }
    }

    private static void incrementTick(){
        tick++;
        if(mCallbackList != null) {
            for (OnUpdateHandler h : mCallbackList){
                h.onTickUpdate(tick, isFirstHalf);
            }
        }
    }

    public static void update(int t, ArrayList<Player> players){
        tick = (t == -1 || t != tick+1) ? ++tick : t;

        activePlayers.clear();
        activePlayers.addAll(players);
    }

    public static boolean check(Player p, String str, Object param){
        {
            if(str.equalsIgnoreCase(action[0])){
                System.out.println("wow");
            }else if(str.equalsIgnoreCase(action[1])){
                if(activePlayers.contains(p)){
                    if(Ground.getAllCapturableRegions(p).contains(new Region(-1,(Integer)param,false,null)))
                        return true;
                }else{
                    Game.getInstance().showFragment(GameFragments.ALERT, "Now " + activePlayers.get(0).getPlayerName() + "'s turn. Sorry...", 2);
                }
            }else if(str.equalsIgnoreCase(action[2])){
                if(activePlayers.contains(p)) {

                    System.out.println(Arrays.toString(activePlayers.toArray()));
                    System.out.println(p.toString() + " []  " +  param.toString());
                    Client.getInstance().addOutCommand(
                            GameCommandType.GAMERULE.getStr(), CommandType.GAMEDATA.getStr(),
                                new MonoPackage("check","required",new MonoPackage(str,param.toString(),p))
                            );
                }else{
                    Game.getInstance().showFragment(GameFragments.ALERT, "Now " + activePlayers.get(0).getPlayerName() + "'s turn. Sorry...", 2);
                }
            }else if(str.equalsIgnoreCase(action[3])){

            }else if(str.equalsIgnoreCase(action[4])){

            }else if(str.equalsIgnoreCase(action[5])){

            }else if(str.equalsIgnoreCase(action[6])){

            }else if(str.equalsIgnoreCase(action[7])){

            }else if(str.equalsIgnoreCase(action[8])){

            }
        }

//        if(str.equalsIgnoreCase("onMapClicked")) {
//
//            if(p.getPlayerID() == activePlayers.get(0).getPlayerID()) {
//                Game.getInstance().commandProcess(new MonoPackage(GameCommandType.GAMERULE.getStr(),"",null));
//                return true;
//            }else{
//                Game.getInstance().showFragment(GameFragments.ALERT, "Now " + activePlayers.get(0).getPlayerName() + "'s turn. Sorry...", 2);
//            }
//        }

        return false;
    }

    static void logic(Player p, String str, Object param){
        if(Game.isServer){
            if(str.equalsIgnoreCase(action[0])){
                System.out.println("wow");
            }else if(str.equalsIgnoreCase(action[1])){

            }else if(str.equalsIgnoreCase(action[2])){

                if(p.getPlayerID() != activePlayers.get(0).getPlayerID()){

                    System.out.println("Ho-rey21!");
                }else if(Ground.regions.get(Integer.parseInt((String) param)).owner == null){

                    System.out.println("Ho-rey22!");
                    Ground.regions.get(Integer.parseInt((String) param)).owner = p;

                    Game.getInstance().useGameServer("all", null,
                            new MonoPackage(GameCommandType.REGIONS.getStr(), CommandType.GAMEDATA.getStr(),
                                new MonoPackage("update",""+(Ground.regions.get(Integer.parseInt((String) param)).id),p)
                            ));

//                    Game.getInstance().useGameServer("player",p,
//                            new MonoPackage(
//                                    GameCommandType.ALERT.getStr(),CommandType.GAMEDATA.getStr(),
//                                    "U captured region " + Ground.regions.get(Integer.parseInt((String) param)).id));
//                    Game.getInstance().useGameServer("!player",p,
//                            new MonoPackage(
//                                    GameCommandType.ALERT.getStr(),CommandType.GAMEDATA.getStr(),
//                                    "Player " + p.getPlayerName() + " captured " +Ground.regions.get(Integer.parseInt((String) param)).id+ " region"));

                    incrementTick();
                }

            }else if(str.equalsIgnoreCase(action[3])){

            }else if(str.equalsIgnoreCase(action[4])){

            }else if(str.equalsIgnoreCase(action[5])){

            }else if(str.equalsIgnoreCase(action[6])){

            }else if(str.equalsIgnoreCase(action[7])){

            }else if(str.equalsIgnoreCase(action[8])){

            }
        }
    }
}
