package com.MonoCycleStudios.team.victorium.Connection;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.widget.BaseAdapter;

import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Server extends AsyncTask<Activity, String, Void> {


    List<ServerTread> connectionList = new ArrayList<>();
    static int port = Lobby.gPort;
    static Activity lActivity;
    boolean islisten = true;
    boolean checkRunning = false;
    ServerTread connection;
    ServerSocket serverSocket;

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

            while (islisten || !isCancelled()) {
                connection = new ServerTread();
                connection.init(serverSocket.accept());
                publishProgress("[exe]");
                connectionList.add(connection);
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

        if (buffer.length != 0){
            if(buffer[0].equalsIgnoreCase("[exe]")){
                if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB)   // strange, worked even w/o this, before
                    connection.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);   // < --
                else
                    connection.execute();

                if(!checkRunning) {
                    checkRunning = true;
                    lActivity.runOnUiThread(new Runnable() {
                        Handler h = new Handler();
                        int delay = 1500; //ms  // how often will check if user is still connected

                        public void run() {
                            h.postDelayed(new Runnable() {
                                public void run() {
                                    checkConnection();
                                    h.postDelayed(this, delay);
                                }
                            }, delay);
                        }
                    });
                }

            }else if(buffer[0].equalsIgnoreCase("[remove]")){
                Lobby.connectionsList.remove(Integer.parseInt(buffer[1]));
                ((BaseAdapter)Lobby.lv.getAdapter()).notifyDataSetChanged();
            }else{
                Lobby.statusUpdate("My ip is " + buffer[0]);
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

                if(st.getStatus().toString().equals("FINISHED")) {
                    iter.remove();
                    publishProgress("[remove]",""+ind);
//                    ind--;
                }
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