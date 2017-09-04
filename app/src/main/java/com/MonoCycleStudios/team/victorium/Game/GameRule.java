package com.MonoCycleStudios.team.victorium.Game;

import android.os.Handler;
import android.os.Looper;

import com.MonoCycleStudios.team.victorium.Connection.Client;
import com.MonoCycleStudios.team.victorium.Connection.Enums.CommandType;
import com.MonoCycleStudios.team.victorium.Connection.MonoPackage;
import com.MonoCycleStudios.team.victorium.Game.Enums.GameCommandType;
import com.MonoCycleStudios.team.victorium.Game.Enums.GameFragments;
import com.MonoCycleStudios.team.victorium.Game.Enums.PlayerState;
import com.MonoCycleStudios.team.victorium.Game.Fragments.Ground;
import com.MonoCycleStudios.team.victorium.widget.MyCountDownTimer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GameRule {

    public static boolean isFirstHalf = true;
    public static boolean isFinished = false;

    public static final String[] action = {
            "None",
            "hit region",
            "hit bulb attack",
            "attack",
            "under attack",
            "spec battle",
            "chose answer",
            "show answer",
            "win",
            "lose",
            "tie"
    };

    static Player activePlayer = null;
    static Player defencePlayer = null;
    static Region activeRegion = null;

    private static int tick = 0;

    private static QuestionChecker questionChecker = new QuestionChecker(null);

    GameRule(Player p){
        activePlayer = p;

        isFirstHalf = true;
        isFinished = false;
        defencePlayer = null;
        defencePlayer = null;
        activeRegion = null;
        tick = 0;
        questionChecker = new QuestionChecker(null);

        if(mCallbackList != null)
            mCallbackList.clear();
    }

    // click handler list
    static ArrayList<OnUpdateHandler> mCallbackList;

    public interface OnUpdateHandler
    {
        void onTickUpdate(int currentTick, boolean isFirstHalf, boolean isNextCard);

        void onNextHalf(int currentTick, boolean isFirstHalf);

        void onFinish();
    }

    public void addOnUpdateHandler( OnUpdateHandler h ) {
        if (h != null) {
            if (mCallbackList == null) {
                mCallbackList = new ArrayList<OnUpdateHandler>();
            }
            mCallbackList.add(h);
        }
    }

    private static void incrementTick(boolean isNextCard){
        tick++;
        System.out.println("[][1][]" + tick + " | " + isFirstHalf);

        if(!isFirstHalf && tick>= (25 - Game.getInstance().playersNumber)*3){
            isFinished = true;
            if(mCallbackList != null) {
                for (OnUpdateHandler h : mCallbackList){
                    h.onFinish();
                }
            }
            return;
        }
        if(!isFinished) {
            if (isFirstHalf && tick >= 25 - Game.getInstance().playersNumber) {
                isFirstHalf = false;
                if (mCallbackList != null) {
                    for (OnUpdateHandler h : mCallbackList) {
                        h.onNextHalf(tick, isFirstHalf);
                    }
                }
            }
            if (mCallbackList != null) {
                for (OnUpdateHandler h : mCallbackList) {
                    h.onTickUpdate(tick, isFirstHalf, isNextCard);
                }
            }
        }
    }

    public static void update(int t, Player actPlayer, Player defPlayer){
        activePlayer = actPlayer;
        defencePlayer = defPlayer;

        if(activePlayer.getPlayerState().equals(PlayerState.DEFEAT)){
            incrementTick(true);
        }
    }

    public static boolean check(Player p, String str, Object param){
        {
            if(str.equalsIgnoreCase(action[0])){
                System.out.println("wow");
            }else if(str.equalsIgnoreCase(action[1])){  //  "hit region"
                if(isFirstHalf) {
                    if (activePlayer.equals(p)) {
                        if (Ground.getAllCapturableRegions(p).contains(new Region(-1, (Integer) param, false, null,-1)))
                            return true;
                    } else {
                        Game.getInstance().showFragment(GameFragments.ALERT, "Now " + activePlayer.getPlayerName() + "'s turn. Sorry...", 2);
                    }
                }else{
                    if (activePlayer.equals(p)) {
                        if (Ground.getAllAttackableRegions(p).contains(new Region(-1, (Integer) param, false, null,-1)))
                            return true;
                    } else {
                        Game.getInstance().showFragment(GameFragments.ALERT, "Now " + activePlayer.getPlayerName() + "'s turn. Sorry...", 2);
                    }
                }
            }else if(str.equalsIgnoreCase(action[2])){  //  "hit bulb attack"
                if(isFirstHalf) {
                    if (activePlayer.equals(p)) {

                        System.out.println(activePlayer.toString());
                        System.out.println(p.toString() + " []  " + param.toString());
                        Client.getInstance().addOutCommand(
                                GameCommandType.GAMERULE.getStr(), CommandType.GAMEDATA.getStr(),
                                new MonoPackage("check", "required", new MonoPackage(str, param.toString(), p))
                        );
                    } else {
                        Game.getInstance().showFragment(GameFragments.ALERT, "Now " + activePlayer.getPlayerName() + "'s turn. Sorry...", 2);
                    }
                }else{
                    if (activePlayer.equals(p)) {
                        Client.getInstance().addOutCommand(
                                GameCommandType.GAMERULE.getStr(), CommandType.GAMEDATA.getStr(),
                                new MonoPackage("check", "required", new MonoPackage(str, param.toString(), p))
                        );
                    } else {
                        Game.getInstance().showFragment(GameFragments.ALERT, "Now " + activePlayer.getPlayerName() + "'s turn. Sorry...", 2);
                    }
                }
            }else if(str.equalsIgnoreCase(action[3])){  //  "attack"
                Client.getInstance().iPlayer.setPlayerState(PlayerState.ATTACK);
            }else if(str.equalsIgnoreCase(action[4])){  //  "under attack"
                Client.getInstance().iPlayer.setPlayerState(PlayerState.DEFEND);
            }else if(str.equalsIgnoreCase(action[5])){  //  "spec battle"
                Client.getInstance().iPlayer.setPlayerState(PlayerState.SPEC);
            }else if(str.equalsIgnoreCase(action[6])){  //  "chose answer"
                if(isFirstHalf) {
                    System.out.println("well, nothing, for this part of game ¯\\_(ツ)_/¯");
                }else{
                    if (activePlayer.equals(p) || defencePlayer.equals(p)) {
                        Client.getInstance().addOutCommand(
                                GameCommandType.GAMERULE.getStr(), CommandType.GAMEDATA.getStr(),
                                new MonoPackage("check", "required", new MonoPackage(str, param.toString(), p))
                        );
                    }
                }
            }else if(str.equalsIgnoreCase(action[7])){  //  "show answer"

            }else if(str.equalsIgnoreCase(action[8])){  //  "win"

            }else if(str.equalsIgnoreCase(action[9])){  //  "lose"

            }else if(str.equalsIgnoreCase(action[10])){  //  "tie"

            }
        }
        return false;
    }

    static void logic(Player p, String str, Object param){
        if(Game.isServer){
            if(str.equalsIgnoreCase(action[0])){
                System.out.println("wow");
            }else if(str.equalsIgnoreCase(action[1])){

            }else if(str.equalsIgnoreCase(action[2])){

                if(isFirstHalf) {
                    if (p.getPlayerID() != activePlayer.getPlayerID()) {

                        System.out.println("Ho-rey21!");
                    } else if (Ground.regions.get(Integer.parseInt((String) param)).owner == null) {

                        System.out.println("Ho-rey22!");
                        Ground.regions.get(Integer.parseInt((String) param)).owner = p;

                        Game.getInstance().useGameServer("all", null,
                                new MonoPackage(GameCommandType.REGIONS.getStr(), CommandType.GAMEDATA.getStr(),
                                        new MonoPackage("update", "" + (Ground.regions.get(Integer.parseInt((String) param)).id), p)
                                ));
                        Game.getInstance().useGameServer("all",null,
                                new MonoPackage(GameCommandType.PLAYER.getStr(),CommandType.GAMEDATA.getStr(),
                                        new MonoPackage("score","100",p)));

                        incrementTick(true);
                    }
                }else{
                    if (p.getPlayerID() != activePlayer.getPlayerID()) {

                        System.out.println("Ho-rey21w!");
                    } else if (Ground.regions.get(Integer.parseInt((String) param)).owner != null) {

                        System.out.println("Ho-rey22w!");
//                        Ground.regions.get(Integer.parseInt((String) param)).owner = p;

                        Object[] newQuestion = QuestionParser.getQuestion("random");
                        int rightAnswer = (int)newQuestion[1];
                        Question tmpQuest = (Question)newQuestion[0];

//                        Question tmpQuest = new Question(QuestionType.ONE_FROM_FOUR_NORMAL,QuestionCategory.MATH,"well, we NEED de PARSER T_T",new String[]{"No!","We really need","C`MON","NOT that hard :c"},rightAnswer);
                        if(!questionChecker.isTie) {
                            activeRegion = Ground.regions.get(Integer.parseInt((String) param));
                            defencePlayer = activeRegion.owner;

                            incrementTick(false);

//                        Game.getInstance().useGameServer("all",null,
//                                new MonoPackage(GameCommandType.GAMERULE.getStr(),CommandType.GAMEDATA.getStr(),
//                                        new MonoPackage("update",param.toString(),new MonoPackage("empty","empty2",activePlayer))));

                            Game.getInstance().useGameServer("player", p,
                                    new MonoPackage(GameCommandType.GAMERULE.getStr(), CommandType.GAMEDATA.getStr(),
                                            new MonoPackage("check", "nope",
                                                    new MonoPackage("attack", param.toString(), Ground.regions.get(Integer.parseInt((String) param)).owner))));

                            Game.getInstance().useGameServer("player", Ground.regions.get(Integer.parseInt((String) param)).owner,
                                    new MonoPackage(GameCommandType.GAMERULE.getStr(), CommandType.GAMEDATA.getStr(),
                                            new MonoPackage("check", "nope",
                                                    new MonoPackage("under attack", param.toString(), p))));

                            Game.getInstance().useGameServer("!players", p, Ground.regions.get(Integer.parseInt((String) param)).owner,
                                    new MonoPackage(GameCommandType.GAMERULE.getStr(), CommandType.GAMEDATA.getStr(),
                                            new MonoPackage("check", "nope",
                                                    new MonoPackage("spec battle", param.toString(), p))));
                        }

                        Game.getInstance().useGameServer("all", null,
                                new MonoPackage(GameCommandType.QUESTION.getStr(), CommandType.GAMEDATA.getStr(), tmpQuest));

                        questionChecker = new QuestionChecker(tmpQuest);
                        questionChecker.rightAnswer = rightAnswer;
                        Thread tqc = new Thread(questionChecker);
                        tqc.start();
                    }
                }

            }else if(str.equalsIgnoreCase(action[3])){

            }else if(str.equalsIgnoreCase(action[4])){

            }else if(str.equalsIgnoreCase(action[5])){

            }else if(str.equalsIgnoreCase(action[6])) {

                String[] params = ((String)param).split(":;");
                questionChecker.gotAnswer(p, Integer.parseInt(params[0]), Integer.parseInt(params[1]));

            }else if(str.equalsIgnoreCase(action[7])){

                Game.getInstance().useGameServer("all", null,
                        new MonoPackage(GameCommandType.QUESTION.getStr(), CommandType.GAMEDATA.getStr(),
                                new MonoPackage("show answer",questionChecker.rightAnswer+"",questionChecker.answers)));

            }else if(str.equalsIgnoreCase(action[8])){

                Game.getInstance().useGameServer("all", null,
                        new MonoPackage(GameCommandType.QUESTION.getStr(), CommandType.GAMEDATA.getStr(), null));

                if(activePlayer.equals(p)) {

                    if (Ground.regions.get((int) param).currentHP - 1 > 0) {

                        justWait(500);

                        Game.getInstance().useGameServer("all", null,
                                new MonoPackage(GameCommandType.REGIONS.getStr(), CommandType.GAMEDATA.getStr(),
                                        new MonoPackage("update", (int) param + "", "hit")
                                ));

                        justWait(500);

                        logic(activePlayer, action[2], ""+param);
                        return;

                    } else {

                        Ground.regions.get((int) param).owner = p;

                        Game.getInstance().useGameServer("all", null,
                                new MonoPackage(GameCommandType.PLAYER.getStr(), CommandType.GAMEDATA.getStr(),
                                        new MonoPackage("score", ""+Ground.regions.get((int) param).getCost(), p)));

                        Game.getInstance().useGameServer("all", null,
                                new MonoPackage(GameCommandType.PLAYER.getStr(), CommandType.GAMEDATA.getStr(),
                                        new MonoPackage("score", "-"+Ground.regions.get((int) param).getCost(), defencePlayer)));

                       justWait(500);

                        Game.getInstance().useGameServer("all", null,
                                new MonoPackage(GameCommandType.REGIONS.getStr(), CommandType.GAMEDATA.getStr(),
                                        new MonoPackage("update", (int) param + "", p)
                                ));

                        justWait(500);

                    }

                }else if (defencePlayer.equals(p)) {

                    Game.getInstance().useGameServer("all", null,
                            new MonoPackage(GameCommandType.PLAYER.getStr(), CommandType.GAMEDATA.getStr(),
                                    new MonoPackage("score", "50", defencePlayer)));

                }

                incrementTick(true);

            }else if(str.equalsIgnoreCase(action[9])){

                Game.getInstance().useGameServer("all", null,
                        new MonoPackage(GameCommandType.QUESTION.getStr(), CommandType.GAMEDATA.getStr(), null));

                incrementTick(true);

            }else if(str.equalsIgnoreCase(action[10])){

            }
        }
    }

    static void justWait(int millis){
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    private static class QuestionChecker implements Runnable{

        Question question;
        private int rightAnswer;
        MyCountDownTimer mcdt;

        boolean isTimeRunsOut = false;
        boolean isTerminated = false;
        boolean isBothAnswered = false;
        boolean isTie = false;

        Map<Player,Integer> answers = new HashMap<>();
        Map<Player,Integer> answersTime = new HashMap<>();  //  NOT YET !!!

        ArrayList<Player> winners = new ArrayList<>();

        QuestionChecker(Question question){
            this.question = question;
            isTimeRunsOut = false;
            isTerminated = false;
            isBothAnswered = false;
            isTie = false;
            answers.clear();
            winners.clear();
        }

        @Override
        public void run() {

            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    mcdt = new MyCountDownTimer(15000+300, 20);     //      TEMP !!! 15sec to answer the question + 300 addition millis due ping and stuff

                    mcdt.addOnUpdateHandler(new MyCountDownTimer.OnUpdateHandler() {
                        @Override
                        public void onTickUpdate(long currentMillis, int milli, int sec, int min) {

                        }

                        @Override
                        public void onFinish() {
                            isTimeRunsOut = true;
                        }
                    });
                    mcdt.start();
                }
            });

            while(!isTimeRunsOut){
                if(isBothAnswered || isTerminated) {
                    mcdt.cancel();
                    break;
                }
            }

            for (Map.Entry entry : answers.entrySet()) {
                Player key = (Player) entry.getKey();
                Integer value = (Integer) entry.getValue();
                System.out.println(key + " : " + value);
                if(question.checkAnswers(value))
                    winners.add(key);
            }

            logic(activePlayer,action[7],null); //  show answer

            try {
                Thread.sleep(2000);     //      TEMP !!! 2sec to show question results
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if(winners.size() >= 2){
                System.out.println("Well, we need to pop one more question \\0/");
                System.out.println("Or check who faster");

                // Super system to check if one int larger then another /0\

                isTie = true;
                logic(activePlayer, action[2], ""+activeRegion.id);
            }else if(winners.size() >= 1){
                logic(winners.get(0),action[8],activeRegion.id);
            }else {
                logic(activePlayer,action[9],null);
            }
        }

        void gotAnswer(Player p, int ind, int time){
            answers.put(p, ind);
            answersTime.put(p, time);
            if (answers.size() >= 2){
                isBothAnswered = true;
            }

            Game.getInstance().useGameServer("all",null,
                    new MonoPackage(GameCommandType.QUESTION.getStr(),CommandType.GAMEDATA.getStr(),
                            new MonoPackage("got answer","",p)));

            System.out.println(" { "+isBothAnswered+" } " + p + " " + ind);
        }

        void setTimeRunsOut(){
            isTimeRunsOut = true;
        }

        void setTerminated(){
            isTerminated = true;
        }
    }
}
