package com.MonoCycleStudios.team.victorium.Game;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.AsyncTask;
import android.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.MonoCycleStudios.team.victorium.Connection.Lobby;
import com.MonoCycleStudios.team.victorium.Connection.Server;
import com.MonoCycleStudios.team.victorium.Game.Enums.GameFragments;
import com.MonoCycleStudios.team.victorium.Game.Enums.GameState;
import com.MonoCycleStudios.team.victorium.Game.Fragments.Ground;
import com.MonoCycleStudios.team.victorium.Game.Fragments.Notifyer;
import com.MonoCycleStudios.team.victorium.R;

import java.util.ArrayList;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {

//        System.out.println("YEEEEEEEEE23746726462374623647623467234EEY!");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        gameState = GameState.LAUNCHING;

        System.out.println("YEEEEEEEE------------------EEEY!");

        // comment V
//        if(gameState == GameState.LAUNCHING && gameServer != null){
//            new GamePrepare().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//        }

        Fragment fragment;        // for change fragment
        fragment = new Ground();
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragmentPlaceGround, fragment);
        ft.commit();

        System.out.println("YEEEEEEE=================EEEEY!" + gameServer);
//        gameState = GameState.PREPARING;
//        if(gameServer != null)
//            new GameCore().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    public static void setup(Server server, int playersNumb){
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

    public void showFragment(GameFragments gf, String str) {
        Fragment fragment = null;        // for change fragment
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        switch(GameFragments.getTypeOf(gf.getStringValue())){
//            case AnotherNotifier1:
//            case AnotherNotifier2:
//            case AnotherNotifier3:
            case NOTIFY:{
                fragment = new Notifyer();
                if (!isFinishing()) {
                    ft.replace(R.id.fragmentNotifyPlaceholder, fragment).commit();
                }
//                ft.commit();
//                ((Notifyer)fragment).setNotifyText(str);

            }break;
            case QUESTION:{
                fragment = new Notifyer();
                ft.replace(R.id.fragmentNotifyPlaceholder, fragment).commit();
//                ft.commit();
//                ((Notifyer)fragment).setNotifyText("WE ARE ALL READY TO POP A QUaSTION \\0/");


            }break;

        }
    }

    class GameCore extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... params) {

            gameServer.notifyAllClients("[gameData]","[waitForPlayers]");
            while (!isCancelled()){

                if(gameState == GameState.RUNNING){
                    gameServer.notifyAllClients("[gameData]","[executeStart]");
                }
            }
            return null;
        }
    }

    class GamePrepare extends AsyncTask<Void, Void, Void> {

        boolean isPrepared = false;
//        boolean isAllLaunched = false;

        @Override
        protected Void doInBackground(Void... params) {
            gameServer.notifyAllClients("[gameData]","[urGameStatus]");
            while(!isPrepared || !isCancelled()){

                System.out.println("YEEEEE+++++++++++++EEEEEEY!");
                int j = 0;
                for(int i = 0; i < Lobby.getConnectionsList().size(); i++){
                    System.out.println(Lobby.getConnectionsList().get(i).getPlayerGameState());
                    if(Lobby.getConnectionsList().get(i).getPlayerGameState() == GameState.PREPARING){
                        j++;    //  cpConnectionsList.remove(i);
                    }
                    if(j >= Lobby.getConnectionsList().size()){
                        isPrepared = true;
                        gameState = GameState.RUNNING;
                    }
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

        protected void onPostExecute(Void param) {

        }
    } // AsyncTask over

}
