package com.workshop.translationworkshop.gms;

public class SpritePoint {
    public short x, y;
    public SpritePoint(short x, short y) {
        this.x = x;
        this.y = y;
    }
    public SpritePoint(int x, int y) {
        this.x = (short) x;
        this.y = (short) y;
    }

    public SpritePoint clone() {
        return new SpritePoint(x, y);
    }

    public String toString() {
        return "(" + x + "," + y + ")";
    }

}
