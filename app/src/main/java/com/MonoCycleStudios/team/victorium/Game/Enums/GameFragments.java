package com.MonoCycleStudios.team.victorium.Game.Enums;

public enum GameFragments {
    NONE("[none]"),
    GROUND_EVENT("[groundEvent]"),
    ALERT("[alert]"),
    NOTIFY("[waitForPlayers]"),
    QUESTION("[question]"),
    QUEUETURNS("[queueTurns]"),
    TIMER("[timer]");

    private final String stringValue;

    GameFragments(final String s) {
        stringValue = s;
    }
    public String getStr(){
        return this.stringValue;
    }
    public static GameFragments getTypeOf(String command) {
        for (GameFragments i : GameFragments.values()) {
            System.out.println("(E)[CT]" + command + " =?= " + i.stringValue.toLowerCase() + " -=- " + command.equalsIgnoreCase(i.stringValue.toLowerCase()) + i);
            if(command.equalsIgnoreCase(i.stringValue.toLowerCase()))
                return i;
        }
        return NONE;
    }
}
