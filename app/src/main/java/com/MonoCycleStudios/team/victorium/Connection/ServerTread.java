/*
 *              [!¡!]
 *  NOW LOCATE IN SERVER AS INNER CLASS
 *              [!¡!]
*/

//package com.MonoCycleStudios.team.victorium.Connection;
//
//import android.os.AsyncTask;
//
//import com.MonoCycleStudios.team.victorium.Connection.Enums.CommandType;
//import com.MonoCycleStudios.team.victorium.Game.Character;
//import com.MonoCycleStudios.team.victorium.Game.Enums.GameCommandType;
//import com.MonoCycleStudios.team.victorium.Game.Player;
//
//import java.io.IOException;
//import java.io.ObjectInputStream;
//import java.io.ObjectOutputStream;
//import java.net.Socket;
//import java.sql.Timestamp;
//import java.util.LinkedList;
//import java.util.Queue;
//
//public class ServerTread extends AsyncTask<Void, MonoPackage, Void> {
//    private Socket socket = null;
////    String line = null;
//    public boolean listening = false;
//    public boolean playing = false;
//
//    void setListening(boolean listening){
//        this.listening = listening;
//    }
//    void setPlaying(boolean playing){
//        System.out.println("[St]Setting out command to start");
//        setOutCommand("String", "[startGame]", null);
//        this.playing = playing;
//    }
//
//
//    //    DataInputStream din;
////    DataOutputStream dout;
//    private static ObjectInputStream oin;
//    private static ObjectOutputStream oout;
//    public static ObjectInputStream getOin() {
//        return oin;
//    }
//
//    private Queue<MonoPackage> objReceived = new LinkedList<>();
//    private Queue<MonoPackage> objToServer = new LinkedList<>();
//    public void setObjToServer(MonoPackage objToServer) {
//        this.objToServer.offer(objToServer);
//    }
//    public Queue<MonoPackage> getObjToServer(){
//        return this.objToServer;
//    }
//
//    Timestamp timestamp;
//
//    static byte[] buf = new byte[1];
//    private int bufNull = 0;
//
//    private Queue<MonoPackage> outCommand = new LinkedList<>();
//    long oPing = -1;
//    boolean isMarkedToRemove = false;
//
//    public MonoPackage getOutCommand() {
//        return outCommand.peek();
//    }
//    public void setOutCommand(String type, String outCommand, Object params) {
//        this.outCommand.offer(new MonoPackage(type, outCommand, params));
//    }
//
//    public void init(Socket socket) {
//        this.socket = socket;
//    }
//    public Socket getSocket(){
//        return socket;
//    }
//
//    @Override
//    protected Void doInBackground(Void... params) {
//
//        listening = true;
//        try {
//            if(socket != null){
//
///**
//*                 [!] All OBJECTS u'll sent, SHOULD BE Serializable
//*                 [!] Use the follow order to perform stable object send/receive :
//*
//*            //oout.writeByte(1);     // notify other side that u send smth
//*            oout.writeObject(new MonoPackage("String", "[rData]", "justRandomStringHereSaysHiToYou:)"));
//*            oout.flush();
//*
//*
//*            if( oin.avalaible() > 0){
//*               //oin.readByte();
//*               foo = (MonoPackage) oin.readObject();
//*            }
//*
//*/
//                oout = new ObjectOutputStream(socket.getOutputStream());
//                oout.flush();
//                oout.reset();
//                oin = new ObjectInputStream(socket.getInputStream());
//
//                InputStreamWaiter isw = new InputStreamWaiter(this);
//                Thread tisw = new Thread(isw);
//                tisw.start();
//
//                while (!isCancelled()) {
//                    if (playing) {
//
//                    }
//                    if(listening) {
//
//                        if (objReceived.size() > 0) { // never mind         //  just in case some obj didn't proceeded ¯\_(ツ)_/¯
//                            processData(objReceived.poll());
//                        }
//
//                        if (outCommand.size() > 0 && (Server.isStreamUsing = !Server.isStreamUsing)) {
//                            System.out.println("Out[S] " + outCommand.size() + " " + Server.isStreamUsing);
////                        oout.writeByte(1);
//                            oout.writeUnshared(outCommand.poll());
//                            oout.flush();
//                            oout.reset();
//                            Thread.sleep(500);
//                            Server.isStreamUsing = false;
//                        }
//                    }
////                    System.out.println("==--1");
//////                    System.out.println(oin.available() + ((outCommand==null)?"null":outCommand.fullToString()));
////                    if (oin.available() > 0) {
////                        System.out.println("==--2");
////
//////                        line = din.readUTF(); // wait for client query
////                        bufNull = oin.read(buf,0,1);
////                        objReceived = (MonoPackage) oin.readObject();
////                        publishProgress();
////                        if (objReceived.descOfObject.equalsIgnoreCase("Ping")) {
////                            System.out.println("==--3");
////                            oPing = (Long)objReceived.obj;
////                        } else {
////
////                            System.out.println("==--4");
////
////                            System.out.println("[St] 3");
////                            System.out.println("I'm sending it back... " + objReceived.fullToString());
////                            oout.writeByte(1);
////                            oout.writeObject(objReceived);//"from[S] " + objReceived.obj.toString()); // sent to client this exact same line
////                            oout.flush();
////                            System.out.println();
////                        }
////                    }
////                    if(outCommand != null) {
////
////                        System.out.println("==--5");
////
//////                        String data = null;
//////                        if(outCommand.toLowerCase().startsWith("newid".toLowerCase())){
//////                            data = String.valueOf(outCommand.substring(5));
//////                            outCommand = "newid";
//////                        }
////
////                        System.out.println("?? " + outCommand.fullToString());
////
////                        switch (CommandType.getTypeOf(outCommand.descOfObject)){
////                            case NEWID: {
////
////                                System.out.println("==--6");
////
////                                System.out.println("[St]NewId+ "+ outCommand.fullToString());
////                                oout.writeByte(1);
////                                oout.writeObject(outCommand);
////                                oout.flush();
////                                outCommand = new MonoPackage("Long", "Ping", -1);
////                                System.out.println("??? " + outCommand.fullToString());
////                            }
//////                            break;
////                            case PING: {
////
////                                System.out.println("==--7");
////                                System.out.println("[St]Ping+ "+ outCommand.fullToString());
////                                timestamp = new Timestamp(System.currentTimeMillis());
////                                outCommand.obj = timestamp.getTime();
////                                oout.writeByte(1);
////                                oout.writeObject(outCommand);
////                                oout.flush();
////                                outCommand = null;
////                            }
////                            break;
////                            default: {
////
////                                System.out.println("==--8");
////                                System.out.println("[St]Smth went wrong. Command '" + outCommand.fullToString() + "' didn't recognized.");
////                            }
////                        }
////
////                        System.out.println("==--9");
////                    }
//                }
//
//                System.out.println("[2.2.4]");
////                if (playing) {
//////                    oout.writeByte(1);
////                    oout.writeObject(new MonoPackage("String", "[startGame]", null));
////                    oout.flush();
////                }
////                while (playing || !isCancelled()) {
////                    System.out.println("[2.2.5]");
////                }
//            }
//
//            assert socket != null;
//            socket.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//    private void processData(MonoPackage monoPackage){
//        if(monoPackage.descOfObject.equalsIgnoreCase("[newPlayer]")){
//            publishProgress(monoPackage);
//        }else if(monoPackage.descOfObject.equalsIgnoreCase("[rData]")){
//            System.out.println("[S] got: " + monoPackage.fullToString());
//            outCommand.offer(monoPackage);
//        }
//    }
//
//    @Override
//    protected void onProgressUpdate(final MonoPackage... packages){
////        Server.lActivity.runOnUiThread(new Runnable() {
////            public void run() {
////                switch (CommandType.getTypeOf(packages[0].descOfObject)){
////                    case NEWPLAYER: {
////                        Player npTMP = null;
////                        // TEMP!!!! Adding twice to locate more zones
////                        for (int i = 0; i < 3; i++)
////                        {
////                            int newID = Lobby.getUnusedIndex();
////                            npTMP = new Player(newID, packages[0].obj.toString(), null, new Character(newID), null);
////                            Lobby.connectionsList.add(npTMP);
////                        }
////
////                        MonoPackage mpTMP = new MonoPackage("Player", "[newPlayer]", npTMP);
////////                        MonoPackage lcl = new MonoPackage("ArrayList", "[playersData]", Lobby.connectionsList);
////                        if(!outCommand.offer(mpTMP)){// & !outCommand.offer(lcl)){
//////                            //  [Log error]
////                            System.out.println("Error on adding outCommand " + mpTMP.fullToString());
////                        }
//////
//////
//////                        Lobby.s1.notifyAllClients("[newPlayer]");
////                        Lobby.statusUpdate(packages[0].obj.toString() + " connected");
////                    }break;
////                    case GAMEDATA:{
////                        switch (GameCommandType.getTypeOf(packages[0].typeOfObject)){
////                            case URGAMESTATUS:{
////                                setObjToServer(packages[0]);
////                            }break;
////                        }
////                    }
////                }
////
////
//////
//////                System.out.println("==--10");
//////
//////                System.out.println("[St] 2.1");
//////                System.out.println("+++ " + objReceived.descOfObject.equalsIgnoreCase("[D]") + " | " + objReceived.descOfObject);
//////                if(objReceived.descOfObject.equalsIgnoreCase("[D]")){
//////
//////                    System.out.println("==--11");
//////                    int newID = Lobby.getUnusedIndex();
//////                    Lobby.connectionsList.add(new Player(null, newID, objReceived.obj.toString(), new Character(newID)));
//////                    outCommand = new MonoPackage("int", "newid", newID);
//////                    System.out.println("2 "+ outCommand.fullToString());
////////                    Lobby.connectionsList.add(line.substring(3));
////////                    outCommand = "Ping";
//////                }
//////                Lobby.statusUpdate(objReceived.obj.toString() + " connected");
////            }
////        });
//        System.out.println("[=!S!=] get " + packages[0].obj.toString() + ".................");
//    }
//
//    void inputStreamCancel(){
//        System.out.println("[St]onInputStreamCancel");
//        try {
//            oin.close();
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        listening = false;
//        this.cancel(true);
//    }
//
//    @Override
//    protected void onCancelled() {
//        System.out.println("[St]onCancel");
//        listening = false;
//
//        try {
//            socket.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }finally {
//            super.cancel(true);
//        }
//    }
//
//    private class InputStreamWaiter implements Runnable{
//        ServerTread st;
//        public InputStreamWaiter(ServerTread st) {
//            this.st = st;
//        }
//        @Override
//        public void run() {
//            while(!st.isCancelled()){
//                try {
//                    objReceived.offer((MonoPackage) oin.readUnshared());
//                } catch (IOException | ClassNotFoundException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }
//}