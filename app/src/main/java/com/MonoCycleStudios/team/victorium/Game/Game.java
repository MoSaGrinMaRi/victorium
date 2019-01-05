package com.MonoCycleStudios.team.victorium.Game;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.app.Fragment;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
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
import com.MonoCycleStudios.team.victorium.Game.Enums.PlayerState;
import com.MonoCycleStudios.team.victorium.Game.Enums.QuestionCategory;
import com.MonoCycleStudios.team.victorium.Game.Enums.QuestionType;
import com.MonoCycleStudios.team.victorium.Game.Fragments.Alert;
import com.MonoCycleStudios.team.victorium.Game.Fragments.Ground;
import com.MonoCycleStudios.team.victorium.Game.Fragments.GroundEvent;
import com.MonoCycleStudios.team.victorium.Game.Fragments.GroundEvents.GroundEvents;
import com.MonoCycleStudios.team.victorium.Game.Fragments.GroundEvents.RegionFX;
import com.MonoCycleStudios.team.victorium.Game.Fragments.GroundEvents.RegionMenu;
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
    private static GameCore gameCore;
    public static boolean isServer = false;
    ListView lv;
    BitmapFactory.Options bmo = new BitmapFactory.Options();
    public static Bitmap castleAtlas;
    public static Bitmap pawnAtlas;
    public static Bitmap categoryAtlas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        instance = this;

        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        showFragment(GameFragments.GROUND_EVENT);

        lv = (ListView) findViewById(R.id.connectionsListView_2);
        lv.setAdapter(Lobby.adapter);
//        Lobby.forceREUpdateAdapter();

        System.out.println(gameState + " [=] " + gameServer);
        if(gameState == GameState.WAITING_FOR_START && gameServer != null){
            new GamePrepare().start();
        }

        Fragment fragment;        // for change fragment
        fragment = gameGround;
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragmentPlaceGround, fragment);
        ft.commit();

        showFragment(GameFragments.GROUND_EVENT);

        bmo.inScaled = true;
