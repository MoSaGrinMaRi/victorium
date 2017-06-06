package com.MonoCycleStudios.team.victorium.Game.Enums;

public enum GameCommandType {
    NONE("[none]"),
    URGAMESTATUS("[urGameStatus]"),
    WAITFORPLAYERS("[waitForPlayers]"),
    EXECUTESTART("[executeStart]");

    private final String stringValue;

    GameCommandType(final String s) {
        stringValue = s;
    }
    public String getStringValue(){
        return this.stringValue;
    }
    public static GameCommandType getTypeOf(String command) {
        for (GameCommandType i : GameCommandType.values()) {
            System.out.println("(E)[CT]" + command + " =?= " + i.stringValue.toLowerCase() + " -=- " + command.equalsIgnoreCase(i.stringValue.toLowerCase()) + i);
            if(command.equalsIgnoreCase(i.stringValue.toLowerCase()))
                return i;
        }
        return NONE;
    }
}
