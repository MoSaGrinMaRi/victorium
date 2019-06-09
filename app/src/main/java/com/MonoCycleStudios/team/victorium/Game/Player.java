package com.MonoCycleStudios.team.victorium.Game;

import android.support.annotation.NonNull;

import com.MonoCycleStudios.team.victorium.Connection.Client;
import com.MonoCycleStudios.team.victorium.Game.Enums.GameState;
import com.MonoCycleStudios.team.victorium.Game.Enums.PlayerState;

import java.io.Serializable;

public class Player implements Serializable, Comparable{

    private static final long serialVersionUID = 666633L;

    private int playerID;
    private String playerName;
    private transient Client playerClient;
    private Character playerCharacter;
    private transient Game playerGame;
    private GameState playerGameState = GameState.NONE;
    private PlayerState playerState = PlayerState.NONE;
    private int score = 0;
    //private int inGame; -1 for 'offline'
    //                     0 for 'online'
    //                     1 for 'ingame'


    public Player(int playerID, String playerName, Client playerClient, Character playerCharacter, Game playerGame) {
        this.playerID = playerID;
        this.playerName = playerName;
        this.playerClient = playerClient;
        this.playerCharacter = playerCharacter;
        this.playerGame = playerGame;
        playerState = PlayerState.IDLE;
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
    public PlayerState getPlayerState() {
        return playerState;
    }
    public int getPlayerScore() {
        return score;
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
    public void setPlayerState(PlayerState playerState) {
        if(this.playerState != PlayerState.DEFEAT || playerState == PlayerState.NONE)
            this.playerState = playerState;
    }
    public void setPlayerScore(int score) {
        this.score = score;
    }

    public void updatePlayerScore(int score){
        this.score += score;
    }

    @Override
    public boolean equals(Object obj){
        boolean sameObj = false;

        if (obj != null && obj instanceof Player)
            if(sameObj = this.playerID == ((Player)obj).playerID)
                sameObj = this.playerName.equalsIgnoreCase(((Player)obj).playerName);

        return sameObj;
    }

    public int hashCode() {
        return java.util.Objects.hashCode(playerID);
    }

    @Override
    public String toString() {
        return playerName + ", " + playerID + ", " + super.toString();
    }

    @Override
    public int compareTo(@NonNull Object o) {
        return this.getPlayerID() - ((Player)o).getPlayerID();
    }
}
