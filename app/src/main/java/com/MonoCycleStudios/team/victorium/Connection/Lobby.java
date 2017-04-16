package com.MonoCycleStudios.team.victorium.Connection;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.MonoCycleStudios.team.victorium.Game.Game;
import com.MonoCycleStudios.team.victorium.R;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;

public class Lobby extends AppCompatActivity {

    public static int gPort = 52232;

    public static ListView lv;
    public static TextView tv;
    public static Button b1;
    public static Button b3;
    Intent intent;
    public EditText tvSip;
    public Server s1;
    public Client c1;
    static String lPlayerName;
    static boolean lIsServer = false;

    static ArrayList<String> connectionsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);

        b1 = (Button)findViewById(R.id.connectBtn);

        b1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick (View v){
                ((Button)findViewById(R.id.connectBtn)).setEnabled(false);
                ((Button)findViewById(R.id.connectBtn)).setClickable(false);
                ((Button)findViewById(R.id.connectBtn)).setText("Connecting");

                c1 = new Client();
                System.out.println("[=2.1=].................");
                c1.execute(lIsServer ? getMyLocalIP() : tvSip.getText().toString(), lPlayerName);
                System.out.println("[=2.2=].................");
            }
        });

        lv = (ListView) findViewById(R.id.connectionsListView);

        tv = (TextView) findViewById(R.id.status);
        tvSip = (EditText) findViewById(R.id.ipAddress);
        if (lIsServer){
            System.out.println("set invisible");
            tvSip.setVisibility(View.INVISIBLE);
            System.out.println("[=1=].................");
            s1 = new Server();
            System.out.println("[=1.1=].................");
            if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB)   // strange, worked even w/o this, before
                s1.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, Lobby.this);   // < --
            else
                s1.execute(Lobby.this);
            System.out.println("[=1.2=].................");


//            runOnUiThread(new Runnable() {
//                Handler h = new Handler();
//                int delay = 2500; //ms  // magic number, if less then, will stuck after creating [C] dout
//
//                public void run() {
//                    h.postDelayed(new Runnable() {
//                        public void run() {
//                            b1.performClick();  // create a client as well
//                        }
//                    }, delay);
//                }
//            });
        }

        Button bS1 = (Button)findViewById(R.id.btnSend1);
        Button bS2 = (Button)findViewById(R.id.btnSend2);

        bS1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick (View v){
                c1.sendString("1");
            }
        });
        bS2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick (View v){
                c1.sendString("2");
            }
        });

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, connectionsList);
        lv.setAdapter(adapter);


        b3 = (Button)findViewById(R.id.launchGame);
        intent = new Intent(this, Game.class);

        b3.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick (View v){
                statusUpdate("Loading");
                startActivity(intent);
            }
        });
    }

    public static synchronized void statusUpdate(String s){
        tv.setText("[S] " + s);
    }

    public void setConfig(boolean isServer, String playerName){
        lIsServer = isServer;
        lPlayerName = playerName;
    }

    public static String getMyLocalIP(){
        try{
            Enumeration e = NetworkInterface.getNetworkInterfaces();
            while(e.hasMoreElements())
            {
                NetworkInterface n = (NetworkInterface) e.nextElement();
                Enumeration ee = n.getInetAddresses();
                while (ee.hasMoreElements())
                {
                    InetAddress i = (InetAddress) ee.nextElement();
                    if(i.getHostAddress().startsWith("192.168."))
                        return i.getHostAddress();
                }
            }
        } catch (SocketException e1) {
            e1.printStackTrace();
        }
        return null;
    }

    private void stopAndClose(){
        if(s1 != null) {
            s1.cancelChild();
            s1.stopListening(true);
            s1.cancel(true);
        }
        if(c1 != null)
            c1.cancel(true);

        connectionsList.clear();

        finish();
    }

    @Override
    public void onBackPressed() {
        System.out.println("BACK PRESSED!!!" + (s1 == null) + " ][ " + (c1 == null));

        stopAndClose();

        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        stopAndClose();

        super.onDestroy();
    }
}
