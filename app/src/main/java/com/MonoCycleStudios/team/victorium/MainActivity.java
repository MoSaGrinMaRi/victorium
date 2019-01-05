package com.MonoCycleStudios.team.victorium;

import android.content.Intent;
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
        Button btnLink = (Button)findViewById(R.id.BtnLinkProfile);

        btnCreate.setTypeface(FontFamily.raleway_sb);
        btnFind.setTypeface(FontFamily.raleway_sb);
        tvUN.setTypeface(FontFamily.raleway_sb);
        btnLink.setTypeface(FontFamily.raleway_sb);

        intent = new Intent(this, Lobby.class);

        ImageView loco = (ImageView) findViewById(R.id.imageView5);
        loco.setOnClickListener(new View.OnClickListener() {
            boolean isFirstLoco = false;
            @Override
            public void onClick(View v) {
                isFirstLoco = !isFirstLoco;
                int drawableID = isFirstLoco ? R.drawable.victorium_loco_1 : R.drawable.victorium_loco_1_2;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ((ImageView) findViewById(R.id.imageView5)).setImageDrawable(v.getContext().getDrawable(drawableID));
                } else {
                    ((ImageView) findViewById(R.id.imageView5)).setImageDrawable(ContextCompat.getDrawable(v.getContext(), drawableID));
                }
            }
        });

        ImageView pic = (ImageView) findViewById(R.id.imageView2);
        pic.setOnClickListener(new View.OnClickListener() {
            boolean isFirstLoco = false;
            @Override
            public void onClick(View v) {
                isFirstLoco = !isFirstLoco;
                int drawableID = isFirstLoco ? R.mipmap.appico_m_r : R.mipmap.appico_w_b;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ((ImageView) findViewById(R.id.imageView2)).setImageDrawable(v.getContext().getDrawable(drawableID));
                } else {
                    ((ImageView) findViewById(R.id.imageView2)).setImageDrawable(ContextCompat.getDrawable(v.getContext(), drawableID));
                }
            }
        });

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Lobby.getMyLocalIP() == null)
                    Toast.makeText(getApplicationContext(), "Connect to the internet!", Toast.LENGTH_SHORT).show();
                else{
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        Button b1 = (Button) findViewById(R.id.BtnCreateGame);
                        ActivityOptionsCompat optionsCompat = setAO(b1);
                        startActivity(intent, optionsCompat.toBundle());
                    }
                    else{
                        startActivity(intent);
                    }

                    System.out.println("User name is: '" + tvUN.getText().toString() + "'");

                    Lobby myLobby = new Lobby();
                    myLobby.setConfig(true, tvUN.getText().toString());
                }

            }
        });

        btnFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Button b1 = (Button) findViewById(R.id.BtnFindGame);
                    ActivityOptionsCompat optionsCompat = setAO(b1);
                    startActivity(intent, optionsCompat.toBundle());
                }
                else{
                    startActivity(intent);
                }

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
