package com.MoSaGrinMaRi.team.victorium.Connection;

import android.os.AsyncTask;

import java.net.*;
import java.io.*;

public class Client extends AsyncTask<String, Void, Void> {
    static String line;
    static String playerName;
    static int port = 62322;
    static String address = "192.168.0.106";
    //boolean checkPostAvalaibe = false;

    @Override
    protected void onPreExecute() {
        System.out.println("[=2.0=].................");
    }

    @Override
    protected Void doInBackground(String... params) {
        this.address = params[0].startsWith("192.168.") ? params[0] : this.address;
        this.playerName = params[1];
        //if(checkPostAvalaibe){
            boolean portAvailable = false;
            int delay = 1; // .01 s
            try {
                Socket socket = new Socket();
                socket.connect(new InetSocketAddress(address, port), delay);
                portAvailable = socket.isConnected();
                socket.close();
            } catch (UnknownHostException uhe) {
                //uhe.printStackTrace();
            } catch (IOException ioe) {
                //ioe.printStackTrace();
            }
            System.out.println("Connection possible: " + portAvailable + " :" + port);

        //}else{

            System.out.println("[=2.3=].................");
            try {
                System.out.println("[=2.4=].................");
                InetAddress ipAddress = InetAddress.getByName(address); // создаем объект который отображает вышеописанный IP-адрес.

                System.out.println("[=2.5=].................");
                System.out.println("Any of you heard of a socket with IP address " + address + " and port " + port + "?");
                Socket socket = new Socket(ipAddress, port); // создаем сокет используя IP-адрес и порт сервера.
                System.out.println("Yes! I just got hold of the program.");
                // Берем входной и выходной потоки сокета, теперь можем получать и отсылать данные клиентом.
                InputStream sin = socket.getInputStream();
                OutputStream sout = socket.getOutputStream();

                // Конвертируем потоки в другой тип, чтоб легче обрабатывать текстовые сообщения.
                DataInputStream in = new DataInputStream(sin);
                DataOutputStream out = new DataOutputStream(sout);
                while (true) {
                    line = playerName;//"222\n\r";
                    System.out.println("Sending this line to the server...");
                    out.writeUTF(line); // отсылаем введенную строку текста серверу.
                    System.out.println("Uno memento...");
                    out.flush(); // заставляем поток закончить передачу данных.
                    System.out.println("One more...");
                    line = in.readUTF(); // ждем пока сервер отошлет строку текста.
                    System.out.println("The server was very polite. It sent me this : " + line);
                    System.out.println("Looks like the server is pleased with us. Go ahead and enter more lines.");
                }
            } catch (Exception x) {
                x.printStackTrace();
            }
       //}
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        System.out.println("[=!C!=].................");
        Lobby.tv.setText("[C]" + line);
    }
}