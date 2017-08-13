package com.MonoCycleStudios.team.victorium.Game.Fragments;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.MonoCycleStudios.team.victorium.R;

public class Alert extends Fragment {

    public void setAlertText(String strToNotify) {
        txtToShowUp = strToNotify;
    }
    public void setIsVisible(boolean isVisible){
        this.isVisible = isVisible;
    }

    String txtToShowUp;
    TextView tv;
    boolean isVisible = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_alert, container, false);

        tv = (TextView) view.findViewById(R.id.alertText);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if(isVisible) {
            if (txtToShowUp != null) {
                tv.setText(txtToShowUp);
            }
        }else{
            tv.setVisibility(View.GONE);
        }
    }

}