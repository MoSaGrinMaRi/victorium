package com.MonoCycleStudios.team.victorium.Game;

import com.MonoCycleStudios.team.victorium.Game.Enums.CharacterColor;
import com.MonoCycleStudios.team.victorium.Game.Enums.CharacterType;

import java.io.Serializable;

public class Character implements Serializable{

    private static final long serialVersionUID = 666632L;

    private final CharacterType type;
    private final CharacterColor color;

    public Character(CharacterType type, CharacterColor color) {
        this.type = type;
        this.color = color;
    }

    public Character(int id) {
        CharacterType ct = CharacterType.NONE;
        CharacterColor cc = CharacterColor.NONE;
        switch(id){
            case 0:{
                ct = CharacterType.COSSACK;
                cc = CharacterColor.RED;
            }break;
            case 1:{
                ct = CharacterType.WIZARD;
                cc = CharacterColor.BLUE;
            }break;
            case 2:{
                ct = CharacterType.WARRIOR;
                cc = CharacterColor.ORANGE;
            }break;

            case 3:{
                ct = CharacterType.COSSACK;
                cc = CharacterColor.GREEN;
            }break;
            case 4:{
                ct = CharacterType.WIZARD;
                cc = CharacterColor.BLACK;
            }break;
            case 5:{
                ct = CharacterType.WARRIOR;
                cc = CharacterColor.PURPULE;
            }break;
        }
        this.type = ct;
        this.color = cc;
    }

    public CharacterColor getColor() {
        return color;
    }

}
