package com.MonoCycleStudios.team.victorium.Connection;

import android.os.AsyncTask;

import java.net.*;
import java.io.*;

public class Client extends AsyncTask<String, String, Void> {
    public static Socket socket;
    static String recivedLine;
    static String line;
    static String playerName;
    static int port = Lobby.gPort;
    static String address;

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

            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());

            {
                out.writeUTF("[D]" + playerName);   //////
                out.flush();                        //////
                line = in.readUTF();
                publishProgress("[connected]");
                System.out.println("The server was very polite. It sent me this : " + line);
            }

            while (!isCancelled()) {
                if(recivedLine != null) {
                    line = playerName + " = " + recivedLine;
                    recivedLine = null;
                    System.out.println("Sending this line to the server...");
                    out.writeUTF(line); // sending our query to server
                    out.flush();
                    System.out.println("waiting");
                    line = in.readUTF(); // waiting for server to respond
                    publishProgress("update");
                    System.out.println("The server was very polite. It sent me this : " + line);
                }
            }
        } catch (Exception x) {
            x.printStackTrace();
        }
        return null;
    }

    public static void sendString(String s){
        recivedLine = s;
    }

    @Override
    protected void onProgressUpdate(String... s) {
        final String[] buffer = s;

        if (buffer.length != 0){
            if(buffer[0].equalsIgnoreCase("[connected]")){
               Lobby.b1.setText("Connected");
            }else {
                Lobby.tv.setText("[C]" + line);
            }
        }
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();

        }System.out.println("[C]OnPostExec");
    }

    @Override
    protected void onCancelled() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("[C]OnCancel");
    }
}