package com.MonoCycleStudios.team.victorium;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.MonoCycleStudios.team.victorium.Connection.Lobby;

public class MainActivity extends AppCompatActivity {

    EditText tvUN;
    Intent intent;
    Spinner spinner;
    String selected;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvUN = (EditText)findViewById(R.id.UserNickname);

        spinner = (Spinner) findViewById(R.id.ConnectionType);
        selected = spinner.getSelectedItem().toString();
        Toast.makeText(getApplicationContext(), selected, Toast.LENGTH_SHORT).show();

        Button b = (Button)findViewById(R.id.ConnectBtn);

        intent = new Intent(this, Lobby.class);

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selected = spinner.getSelectedItem().toString();

                startActivity(intent);
                System.out.println("User name is: '" + tvUN.getText().toString()+"'");

                Lobby myLobby = new Lobby();
                if(selected.equalsIgnoreCase("server")) {
                    myLobby.setConfig(true, tvUN.getText().toString());

                }else {
                    myLobby.setConfig(false, tvUN.getText().toString());
                }
            }
        });

    }
}
