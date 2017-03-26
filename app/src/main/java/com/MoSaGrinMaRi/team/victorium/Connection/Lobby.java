package com.MoSaGrinMaRi.team.victorium.Connection;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.MoSaGrinMaRi.team.victorium.R;

public class Lobby extends AppCompatActivity {

    public static TextView tv;
    public static EditText tvSip;
    public static Server s1;
    public static Client c1;
    static String localPlayerName;
    static boolean isServer1 = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);

        tv = (TextView) findViewById(R.id.status);
        tvSip = (EditText) findViewById(R.id.ipAddress);
        if (isServer1){
            System.out.println("set invisible");
            tvSip.setVisibility(View.INVISIBLE);
            System.out.println("[=1=].................");
            s1 = new Server();
            System.out.println("[=1.1=].................");
            s1.execute();
            System.out.println("[=1.2=].................");
        }

        Button b1 = (Button)findViewById(R.id.connectBtn);

        b1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick (View v){
                c1 = new Client();
                System.out.println("[=2.1=].................");
                c1.execute(tvSip.getText().toString(), localPlayerName);
                System.out.println("[=2.2=].................");
            }
        });
    }

    public static void statusUpdate(String s){
        tv.setText("[S] " + s);
    }

    public static void setConfig(boolean isServer, String playerName){
        isServer1 = isServer;
        localPlayerName = playerName;
    }
}
