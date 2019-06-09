package com.MonoCycleStudios.team.victorium.Connection;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.transition.TransitionManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.MonoCycleStudios.team.victorium.Connection.Enums.CommandType;
import com.MonoCycleStudios.team.victorium.Game.Enums.GameState;
import com.MonoCycleStudios.team.victorium.Game.Game;
import com.MonoCycleStudios.team.victorium.Game.Player;
import com.MonoCycleStudios.team.victorium.R;
import com.MonoCycleStudios.team.victorium.widget.Utils.FontFamily;
import com.MonoCycleStudios.team.victorium.widget.Utils.MMSystem;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Random;

public class Lobby extends AppCompatActivity {

    public static int gPort = 52232;
    public static int MAX_PLAYERS = 6;
    public static int MIN_PLAYERS_TO_START = 2;

    public static ListView lv;
    private static LinearLayout ll;
    private static ImageView imBG;
    private static ImageView imEdit;
    public static OurArrayListAdapter adapter;
    public static Button b1;
    public static Button b2;
    public static Button b3;
    public EditText tvSip;
    public static Server s1;
    public static Client c1;
    static String lPlayerName;
    static boolean lIsServer = false;

    Random rand;
    private String[] gamemaps = {
            "mmnew",
            "mmnew"
    };
    public static String gameMapName = "mmnew";
    public static boolean isShowScore = false;
    public static boolean isShowStatus= false;

    public static Lobby thisActivity;   // need another approach, i guess...

    static ArrayList<Player> playerArrayList = new ArrayList<>();

    BitmapFactory.Options bmo = new BitmapFactory.Options();
    public static Bitmap avatarAtlas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);

        thisActivity = this;

        isShowScore = false;

        rand = new Random();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        ll = findViewById(R.id.llLobbyPlayersList);
        imBG = findViewById(R.id.connectionsBgImageView);

        imEdit = findViewById(R.id.imageView4);

        b1 = findViewById(R.id.btnConnect);

        b1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick (View v){
                ((Button)findViewById(R.id.btnConnect)).setEnabled(false);
                ((Button)findViewById(R.id.btnConnect)).setClickable(false);
                ((Button)findViewById(R.id.btnConnect)).setText("Connecting...");

                c1 = new Client();
                MMSystem.out.println("[=2.1=].................");

                c1.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, lIsServer ? getMyLocalIP() : tvSip.getText().toString(), lPlayerName);
                MMSystem.out.println("[=2.2=].................");

                LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                        0,
                        0,
                        40.0f
                );
                findViewById(R.id.view2).setLayoutParams(param);
                TransitionManager.beginDelayedTransition(((LinearLayout)findViewById(R.id.llLobbyControl)));
                TransitionManager.beginDelayedTransition(ll);
                ll.setVisibility(View.VISIBLE);
                imBG.setVisibility(View.VISIBLE);
                imEdit.setVisibility(View.GONE);
                tvSip.setEnabled(false);
            }
        });

        b2 = findViewById(R.id.btnLobbyBack);
        b2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick (View v){
                onBackPressed();
            }
        });

        b3 = findViewById(R.id.btnLaunchGame);
        b3.setEnabled(false);
        b3.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick (View v){
                b3.setEnabled(false);
                s1.notifyAllClients(new MonoPackage("", CommandType.STARTGAME.getStr(), gamemaps[rand.nextInt(2)]));
                s1.stopListening(false);
            }
        });

        b1.setTypeface(FontFamily.hammers_r);
        b2.setTypeface(FontFamily.hammers_r);
        b3.setTypeface(FontFamily.hammers_r);

        lv = findViewById(R.id.connectionsListView);
        lv.setDivider(null);

        tvSip = findViewById(R.id.InpIpAddress);
        if (lIsServer){
            MMSystem.out.println("set invisible");
            tvSip.setEnabled(false);
            tvSip.setText(getMyLocalIP());
            MMSystem.out.println("[=1=].................");
            s1 = new Server();
            MMSystem.out.println("[=1.1=].................");
            s1.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "");
            MMSystem.out.println("[=1.2=].................");

            b1.performClick();
            b1.setVisibility(View.INVISIBLE);
        }else{
            b3.setVisibility(View.INVISIBLE);
        }

        adapter = new OurArrayListAdapter(this, android.R.layout.simple_list_item_1, playerArrayList);
        forceREUpdateAdapter();

        bmo.inScaled = true;
