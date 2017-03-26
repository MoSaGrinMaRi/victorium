package com.MoSaGrinMaRi.team.victorium.Connection;

import android.os.AsyncTask;

import java.net.*;
import java.io.*;
import java.util.Enumeration;

public class Server extends AsyncTask<Void, String, Void> {

    static String line;
    static String playerName;

    @Override
    protected Void doInBackground(Void... params) {

        System.out.println("[=3.3=].................");
        int port = 62322; // случайный порт (может быть любое число от 1025 до 65535)
        try {

            publishProgress("NO IP 0_0?");
            Enumeration e = NetworkInterface.getNetworkInterfaces();
            while(e.hasMoreElements())
            {
                NetworkInterface n = (NetworkInterface) e.nextElement();
                Enumeration ee = n.getInetAddresses();
                while (ee.hasMoreElements())
                {
                    InetAddress i = (InetAddress) ee.nextElement();
                    if(i.getHostAddress().startsWith("192.168."))
                        publishProgress(i.getHostAddress());
                }
            }

            System.out.println("[=3.4=].................");
            ServerSocket ss = new ServerSocket(port); // создаем сокет сервера и привязываем его к вышеуказанному порту
            System.out.println("Waiting for a client...");
            System.out.println("[=3.5=].................");
            Socket socket = ss.accept(); // заставляем сервер ждать подключений и выводим сообщение когда кто-то связался с сервером

            System.out.println("[=3.6=].................");
            System.out.println("Got a client :) ... Finally, someone saw me through all the cover!");
            System.out.println();

            PrintWriter pwOut = new PrintWriter(socket.getOutputStream(), true);
            KnockKnockProtocol kkp = new KnockKnockProtocol();
            String outputLine = kkp.processInput(null);
            pwOut.println(outputLine);

            // Берем входной и выходной потоки сокета, теперь можем получать и отсылать данные клиенту.
            InputStream sin = socket.getInputStream();
            OutputStream sout = socket.getOutputStream();

            // Конвертируем потоки в другой тип, чтоб легче обрабатывать текстовые сообщения.
            DataInputStream in = new DataInputStream(sin);
            DataOutputStream out = new DataOutputStream(sout);

            line = null;
            while(true) {
                line = in.readUTF(); // ожидаем пока клиент пришлет строку текста.
                System.out.println("The dumb client just sent me this line : " + line);
                publishProgress();
                System.out.println("I'm sending it back...");
                out.writeUTF("from[S] "+line); // отсылаем клиенту обратно ту самую строку текста.
                out.flush(); // заставляем поток закончить передачу данных.
                System.out.println("Waiting for the next line...");
                System.out.println();
            }
        } catch(Exception x) {
            x.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(String... aVoid){
        if(aVoid.length != 0){
            Lobby.statusUpdate("My ip is " + aVoid[0]);
        }else {
            Lobby.statusUpdate(line);
            System.out.println("[=!S!=] get " + line + ".................");
        }
    }
}