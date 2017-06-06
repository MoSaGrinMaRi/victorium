package com.MonoCycleStudios.team.victorium.Game;

import com.MonoCycleStudios.team.victorium.Connection.Client;
import com.MonoCycleStudios.team.victorium.Game.Enums.GameState;

import java.io.Serializable;

public class Player implements Serializable {

    private static final long serialVersionUID = 666633L;

    private int playerID;
    private String playerName;
    private Client playerClient;
    private Character playerCharacter;
    private Game playerGame;
    private GameState playerGameState = GameState.NONE;
    //private int inGame; -1 for 'offline'
    //                     0 for 'online'
    //                     1 for 'ingame'


    public Player(int playerID, String playerName, Client playerClient, Character playerCharacter, Game playerGame) {
        this.playerID = playerID;
        this.playerName = playerName;
        this.playerClient = playerClient;
        this.playerCharacter = playerCharacter;
        this.playerGame = playerGame;
    }

    public int getPlayerID() {
        return playerID;
    }
    public String getPlayerName() {
        return playerName;
    }
    public Client getPlayerClient() {
        return playerClient;
    }
    public Character getPlayerCharacter() {
        return playerCharacter;
    }
    public Game getPlayerGame() {
        return Game.getInstance();
//        return playerGame;
    }
    public GameState getPlayerGameState() {
        return playerGameState;
    }

    public void setPlayerClient(Client playerClient) {
        this.playerClient = playerClient;
    }
    public void setPlayerGame(Game playerGame) {
        this.playerGame = playerGame;
    }
    public void setPlayerGameState(GameState playerGameState) {
        this.playerGameState = playerGameState;
    }


}
