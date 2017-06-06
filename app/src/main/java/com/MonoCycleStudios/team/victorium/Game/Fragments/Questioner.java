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

    }

    Button b1;
    Button b2;
    Button b3;
    Button b4;
    TextView tv;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_notify, container, false);

        tv = (TextView) view.findViewById(R.id.textView);
        b1 = (Button) view.findViewById(R.id.button1);
        b2 = (Button) view.findViewById(R.id.button2);
        b3 = (Button) view.findViewById(R.id.button3);
        b4 = (Button) view.findViewById(R.id.button4);

        return view;
    }

}
