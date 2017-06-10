package com.MonoCycleStudios.team.victorium.Connection;

import android.app.Activity;
import android.os.AsyncTask;

import com.MonoCycleStudios.team.victorium.Connection.Enums.CommandType;
import com.MonoCycleStudios.team.victorium.Game.Enums.GameCommandType;
import com.MonoCycleStudios.team.victorium.Game.Enums.GameFragments;
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
//    static String line;
    static String playerName;
    private static int port = Lobby.gPort;
    private static String address;
    private Activity lActivity;
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
    //    DataInputStream in;
//    DataOutputStream out;
    private static ObjectInputStream oin;
    private static ObjectOutputStream oout;
    public static ObjectInputStream getOin() {
        return oin;
    }


    public void init(Activity activity) {
        this.lActivity = activity;
    }

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

/*
*                 [!] All OBJECTS u'll sent, SHOULD BE Serializable
*                 [!] Use the follow order to perform stable object send/receive :
*
*            oout.writeByte(1);     // notify other side that u send smth
*            oout.writeObject(new MonoPackage("String", "[rData]", "justRandomStringHereSaysHiToYou:)"));
*            oout.flush();
*
*
*            if( oin.avalaible() > 0){
*               oin.readByte();
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
//                oout.writeByte(2);
                oout.writeUnshared(new MonoPackage("String", "[newPlayer]", playerName));
                oout.flush();
                oout.reset();

//                processData(objReceived.poll());
//                System.out.println("2");
//                processData(objReceived.poll());
////                oin.readByte();
////                iPlayer = (Player) ((MonoPackage) oin.readObject()).obj;
//                System.out.println("3");
////                processData((MonoPackage) oin.readObject());
////                publishProgress(new MonoPackage("String","[rData]","(connected)"));
////                oin.readByte();
////                publishProgress((MonoPackage) oin.readObject());
//                System.out.println("The server was very polite. AND EVEN ANSWERED ME *_*");
            }


            while (!isCancelled()) {

                if(outCommand.size() > 0){
//                    oout.writeByte(1);
                    oout.writeUnshared(outCommand.poll());
                    oout.flush();
                    oout.reset();
                    Thread.sleep(500);
                }
                if (objReceived.size() > 0){
                    System.out.println(objReceived.size() + " | " + objReceived.peek().fullToString());
//                    objReceived.offer((MonoPackage) oin.readObject());
//                    isw.isOisAvailable = !isw.isOisAvailable;
                    processData(objReceived.poll());
                }

//                System.out.println("==++1");
//                if(recivedLine != null) {
//                    System.out.println("==++2");
//                    System.out.println("Sending this line to the server...");
//                    oout.writeByte(1);
//                    oout.writeObject(new MonoPackage("String", "line?", playerName + " = " + recivedLine));
//                    oout.flush();
//                    recivedLine = null;
////                    out.writeUTF(line); // sending our query to server
//                    System.out.println("waiting");
//
//                    bufNull=oin.read(buf,0,1);
//                    objReceived.offer((MonoPackage) oin.readObject());
////                    line = in.readUTF(); // waiting for server to respond
//                    if(objReceived.peek().typeOfObject.equalsIgnoreCase("Long") &&
//                            objReceived.peek().descOfObject.equalsIgnoreCase("Ping")) {// &&
//                        //objReceived.obj.toString().startsWith("[P]"))//line.startsWith("[P]"))
//
//                        System.out.println("==++3");
//                        getNotify(objReceived.poll());
//                    }
//                    else {
//                        System.out.println("==++4");
//                        publishProgress("update");
//                    }
//                    System.out.println("The server was very polite. It sent me this : " + objReceived.obj.toString());
//                }
//                if(oin.available() > 0){
//
//                    System.out.println("==++5");
//                    System.out.println("[2.2.6==]");
//                    bufNull=oin.read(buf,0,1);
//                    getNotify((MonoPackage) oin.readObject());
//                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void processData(MonoPackage monoPackage){
        if((CommandType.getTypeOf(monoPackage.descOfObject)) != CommandType.NONE){
            System.out.println("?SA?D?AS?AS?ASD?AD? + " + monoPackage.fullToString());
            publishProgress(monoPackage);
        }
//        if(monoPackage.descOfObject.equalsIgnoreCase("[newPlayer]")){
//            publishProgress(monoPackage);
//        }else if(monoPackage.descOfObject.equalsIgnoreCase("[rData]")) {
//            publishProgress(monoPackage);
//        }
    }

//    private void getNotify(MonoPackage command){//(String command){
////        String toSend = null;
//////        String toSend2 = line;
////        if(command.startsWith("[P]")){
////            toSend = command;
////            command = "Ping";
//////        }else if(toSend2.startsWith("[P]")){
//////            toSend = toSend2;
//////            command = "Ping";
////        }
//
//        System.out.println("==++6");
//        try {
//
//            System.out.println("?c? " + command.fullToString());
//            switch (CommandType.getTypeOf(command.descOfObject)) {
//                case START_GAME: {
//
//                    System.out.println("==++7");
//                    System.out.println("[C]Get command " + command.fullToString());
//                    Lobby.startGameActivity(lActivity);
//                }
//                break;
//                case PING: {
//
//                    System.out.println("==++8");
////                    toSend = command;
//                    System.out.println("[C]Get command " + command.fullToString());
//                    publishProgress("update");
//                    oout.writeByte(1);
//                    oout.writeObject(objReceived);
//                    oout.flush();
//                }
//                break;
//                case NONE:
//                default: {
//
//                    System.out.println("==++9");
//                    System.out.println("[C]Smth went wrong. Command '" + command.fullToString() + "' didn't recognized.");
//                }
//            }
//
//            System.out.println("==++10");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

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
                    Lobby.startGameActivity(lActivity);
                }break;
                case PLAYERSDATA:{
//                    Lobby.setConnectionsList((ArrayList<Player>) packages[0].obj);
                    Lobby.tv.setText(packages[0].descOfObject);
                    Lobby.adapter.clear();
                    Lobby.adapter.addAll((ArrayList<Player>) packages[0].obj);
                    Lobby.adapter.notifyDataSetChanged();
                }break;
                case GAMEDATA:{
                    switch (GameCommandType.getTypeOf(packages[0].obj.toString())) {
                        case URGAMESTATUS:{
                            outCommand.offer(new MonoPackage("[urGameStatus]", "[gameData]", Game.getInstance().gameState));
                        }break;
                        case WAITFORPLAYERS:{
                            Game.getInstance().showFragment(GameFragments.NOTIFY, "We are all waiting.. sry");
//                            connectionList.get(i).setOutCommand("GameCommandType", "[gameData]", GameCommandType.WAITFORPLAYERS);
                        }break;
                        case EXECUTESTART:{
//                            connectionList.get(i).setOutCommand("GameCommandType", "[gameData]", GameCommandType.EXECUTESTART);
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
                    Thread.sleep(1);
                } catch (IOException | InterruptedException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}