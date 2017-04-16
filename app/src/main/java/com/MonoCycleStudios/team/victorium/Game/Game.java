package com.MonoCycleStudios.team.victorium.Game;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;

import com.MonoCycleStudios.team.victorium.Connection.Server;
import com.MonoCycleStudios.team.victorium.R;

public class Game extends AppCompatActivity {

    private GameState gameState;
    private Server gameServer;
    private Ground gameGround;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        Fragment fragment;        // for change fragment
//        fragment = new Ground();
//        FragmentManager fm = getFragmentManager();
//        FragmentTransaction ft = fm.beginTransaction();
//        ft.replace(R.id.fragmentPlaceGround,
//                fragment);
//        ft.commit();

        setContentView(R.layout.activity_game);
    }
}
