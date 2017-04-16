package com.MonoCycleStudios.team.victorium.Game;

import com.MonoCycleStudios.team.victorium.Game.Enums.QuestionType;

public class Question {
    private final QuestionType questionType;
    private final String questionBody;
    private final String[] Answers = new String[4];
    private final int rightAnswers;

    Question(QuestionType questionType, String questionBody, String[] Answers, int rightAnswers){
        this.questionType = questionType;
        this.questionBody = questionBody;
        for(int i = 0; i < Answers.length; i++) {
            this.Answers[i] = Answers[i];
        }
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
