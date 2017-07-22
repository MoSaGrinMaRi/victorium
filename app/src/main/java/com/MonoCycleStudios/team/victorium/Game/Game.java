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
import com.MonoCycleStudios.team.victorium.Game.Enums.QuestionType;
import com.MonoCycleStudios.team.victorium.Game.Fragments.Ground;
import com.MonoCycleStudios.team.victorium.Game.Fragments.Notifier;
import com.MonoCycleStudios.team.victorium.Game.Fragments.Questioner;
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

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        instance = this;

        if(gameState == GameState.WAITING_FOR_START && gameServer != null){
            new GamePrepare().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }

        Fragment fragment;        // for change fragment
        fragment = new Ground();
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragmentPlaceGround, fragment);
        ft.commit();

        System.out.println("YEEEEEEE=================EEEEY!" + gameServer);
        if(gameServer != null)
            new GameCore().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        showFragment(GameFragments.NOTIFY, "We wait for all of Players... SRY");

        gameState = GameState.LAUNCHING;
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

    Fragment fragment = null;        // for change fragment
    public void showFragment(GameFragments gf, String str) {
        System.out.println("====================================================");
        System.out.println("We got " + gf.getStringValue() + " and " + str);
        System.out.println("====================================================");
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
//        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);  //  strange, got here an exception sometimes

        switch(GameFragments.getTypeOf(gf.getStringValue())){
//            case AnotherNotifier1:
//            case AnotherNotifier2:
//            case AnotherNotifier3:
            case NOTIFY:{
                fragment = new Notifier();
                ((Notifier)fragment).setNotifyText(str);
                if (!isFinishing() && fragment != null) {
                    ft.replace(R.id.fragmentNotifyPlaceholder, fragment, "tag1");
//                    ft.addToBackStack(null);
                    ft.commit();
                }

            }break;
            case QUESTION:{
                fragment = new Questioner();
                ((Questioner)fragment).setQuestionDisplay(
                        new Question(QuestionType.FILM,
                        "How many oscar DiCaprio have?",
                                new String[]{"0","-1","-2213","1"},
                        3));
                if (!isFinishing() && fragment != null) {
                    ft.replace(R.id.fragmentQuestionPlaceholder, fragment, "tag2");
//                    ft.addToBackStack(null);
                    ft.commit();
                }
//                ((Notifier)fragment).setNotifyText("WE ARE ALL READY TO POP A QUaSTION \\0/");

            }break;
            case NONE:{
                if (!isFinishing() && fragment != null) {
//                    ft.remove(fragment);
//                    ft.replace(R.id.fragmentNotifyPlaceholder, null);
                    ft.remove(fm.findFragmentById(R.id.fragmentNotifyPlaceholder)).commit();
                    fragment = null;
//                    ft.commitAllowingStateLoss();
                }
            }
        }
    }

    class GameCore extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... params) {

            while (!isCancelled()){

                if(gameState == GameState.PREPARING){
                    gameServer.notifyAllClients("[gameData]","[executeStart]");
                    gameState = GameState.RUNNING;
                }
                System.out.println("2" + 2+2);
            }
            return null;
        }
    }

    class GamePrepare extends AsyncTask<Void, Void, Void> {

        boolean isPrepared = false;

        @Override
        protected Void doInBackground(Void... params) {
            int j = 0;
            while(!isPrepared || !isCancelled()){

                gameServer.notifyAllClients("[gameData]","[urGameStatus]");
                if(j >= playersNumber){
                    isPrepared = true;
                    gameState = GameState.PREPARING;
                    this.cancel(true);
                }else {

                    j = 0;

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    for (int i = 0; i < Lobby.getConnectionsList().size(); i++){
//                        System.out.println("+_+" + Lobby.getConnectionsList().get(i).getPlayerGameState());
                        if (Lobby.getConnectionsList().get(i).getPlayerGameState() == GameState.LAUNCHING) {
                            j++;
                        }
                    }
                }
            }
            return null;
        }

        protected void onPostExecute(Void param) {

        }
    } // AsyncTask over

}
