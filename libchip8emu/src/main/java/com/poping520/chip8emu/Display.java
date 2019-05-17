package com.poping520.chip8emu;

/**
 * @author WangKZ
 * create on 2019/5/17 18:03
 */
public final class Display {

    private static final int WIDTH = 64;

    private static final int HEIGHT = 32;

    private boolean[] mBitMap;

    private Memory mMemory;

    public Display(Memory memory) {
        this.mMemory = memory;
    }

    public void clear() {

    }

    public void draw(int x, int y, int len) {

    }
}
