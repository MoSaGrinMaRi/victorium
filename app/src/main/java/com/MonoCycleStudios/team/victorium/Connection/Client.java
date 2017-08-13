package com.MonoCycleStudios.team.victorium.Connection;

import android.os.AsyncTask;

import com.MonoCycleStudios.team.victorium.Connection.Enums.CommandType;
import com.MonoCycleStudios.team.victorium.Game.Enums.GameCommandType;
import com.MonoCycleStudios.team.victorium.Game.Game;
import com.MonoCycleStudios.team.victorium.Game.Player;

import java.net.*;
import java.io.*;
import java.util.ArrayList;
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

    private static String playerName;
    private static int port = Lobby.gPort;
    private static String address;
    public static Player iPlayer;

    private Queue<MonoPackage> objReceived = new LinkedList<>();

    private static Queue<MonoPackage> outCommand = new LinkedList<>();
    public MonoPackage getOutCommand() {
        return outCommand.peek();
    }

    public void addOutCommand(String type, String outCommand, Object params) {
        addOutCommand(new MonoPackage(type, outCommand, params));
    }
    public void addOutCommand(MonoPackage monoPackage){
        System.out.println(" - "+Client.getInstance().outCommand.size()+" - ");
        outCommand.offer(monoPackage);
        System.out.println(" - "+Client.getInstance().outCommand.size()+" - ");
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
            address = params[0].startsWith("192.168.") ? params[0] : Lobby.getMyLocalIP();
            playerName = params[1];
        }else{
            return null;
        }

        System.out.println("[=2.3=].................");

        System.out.println("[=2.4=].................");
        InetAddress ipAddress = null;
        try {
            ipAddress = InetAddress.getByName(address);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        System.out.println("[=2.5=].................");
        System.out.println("Any of you heard of a socket with IP address " + address + " and port " + port + "?");
        try {
            socket = new Socket(ipAddress, port);
        } catch (IOException e) {
            e.printStackTrace();
        }
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

        try {
            oout = new ObjectOutputStream(socket.getOutputStream());
            oout.flush();
            oout.reset();
            oin = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        System.out.println("Yes! I just connected!2");

        InputStreamWaiter isw = new InputStreamWaiter();
        Thread tisw = new Thread(isw);
        tisw.start();

        // init block
        try{
            oout.writeUnshared(new MonoPackage("String", "[newPlayer]", playerName));
            oout.flush();
            oout.reset();
        }catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        System.out.println("Yes! I just connected!3");

        while (!isCancelled()) {

            if(!outCommand.isEmpty()) {
                MonoPackage command = null;

                try{
                    System.out.println("Out[C] " + outCommand.size());
                    command = outCommand.poll();
                } catch (NullPointerException npe){npe.printStackTrace();}

                if(command != null) {
                    try {
                        oout.writeUnshared(command);
                        oout.flush();
                        oout.reset();
                    } catch (IOException e) {e.printStackTrace();}
                }
            }
            if (!objReceived.isEmpty()){
                MonoPackage command = null;
                try{
                    System.out.println(objReceived.size() + " | " + objReceived.peek().fullToString());
                    command = objReceived.poll();
                } catch (NullPointerException npe){npe.printStackTrace();}

                processData(command);
            }
        }
        return null;
    }

    private void processData(MonoPackage monoPackage){
        if(monoPackage == null){return;}
        if((CommandType.getTypeOf(monoPackage.descOfObject)) != CommandType.NONE){
            System.out.println("?SA?D?AS?AS?ASD?AD? + " + monoPackage.fullToString());
            if((CommandType.getTypeOf(monoPackage.descOfObject)) == CommandType.PING)
                outCommand.offer(monoPackage);
            else
                publishProgress(monoPackage);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void onProgressUpdate(MonoPackage... packages) {
        MonoPackage monoPackage = packages[0];
        if (monoPackage != null){
            switch (CommandType.getTypeOf(monoPackage.descOfObject)){
                case NEWPLAYER: {
                    if (monoPackage.typeOfObject.equalsIgnoreCase("Player")) {
                        iPlayer = (Player) monoPackage.obj;
                        iPlayer.setPlayerClient(this);
                        Lobby.b1.setText("Connected");
                    }
                }break;
                case RDATA:{
                    Lobby.tv.setText("[C] got: " + monoPackage.obj);
                }break;
                case STARTGAME:{
                    System.out.println("?SA?D?AS?AS?ASD?AD?");
                    Lobby.startGameActivity();
                }break;
                case PLAYERSDATA:{
                    Lobby.adapter.clear();
                    Lobby.adapter.addAll((ArrayList<Player>) monoPackage.obj);
                    Lobby.adapter.notifyDataSetChanged();
                    Lobby.setPlayerArrayList((ArrayList<Player>) monoPackage.obj);
                }break;
                case GAMEDATA:{
                    if(monoPackage.typeOfObject.equalsIgnoreCase(GameCommandType.URGAMESTATUS.getStr())){
                        iPlayer.setPlayerGameState(Game.getInstance().gameState);
                        outCommand.offer(new MonoPackage("[urGameStatus]", "[gameData]", iPlayer.getPlayerGameState()));
                    }else {
                        Game.getInstance().commandProcess(monoPackage);
                    }
                }break;
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
            oin.close();
            oout.close();
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
                    c.cancel(true);
                }
            }
        }
    }
}