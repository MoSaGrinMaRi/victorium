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
import android.widget.Toast;

import com.MonoCycleStudios.team.victorium.Connection.Lobby;
import com.MonoCycleStudios.team.victorium.widget.Utils.FontFamily;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    EditText tvUN;
    Intent intent;

    BitmapFactory.Options bmo = new BitmapFactory.Options();
    public static Bitmap avatarAtlas;
    int frameWidth = 316;
    int frameHeight = 316;
//    Spinner spinner;
//    String selected;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        overridePendingTransition(R.animator.slideout_bottom, R.animator.slidein_top);
//
//        overridePendingTransition(R.animator.slidein_left, R.animator.slidein_left);

        tvUN = (EditText)findViewById(R.id.InpUserNickname);
        tvUN.setText(tvUN.getText() + " #" + (new Random().nextInt((99 - 10) + 1) + 10));

//        spinner = (Spinner) findViewById(R.id.ConnectionType);
//        selected = spinner.getSelectedItem().toString();
//        Toast.makeText(getApplicationContext(), selected, Toast.LENGTH_SHORT).show();

        Button btnCreate = (Button)findViewById(R.id.BtnCreateGame);
        Button btnFind = (Button)findViewById(R.id.BtnFindGame);
//        Button btnLink = (Button)findViewById(R.id.BtnLinkProfile);

        btnCreate.setTypeface(FontFamily.raleway_sb);
        btnFind.setTypeface(FontFamily.raleway_sb);
        tvUN.setTypeface(FontFamily.raleway_sb);
//        btnLink.setTypeface(FontFamily.raleway_sb);

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

        ImageView loco = (ImageView) findViewById(R.id.imageView5);
        loco.setOnClickListener(new View.OnClickListener() {
            boolean isFirstLoco = false;
            @Override
            public void onClick(View v) {
                isFirstLoco = !isFirstLoco;
                int drawableID = isFirstLoco ? R.drawable.victorium_loco_1 : R.drawable.victorium_loco_1_2;
                ((ImageView) findViewById(R.id.imageView5)).setImageDrawable(v.getContext().getDrawable(drawableID));
            }
        });

        ImageView pic = (ImageView) findViewById(R.id.imageView2);
        pic.setOnClickListener(new View.OnClickListener() {
            int frameCountX = 0;    //  MAX = 6; Only 6 player
            @Override
            public void onClick(View v) {
                if(++frameCountX >= 6){
                    frameCountX = 0;
                }

                ((ImageView) findViewById(R.id.imageView2)).setImageBitmap(Bitmap.createBitmap(avatarAtlas, //Lobby.flagAtlas
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

                    System.out.println("User name is: '" + tvUN.getText().toString() + "'");

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

                System.out.println("User name is: '" + tvUN.getText().toString() + "'");

                Lobby myLobby = new Lobby();
                myLobby.setConfig(false, tvUN.getText().toString());
            }
        });

    }

    ActivityOptionsCompat setAO(View v){
        return ActivityOptionsCompat.makeSceneTransitionAnimation(this, v, "sharedGameBack");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        overridePendingTransition(R.animator.slideout_left, R.animator.slidein_right);
        System.exit(0);
    }
}
