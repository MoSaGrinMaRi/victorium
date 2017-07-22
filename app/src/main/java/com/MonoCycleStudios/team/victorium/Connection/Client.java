package com.MonoCycleStudios.team.victorium.Connection;

import android.app.Activity;
import android.os.AsyncTask;

import com.MonoCycleStudios.team.victorium.Connection.Enums.CommandType;
import com.MonoCycleStudios.team.victorium.Game.Enums.GameCommandType;
import com.MonoCycleStudios.team.victorium.Game.Enums.GameFragments;
import com.MonoCycleStudios.team.victorium.Game.Enums.GameState;
import com.MonoCycleStudios.team.victorium.Game.Game;
import com.MonoCycleStudios.team.victorium.Game.Player;

import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

public class Client extends AsyncTask<String, MonoPackage, Void> {

    private static volatile Client instance;

    public static Client getInstance() {
        Client localInstance = instance;
        if (localInstance == null) {
            synchronized (Client.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new Client();
                }
            }
        }
        return localInstance;
    }


    private static Socket socket;

    static String playerName;
    private static int port = Lobby.gPort;
    private static String address;
    static Player iPlayer;

    private Queue<MonoPackage> objReceived = new LinkedList<>();
    private byte[] buf = new byte[1];
    private int bufNull = 0;

    private Queue<MonoPackage> outCommand = new LinkedList<>();
    public MonoPackage getOutCommand() {
        return outCommand.peek();
    }
    public void addOutCommand(String type, String outCommand, Object params) {
        this.outCommand.add(new MonoPackage(type, outCommand, params));
    }

    private static ObjectInputStream oin;
    private static ObjectOutputStream oout;

    @Override
    protected void onPreExecute() {
        System.out.println("[=2.0=].................");
    }

    @Override
    protected Void doInBackground(String... params) {

        System.out.println("=!= "+Lobby.getMyLocalIP());
        if(params.length != 0) {
            System.out.println("=!= "+Lobby.getMyLocalIP());
            this.address = params[0].startsWith("192.168.") ? params[0] : Lobby.getMyLocalIP();
            this.playerName = params[1];
        }else{
            return null;
        }

        System.out.println("[=2.3=].................");
        try {
            System.out.println("[=2.4=].................");
            InetAddress ipAddress = InetAddress.getByName(address);

            System.out.println("[=2.5=].................");
            System.out.println("Any of you heard of a socket with IP address " + address + " and port " + port + "?");
            socket = new Socket(ipAddress, port);
            System.out.println("Yes! I just connected!");

/**
 *                 [!] All OBJECTS u'll sent, SHOULD BE Serializable
 *                 [!] Use the follow order to perform stable object send/receive :
 *
 *            oout.writeObject(new MonoPackage("String", "[rData]", "justRandomStringHereSaysHiToYou:)"));
 *            oout.flush();
 *            oout.reset();     // !!! IMPORTANT
 *
 *
 *            if( oin.avalaible() > 0){
 *               foo = (MonoPackage) oin.readObject();
 *            }
 *
 */

            oout = new ObjectOutputStream(socket.getOutputStream());
            oout.flush();
            oout.reset();
            oin = new ObjectInputStream(socket.getInputStream());

            InputStreamWaiter isw = new InputStreamWaiter();
            Thread tisw = new Thread(isw);
            tisw.start();

            // init block
            {
                oout.writeUnshared(new MonoPackage("String", "[newPlayer]", playerName));
                oout.flush();
                oout.reset();
            }

            while (!isCancelled()) {

                if(outCommand.size() > 0){
                    System.out.println("Out[C] " + outCommand.size());
                    oout.writeUnshared(outCommand.poll());
                    oout.flush();
                    oout.reset();
                }
                if (objReceived.size() > 0){
                    System.out.println(objReceived.size() + " | " + objReceived.peek().fullToString());
                    processData(objReceived.poll());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void processData(MonoPackage monoPackage){
        if((CommandType.getTypeOf(monoPackage.descOfObject)) != CommandType.NONE){
            System.out.println("?SA?D?AS?AS?ASD?AD? + " + monoPackage.fullToString());
            publishProgress(monoPackage);
        }
    }

    @Override
    protected void onProgressUpdate(MonoPackage... packages) {
        System.out.println("!!@^%#^&!@%#!^&#^&!*@#&*!");
        if (packages.length != 0){
            switch (CommandType.getTypeOf(packages[0].descOfObject)){
                case NEWPLAYER: {
                    if (packages[0].typeOfObject.equalsIgnoreCase("Player")) {
                        iPlayer = (Player) packages[0].obj;
                        iPlayer.setPlayerClient(this);
                        processData(new MonoPackage("String","[newPlayer]","(connected)"));
                    } else if (((String) packages[0].obj).equalsIgnoreCase("(connected)")) {
                        Lobby.b1.setText("Connected");
                    }
                }break;
                case RDATA:{
                    Lobby.tv.setText("[C] got: " + packages[0].obj);
                }break;
                case START_GAME:{
                    System.out.println("?SA?D?AS?AS?ASD?AD?");
                    Lobby.startGameActivity();
                }break;
                case PLAYERSDATA:{
                    Lobby.adapter.clear();
                    Lobby.adapter.addAll((ArrayList<Player>) packages[0].obj);
                    Lobby.adapter.notifyDataSetChanged();
                    Lobby.setConnectionsList((ArrayList<Player>) packages[0].obj);
                }break;
                case GAMEDATA:{
                    switch (GameCommandType.getTypeOf(packages[0].obj.toString())) {
                        case URGAMESTATUS:{
                            iPlayer.setPlayerGameState(Game.getInstance().gameState);
                            outCommand.offer(new MonoPackage("[urGameStatus]", "[gameData]", iPlayer.getPlayerGameState()));
                        }break;
                        case WAITFORPLAYERS:{
                            Game.getInstance().showFragment(GameFragments.NOTIFY, "We are all waiting.. sry");
                        }break;
                        case EXECUTESTART:{
                            iPlayer.setPlayerGameState(GameState.RUNNING);
                            Game.getInstance().showFragment(GameFragments.NONE, "");
                        }break;
                    }
                }
                default:
                    Lobby.tv.setText("[C]" + packages[0].obj.toString());
            }
        }
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        try {
            oin.close();
            oout.close();
            socket.shutdownInput();
            socket.shutdownOutput();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();

        }System.out.println("[C]OnPostExec");
    }

    @Override
    protected void onCancelled() {
        try {
            socket.shutdownInput();
            socket.shutdownOutput();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("[C]OnCancel");
    }

    private class InputStreamWaiter implements Runnable{
        Client c = Client.getInstance();
//        public InputStreamWaiter(Client c) {
//            this.c = c;
//        }

        @Override
        public void run() {
            while(!c.isCancelled()){
                try {
                    objReceived.offer((MonoPackage) oin.readUnshared());
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}