//        bmo.inSampleSize = 32;
        bmo.inDensity = 316;
        bmo.inTargetDensity = 316;
        avatarAtlas = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.avatar_atlas, bmo);

    }

    public class OurArrayListAdapter extends ArrayAdapter<Player> {

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
                ImageView im1 = v.findViewById(R.id.imageView);
                TextView tt1 = v.findViewById(R.id.playerName);
                TextView tt3 = v.findViewById(R.id.playerScore);

                if (tt1 != null)
                    tt1.setText(p.getPlayerName());
                if (tt3 != null) {
                    MMSystem.out.println("[1][][] "+ p.getPlayerScore() + p.getPlayerName() + p.getPlayerGameState() );
                    if(Lobby.isShowScore) {
                        tt3.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
                        tt3.requestLayout();
                        tt3.setText(String.valueOf(p.getPlayerScore()));
                    }else{
                        tt3.getLayoutParams().height = 0;
                        tt3.requestLayout();
                    }
                }
                if (im1 != null) {

                    //        TODO: Move to queue turn to use
//                    int frameWidth = 48;//78;
//                    int frameHeight = 110;//140;
                    int frameWidth = 316;
                    int frameHeight = 316;
                    int frameCountX = 0;    //  MAX = 6; Only 6 player

                    switch (p.getPlayerCharacter().getColor()){
                        case RED:
                            frameCountX = 0;
                            break;
                        case BLUE:
                            frameCountX = 1;
                            break;
                        case ORANGE:
                            frameCountX = 2;
                            break;
                        case GREEN:
                            frameCountX = 3;
                            break;
                        case BLACK:
                            frameCountX = 4;
                            break;
                        case PURPLE:
                            frameCountX = 5;
                            break;

                    }

                    im1.setImageBitmap(Bitmap.createBitmap(Lobby.avatarAtlas,
                            frameWidth * frameCountX,
                            0,
                            frameWidth,
                            frameHeight));
                }

                if(Lobby.isShowStatus){
                    if(Lobby.getPlayersList().get(position).getPlayerGameState() != GameState.LAUNCHING){
                        v.setAlpha(0.5f);
                    }else{
                        v.setAlpha(1);
                    }
                }else{
                    v.setAlpha(1);
                }
            }
            return v;
        }

    }

    public static void forceREUpdateAdapter(){
        adapter.clear();
        adapter.addAll(playerArrayList);
        adapter.notifyDataSetChanged();
        lv.setAdapter(adapter);
    }

    public static synchronized void statusUpdate(String s){
        MMSystem.out.println("[LOBBY STATUS] " + s);
    }

    public void setConfig(boolean isServer, String playerName){
        lIsServer = isServer;
        lPlayerName = playerName;
    }

    public static void startGameActivity(){

        MMSystem.out.println("YEY!");

        Collections.sort(Lobby.getPlayersList());

        isShowScore = true;
        isShowStatus= true;

        thisActivity.startActivity(new Intent(thisActivity, Game.getInstance().getClass()));
        thisActivity.overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);

        if(lIsServer){
            Game.getInstance().setup(s1, playerArrayList.size());
        }else{
            Game.getInstance().setup(null, playerArrayList.size());
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
                    if(isStartWith(i.getHostAddress()))
                        return i.getHostAddress();
                }
            }
        } catch (SocketException e1) {
            e1.printStackTrace();
        }
        return null;
    }

    private static boolean isStartWith(String address){
        String[] addressStart = {
                "192.168.",
                "172.16.",
                "172.17.",
                "172.18.",
                "172.19.",
                "172.20.",
                "172.21.",
                "172.22.",
                "172.23.",
                "172.24.",
                "172.25.",
                "172.26.",
                "172.27.",
                "172.28.",
                "172.29.",
                "172.30.",
                "172.31."
        };
        for (String anAddressStart : addressStart) {
            if (address.startsWith(anAddressStart)) {
                return true;
            }
        }
        return false;
    }

    public static int getUnusedIndex(){
        int i = 0;
        for(; i < Lobby.MAX_PLAYERS && i < playerArrayList.size(); i++){
            MMSystem.out.println("]]=[[ " + (playerArrayList.size()-1) + " }{ " + i + " }{ " + playerArrayList.get(i).getPlayerID());
            if(i != playerArrayList.get(i).getPlayerID()){
                return i;
            }
        }
        MMSystem.out.println("]]+[[ " +i);
        return i;
    }

    public static ArrayList<Player> getPlayersList(){
        return playerArrayList;
    }
    public static void setPlayerArrayList(ArrayList<Player> playerArrayList) {
        Lobby.playerArrayList = playerArrayList;
    }

    public void stopAndClose(){
        if(s1 != null) {
            s1.cancelChild();
            s1.stopListening(true);
            s1.cancel(true);
            s1 = null;
        }
        if(c1 != null) {
            c1.cancel(true);
            c1 = null;
        }

        TransitionManager.beginDelayedTransition(((LinearLayout)findViewById(R.id.llLobbyControl)));
        TransitionManager.beginDelayedTransition(ll);
        ll.setVisibility(View.GONE);

        playerArrayList.clear();

        thisActivity = null;
        finishAfterTransition();
    }

    @Override
    public void onBackPressed() {
        MMSystem.out.println("BACK PRESSED!!!" + (s1 == null) + " ][ " + (c1 == null));

        stopAndClose();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
//        overridePendingTransition(R.animator.slideout_right, R.animator.slidein_left);

        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        stopAndClose();

        super.onDestroy();
    }
}
