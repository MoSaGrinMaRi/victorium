package com.MonoCycleStudios.team.victorium.Connection.Enums;

public enum CommandType {
    NONE("[none]"),
    RDATA("[rData]"),
    STARTGAME("[startGame]"),
    PLAYERSDATA("[playersData]"),
    NEWPLAYER("[newPlayer]"),
    GAMEDATA("[gameData]"),
    PING("[ping]");

    private final String stringValue;

    CommandType(final String s) {
        stringValue = s;
    }
    public String getStr(){
        return this.stringValue;
    }
    public static CommandType getTypeOf(String command) {
        for (CommandType i : CommandType.values()) {
//            System.out.println("(E)[CT]" + command + " =?= " + i.stringValue.toLowerCase() + " -=- " + command.equalsIgnoreCase(i.stringValue.toLowerCase()) + i);
            if(command.equalsIgnoreCase(i.stringValue.toLowerCase()))
                return i;
        }
        return NONE;
    }
}