package com.MonoCycleStudios.team.victorium.Game;

import com.MonoCycleStudios.team.victorium.Game.Enums.QuestionType;

import java.io.Serializable;

public class Question implements Serializable {

    private static final long serialVersionUID = 669933L;

    private final QuestionType questionType;
    private final String questionBody;
    private final String[] Answers = new String[4];
    private final int rightAnswers;

    Question(QuestionType questionType, String questionBody, String[] Answers, int rightAnswers){
        this.questionType = questionType;
        this.questionBody = questionBody;
        System.arraycopy(Answers, 0, this.Answers, 0, Answers.length);
        this.rightAnswers = rightAnswers;

    }

    public String getQuestion() {
        return questionBody;
    }

    public String[] getAnswers() {
        return Answers;
    }

    public boolean checkAnswers(int userAnswer) {
        return Answers[rightAnswers].equals(userAnswer);
    }
}
