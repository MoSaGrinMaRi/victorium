package com.MonoCycleStudios.team.victorium.Game;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.AsyncTask;
import android.app.Fragment;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import com.MonoCycleStudios.team.victorium.Connection.Client;
import com.MonoCycleStudios.team.victorium.Connection.Enums.CommandType;
import com.MonoCycleStudios.team.victorium.Connection.Lobby;
import com.MonoCycleStudios.team.victorium.Connection.MonoPackage;
import com.MonoCycleStudios.team.victorium.Connection.Server;
import com.MonoCycleStudios.team.victorium.Game.Enums.GameCommandType;
import com.MonoCycleStudios.team.victorium.Game.Enums.GameFragments;
import com.MonoCycleStudios.team.victorium.Game.Enums.GameState;
import com.MonoCycleStudios.team.victorium.Game.Enums.QuestionType;
import com.MonoCycleStudios.team.victorium.Game.Fragments.Alert;
import com.MonoCycleStudios.team.victorium.Game.Fragments.Ground;
import com.MonoCycleStudios.team.victorium.Game.Fragments.MyTimer;
import com.MonoCycleStudios.team.victorium.Game.Fragments.Notifier;
import com.MonoCycleStudios.team.victorium.Game.Fragments.Questioner;
import com.MonoCycleStudios.team.victorium.Game.Fragments.QueueTurners;
import com.MonoCycleStudios.team.victorium.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

public class Game extends AppCompatActivity {

    private static volatile Game instance;

    public static Game getInstance() {
        Game localInstance = instance;
        if (localInstance == null) {
            synchronized (Game.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new Game();
                }
            }
        }
        return localInstance;
    }

    private static int playersNumber;
    public static GameState gameState = GameState.WAITING_FOR_START;
    private static Server gameServer;
    private static Ground gameGround; //    for later change battlefield
    public static boolean isServer = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        instance = this;

        ListView lv = (ListView) findViewById(R.id.connectionsListView_2);
        lv.setAdapter(Lobby.adapter);
//        Lobby.forceREUpdateAdapter();

        if(gameState == GameState.WAITING_FOR_START && gameServer != null){
            new GamePrepare().start();
        }

        Fragment fragment;        // for change fragment
        fragment = gameGround;
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragmentPlaceGround, fragment);
        ft.commit();

        System.out.println("YEEEEEEE=================EEEEY!" + gameServer);
        if(gameServer != null) {
            new GameCore().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            isServer = true;
        }

        showFragment(GameFragments.NOTIFY, "We wait for all of Players... SRY");

        gameState = GameState.LAUNCHING;
    }

    boolean doubleBackToExitPressedOnce = false;
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            //  **  //
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    public void setup(Server server, int playersNumb){
        playersNumber = playersNumb;
        gameServer = server;
        gameGround = new Ground();
    }

    void setGameState(GameState newGameState){
        gameState = newGameState;
//        switch(newGameState){
//
//        }
    }
