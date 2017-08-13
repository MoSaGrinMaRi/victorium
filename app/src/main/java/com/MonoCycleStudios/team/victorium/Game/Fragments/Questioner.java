package com.MonoCycleStudios.team.victorium.Game.Fragments;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.MonoCycleStudios.team.victorium.Game.Enums.GameFragments;
import com.MonoCycleStudios.team.victorium.Game.Game;
import com.MonoCycleStudios.team.victorium.Game.Question;
import com.MonoCycleStudios.team.victorium.R;

public class Questioner extends Fragment {

    public void setQuestionDisplay(Question questionDisplay) {
        questionToShow = questionDisplay;
    }
    public void setIsVisible(boolean isVisible){
        this.isVisible = isVisible;
    }

    Question questionToShow;

    Button b1;
    Button b2;
    Button b3;
    Button b4;
    TextView tv;
    LinearLayout ll;
    boolean isVisible = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_question, container, false);

        ll = (LinearLayout) view.findViewById(R.id.llQuestion);
        tv = (TextView) view.findViewById(R.id.textView);
        b1 = (Button) view.findViewById(R.id.button1);
        b2 = (Button) view.findViewById(R.id.button2);
        b3 = (Button) view.findViewById(R.id.button3);
        b4 = (Button) view.findViewById(R.id.button4);

        b4.setOnClickListener(new View.OnClickListener(){   //  TEMP !!!
            @Override
            public void onClick (View v){
                Game.getInstance().showFragment(GameFragments.NONE, GameFragments.QUESTION);
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if(isVisible) {
            if (questionToShow != null) {
                tv.setText(questionToShow.getQuestion());
                b1.setText(questionToShow.getAnswers()[0]);
                b2.setText(questionToShow.getAnswers()[1]);
                b3.setText(questionToShow.getAnswers()[2]);
                b4.setText(questionToShow.getAnswers()[3]);
            }
        }else{
            ll.setVisibility(View.GONE);
        }
    }

}
