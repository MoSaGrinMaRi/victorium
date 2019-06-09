package com.MonoCycleStudios.team.victorium.Game.Fragments;
import android.app.Fragment;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.MonoCycleStudios.team.victorium.Connection.Client;
import com.MonoCycleStudios.team.victorium.Connection.Lobby;
import com.MonoCycleStudios.team.victorium.Game.Enums.GameFragments;
import com.MonoCycleStudios.team.victorium.Game.Enums.PlayerState;
import com.MonoCycleStudios.team.victorium.Game.Enums.QuestionCategory;
import com.MonoCycleStudios.team.victorium.Game.Game;
import com.MonoCycleStudios.team.victorium.Game.GameRule;
import com.MonoCycleStudios.team.victorium.Game.Player;
import com.MonoCycleStudios.team.victorium.Game.Question;
import com.MonoCycleStudios.team.victorium.R;
import com.MonoCycleStudios.team.victorium.widget.MyCountDownTimer;
import com.MonoCycleStudios.team.victorium.widget.Utils.MMSystem;

import java.util.HashMap;
import java.util.Map;

import static com.MonoCycleStudios.team.victorium.R.color.mWhite;

public class Questioner extends Fragment {

    public void setQuestionDisplay(Question questionDisplay) {
        questionToShow = questionDisplay;
    }
    public void setIsVisible(boolean isVisible){
        this.isVisible = isVisible;
    }
    public void setFighters(Player p1, Player p2){fighters = new Player[]{p1,p2};}
    public void setInteractive(PlayerState ps){if(ps.equals(PlayerState.SPEC))isInteractive = false;}

    Question questionToShow;
    Player[] fighters;

    Button[] buttons = new Button[4];
    TextView tv;
    TextView timertv;
//    ImageView civ;
    ImageView liv;
    ImageView riv;
    ProgressBar lpb;
//    ProgressBar rpb;
    RelativeLayout rlWrapper;
    boolean isVisible = true;
    boolean isInteractive = true;

    boolean isLeftPBUpdate = true;
    boolean isRightPBUpdate = true;

    public MyCountDownTimer mcdt;
    boolean isCreated = false;
    boolean started = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_question, container, false);

        rlWrapper = (RelativeLayout) view.findViewById(R.id.rlWrapper);
        lpb = (ProgressBar) view.findViewById(R.id.leftProgressBarFlipped);
        timertv = (TextView) view.findViewById(R.id.timerTv);
//        rpb = (ProgressBar) view.findViewById(R.id.rightProgressBarFlipped);
        tv = (TextView) view.findViewById(R.id.textView);
        buttons[0] = (Button) view.findViewById(R.id.button1);
        buttons[1] = (Button) view.findViewById(R.id.button2);
        buttons[2] = (Button) view.findViewById(R.id.button3);
        buttons[3] = (Button) view.findViewById(R.id.button4);
