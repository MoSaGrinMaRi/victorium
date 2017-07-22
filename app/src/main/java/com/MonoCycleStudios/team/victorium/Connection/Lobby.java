package com.MonoCycleStudios.team.victorium.Connection;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.MonoCycleStudios.team.victorium.Game.Game;
import com.MonoCycleStudios.team.victorium.Game.Player;
import com.MonoCycleStudios.team.victorium.R;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;

public class Lobby extends AppCompatActivity {

    public static int gPort = 52232;
    public static int MAX_PLAYERS = 6;

    public static ListView lv;
    public static TextView tv;
    public static OurArrayListAdapter adapter;
    public static Button b1;
    public static Button b3;
    public EditText tvSip;
    public static Server s1;
    public static Client c1;
    static String lPlayerName;
    static boolean lIsServer = false;

    private static Activity thisActivity;   // need another approach, i guess...

    static ArrayList<Player> connectionsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);

        thisActivity = this;

        b1 = (Button)findViewById(R.id.connectBtn);

        b1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick (View v){
                ((Button)findViewById(R.id.connectBtn)).setEnabled(false);
                ((Button)findViewById(R.id.connectBtn)).setClickable(false);
                ((Button)findViewById(R.id.connectBtn)).setText("Connecting");

                c1 = Client.getInstance();
                System.out.println("[=2.1=].................");
//                c1.init(Lobby.this);
                c1.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, lIsServer ? getMyLocalIP() : tvSip.getText().toString(), lPlayerName);
                System.out.println("[=2.2=].................");
            }
        });

        b3 = (Button)findViewById(R.id.launchGame);

        b3.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick (View v){
                statusUpdate("Loading");
                s1.notifyAllClients("[startGame]");
                s1.stopListening(false);
            }
        });

        lv = (ListView) findViewById(R.id.connectionsListView);

        tv = (TextView) findViewById(R.id.status);
        tvSip = (EditText) findViewById(R.id.ipAddress);
        if (lIsServer){
            System.out.println("set invisible");
            tvSip.setEnabled(false);
            tvSip.setText(getMyLocalIP());
            System.out.println("[=1=].................");
            s1 = new Server();
            System.out.println("[=1.1=].................");
            s1.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "");
            System.out.println("[=1.2=].................");

            b1.performClick();
            b1.setVisibility(View.INVISIBLE);
        }else{
            b3.setVisibility(View.INVISIBLE);
        }

        Button bS1 = (Button)findViewById(R.id.btnSend1);
        Button bS2 = (Button)findViewById(R.id.btnSend2);

        bS1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick (View v){ c1.addOutCommand("String","[rData]","1");
            }
        });
        bS2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick (View v){ c1.addOutCommand("String","[rData]","2");
            }
        });

        adapter = new OurArrayListAdapter(this, android.R.layout.simple_list_item_1, connectionsList);
        adapter.clear();
        adapter.addAll(connectionsList);
        adapter.notifyDataSetChanged();
        lv.setAdapter(adapter);
    }

    public class OurArrayListAdapter extends ArrayAdapter<Player> {
//        public OurArrayListAdapter(Context context, int textViewResourceId) {
//            super(context, textViewResourceId);
//        }

        public OurArrayListAdapter(Context context, int resource, ArrayList<Player> items) {
            super(context, resource, items);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View v = convertView;
            if (v == null) {
                LayoutInflater vi;
                vi = LayoutInflater.from(getContext());
                v = vi.inflate(R.layout.itemlistrow, null);
            }

            Player p = getItem(position);
            if (p != null) {
                ImageView im1 = (ImageView) v.findViewById(R.id.imageView);
                TextView tt1 = (TextView) v.findViewById(R.id.playerName);
                TextView tt2 = (TextView) v.findViewById(R.id.playerID);

                if (tt1 != null)
                    tt1.setText(p.getPlayerName());
                if (tt2 != null)
                    tt2.setText(String.valueOf(p.getPlayerID()));
                if (im1 != null)
                    im1.setColorFilter(p.getPlayerCharacter().getColor().getARGB());
            }
            return v;
        }

    }

    public static synchronized void statusUpdate(String s){
        tv.setText("[S] " + s);
    }

    public void setConfig(boolean isServer, String playerName){
        lIsServer = isServer;
        lPlayerName = playerName;
    }

    public static void startGameActivity(){

        System.out.println("YEY!");

        thisActivity.startActivity(new Intent(thisActivity, Game.getInstance().getClass()));

        if(lIsServer){
            Game.getInstance().setup(s1, connectionsList.size());
        }else{
            Game.getInstance().setup(null, connectionsList.size());
        }
        c1.iPlayer.setPlayerGame(Game.getInstance());
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

    public static int getUnusedIndex(){
        int i = 0;
        for(; i < Lobby.MAX_PLAYERS && i < connectionsList.size(); i++){
            System.out.println("]]=[[ " + (connectionsList.size()-1) + " }{ " + i + " }{ " + connectionsList.get(i).getPlayerID());
            if(i != connectionsList.get(i).getPlayerID()){
                return i;
            }
        }
        System.out.println("]]+[[ " +i);
        return i;
    }

    public static ArrayList<Player> getConnectionsList(){
        return connectionsList;
    }
    public static void setConnectionsList(ArrayList<Player> connectionsList) {
        Lobby.connectionsList = connectionsList;
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
