package com.workshop.translationworkshop.gms;

public class ReplacePointer {
    public int address, value;
    public boolean isShort = false;

    public ReplacePointer(int address, int value) {
        this.address = address;
        this.value = value;
    }
    public ReplacePointer(int address, int value, boolean isShort) {
        this.address = address;
        this.value = value;
        this.isShort = isShort;
    }
}