//        bmo.inSampleSize = 32;
        bmo.inDensity = 4112;
        bmo.inTargetDensity = (4112/8);
        castleAtlas = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.castle_atlas, bmo);

        bmo.inDensity = 1280;
        bmo.inTargetDensity = 1280;
        categoryAtlas = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.category_atlas, bmo);

        bmo.inDensity = 1566;
        bmo.inTargetDensity = (1566/6); //  x2 in drawing
        pawnAtlas = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.pawn_atlas, bmo);

        System.out.println("YEEEEEEE=================EEEEY!" + gameServer);
        if(gameServer != null) {
            gameCore = new GameCore();
            gameCore.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
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
    public GroundEvent fragment4;
    public void showFragment(GameFragments gf, Object... obj) {
        System.out.println("====================================================");
        System.out.println("We got " + gf.getStr() + " and " + obj.toString());
        System.out.println("====================================================");
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        switch(GameFragments.getTypeOf(gf.getStr())){
            case GROUND_EVENT:{
                if(fragment4 == null) {
                    fragment4 = new GroundEvent();

                    if (!isFinishing()) {
                        ft.replace(R.id.fragmentGroundEventPlaceholder, fragment4);
                        ft.commit();
                    }
                }else if(obj != null && obj.length > 1){
                        System.out.println("4567890 "+obj[0]);

                        if(((String)obj[1]).equalsIgnoreCase("add"))
                            fragment4.addView((GroundEvents) obj[0],-1);
                        else if(((String)obj[1]).equalsIgnoreCase("update"))
                            fragment4.updateView((GroundEvents) obj[0], "-1");
                        else if(((String)obj[1]).equalsIgnoreCase("remove"))
                            fragment4.removeView((GroundEvents) obj[0], (String)obj[2]);

                }
            }break;
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
                                    if(GameRule.isFirstHalf)
                                        gameGround.getAllCapturableRegions(GameRule.activePlayer);
                                    else
                                        gameGround.getAllAttackableRegions(GameRule.activePlayer);
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

//                                    Object[] newQuestion = QuestionParser.getQuestion("none");
//                                    Question q = (Question)newQuestion[0];
//
//                                    gameServer.notifyPlayer(Lobby.getPlayersList().get(0), new MonoPackage(GameCommandType.QUESTION.getStr(),CommandType.GAMEDATA.getStr(),q));
//                                    gameServer.notifyPlayer(Lobby.getPlayersList().get(1), new MonoPackage(GameCommandType.QUESTION.getStr(),CommandType.GAMEDATA.getStr(),q));

                                }
                            });

                    }break;
                    case "update":{
                        int regionID = Integer.parseInt(tmp.getDescOfObject());

                        if(!GameRule.isFirstHalf) {
                            final RegionFX rfx = new RegionFX();
//                            final int regionInd = gameGround.regions.lastIndexOf(new Region(regionID,-1,false,null,-1));
//                            Region r = gameGround.regions.get(regionID);

//                            rfx.setFXType(""+r.getCost());
                            String[] coords = gameGround.getRegionCoords(gameGround.getMapIDFromRegionID(regionID)).split(":;");
                            int x, y;
                            x = Integer.parseInt(coords[0]);
                            y = Integer.parseInt(coords[1]);
                            rfx.setPosition(x, y);

                            System.out.println("567890- " + x + " " + y);
//                            Game.getInstance().showFragment(GameFragments.GROUND_EVENT, rfx, "remove", "all");
                            Game.getInstance().showFragment(GameFragments.GROUND_EVENT, rfx, "add");

//                            rfx.blockingCode();
                        }

                        if(tmp.getObj() instanceof Player)
                            gameGround.updateRegion(regionID, (Player) tmp.getObj());
                        else if(tmp.getObj() instanceof String)
                            gameGround.updateRegion(Integer.parseInt(tmp.getDescOfObject()), null);
                    }break;
                }
            }
            break;
            case PLAYER:{
                switch(((MonoPackage)monoPackage.getObj()).getTypeOfObject()) {
                    case "score": {
                        MonoPackage tmp = (MonoPackage)monoPackage.getObj();

                        Player p = Lobby.getPlayersList().get(Lobby.getPlayersList().indexOf((tmp.getObj())));
                        if(tmp.getDescOfObject().startsWith("s")){
                            int score = Integer.parseInt(tmp.getDescOfObject().substring(1));
                            p.setPlayerScore(score);
                        }else{
                            int score = Integer.parseInt(tmp.getDescOfObject());
                            p.updatePlayerScore(score);
                        }

//                        if(score > 0)
//                            Game.getInstance().showFragment(GameFragments.ALERT, p.getPlayerName() + " earn " + score , 2);
//                        else
//                            Game.getInstance().showFragment(GameFragments.ALERT, p.getPlayerName() + " lost " + score , 2);

                        System.out.println("[2][][] "+ p.getPlayerScore() + p.getPlayerName() );

                        Lobby.forceREUpdateAdapter();
                        lv.setAdapter(Lobby.adapter);

                        System.out.println("[3][][] "+ p.getPlayerScore() + p.getPlayerName() );

                    }
                }
            }break;
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
                        else if(tmp.getObj() instanceof Player)
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
                    }break;
                    case "finish":{
                        fragment2.showNextCard();
                        for(Region rg : gameGround.regions){
                            rg.isActive = false;
                            rg.isInteractive = false;
                        }
                        gameGround.redrawMap();

                        Player p = (Player) ((MonoPackage)monoPackage.getObj()).getObj();
                        showFragment(GameFragments.NOTIFY,p.getPlayerName() + " win This GAME w/ score: "+p.getPlayerScore()+"!!! Blame him >:D");

                        new Handler().postDelayed(new Runnable() {
                            public void run() {
                                finish();
                                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

                            }
                        }, 8000);

                    }break;
                }
            }
        }
    }

    @Override
    protected void onDestroy() {

//        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        Lobby.thisActivity.stopAndClose();

        Ground.regions = new ArrayList<>();

        GameRule.cleanup();

        playersNumber = -1;
        gameState = GameState.WAITING_FOR_START;
        gameServer = null;
        gameGround = null;
        gameCore = null;
        isServer = false;
        instance = null;

        super.onDestroy();
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

                    ArrayList<Region> freeRegions = new ArrayList<>();
                    freeRegions.addAll(gameGround.regions);

                    for(int i = 0; i < playersNumber; i++){

                        if(freeRegions.size() <= 0){    //  in case no space to put next base, regenerate new
                            freeRegions.addAll(gameGround.regions);
                            for(int j = 0; j < gameGround.regions.size()-1; j++) {
                                gameGround.regions.get(j).owner = null;
                                gameGround.regions.get(j).isBase = false;
                            }
                            i = 0;
                        }

                        do {
                            System.out.println("AL9RM!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                            gameRule.justWait(100);
                            freeRegions.addAll(gameGround.regions);
                        }while (freeRegions.size() <= 0);

                        Region rg = Ground.regions.get(freeRegions.get(new Random().nextInt(freeRegions.size() - 1)).id);


                        if(rg.owner == null){
                            rg.owner = Lobby.getPlayersList().get(i);
                            System.out.println("tyuio0 "+ rg.owner + " " +Lobby.getPlayersList().get(i).getPlayerState());
                            rg.setIsBase(true);

//                            System.out.println("qwerty " + rg.id + "\n" + Arrays.toString(freeRegions.toArray()) + "\n" + freeRegions.size() + " " + gameGround.regions.size());
                            freeRegions.remove(rg);

                            for(Region r : rg.getNeighbourhoods()){
                                freeRegions.remove(r);
                                if(playersNumber <= 4) {    //  TEMP !!!    4 breakpoint, if 4 and less, try to make 2 region margin
                                    for (Region r2 : r.getNeighbourhoods()) {
                                        freeRegions.remove(r2);
                                        if (playersNumber <= 2) {    //  TEMP !!!    2 breakpoint, if 2 and less, try to make 3 region margin
                                            for (Region r3 : r2.getNeighbourhoods()) {
                                                freeRegions.remove(r3);
                                            }
                                        }
                                    }
                                }
                            }
//                            System.out.println("qwerty " + rg.id + "\n" + Arrays.toString(freeRegions.toArray()) + "\n" + freeRegions.size() + " " + gameGround.regions.size());
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
                            gameRule.update(-1, Lobby.getPlayersList().get(queueTurns.get(fragment2.getCardIndex() + 1).getPlayerID()), null);

                        gameServer.notifyAllClients(new MonoPackage(GameCommandType.GAMERULE.getStr(), CommandType.GAMEDATA.getStr(),
                                new MonoPackage("update", isNextCard ? "nextCard" : "nope",
                                        new MonoPackage("", "", new Player[]{GameRule.activePlayer,GameRule.defencePlayer}))));

                        gameRule.updateCheck();
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

                @Override
                public void onFinish() {
                    gameServer.notifyAllClients(new MonoPackage(GameCommandType.GAMERULE.getStr(),CommandType.GAMEDATA.getStr(),
                            new MonoPackage("finish","",getWinner(Lobby.getPlayersList()))));


                }
            });
//
//            while(!isCancelled() || !gameRule.isFinished){
//                if(2>1){
//                }
//            }
            return null;
        }

        Player getWinner(ArrayList<Player> playerArrayList){
            Player winner = playerArrayList.get(0);

            for(Player p : playerArrayList){
                if(winner.getPlayerScore() < p.getPlayerScore())
                    winner = p;
            }
            return winner;
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

        ArrayList<Player> generatePlayersQueueTurnReverse(ArrayList<Player> playerArrayList){
            ArrayList<Player> generatedPlayersQueueTurn = new ArrayList<>();

            Player last;
            for(int i = 0; i < 25/playersNumber - 1; i++) {
                for(int j = 0; j < playersNumber; j++) {
                    generatedPlayersQueueTurn.add(playerArrayList.get(j));
                }
                //  ????????????????
                /*
                123456      123456  ||  12      12  ||  123     123 ||  1234    1234
                612345      234561      21      21      312     231     4123    2341
                561234      345612      12      12      231     312     3412    3412
                456123      456123      21      21      123     123     2341    4123
                345612      561234      12      12      312     231     1234    1234
                234561      612345      21      21      231     312     4123    2341
                 */
                last = playerArrayList.get(playerArrayList.size()-1);
//                System.arraycopy(playerArrayList.toArray(), 1, playerArrayList.toArray(), 0, playerArrayList.size() - 1 );
                System.arraycopy(playerArrayList.toArray(), 0, playerArrayList.toArray(), 1, playerArrayList.size() - 1 );
                playerArrayList.remove(playerArrayList.size()-1);
                playerArrayList.add(0, last);
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
