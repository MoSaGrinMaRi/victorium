package com.MonoCycleStudios.team.victorium.Connection;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Handler;
import android.widget.BaseAdapter;

import com.MonoCycleStudios.team.victorium.Connection.Enums.CommandType;
import com.MonoCycleStudios.team.victorium.Game.Enums.GameCommandType;
import com.MonoCycleStudios.team.victorium.Game.Enums.GameState;
import com.MonoCycleStudios.team.victorium.Game.Game;

import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;

public class Server extends AsyncTask<Activity, String, Void> {

    List<ServerTread> connectionList = new ArrayList<>();
    static int port = Lobby.gPort;
    static Activity lActivity;
    boolean islisten = true;
    boolean isObjWaiter = true;
    boolean isCheckRunning = false;
    ServerTread connection;
    ServerSocket serverSocket;

    public static boolean isStreamUsing = false;

    public List<ServerTread> getConnectionList() {
        return connectionList;
    }

    @Override
    protected Void doInBackground(Activity... params) {
        if (params.length != 0) {
            lActivity = params[0];
        }else{
            return null;
        }

        try {
            String myIP = Lobby.getMyLocalIP();
            if(Lobby.getMyLocalIP() == null) publishProgress("NO IP 0_0?"); else publishProgress(myIP + " : " + port);
            serverSocket = new ServerSocket(port);
            System.out.println("Initialized");

            while (islisten || connectionList.size() < 6|| !isCancelled()) {     // !!! [GLOBAL VAR] MAX_PLAYERS = 6
                connection = new ServerTread();
                connection.init(serverSocket.accept());
                connectionList.add(connection);
//                connection.getSocket().setKeepAlive(true);
                publishProgress("[exe]");
            }
            while(isObjWaiter || !isCancelled()){
                Queue<MonoPackage> qmp = null;
                for(int i = 0; i < connectionList.size(); i++){
                    if((qmp = connectionList.get(i).getObjToServer()).size() > 0){
                        for(int j = 0; j < qmp.size(); j++) {
                            MonoPackage pck = qmp.poll();
                            switch (CommandType.getTypeOf(pck.descOfObject)) {
                                case GAMEDATA: {
                                    switch (GameCommandType.getTypeOf(pck.descOfObject)) {
                                        case URGAMESTATUS:{
                                            Lobby.getConnectionsList().get(i).setPlayerGameState((GameState) pck.obj);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
            System.err.println("Could not listen on port " + port);
            ioe.printStackTrace();
        } catch(Exception x) {
            x.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(String... s){
        final String[] buffer = s;
        switch (buffer[0].toLowerCase()){
            case "[exe]":{
                if(buffer[0].equalsIgnoreCase("[exe]")) {
                    connectionList.get(connectionList.size() - 1).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                    if (!isCheckRunning) {
                        isCheckRunning = true;
                        lActivity.runOnUiThread(new Runnable() {
                            Handler h = new Handler();
                            int delay = 1500; //ms  // how often will check if user is still connected

                            public void run() {
                                h.postDelayed(new Runnable() {
                                    public void run() {
//                                        checkConnection();
                                        if (Lobby.connectionsList.size() > 0)
                                            Lobby.b3.setEnabled(true);
                                        else
                                            Lobby.b3.setEnabled(false);
                                        h.postDelayed(this, delay);
                                    }
                                }, delay);
                            }
                        });
                    }
                }
            }break;

            case "[remove]":{
                Lobby.connectionsList.remove(Integer.parseInt(buffer[1]));
                ((BaseAdapter)Lobby.lv.getAdapter()).notifyDataSetChanged();
            }break;
        }
    }


    public void notifyAllClients(String... command){

        for (int i = 0; i < connectionList.size(); i++){
            switch (CommandType.getTypeOf(command[0])) {
                case START_GAME: {
                    if(i == 0)  // TEMP!!!!
                        Lobby.startGameActivity(lActivity);
                    System.out.println(connectionList.get(i) + "setPlaying True");
                    connectionList.get(i).setPlaying(true);
//                        connectionList.get(i).setListening(false);
                }break;
                case NEWPLAYER:{
                    System.out.println("trying send List to " + i);
                    connectionList.get(i).setOutCommand("ArrayList", "[playersData]", Lobby.connectionsList);
                }break;
                case GAMEDATA:{
                    switch (GameCommandType.getTypeOf(command[1])) {
                        case URGAMESTATUS:{
                            connectionList.get(i).setOutCommand("GameCommandType", "[gameData]", GameCommandType.URGAMESTATUS.getStringValue());
                        }break;
                        case WAITFORPLAYERS:{
                            connectionList.get(i).setOutCommand("GameCommandType", "[gameData]", GameCommandType.WAITFORPLAYERS.getStringValue());
                        }break;
                        case EXECUTESTART:{
                            connectionList.get(i).setOutCommand("GameCommandType", "[gameData]", GameCommandType.EXECUTESTART.getStringValue());
                        }break;
                    }
                }
                default: {
                    System.out.println("Smth went wrong. Command '" + command[0] + "' didn't recognized.");
                }
            }
        }
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        System.out.println("[S]OnPostExecute");
    }

    public void cancelChild(){
        System.out.println("[S]onCancelChild");
        if (connectionList != null && !connectionList.isEmpty()) {
            System.out.println(connectionList.get(0).getStatus());

            for (ServerTread i : connectionList) {
                i.inputStreamCancel();
                i.cancel(true);
            }
            System.out.println(connectionList.get(0).getStatus());
        }
    }

    private void checkConnection(){
        if (connectionList != null && !connectionList.isEmpty()) {
            int ind = 0;
            Iterator<ServerTread> iter = connectionList.iterator();

            while (iter.hasNext()) {
                ServerTread st = iter.next();

//                System.out.println(st.toString() + " \" " + st.oPing + " \" " + st.outCommand + " \" " + st.isMarkedToRemove);
//                if(st.oPing == -1) {
//                    if(st.isMarkedToRemove) {
//                        iter.remove();
//                        publishProgress("[remove]", "" + ind);
////                    ind--;
//                    }else{
//                        st.isMarkedToRemove = true;
//                        st.setOutComnd("[P]", -1);
//                    }
//                }else {
//                    st.oPing = -1;
//                    st.setOutComnd("[P]", -1);
//                }

                ind++;
            }
        }
    }

    public void stopListening(boolean isCompletely){
        System.out.println("[S]onStopListening");
        islisten = false;
        if(isCompletely){
            try {
                if (serverSocket != null)serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onCancelled() {
        System.out.println("[S]OnCancel");
        islisten = false;
        super.onCancelled();
    }
}