package com.MonoCycleStudios.team.victorium.Connection;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Handler;
import android.widget.BaseAdapter;

import com.MonoCycleStudios.team.victorium.Connection.Enums.CommandType;
import com.MonoCycleStudios.team.victorium.Game.Character;
import com.MonoCycleStudios.team.victorium.Game.Enums.GameCommandType;
import com.MonoCycleStudios.team.victorium.Game.Enums.GameState;
import com.MonoCycleStudios.team.victorium.Game.Game;
import com.MonoCycleStudios.team.victorium.Game.Player;

import java.net.*;
import java.io.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Server extends AsyncTask<String, String, Void> {

    List<ServerTread> connectionList = new ArrayList<>();
    static int port = Lobby.gPort;
//    static Activity lActivity;
    boolean islisten = true;
    boolean isObjWaiter = true;
    boolean isCheckRunning = false;
    ServerTread connection;
    ServerSocket serverSocket;

    Socket tmpSocket;

    public List<ServerTread> getConnectionList() {
        return connectionList;
    }

    @Override
    protected Void doInBackground(String... params) {
//        if (params.length != 0) {
//            lActivity = params[0];
//        }else{
//            return null;
//        }

        try {
            String myIP = Lobby.getMyLocalIP();
            if(Lobby.getMyLocalIP() == null) publishProgress("NO IP 0_0?"); else publishProgress(myIP + " : " + port);
            serverSocket = new ServerSocket(port);
            System.out.println("Initialized");

            while (islisten || connectionList.size() <= Lobby.MAX_PLAYERS || !isCancelled()) {
                connection = new ServerTread();
                connection.init(serverSocket.accept());
                if(!islisten)
                    break;
                connectionList.add(connection);
//                connection.getSocket().setKeepAlive(true);
                publishProgress("[exe]");
            }
            System.out.println("-0-0-0-0-0-0-0-0-");
            while(isObjWaiter || !isCancelled()){
                Queue<MonoPackage> qmp = null;
                for(int i = 0; i < connectionList.size(); i++){
                    if((qmp = connectionList.get(i).getObjToServer()).size() > 0){
                        for(int j = 0; j < qmp.size(); j++) {
                            MonoPackage pck = qmp.poll();
                            switch (CommandType.getTypeOf(pck.descOfObject)) {
                                case GAMEDATA: {
                                    switch (GameCommandType.getTypeOf(pck.typeOfObject)) {
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
                    connectionList.get(connectionList.size() - 1).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "");

//                    if (!isCheckRunning) {
//                        isCheckRunning = true;
//                        lActivity.runOnUiThread(new Runnable() {
//                            Handler h = new Handler();
//                            int delay = 1500; //ms  // how often will check if user is still connected
//
//                            public void run() {
//                                h.postDelayed(new Runnable() {
//                                    public void run() {
////                                        checkConnection();
//                                        if (Lobby.connectionsList.size() > 0)
//                                            Lobby.b3.setEnabled(true);
//                                        else
//                                            Lobby.b3.setEnabled(false);
//                                        h.postDelayed(this, delay);
//                                    }
//                                }, delay);
//                            }
//                        });
//                    }
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
                }break;
                default: {
                    System.out.println("Smth went wrong. Command '" + command[0] + "' didn't recognized." + Arrays.toString(command));
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
        }else{
            Thread tmpThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        System.out.println("-9-9-9-");
                        Socket tmpSock = new Socket(Lobby.getMyLocalIP(), Lobby.gPort);
                        System.out.println("-8-8-8-");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return;
                }
            });
            tmpThread.start();
        }
    }

    @Override
    protected void onCancelled() {
        System.out.println("[S]OnCancel");
        islisten = false;
        super.onCancelled();
    }


    /** ==== ==== ==== ==== ==== ==== ==== ==== *
      * ==== ==== ==== ==== ==== ==== ==== ==== *
      * ==== ==== ==== ==== ==== ==== ==== ==== **/


    class ServerTread extends AsyncTask<String, MonoPackage, Void> {
        private Socket socket = null;
        public boolean listening = false;
        public boolean playing = false;

        void setListening(boolean listening){
            this.listening = listening;
        }
        void setPlaying(boolean playing){
            System.out.println("[St]Setting out command to start");
            setOutCommand("String", "[startGame]", null);
            this.playing = playing;
        }

        private ObjectInputStream oin;
        private ObjectOutputStream oout;
        public ObjectInputStream getOin() {
            return oin;
        }

        private Queue<MonoPackage> objReceived = new LinkedList<>();
        private Queue<MonoPackage> objToServer = new LinkedList<>();
        public void setObjToServer(MonoPackage objToServer) {
            this.objToServer.offer(objToServer);
        }
        public Queue<MonoPackage> getObjToServer(){
            return this.objToServer;
        }

        Timestamp timestamp;

        byte[] buf = new byte[1];
        private int bufNull = 0;

        private Queue<MonoPackage> outCommand = new LinkedList<>();
        long oPing = -1;
        boolean isMarkedToRemove = false;

        public MonoPackage getOutCommand() {
            return outCommand.peek();
        }
        public void setOutCommand(String type, String outCommand, Object params) {
            this.outCommand.offer(new MonoPackage(type, outCommand, params));
        }

        public void init(Socket socket) {
            this.socket = socket;
        }
        public Socket getSocket(){
            return socket;
        }

        @Override
        protected Void doInBackground(String... params) {
            listening = true;
            try {
                if(socket != null){
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

                    InputStreamWaiter isw = new InputStreamWaiter(this);
                    Thread tisw = new Thread(isw);
                    tisw.start();

                    while (!isCancelled()) {
//                        if (playing) {
//
//                        }
                        if(listening) {

                            if (objReceived.size() > 0) {
                                processData(objReceived.poll());
                            }

                            if (outCommand.size() > 0) {
                                System.out.println("Out[S] " + outCommand.size());
                                oout.writeUnshared(outCommand.poll());
                                oout.flush();
                                oout.reset();
                            }
                        }
                    }
                    System.out.println("[2.2.4]");
//                if (playing) {
////                    oout.writeByte(1);
//                    oout.writeObject(new MonoPackage("String", "[startGame]", null));
//                    oout.flush();
//                }
//                while (playing || !isCancelled()) {
//                    System.out.println("[2.2.5]");
//                }
                }
                assert socket != null;
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        private void processData(MonoPackage monoPackage){
            if(monoPackage.descOfObject.equalsIgnoreCase("[rData]")){
                System.out.println("[St] got: " + monoPackage.fullToString());
                outCommand.offer(monoPackage);
            }else{
                publishProgress(monoPackage);
            }
        }

        @Override
        protected void onProgressUpdate(final MonoPackage... packages){
            System.out.println("-=[St] receive=-" + packages[0].fullToString());
            switch (CommandType.getTypeOf(packages[0].descOfObject)){
                case NEWPLAYER: {
                    Player npTMP = null;
//                            // TEMP!!!! Adding trice to locate more zones
//                            for (int i = 0; i < 3; i++)
//                            {
                    int newID = Lobby.getUnusedIndex();
                    npTMP = new Player(newID, packages[0].obj.toString(), null, new Character(newID), null);
                    Lobby.connectionsList.add(npTMP);
//                            }

                    MonoPackage mpTMP = new MonoPackage("Player", "[newPlayer]", npTMP);
                    if(!outCommand.offer(mpTMP)){   //  [Log error]
                        System.out.println("Error on adding outCommand " + mpTMP.fullToString());
                    }

                    Server.this.notifyAllClients("[newPlayer]");
                    Lobby.statusUpdate(packages[0].obj.toString() + " connected");
                }break;
                case GAMEDATA:{
                    switch (GameCommandType.getTypeOf(packages[0].typeOfObject)){
                        case URGAMESTATUS:{
                            setObjToServer(packages[0]);
                        }break;
                    }
                }
//                        case PING: {
//
//                                System.out.println("==--7");
//                                System.out.println("[St]Ping+ "+ outCommand.fullToString());
//                                timestamp = new Timestamp(System.currentTimeMillis());
//                                outCommand.obj = timestamp.getTime();
//                                oout.writeByte(1);
//                                oout.writeObject(outCommand);
//                                oout.flush();
//                                outCommand = null;
//                            }
            }
            System.out.println("[=!S!=] get " + packages[0].obj.toString() + ".................");
        }

        void inputStreamCancel(){
            System.out.println("[St]onInputStreamCancel");
            try {
                oin.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
            listening = false;
            this.cancel(true);
        }

        @Override
        protected void onCancelled() {
            System.out.println("[St]onCancel");
            listening = false;

            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                super.cancel(true);
            }
        }

        private class InputStreamWaiter implements Runnable{
            ServerTread st;
            public InputStreamWaiter(ServerTread st) {
                this.st = st;
            }
            @Override
            public void run() {
                while(!st.isCancelled()){
                    try {
                        objReceived.offer((MonoPackage) oin.readUnshared());
                    } catch (IOException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}