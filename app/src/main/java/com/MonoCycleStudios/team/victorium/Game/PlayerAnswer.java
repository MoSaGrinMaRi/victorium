package com.MonoCycleStudios.team.victorium.Game;

public class PlayerAnswer {
    private Question question;
    private int playerAnswer = -1;

    PlayerAnswer(Question question, int playerAnswer){
        this.question = question;
        this.playerAnswer = playerAnswer;
    }
}
