package com.MonoCycleStudios.team.victorium.Connection;

import android.os.AsyncTask;
import android.os.Handler;
import android.widget.Toast;

import com.MonoCycleStudios.team.victorium.Connection.Enums.CommandType;
import com.MonoCycleStudios.team.victorium.Game.Character;
import com.MonoCycleStudios.team.victorium.Game.Enums.GameCommandType;
import com.MonoCycleStudios.team.victorium.Game.Enums.GameState;
import com.MonoCycleStudios.team.victorium.Game.Game;
import com.MonoCycleStudios.team.victorium.Game.Player;

import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Server extends AsyncTask<String, MonoPackage, Void> {

    List<ServerTread> connectionList = new ArrayList<>();
    boolean islisten = true;
    boolean isObjWaiter = true;
    boolean isCheckRunning = false;
    ServerTread connection;
    ServerSocket serverSocket;
    Thread tmpThread = null;

    public List<ServerTread> getConnectionList() {
        return connectionList;
    }

    @Override
    protected Void doInBackground(String... params) {
        try {
            if(Lobby.getMyLocalIP() == null) { return null; }
            serverSocket = new ServerSocket(Lobby.gPort);
            System.out.println("Initialized");

            while (islisten || connectionList.size() <= Lobby.MAX_PLAYERS || !isCancelled()) {
                connection = new ServerTread();
                connection.init(serverSocket.accept());
                if(!islisten)
                    break;
                connectionList.add(connection);
//                connection.getSocket().setKeepAlive(true);
                publishProgress(new MonoPackage("[exe]",CommandType.RDATA.getStr(),""));
            }
            System.out.println("-0-0-0-0-0-0-0-0-");
            while(isObjWaiter || !isCancelled()){
                Queue<MonoPackage> qmp = null;
                for(int i = 0; i < connectionList.size(); i++){
                    if((qmp = connectionList.get(i).getObjToServer()).size() > 0){
                        for(int j = 0; j < qmp.size(); j++) {
                            MonoPackage pck = qmp.poll();
                            System.out.println("12345678909999");
                            switch (CommandType.getTypeOf(pck.descOfObject)) {
                                case GAMEDATA: {
                                    switch (GameCommandType.getTypeOf(pck.typeOfObject)) {
                                        case URGAMESTATUS:{
                                            Lobby.getPlayersList().get(i).setPlayerGameState((GameState) pck.obj);
                                        }break;
                                        case GAMERULE:
                                        case QUESTION:
                                        case ALERT:
                                        case REGIONS:{
                                            System.out.println("123456789098765");
                                            Game.getInstance().commandProcess(pck);
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
            System.err.println("Could not listen on port " + Lobby.gPort);
            ioe.printStackTrace();
        } catch(Exception x) {
            System.out.println("We got an EXCEPTION, OH NO :c");
            x.printStackTrace();
        }
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void onProgressUpdate(MonoPackage... packages){
        if (packages.length != 0) {
            switch (CommandType.getTypeOf(packages[0].descOfObject)) {
                case RDATA: {
                    if (packages[0].typeOfObject.equalsIgnoreCase("[exe]")) {
                        connectionList.get(connectionList.size() - 1).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "");

                        if (!isCheckRunning) {
                            isCheckRunning = true;
                            tmpThread = new Thread(new Runnable() {
                                Handler h = new Handler();
                                int delay = 5000; //ms  // how often will check if user is still connected

                                public void run() {
                                    h.postDelayed(new Runnable() {
                                        public void run() {
                                            checkConnection();
                                            checkStartAvailable();
                                            if(isCheckRunning)
                                                h.postDelayed(this, delay);
                                        }
                                    }, delay);
                                }
                            });
                            tmpThread.start();
                        }
                    } else if (packages[0].typeOfObject.equalsIgnoreCase("[remove]")) {
                        for (ServerTread st : (ArrayList<ServerTread>) packages[0].obj) {
                            connectionList.remove(st);
                            Lobby.playerArrayList.remove(findPlayer(st.myPlayer.getPlayerName(), st.myPlayer.getPlayerID()));
                        }
                        Lobby.forceREUpdateAdapter();
                        checkStartAvailable();
                        notifyAllClients(new MonoPackage("",CommandType.NEWPLAYER.getStr(),null));
                    }
                }
                break;
            }
        }
    }

    private Player findPlayer(String playerName, int playerID) {
        for(Player p : Lobby.playerArrayList) {
            if(p.getPlayerName().equals(playerName) && (p.getPlayerID() == playerID)) {
                return p;
            }
        }
        return null;
    }

    private ServerTread findClient(Player p) {
        for(ServerTread tmp : connectionList) {
            if(tmp.myPlayer.getPlayerName().equals(p.getPlayerName()) && (tmp.myPlayer.getPlayerID() == p.getPlayerID())) {
                return tmp;
            }
        }
        return null;
    }

    private void checkConnection(){
        if (connectionList != null && !connectionList.isEmpty()){
            Iterator<ServerTread> iter = connectionList.iterator();

            ArrayList<ServerTread> listToRemove = new ArrayList<>();
            while (iter.hasNext()){
                ServerTread st = iter.next();

                System.out.println(st.toString() + " \" " + st.oPing  + " \" " + st.isMarkedToRemove);
                if(st.oPing == -1){
                    if(st.isMarkedToRemove){
                        listToRemove.add(st);
                    }else{
                        st.isMarkedToRemove = true;
                        st.outCommand.offer(new MonoPackage("Int", CommandType.PING.getStr(), System.currentTimeMillis()));
                    }
                }else{
                    if(st.isMarkedToRemove)
                        st.isMarkedToRemove = false;
                    st.oPing += 1;
                    st.outCommand.offer(new MonoPackage("Int", CommandType.PING.getStr(), System.currentTimeMillis()));
                }
            }
            publishProgress(new MonoPackage("[remove]",CommandType.RDATA.getStr(),listToRemove));
        }else{
            isCheckRunning = false;
        }
    }

    private void checkStartAvailable(){
        if (Lobby.playerArrayList.size() >= Lobby.MIN_PLAYERS_TO_START)
            Lobby.b3.setEnabled(true);
        else
            Lobby.b3.setEnabled(false);
    }

    /**
     *  Usage:
     *  notifyAllClients(new MonoPackage("","",null));
     *  */
    public void notifyAllClients(MonoPackage command){
        for (int i = 0; i < connectionList.size(); i++){
            notifyClient(connectionList.get(i), command);
        }
    }
    /**
     *  Usage:
     *  notifyPlayer(Player, new MonoPackage("","",null));
     *  */
    public void notifyPlayer(Player p, MonoPackage command) {
        ServerTread st = findClient(p);

        System.out.println("1234 st=" + st);
        if(st != null){
            notifyClient(st, command);
        }
    }
    /**
     *  Usage:
     *  notifyClient(ServerThread, new MonoPackage("","",null));
     *  */
    public void notifyClient(ServerTread st, MonoPackage command) {
        switch (CommandType.getTypeOf(command.descOfObject)) {
            case STARTGAME: {
                System.out.println(st.myPlayer.getPlayerName() + "setPlaying True");
                st.setPlaying(true);
            }break;
            case NEWPLAYER:{
                if(st.myPlayer != null) {
                    System.out.println("trying send List to " + st.myPlayer.getPlayerName() + " " + st.myPlayer.getPlayerID());
                    st.setOutCommand("ArrayList", CommandType.PLAYERSDATA.getStr(), Lobby.playerArrayList);
                }else{
                    Toast.makeText(Lobby.thisActivity.getApplicationContext(), "S1 tried to connect, but smth happened", Toast.LENGTH_SHORT).show();
                }
            }break;
            case GAMEDATA:{
                st.setOutCommand(command.typeOfObject,command.descOfObject,command.obj);
            }break;
            default: {
                System.out.println("Smth went wrong. Command '" + command.fullToString() + "' didn't recognized.");
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

    public void stopListening(boolean isCompletely){
        System.out.println("[S]onStopListening");
        islisten = false;
        if(isCompletely){
            try {
                isCheckRunning = false;
                tmpThread.interrupt();
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
        connectionList = null;
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

        private BlockingQueue<MonoPackage> objReceived = new LinkedBlockingQueue<>();
        private Queue<MonoPackage> objToServer = new LinkedList<>();
        public void setObjToServer(MonoPackage objToServer) {
            this.objToServer.offer(objToServer);
        }
        public Queue<MonoPackage> getObjToServer(){
            return this.objToServer;
        }

        public Player myPlayer;

        long oPing = -3;
        boolean isMarkedToRemove = false;

        private BlockingQueue<MonoPackage> outCommand = new LinkedBlockingQueue<>();

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
                try {
                    oout = new ObjectOutputStream(socket.getOutputStream());
                    oout.reset();
                    oin = new ObjectInputStream(socket.getInputStream());
                    oout.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                    InputStreamWaiter isw = new InputStreamWaiter(this);
                    Thread tisw = new Thread(isw);
                    tisw.start();

                    while (!isCancelled()) {
//                        if (playing) {
//
//                        }
                        if(listening) {

                            synchronized (objReceived) {
                                if (!objReceived.isEmpty()) {
                                    MonoPackage command = null;
                                    try {
                                        command = objReceived.poll();
                                    } catch (NullPointerException | NoSuchElementException npe) {
                                        npe.printStackTrace();
                                    }

                                    processData(command);
                                }
                            }
                            synchronized (outCommand) {
                                if (!outCommand.isEmpty()) {
                                    MonoPackage command = null;

                                    try {
                                        System.out.println("Out[St] " + outCommand.size());
                                        command = outCommand.poll();
                                    } catch (NullPointerException | NoSuchElementException npe) {
                                        npe.printStackTrace();
                                    }

                                    if (command != null) {
                                        try {
                                            oout.writeUnshared(command);
                                            oout.flush();
                                            oout.reset();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
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
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        private void processData(MonoPackage monoPackage){
            if(monoPackage == null){return;}    //  strange but smtm happen
            if(monoPackage.descOfObject.equalsIgnoreCase("[rData]")){
                System.out.println("[St] got: " + monoPackage.fullToString());
                outCommand.offer(monoPackage);
            }else{
                publishProgress(monoPackage);
            }
        }

        @Override
        protected void onProgressUpdate(MonoPackage... packages){
            if (packages.length != 0) {
                System.out.println("-=[St] receive=-" + packages[0].fullToString());
                switch (CommandType.getTypeOf(packages[0].descOfObject)) {
                    case NEWPLAYER: {
//                            // TEMP!!!! Adding trice to locate more zones
//                            for (int i = 0; i < 3; i++)
//                            {
                        int newID = Lobby.getUnusedIndex();
                        myPlayer = new Player(newID, packages[0].obj.toString(), null, new Character(newID), null);
                        Lobby.playerArrayList.add(myPlayer);
//                            }

                        checkStartAvailable();

                        MonoPackage mpTMP = new MonoPackage("Player",CommandType.NEWPLAYER.getStr(),myPlayer);
                        if (!outCommand.offer(mpTMP)) {   //  [Log error]
                            System.out.println("Error on adding outCommand " + mpTMP.fullToString());
                        }

                        Server.this.notifyAllClients(new MonoPackage("",CommandType.NEWPLAYER.getStr(),null));
                        Lobby.statusUpdate(packages[0].obj.toString() + " connected");
                    }
                    break;
                    case GAMEDATA: {
                        System.out.println("1234567890987654321");
                        setObjToServer(packages[0]);
                    }break;
                    case PING:{
                        oPing = System.currentTimeMillis() - (Long)packages[0].obj;
                        System.out.println(oPing);
                    }break;
                }
                System.out.println("[=!S!=] get " + packages[0].obj.toString() + ".................");
            }
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
            InputStreamWaiter(ServerTread st) {
                this.st = st;
            }
            @Override
            public void run() {
                MonoPackage monoPackage = null;
                while(!st.isCancelled()){
                    try {
                        monoPackage = (MonoPackage) oin.readUnshared();
                        synchronized (objReceived) {
                            objReceived.offer(monoPackage);
                        }
                    } catch (IOException | ClassNotFoundException e) {
                        e.printStackTrace();
                        st.cancel(true);
                        st.listening = false;
                        oPing = -1;
                        isMarkedToRemove = true;
                        objToServer.offer(new MonoPackage("[remove]",CommandType.RDATA.getStr(),(new ArrayList<>().add(st))));
                    }
                }
            }
        }
    }
}