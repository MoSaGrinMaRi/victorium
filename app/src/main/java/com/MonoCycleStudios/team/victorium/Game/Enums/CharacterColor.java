package com.MonoCycleStudios.team.victorium.Game.Enums;

public enum CharacterColor{
    NONE(128, 255, 255),
    RED(200, 0, 0),
    BLUE(0, 102, 204),
    GREEN(51, 204, 0),
    ORANGE(240, 150, 30),
    PURPLE(102, 51, 153),
    BLACK(34, 34, 34),
    WHITE(255, 255, 255);

    private final int r;
    private final int g;
    private final int b;
    private final String rgb;

    CharacterColor(final int r,final int g,final int b) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.rgb = r + ", " + g + ", " + b;
    }

    public String getRGB() {
        return rgb;
    }

    public int getARGB(){
        return 0xFF000000 | ((r << 16) & 0x00FF0000) | ((g << 8) & 0x0000FF00) | b;
    }
}
