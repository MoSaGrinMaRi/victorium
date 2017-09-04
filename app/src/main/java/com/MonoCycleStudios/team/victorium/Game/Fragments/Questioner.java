package com.MonoCycleStudios.team.victorium.Game.Fragments;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.MonoCycleStudios.team.victorium.Connection.Client;
import com.MonoCycleStudios.team.victorium.Game.Enums.GameFragments;
import com.MonoCycleStudios.team.victorium.Game.Enums.PlayerState;
import com.MonoCycleStudios.team.victorium.Game.Enums.QuestionCategory;
import com.MonoCycleStudios.team.victorium.Game.Game;
import com.MonoCycleStudios.team.victorium.Game.GameRule;
import com.MonoCycleStudios.team.victorium.Game.Player;
import com.MonoCycleStudios.team.victorium.Game.Question;
import com.MonoCycleStudios.team.victorium.R;
import com.MonoCycleStudios.team.victorium.widget.MyCountDownTimer;

import java.util.HashMap;
import java.util.Map;

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
    ImageView civ;
    ImageView liv;
    ImageView riv;
    ProgressBar lpb;
    ProgressBar rpb;
    LinearLayout ll;
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

        ll = (LinearLayout) view.findViewById(R.id.llQuestionWrapper);
        lpb = (ProgressBar) view.findViewById(R.id.leftProgressBarFlipped);
        rpb = (ProgressBar) view.findViewById(R.id.rightProgressBarFlipped);
        tv = (TextView) view.findViewById(R.id.textView);
        buttons[0] = (Button) view.findViewById(R.id.button1);
        buttons[1] = (Button) view.findViewById(R.id.button2);
        buttons[2] = (Button) view.findViewById(R.id.button3);
        buttons[3] = (Button) view.findViewById(R.id.button4);
        civ = (ImageView) view.findViewById(R.id.categoryImg);
        liv = (ImageView) view.findViewById(R.id.leftImgView);
        riv = (ImageView) view.findViewById(R.id.rightImgView);

        buttons[0].setOnClickListener(new View.OnClickListener(){   //  TEMP !!!
            @Override
            public void onClick (View v){
                GameRule.check(Client.getInstance().iPlayer,"chose answer",0+":;"+mcdt.currentMillis);
//                gd.setCornerRadii(new float[]{30, 30, 30, 30, 0, 0, 30, 30});
                buttons[0].setBackgroundResource(R.drawable.shape_tablerow_image);
                GradientDrawable gd = (GradientDrawable) buttons[0].getBackground().getCurrent();
                gd.setStroke(12, Client.getInstance().iPlayer.getPlayerCharacter().getColor().getARGB());

                setAllButtonInactive();
            }
        });
        buttons[1].setOnClickListener(new View.OnClickListener(){   //  TEMP !!!
            @Override
            public void onClick (View v){
                GameRule.check(Client.getInstance().iPlayer,"chose answer",1+":;"+mcdt.currentMillis);
                buttons[1].setBackgroundResource(R.drawable.shape_tablerow_image);
                GradientDrawable gd = (GradientDrawable) buttons[1].getBackground().getCurrent();
                gd.setStroke(12, Client.getInstance().iPlayer.getPlayerCharacter().getColor().getARGB());

                setAllButtonInactive();
            }
        });
        buttons[2].setOnClickListener(new View.OnClickListener(){   //  TEMP !!!
            @Override
            public void onClick (View v){
                GameRule.check(Client.getInstance().iPlayer,"chose answer",2+":;"+mcdt.currentMillis);
                buttons[2].setBackgroundResource(R.drawable.shape_tablerow_image);
                GradientDrawable gd = (GradientDrawable) buttons[2].getBackground().getCurrent();
                gd.setStroke(12, Client.getInstance().iPlayer.getPlayerCharacter().getColor().getARGB());

                setAllButtonInactive();
            }
        });
        buttons[3].setOnClickListener(new View.OnClickListener(){   //  TEMP !!!
            @Override
            public void onClick (View v){
                GameRule.check(Client.getInstance().iPlayer,"chose answer",3+":;"+mcdt.currentMillis);
                buttons[3].setBackgroundResource(R.drawable.shape_tablerow_image);
                GradientDrawable gd = (GradientDrawable) buttons[3].getBackground().getCurrent();
                gd.setStroke(12, Client.getInstance().iPlayer.getPlayerCharacter().getColor().getARGB());

                setAllButtonInactive();
            }
        });

        isCreated = true;

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if(isVisible) {
            if (questionToShow != null) {

                civ.setImageBitmap(getCategoryImg(questionToShow.getQuestionCategory()));
                tv.setText(questionToShow.getQuestion());
                buttons[0].setText(questionToShow.getAnswers()[0]);
                buttons[1].setText(questionToShow.getAnswers()[1]);
                buttons[2].setText(questionToShow.getAnswers()[2]);
                buttons[3].setText(questionToShow.getAnswers()[3]);
                if(fighters != null) {
                    liv.setColorFilter(fighters[0].getPlayerCharacter().getColor().getARGB());
                    riv.setColorFilter(fighters[1].getPlayerCharacter().getColor().getARGB());
                }
                if(!isInteractive){
                    setAllButtonInactive();
                }
            }
        }else{
            ll.setVisibility(View.GONE);
        }

        if(questionToShow != null)
            setTimer(15000, 20);    //  TEMP !!! 15sec to answer the question
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
                buttons[i].setHeight(buttons[i].getHeight() + 50);
                buttons[i].setBackgroundResource(R.drawable.question_bg_rightanswer);
            }else {
                buttons[i].setBackgroundResource(R.drawable.question_bg_placeholder);
            }

            GradientDrawable drawBord = (GradientDrawable) buttons[i].getBackground().getCurrent();
            GradientDrawable[] layers = {
                    drawBord
            };
            LayerDrawable layerDrawable = new LayerDrawable(layers);
            layerDrawable.setLayerInset(0, 0, 0, 0, 0);
            buttons[i].setBackground(layerDrawable);
        }

        for (Map.Entry entry: answers.entrySet()) {
            Player key = (Player) entry.getKey();
            Integer value = (Integer) entry.getValue();
            System.out.println(key + " ; " + value);

            LayerDrawable curr = (LayerDrawable) buttons[value].getBackground().getCurrent();

            buttons[value].setBackgroundResource(R.drawable.question_answer_chosed);
            GradientDrawable newLayer = (GradientDrawable) buttons[value].getBackground().getCurrent();

            switch(curr.getNumberOfLayers()){
                case 1:{

                    newLayer.setStroke(12, key.getPlayerCharacter().getColor().getARGB());

                    buttons[value].setBackground(new LayerDrawable(new Drawable[]{
                            curr.getDrawable(0),
                            newLayer,
                    }));
                }break;
                case 2:{

                    newLayer.setStroke(12, key.getPlayerCharacter().getColor().getARGB(), 150, 150);

                    buttons[value].setBackground(new LayerDrawable(new Drawable[]{
                            curr.getDrawable(0),
                            curr.getDrawable(1),
                            newLayer,
                    }));
                }break;
            }
        }
    }


    void updateProgressBars(long currentMillis){
        if(isLeftPBUpdate)
            lpb.setProgress((int)((currentMillis / (mcdt.askedMillis * 1.0))*100));
        if(isRightPBUpdate)
            rpb.setProgress((int)((currentMillis / (mcdt.askedMillis * 1.0))*100));
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
