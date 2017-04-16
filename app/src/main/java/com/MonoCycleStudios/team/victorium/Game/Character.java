package com.MonoCycleStudios.team.victorium.Game;

import com.MonoCycleStudios.team.victorium.Game.Enums.CharacterColor;
import com.MonoCycleStudios.team.victorium.Game.Enums.CharacterType;

public class Character {

    private final CharacterType type;
    private final CharacterColor color;

    Character(CharacterType type, CharacterColor color) {
        this.type = type;
        this.color = color;
    }
}
