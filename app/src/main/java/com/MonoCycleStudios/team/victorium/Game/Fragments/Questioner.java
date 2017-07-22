package com.MonoCycleStudios.team.victorium.Game.Fragments;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.MonoCycleStudios.team.victorium.Game.Question;
import com.MonoCycleStudios.team.victorium.R;

public class Questioner extends Fragment {

    public void setQuestionDisplay(Question questionDisplay) {
        questionToShow = questionDisplay;
    }

    Question questionToShow;

    Button b1;
    Button b2;
    Button b3;
    Button b4;
    TextView tv;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_question, container, false);

        tv = (TextView) view.findViewById(R.id.textView);
        b1 = (Button) view.findViewById(R.id.button1);
        b2 = (Button) view.findViewById(R.id.button2);
        b3 = (Button) view.findViewById(R.id.button3);
        b4 = (Button) view.findViewById(R.id.button4);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        // During startup, check if there are arguments passed to the fragment.
        // onStart is a good place to do this because the layout has already been
        // applied to the fragment at this point so we can safely call the method
        // below that sets the article text.
        Bundle args = getArguments();
        if (questionToShow != null) {
            tv.setText(questionToShow.getQuestion());
            b1.setText(questionToShow.getAnswers()[0]);
            b2.setText(questionToShow.getAnswers()[1]);
            b3.setText(questionToShow.getAnswers()[2]);
            b4.setText(questionToShow.getAnswers()[3]);
        }
    }

}
