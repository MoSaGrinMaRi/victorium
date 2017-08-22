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
import com.MonoCycleStudios.team.victorium.Game.Enums.QuestionCategory;
import com.MonoCycleStudios.team.victorium.Game.Enums.QuestionType;
import com.MonoCycleStudios.team.victorium.Game.Fragments.Alert;
import com.MonoCycleStudios.team.victorium.Game.Fragments.Ground;
import com.MonoCycleStudios.team.victorium.Game.Fragments.TimeDisplacer;
import com.MonoCycleStudios.team.victorium.Game.Fragments.Notifier;
import com.MonoCycleStudios.team.victorium.Game.Fragments.Questioner;
import com.MonoCycleStudios.team.victorium.Game.Fragments.QueueTurners;
import com.MonoCycleStudios.team.victorium.R;
import com.MonoCycleStudios.team.victorium.widget.MyCountDownTimer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
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

    public static int playersNumber;
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
    Questioner fragment;
    QueueTurners fragment2;
    TimeDisplacer fragment3;
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
                                    gameGround.getAllCapturableRegions(GameRule.activePlayer);
                                }
                            }, delay);
                        }
                    });
                    tmpThread.start();
                }
            }break;
            case QUESTION:{
                if(obj[0] != null) {
                    fragment = new Questioner();

                    if (obj[0] instanceof String) {
                        fragment.setQuestionDisplay(
                                new Question(QuestionType.ONE_FROM_FOUR_NORMAL,
                                        QuestionCategory.FILM,
                                        "How many oscar DiCaprio have?",
                                        new String[]{"0", "-1", "-2213", "1"},
                                        3));
                    } else {
                        fragment.setQuestionDisplay((Question) obj[0]);
                        if (GameRule.defencePlayer != null)
                            fragment.setFighters(GameRule.activePlayer, GameRule.defencePlayer);
                        fragment.setInteractive(Client.getInstance().iPlayer.getPlayerState());
                    }
                    if (!isFinishing()) {
                        ft.setCustomAnimations(R.animator.fadein_translate_fromtop, R.animator.fadeout_translate_todown);
                        ft.replace(R.id.fragmentQuestionPlaceholder, fragment, "tag2");
                        ft.commit();
                    }
                }else {
                    showFragment(GameFragments.NONE,GameFragments.QUESTION);
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
                fragment3 = new TimeDisplacer();
                if (!isFinishing()) {
                    ft.replace(R.id.fragmentTimer, fragment3);
                    ft.commit();
                }
                if(obj[0] instanceof MyCountDownTimer)
                    fragment3.setTimer((MyCountDownTimer)obj[0]);
                else
                    fragment3.setTimer((int) obj[0],(int) obj[1]);
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
        System.out.println("1234567890");
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

                        showFragment(GameFragments.TIMER, 6000, 16*(Client.iPlayer.getPlayerID()*2+1));
                        if(isServer)
                            fragment3.mcdt.addOnUpdateHandler(new MyCountDownTimer.OnUpdateHandler() {
                                @Override
                                public void onTickUpdate(long currentMillis, int milli, int sec, int min) {

                                }

                                @Override
                                public void onFinish() {
                                    Question q = new Question(QuestionType.ONE_FROM_FOUR_NORMAL,QuestionCategory.FILM,"just test",new String[]{"1","2","3","4"},3);

                                    gameServer.notifyPlayer(Lobby.getPlayersList().get(0), new MonoPackage(GameCommandType.QUESTION.getStr(),CommandType.GAMEDATA.getStr(),q));
                                    gameServer.notifyPlayer(Lobby.getPlayersList().get(1), new MonoPackage(GameCommandType.QUESTION.getStr(),CommandType.GAMEDATA.getStr(),q));

                                }
                            });

                    }break;
                    case "update":{
                        gameGround.updateRegion(Integer.parseInt(tmp.getDescOfObject()),(Player)tmp.getObj());
                    }break;
                }
            }
            break;
            case QUESTION:{
                if(monoPackage.getObj() instanceof Question || monoPackage.getObj() == null)
                    showFragment(GameFragments.QUESTION, monoPackage.getObj());
                else{
                    MonoPackage tmp = (MonoPackage)monoPackage.getObj();
                    switch (tmp.getTypeOfObject()){
                        case "show answer":{
                            fragment.updateView((HashMap<Player, Integer>) tmp.getObj(),Integer.parseInt(tmp.getDescOfObject()));
                        }break;
                        case "got answer":{
                            fragment.gotAnswer((Player) tmp.getObj());
                        }break;
                    }
                }
            }break;
            case ALERT:{
                showFragment(GameFragments.ALERT, monoPackage.getObj(), 3);
            }break;
            case GAMERULE:{
                System.out.println("Ho-rey!-1" + ((MonoPackage)monoPackage.getObj()).getTypeOfObject());
                switch(((MonoPackage)monoPackage.getObj()).getTypeOfObject()){
                    case "update":{
                        MonoPackage tmp = (MonoPackage)((MonoPackage)monoPackage.getObj()).getObj();
                        if(tmp.getObj() instanceof Player[])
                            GameRule.update(-1,((Player[])tmp.getObj())[0],((Player[])tmp.getObj())[1]);
                        else
                            GameRule.update(-1,(Player) tmp.getObj(), null);

                        if(((MonoPackage)monoPackage.getObj()).getDescOfObject().equalsIgnoreCase("nextCard")) {
                            fragment2.showNextCard();

                            for(Region rg : gameGround.regions){
                                rg.isActive = true;
                                rg.isInteractive = false;
                            }

                            if(GameRule.isFirstHalf)
                                gameGround.getAllCapturableRegions(GameRule.activePlayer);
                            else
                                gameGround.getAllAttackableRegions(GameRule.activePlayer);

                        }else if(((MonoPackage)monoPackage.getObj()).getDescOfObject().equalsIgnoreCase("nextHalf")) {
                            GameRule.isFirstHalf = false;
                            showFragment(GameFragments.NOTIFY, "Fight!", 4);
                            fragment2.reloadCard();
                            gameGround.getAllAttackableRegions(GameRule.activePlayer);
                        }
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

    public void useGameServer(String str, Player p1, Player p2, MonoPackage monoPackage){
        if((str.equalsIgnoreCase("players"))) {

            gameServer.notifyPlayer(p1, monoPackage);
            gameServer.notifyPlayer(p2, monoPackage);

        }else if((str.equalsIgnoreCase("!players"))){
            for(Player tmp : Lobby.getPlayersList()){
                System.out.println(!(tmp.equals(p1) || tmp.equals(p2)) + " " + tmp.toString() + " " +p1.toString() + " " +p2.toString()  );
                if(!(tmp.equals(p1) || tmp.equals(p2)))
                    gameServer.notifyPlayer(tmp, monoPackage);
            }
        }
    }

    public void useGameServer(String str, Player p, MonoPackage monoPackage){
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
                            new MonoPackage("update","",new MonoPackage("","",GameRule.activePlayer))));

                    break;
                }
            }
            Collections.sort(Lobby.getPlayersList());

            gameRule.addOnUpdateHandler(new GameRule.OnUpdateHandler(){
                boolean isUpdate = true;

                @Override
                public void onTickUpdate(int currentTick, boolean isFirstHalf, boolean isNextCard) {
                    if(isUpdate) {
                        if(isNextCard)
                            gameRule.update(-1, queueTurns.get(fragment2.getCardIndex() + 1),null);

                        gameServer.notifyAllClients(new MonoPackage(GameCommandType.GAMERULE.getStr(), CommandType.GAMEDATA.getStr(),
                                new MonoPackage("update", isNextCard ? "nextCard" : "nope",
                                        new MonoPackage("", "", new Player[]{GameRule.activePlayer,GameRule.defencePlayer}))));
                    }else{
                        isUpdate = true;
                    }

                }

                @Override
                public void onNextHalf(int currentTick, boolean isFirstHalf) {
                    isUpdate = false;
                    useGameServer("all",null,
                            new MonoPackage(GameCommandType.GAMERULE.getStr(),CommandType.GAMEDATA.getStr(),
                                    new MonoPackage("update","nextHalf",
                                            new MonoPackage("","",queueTurns.get(0)))));
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
                if(GameRule.isFirstHalf)
                    generatedPlayersQueueTurn.add(playerArrayList.get(0));
                else {
                    Player pToAdd = playerArrayList.get(0);
                    for(Player tmp : playerArrayList){
                        if(tmp.getPlayerScore() <= pToAdd.getPlayerScore())
                            pToAdd = tmp;
                    }
                    generatedPlayersQueueTurn.add(pToAdd);
                }
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
