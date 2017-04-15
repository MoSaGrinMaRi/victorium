package com.MonoCycleStudios.team.victorium.Game;

import com.MonoCycleStudios.team.victorium.Connection.Client;

public class Player {
    private String playerName;
    private int playerID;
    private Client playerClient;
    private Character playerCharacter;
    //private int inGame; -1 for 'offline'
    //                     0 for 'online'
    //                     1 for 'ingame'

    Player(Client playerClient, int playerID, String playerName, Character playerCharacter)
    {
        this.playerClient = playerClient;
        this.playerID = playerID;
        this.playerName = playerName;
        this.playerCharacter = playerCharacter;

    }
}
