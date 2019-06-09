package com.MonoCycleStudios.team.victorium;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.MonoCycleStudios.team.victorium.Connection.Lobby;
import com.MonoCycleStudios.team.victorium.widget.Utils.FontFamily;
import com.MonoCycleStudios.team.victorium.widget.Utils.MMSystem;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    EditText tvUN;
    Intent intent;

    BitmapFactory.Options bmo = new BitmapFactory.Options();
    public static Bitmap avatarAtlas;
    int frameWidth = 316;
    int frameHeight = 316;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        overridePendingTransition(R.animator.slideout_bottom, R.animator.slidein_top);

        tvUN = findViewById(R.id.InpUserNickname);
        tvUN.setText(tvUN.getText() + " #" + (new Random().nextInt((99 - 10) + 1) + 10));

        Button btnCreate = findViewById(R.id.BtnCreateGame);
        Button btnFind = findViewById(R.id.BtnFindGame);

        TextView title = findViewById(R.id.title);
        title.setTypeface(FontFamily.hammers_r);
        btnCreate.setTypeface(FontFamily.hammers_r);
        btnFind.setTypeface(FontFamily.hammers_r);
        tvUN.setTypeface(FontFamily.hammers_r);

        bmo.inScaled = true;
//        bmo.inSampleSize = 32;
        bmo.inDensity = 316;
        bmo.inTargetDensity = 316;
        avatarAtlas = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.avatar_atlas, bmo);

        ((ImageView) findViewById(R.id.imageView2)).setImageBitmap(Bitmap.createBitmap(avatarAtlas,
                0,
                0,
                frameWidth,
                frameHeight));

        intent = new Intent(this, Lobby.class);

        ImageView pic = findViewById(R.id.imageView2);
        pic.setOnClickListener(new View.OnClickListener() {
            int frameCountX = 0;    //  MAX = 6; Only 6 player
            @Override
            public void onClick(View v) {
                if(++frameCountX >= 6){
                    frameCountX = 0;
                }

                ((ImageView) findViewById(R.id.imageView2)).setImageBitmap(Bitmap.createBitmap(avatarAtlas,
                        frameWidth * frameCountX,
                        0,
                        frameWidth,
                        frameHeight));

            }
        });

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Lobby.getMyLocalIP() == null)
                    Toast.makeText(getApplicationContext(), "Connect to the internet!", Toast.LENGTH_SHORT).show();
                else{
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

                    MMSystem.out.println("User name is: '" + tvUN.getText().toString() + "'");

                    Lobby myLobby = new Lobby();
                    myLobby.setConfig(true, tvUN.getText().toString());
                }

            }
        });

        btnFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

                MMSystem.out.println("User name is: '" + tvUN.getText().toString() + "'");

                Lobby myLobby = new Lobby();
                myLobby.setConfig(false, tvUN.getText().toString());
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        overridePendingTransition(R.animator.slideout_left, R.animator.slidein_right);
        System.exit(0);
    }
}
