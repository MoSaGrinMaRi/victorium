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

// ALTER TABLE `category` AUTO_INCREMENT = 0;
//
// INSERT INTO `category`(`name`, `real_c_index`, `img_shift`) VALUES ('None', 0, 0);
// INSERT INTO `category`(`name`, `real_c_index`, `img_shift`) VALUES ('Math', 1, 1);
// INSERT INTO `category`(`name`, `real_c_index`, `img_shift`) VALUES ('History', 2, 2);
// INSERT INTO `category`(`name`, `real_c_index`, `img_shift`) VALUES ('Film', 3, 3);
// INSERT INTO `category`(`name`, `real_c_index`, `img_shift`) VALUES ('Geography', 4, 4);
// INSERT INTO `category`(`name`, `real_c_index`, `img_shift`) VALUES ('Sport', 5, 5);
// INSERT INTO `category`(`name`, `real_c_index`, `img_shift`) VALUES ('Music', 6, 6);
// INSERT INTO `category`(`name`, `real_c_index`, `img_shift`) VALUES ('Literature', 7, 7);
// INSERT INTO `category`(`name`, `real_c_index`, `img_shift`) VALUES ('Biology', 8, 8);
// INSERT INTO `category`(`name`, `real_c_index`, `img_shift`) VALUES ('Meme', 9, 9);