//
//    Fragment fragment = null;        // for change fragment
    QueueTurners fragment2;
    public void showFragment(GameFragments gf, Object... obj) {
        System.out.println("====================================================");
        System.out.println("We got " + gf.getStr() + " and " + obj.toString());
        System.out.println("====================================================");
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        switch(GameFragments.getTypeOf(gf.getStr())){
//            case AnotherNotifier1:
//            case AnotherNotifier2:
//            case AnotherNotifier3:
            case ALERT:{
                Alert fragment = new Alert();
                fragment.setAlertText((String)obj[0]);
                if (!isFinishing()) {
                    ft.setCustomAnimations(R.animator.fadein_scale_in, R.animator.fadeout_scale_out);
                    ft.replace(R.id.fragmentAlertPlaceholder, fragment, "tag0");
                    ft.commit();
                }
                if(obj.length> 1){
                    final int delay = ((int)obj[1]) * 1000; //ms

                    Thread tmpThread = new Thread(new Runnable() {
                        public void run() {
                            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                public void run() {
                                    showFragment(GameFragments.NONE, GameFragments.ALERT);
                                }
                            }, delay);
                        }
                    });
                    tmpThread.start();
                }
            }break;
            case NOTIFY:{
                Notifier fragment = new Notifier();
                fragment.setNotifyText((String)obj[0]);
                if (!isFinishing()) {
                    ft.setCustomAnimations(R.animator.fadein_scale_in, R.animator.fadeout_scale_out);
                    ft.replace(R.id.fragmentNotifyPlaceholder, fragment, "tag1");
                    ft.commit();
                }
                if(obj.length > 1){
                    final int delay = ((int)obj[1]) * 1000; //ms

                    Thread tmpThread = new Thread(new Runnable() {
                        public void run() {
                            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                public void run() {
                                    showFragment(GameFragments.NONE, GameFragments.NOTIFY);
                                }
                            }, delay);
                        }
                    });
                    tmpThread.start();
                }
            }break;
            case QUESTION:{
                Questioner fragment = new Questioner();
                if(obj[0] instanceof String) {
                    fragment.setQuestionDisplay(
                            new Question(QuestionType.FILM,
                                    "How many oscar DiCaprio have?",
                                    new String[]{"0", "-1", "-2213", "1"},
                                    3));
                }else{
                    fragment.setQuestionDisplay((Question)obj[0]);
                }
                if (!isFinishing()) {
                    ft.setCustomAnimations(R.animator.fadein_translate_fromtop, R.animator.fadeout_translate_todown);
                    ft.replace(R.id.fragmentQuestionPlaceholder, fragment, "tag2");
                    ft.commit();
                }
            }break;
            case QUEUETURNS:{
                fragment2 = new QueueTurners();
                fragment2.setPlayerArrayList((ArrayList<Player>) obj[0]);
                if (!isFinishing()) {
                    ft.replace(R.id.fragmentQueueTurns, fragment2);
                    ft.commit();
                }
            }break;
            case TIMER:{
                Fragment fragment3 = new MyTimer();
                if (!isFinishing()) {
                    ft.replace(R.id.fragmentTimer, fragment3);
                    ft.commit();
                }
                ((MyTimer)fragment3).setTimer((int) obj[0],(int) obj[1]);
            }break;
            case NONE:{
                if (!isFinishing()){
                    ft.setCustomAnimations(R.animator.fadein_scale_in, R.animator.fadeout_scale_out);
                    switch (GameFragments.getTypeOf(((GameFragments)obj[0]).getStr())){
                        case ALERT: {
                            Alert voidFragment = new Alert();
                            voidFragment.setIsVisible(false);
                            ft.replace(R.id.fragmentAlertPlaceholder, voidFragment, "rem");
                        }break;
                        case NOTIFY: {
                            Notifier voidFragment = new Notifier();
                            voidFragment.setIsVisible(false);
                            ft.replace(R.id.fragmentNotifyPlaceholder, voidFragment, "rem");
                        }break;
                        case QUESTION: {
                            Questioner voidFragment = new Questioner();
                            voidFragment.setIsVisible(false);
                            ft.replace(R.id.fragmentQuestionPlaceholder, voidFragment, "rem");
                        }break;
                    }
                    ft.commit();
                }
            }break;
        }
    }

    public void commandProcess(MonoPackage monoPackage){
        switch (GameCommandType.getTypeOf(monoPackage.getTypeOfObject())) {
            case WAITFORPLAYERS: {
                showFragment(GameFragments.NOTIFY, "We are all waiting.. sry");
            }
            break;
            case EXECUTESTART: {
                Client.getInstance().iPlayer.setPlayerGameState(GameState.RUNNING);
                showFragment(GameFragments.NOTIFY, "Capture the field!", 4);
            }
            break;
            case QUEUTURNS: {
                showFragment(GameFragments.QUEUETURNS, monoPackage.getObj());
            }
            break;
            case REGIONS: {
                MonoPackage tmp = (MonoPackage)monoPackage.getObj();
                switch(tmp.getTypeOfObject()){
                    case "set":{
                        gameGround.setRegions((ArrayList<Region>)tmp.getObj());
                    }break;
                    case "update":{
                        gameGround.updateRegion(Integer.parseInt(tmp.getDescOfObject()),(Player)tmp.getObj());
                    }break;
                }
            }
            break;
            case QUESTION:{
                showFragment(GameFragments.QUESTION, monoPackage.getObj());
            }break;
            case ALERT:{
                showFragment(GameFragments.ALERT, monoPackage.getObj(), 3);
            }break;
            case GAMERULE:{
                System.out.println("Ho-rey!-1" + ((MonoPackage)monoPackage.getObj()).getTypeOfObject());
                switch(((MonoPackage)monoPackage.getObj()).getTypeOfObject()){
                    case "update":{
                        MonoPackage tmp = (MonoPackage)((MonoPackage)monoPackage.getObj()).getObj();
                        GameRule.update(-1,(ArrayList<Player>)tmp.getObj());

                        if(((MonoPackage)monoPackage.getObj()).getDescOfObject().equalsIgnoreCase("nextCard"))
                            fragment2.showNextCard();
                    }break;
                    case "check":{
                        if(((MonoPackage)monoPackage.getObj()).getDescOfObject().equalsIgnoreCase("required")){
                            MonoPackage tmp = (MonoPackage)((MonoPackage)monoPackage.getObj()).getObj();
                            System.out.println("Ho-rey!");
                            GameRule.logic(
                                    (Player) tmp.getObj(),
                                    tmp.getTypeOfObject(),
                                    tmp.getDescOfObject()
                            );
                        }else{
                            System.out.println("Ho-rey!-2");
                            MonoPackage tmp = (MonoPackage)((MonoPackage)monoPackage.getObj()).getObj();
                            GameRule.check(
                                    (Player) tmp.getObj(),
                                    tmp.getTypeOfObject(),
                                    tmp.getDescOfObject()
                            );
                        }
                    }
                }
            }
        }
    }

    public void useGameServer(String str,Player p,MonoPackage monoPackage){
        if(isServer){
            if(str.equalsIgnoreCase("all")){
                gameServer.notifyAllClients(monoPackage);
            }else if((str.equalsIgnoreCase("player"))){
                gameServer.notifyPlayer(p, monoPackage);
            }else if((str.equalsIgnoreCase("!player"))){
                for(Player tmp : Lobby.getPlayersList()){
                    if(!tmp.equals(p))
                        gameServer.notifyPlayer(tmp, monoPackage);
                }
            }
        }
    }

    public void timeIsOver(){
        Question q = new Question(QuestionType.FILM,"just test",new String[]{"1","2","3","4"},3);

        gameServer.notifyPlayer(Lobby.getPlayersList().get(2), new MonoPackage(GameCommandType.QUESTION.getStr(),CommandType.GAMEDATA.getStr(),q));
        gameServer.notifyPlayer(Lobby.getPlayersList().get(1), new MonoPackage(GameCommandType.QUESTION.getStr(),CommandType.GAMEDATA.getStr(),q));

    }

    private class GameCore extends AsyncTask<Void, Void, Void>{

        ArrayList<Player> queueTurns;
        GameRule gameRule;

        @Override
        protected Void doInBackground(Void... params) {

            while (!isCancelled()){

                if(gameState == GameState.PREPARING) {
                    gameServer.notifyAllClients(new MonoPackage(GameCommandType.EXECUTESTART.getStr(),CommandType.GAMEDATA.getStr(),null));
                    gameState = GameState.RUNNING;

                    for(int i = 0; i < playersNumber; i++){
                        Region rg = Ground.regions.get(new Random().nextInt((20 - 2) + 1) + 2);
                        if(rg.owner == null){
                            rg.owner = Lobby.getPlayersList().get(i);
                            rg.isBase = true;
                        }else{
                            --i;
                        }
                    }
                    gameServer.notifyAllClients(new MonoPackage(GameCommandType.REGIONS.getStr(),CommandType.GAMEDATA.getStr(),
                            new MonoPackage("set","",Ground.regions)));

                    queueTurns = generatePlayersQueueTurn(Lobby.getPlayersList());
                    gameServer.notifyAllClients(new MonoPackage(GameCommandType.QUEUTURNS.getStr(),CommandType.GAMEDATA.getStr(),queueTurns));

                    gameRule = new GameRule(queueTurns.get(0));
                    gameServer.notifyAllClients(new MonoPackage(GameCommandType.GAMERULE.getStr(),CommandType.GAMEDATA.getStr(),
                            new MonoPackage("update","",new MonoPackage("","",GameRule.activePlayers))));

                    break;
                }
            }
            Collections.sort(Lobby.getPlayersList());

            showFragment(GameFragments.TIMER, 6000, 85);

            gameRule.addOnUpdateHandler(new GameRule.OnUpdateHandler(){
                @Override
                public void onTickUpdate(int currentTick, boolean isFirstHalf) {
                    gameRule.update(-1, new ArrayList<>(Arrays.asList(queueTurns.get(fragment2.getCardIndex()+1))));

                    gameServer.notifyAllClients(new MonoPackage(GameCommandType.GAMERULE.getStr(),CommandType.GAMEDATA.getStr(),
                            new MonoPackage("update","nextCard",new MonoPackage("","",GameRule.activePlayers))));

                }
            });

            while(!isCancelled()){
                if(2>1){
                }
            }
            return null;
        }

        ArrayList<Player> generatePlayersQueueTurn(ArrayList<Player> playerArrayList){
            ArrayList<Player> generatedPlayersQueueTurn = new ArrayList<>();

            Player first;
            for(int i = 0; i < 25/playersNumber - 1; i++) {
                for(int j = 0; j < playersNumber; j++) {
                    generatedPlayersQueueTurn.add(playerArrayList.get(j));
                }
                first = playerArrayList.get(0);
                System.arraycopy(playerArrayList.toArray(), 1, playerArrayList.toArray(), 0, playerArrayList.size() - 1 );
                playerArrayList.remove(0);
                playerArrayList.add(playerArrayList.size(), first);
            }
            if(25%playersNumber != 0){
                generatedPlayersQueueTurn.add(new Player(-1,"-2",null,new Character(0),null));//    TEMP!!! Need to be random Player who have least captured zones
            }
            System.out.println(Arrays.toString(generatedPlayersQueueTurn.toArray()));
            return generatedPlayersQueueTurn;
        }
    }

    private class GamePrepare extends Thread {

        boolean isPrepared = false;

        public void run() {
            int j = 0;
            while (!isPrepared) {

                gameServer.notifyAllClients(new MonoPackage(GameCommandType.URGAMESTATUS.getStr(),CommandType.GAMEDATA.getStr(),null));
                if (j >= playersNumber) {
                    isPrepared = true;
                    gameState = GameState.PREPARING;
                    try {
                        this.finalize();
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }
                } else {

                    j = 0;

                    try {
                        Collections.sort(Lobby.getPlayersList());
                        Thread.sleep(4000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    for (int i = 0; i < Lobby.getPlayersList().size(); i++) {
                        if (Lobby.getPlayersList().get(i).getPlayerGameState() == GameState.LAUNCHING) {
                            j++;
                        }
                    }
                }
            }
        }
    }
}
