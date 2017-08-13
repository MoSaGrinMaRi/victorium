package com.MonoCycleStudios.team.victorium.Game.Fragments;
import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.MonoCycleStudios.team.victorium.Game.Player;
import com.MonoCycleStudios.team.victorium.Game.Question;
import com.MonoCycleStudios.team.victorium.R;

import java.util.ArrayList;

public class QueueTurners extends Fragment {

    public void setPlayerArrayList(ArrayList<Player> playerArrayList) {
        this.playerArrayList = playerArrayList;
    }

    ArrayList<Player> playerArrayList;

    LinearLayout ll;
    ArrayList<Button> buttonArrayList = new ArrayList<>();

    int cardIndex = -1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_queueturns, container, false);

        ll = (LinearLayout) view.findViewById(R.id.pqtLinearLayout);
        System.out.println("=-=-=" + playerArrayList.size());
        for(int i = 1; i <= 23; i++){   // TEMP!!! 23 GLOBAL VAR REGION AMOUNT(25) - 2
//            System.out.println("=-=1-=");
            Button tmpBtn = (Button) view.findViewWithTag("pqtBtn"+i);
//            System.out.println("=-=2-="+tmpBtn);
            buttonArrayList.add(tmpBtn);
        }
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        for(int i = 23; i > playerArrayList.size(); i--) {  // TEMP!!! 23 GLOBAL VAR REGION AMOUNT(25) - 2
            buttonArrayList.get(i-1).setVisibility(View.GONE);
            buttonArrayList.remove(i-1);
        }
        for(int i = 0; i < playerArrayList.size(); i++){
            buttonArrayList.get(i).setBackgroundColor(playerArrayList.get(i).getPlayerCharacter().getColor().getARGB());
        }
        showNextCard();
    }

    public int getCardIndex(){
        return cardIndex;
    }

    public void showNextCard(){
        System.out.println("=+-" + cardIndex);
        cardIndex++;
        Animator set = AnimatorInflater.loadAnimator(getActivity(), R.animator.card_hide);
        if(cardIndex > 0) {
            set.setTarget(buttonArrayList.get(cardIndex-1));
            set.start();
        }
        if(cardIndex < buttonArrayList.size()-1) {
            set = AnimatorInflater.loadAnimator(getActivity(), R.animator.card_showup);
            set.setTarget(buttonArrayList.get(cardIndex));
            set.start();
        }
    }

}