//        civ = (ImageView) view.findViewById(R.id.categoryImg);
        liv = (ImageView) view.findViewById(R.id.leftImgView);
        riv = (ImageView) view.findViewById(R.id.rightImgView);


        buttons[0].setOnClickListener(new View.OnClickListener(){   //  TEMP !!!
            @Override
            public void onClick (View v){
                selectAnswer(0);
            }
        });
        buttons[1].setOnClickListener(new View.OnClickListener(){   //  TEMP !!!
            @Override
            public void onClick (View v){
                selectAnswer(1);
            }
        });
        buttons[2].setOnClickListener(new View.OnClickListener(){   //  TEMP !!!
            @Override
            public void onClick (View v){
                selectAnswer(2);
            }
        });
        buttons[3].setOnClickListener(new View.OnClickListener(){   //  TEMP !!!
            @Override
            public void onClick (View v){
                selectAnswer(3);
            }
        });

        isCreated = true;

        return view;
    }

    private void selectAnswer(int index){
        GameRule.check(Client.getInstance().iPlayer,"chose answer",index+":;"+mcdt.currentMillis);
        int[] location = new int[2];
        int[] location2 = new int[2];
        buttons[index].getLocationOnScreen(location);
        if(fighters[0].equals(Client.getInstance().iPlayer)){
            liv.getLocationOnScreen(location2);
            liv.animate().translationX(location[0]-location2[0] + 60).translationY(location[1]-location2[1] + buttons[index].getHeight()/2 - liv.getHeight()/2).setDuration(300);
        }
        if(fighters[1].equals(Client.getInstance().iPlayer)) {
            riv.getLocationOnScreen(location2);
            riv.animate().translationX(location[0]-location2[0] - 60 + buttons[index].getWidth() - riv.getWidth()).translationY(location[1]-location2[1] + buttons[index].getHeight()/2 - riv.getHeight()/2).setDuration(300);
        }

        setAllButtonInactive();
    }

    @Override
    public void onStart() {
        super.onStart();
        if(isVisible) {
            if (questionToShow != null) {

//                civ.setImageBitmap(getCategoryImg(questionToShow.getQuestionCategory()));
                tv.setText(questionToShow.getQuestion());
                buttons[0].setText(questionToShow.getAnswers()[0]);
                buttons[1].setText(questionToShow.getAnswers()[1]);
                buttons[2].setText(questionToShow.getAnswers()[2]);
                buttons[3].setText(questionToShow.getAnswers()[3]);
                if(fighters != null) {
//                    liv.setColorFilter(fighters[0].getPlayerCharacter().getColor().getARGB());
//                    riv.setColorFilter(fighters[1].getPlayerCharacter().getColor().getARGB());
                    int frameWidth = 316;
                    int frameHeight = 316;
                    int frameCountX = 0;    //  MAX = 6; Only 6 player
                    for(int i = 0; i < 2; i++) {

                        switch (fighters[i].getPlayerCharacter().getColor()) {
                            case RED:
                                frameCountX = 0;
                                break;
                            case BLUE:
                                frameCountX = 1;
                                break;
                            case ORANGE:
                                frameCountX = 2;
                                break;
                            case GREEN:
                                frameCountX = 3;
                                break;
                            case BLACK:
                                frameCountX = 4;
                                break;
                            case PURPLE:
                                frameCountX = 5;
                                break;

                        }
                        if(i == 0) {
                            liv.setImageBitmap(Bitmap.createBitmap(Lobby.avatarAtlas,
                                    frameWidth * frameCountX,
                                    0,
                                    frameWidth,
                                    frameHeight));
                        }else{
                            riv.setImageBitmap(Bitmap.createBitmap(Lobby.avatarAtlas,
                                    frameWidth * frameCountX,
                                    0,
                                    frameWidth,
                                    frameHeight));
                        }
                    }


                }
                if(!isInteractive){
                    setAllButtonInactive();
                }
            }
        }else{
            rlWrapper.setVisibility(View.GONE);
        }

        if(questionToShow != null)
            setTimer(12000, 30);    //  TEMP !!! 12sec to answer the question
    }

    Bitmap getCategoryImg(QuestionCategory qc){
        int frameWidth = 128;
        int frameHeight = 128;
        int frameCountX = 0;    //  MAX = 9; Only 10 category(including None)

        switch (qc){
            case NONE:
                frameCountX = 0;
                break;
            case MATH:
                frameCountX = 1;
                break;
            case HISTORY:
                frameCountX = 2;
                break;
            case FILM:
                frameCountX = 3;
                break;
            case GEOGRAPHY:
                frameCountX = 4;
                break;
            case SPORT:
                frameCountX = 5;
                break;
            case MUSIC:
                frameCountX = 6;
                break;
            case LITERATURE:
                frameCountX = 7;
                break;
            case BIOLOGY:
                frameCountX = 8;
                break;
            case MEME:
                frameCountX = 9;
                break;
        }

        return Bitmap.createBitmap(Game.categoryAtlas,
                frameWidth * frameCountX,
                0,
                frameWidth,
                frameHeight);
    }

    void setAllButtonInactive(){
        buttons[0].setEnabled(false);
        buttons[1].setEnabled(false);
        buttons[2].setEnabled(false);
        buttons[3].setEnabled(false);
    }

    public void gotAnswer(Player p){
        if(fighters != null) {
            if(fighters[0].equals(p))
                isLeftPBUpdate = false;
            if(fighters[1].equals(p))
                isRightPBUpdate = false;
        }
    }

    public void updateView(HashMap<Player, Integer> answers, int rightAnswer){

        mcdt.cancel();
        for(int i = 0; i < buttons.length; i++){
            if(i == rightAnswer) {
//                buttons[i].setHeight(buttons[i].getHeight() + 50);
//                ContextCompat.getColor(getActivity(), R.color.mWhite);

                buttons[i].setTextColor(ContextCompat.getColor(getActivity(), R.color.mBlack));
                Drawable drawable = buttons[i].getBackground();
                drawable = DrawableCompat.wrap(drawable);
                DrawableCompat.setTint(drawable.mutate(), ContextCompat.getColor(getActivity(), R.color.mWhite));


//                buttons[i].getProgressDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN);
//                buttons[i].setColorFilter(ContextCompat.getColor(getActivity(), R.color.mWhite));
//                imageView.setColorFilter(ContextCompat.getColor(context, R.color.COLOR_YOUR_COLOR), android.graphics.PorterDuff.Mode.MULTIPLY);
//                buttons[i].setBackgroundResource(R.drawable.question_bg_rightanswer);
//            }else {
//                buttons[i].setBackgroundResource(R.drawable.question_bg_placeholder);
            }

//            GradientDrawable drawBord = (GradientDrawable) buttons[i].getBackground().getCurrent();
//            GradientDrawable[] layers = {
//                    drawBord
//            };
//            LayerDrawable layerDrawable = new LayerDrawable(layers);
//            layerDrawable.setLayerInset(0, 0, 0, 0, 0);
//            buttons[i].setBackground(layerDrawable);
        }

        int[] location = new int[2];
        int[] location2 = new int[2];
        for (Map.Entry entry: answers.entrySet()) {
            Player key = (Player) entry.getKey();
            Integer value = (Integer) entry.getValue();
            MMSystem.out.println(key + " ; " + value);


            buttons[value].getLocationOnScreen(location);
//            int x = location[0];
//            int y = location[1];
            if(fighters[0].equals(key) && !key.equals(Client.getInstance().iPlayer)){
                liv.getLocationOnScreen(location2);
                liv.animate().translationX(location[0]-location2[0] + 60).translationY(location[1]-location2[1] + buttons[value].getHeight()/2 - liv.getHeight()/2).setDuration(300);
            }
            if(fighters[1].equals(key) && !key.equals(Client.getInstance().iPlayer)) {
                riv.getLocationOnScreen(location2);
                riv.animate().translationX(location[0]-location2[0] - 60 + buttons[value].getWidth() - riv.getWidth()).translationY(location[1]-location2[1] + buttons[value].getHeight()/2  - riv.getHeight()/2).setDuration(300);
            }

//            LayerDrawable curr = (LayerDrawable) buttons[value].getBackground().getCurrent();

//            buttons[value].setBackgroundResource(R.drawable.question_answer_chosed);
//            GradientDrawable newLayer = (GradientDrawable) buttons[value].getBackground().getCurrent();

//            switch(curr.getNumberOfLayers()){
//                case 1:{
//
//                    newLayer.setStroke(12, key.getPlayerCharacter().getColor().getARGB());
//
//                    buttons[value].setBackground(new LayerDrawable(new Drawable[]{
//                            curr.getDrawable(0),
//                            newLayer,
//                    }));
//                }break;
//                case 2:{
//
//                    newLayer.setStroke(12, key.getPlayerCharacter().getColor().getARGB(), 150, 150);
//
//                    buttons[value].setBackground(new LayerDrawable(new Drawable[]{
//                            curr.getDrawable(0),
//                            curr.getDrawable(1),
//                            newLayer,
//                    }));
//                }break;
//            }
        }
    }


    void updateProgressBars(long currentMillis){
        if(isLeftPBUpdate || isRightPBUpdate) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                lpb.setProgress(100 - (int) ((currentMillis / (mcdt.askedMillis * 1.0)) * 100), true);
            }else{
                lpb.setProgress(100 - (int) ((currentMillis / (mcdt.askedMillis * 1.0)) * 100));
            }
            timertv.setText(""+(currentMillis/1000)+"s");
        }
//        if(isRightPBUpdate)
//            rpb.setProgress((int)((currentMillis / (mcdt.askedMillis * 1.0))*100));
    }

    public void setTimer(final long millisInFuture, final long countDownInterval){
        if(started){
            mcdt.cancel();
            started = false;
        }

        started = true;
        mcdt = new MyCountDownTimer(millisInFuture, countDownInterval);

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                mcdt.addOnUpdateHandler(new MyCountDownTimer.OnUpdateHandler(){

                    @Override
                    public void onTickUpdate(long currentMillis, int milli, int sec, int min) {
                        updateProgressBars(currentMillis);
                    }

                    @Override
                    public void onFinish() {
                        updateProgressBars(0);
                        started = false;
                        setAllButtonInactive();

                        if(fighters == null)
                            Game.getInstance().showFragment(GameFragments.NONE,GameFragments.QUESTION);
                    }
                });
                mcdt.start();
            }
        });
    }

    public void setTimer(MyCountDownTimer mcdt){
        this.mcdt = mcdt;
    }

    public void stopTimer(){
        mcdt.cancel();
    }

}
