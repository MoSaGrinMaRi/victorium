package com.MonoCycleStudios.team.victorium.Game.Fragments;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.MonoCycleStudios.team.victorium.R;

public class Notifyer extends Fragment {

    public void setNotifyText(String strToNotify) {
        b1.setText(strToNotify);
    }

    Button b1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_notify, container, false);

        b1 = (Button) view.findViewById(R.id.notifyBtn);

        return view;
    }

}
