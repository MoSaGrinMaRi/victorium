package com.MonoCycleStudios.team.victorium.Game.Fragments;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.MonoCycleStudios.team.victorium.R;

import java.lang.reflect.Field;

public class Notifier extends Fragment {

    public void setNotifyText(String strToNotify) {
        txtToShowUp = strToNotify;
    }

    String txtToShowUp;
    Button b1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_notify, container, false);

        b1 = (Button) view.findViewById(R.id.notifyBtn);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (txtToShowUp != null) {
            b1.setText(txtToShowUp);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);

        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

}
