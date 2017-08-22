package com.MonoCycleStudios.team.victorium.Game;

import com.MonoCycleStudios.team.victorium.Game.Enums.QuestionCategory;
import com.MonoCycleStudios.team.victorium.Game.Enums.QuestionType;

import java.io.Serializable;

public class Question implements Serializable {

    private static final long serialVersionUID = 669933L;

    private final QuestionType questionType;
    private final QuestionCategory questionCategory;
    private final String questionBody;
    private final String[] Answers = new String[4];
    private final int rightAnswer;

    Question(QuestionType questionType, QuestionCategory questionCategory, String questionBody, String[] Answers, int rightAnswer){
        this.questionType = questionType;
        this.questionCategory = questionCategory;
        this.questionBody = questionBody;
        System.arraycopy(Answers, 0, this.Answers, 0, Answers.length);
        this.rightAnswer = rightAnswer;

    }

    public String getQuestion() {
        return questionBody;
    }

    public QuestionType getQuestionType() {
        return questionType;
    }

    public String[] getAnswers() {
        return Answers;
    }

    public boolean checkAnswers(int userAnswer) {   //  !!! Should we support shuffled answers?
        if(userAnswer >= 0 && userAnswer < 4)
            return (rightAnswer == userAnswer);
        else
            return false;
    }
}
