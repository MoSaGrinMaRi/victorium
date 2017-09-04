package com.MonoCycleStudios.team.victorium.Game.Enums;

public enum QuestionCategory {
    NONE("none"),
    MATH("math"),
    HISTORY("history"),
    FILM("film"),
    GEOGRAPHY("geography"),
    SPORT("sport"),
    MUSIC("music"),
    LITERATURE("literature"),
    BIOLOGY("biology"),
    MEME("meme");

    private final String stringValue;

    QuestionCategory(final String s) {
        stringValue = s;
    }
    public String getStr(){
        return this.stringValue;
    }
    public static QuestionCategory getTypeOf(String command) {
        for (QuestionCategory i : QuestionCategory.values()) {
            System.out.println("(E)[QC]" + command + " =?= " + i.stringValue.toLowerCase() + " -=- " + command.equalsIgnoreCase(i.stringValue.toLowerCase()) + i);
            if(command.equalsIgnoreCase(i.stringValue.toLowerCase()))
                return i;
        }
        return NONE;
    }


}
