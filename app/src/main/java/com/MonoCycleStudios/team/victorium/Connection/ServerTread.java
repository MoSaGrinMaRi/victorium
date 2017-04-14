package com.MonoCycleStudios.team.victorium.Connection;

import android.os.AsyncTask;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ServerTread extends AsyncTask<Void, Void, Void> {
    private Socket socket = null;
    String line = null;
    public boolean listening = false;
    DataInputStream din;

    public void init(Socket socket) {
        this.socket = socket;
    }

    @Override
    protected Void doInBackground(Void... params) {

        listening = true;
        try {
            if(socket != null){
                din = new DataInputStream(socket.getInputStream());
                DataOutputStream dout = new DataOutputStream(socket.getOutputStream());
                while (listening || !isCancelled()) {
                    line = din.readUTF(); // wait for client query
                    publishProgress();
                    System.out.println("I'm sending it back...");
                    dout.writeUTF("from[S] " + line); // sent to client this exact same line
                    System.out.println();
                }
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Void... aVoid){
        Server.lActivity.runOnUiThread(new Runnable() {
            public void run() {
                if(line.startsWith("[D]")){
                    Lobby.connectionsList.add(line.substring(3));
                }
                Lobby.statusUpdate(line);
            }
        });
        System.out.println("[=!S!=] get " + line + ".................");
    }

    public void inputStreamCancel(){
        System.out.println("[St]onInputStreamCancel");
        try {
            din.close();
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

}
