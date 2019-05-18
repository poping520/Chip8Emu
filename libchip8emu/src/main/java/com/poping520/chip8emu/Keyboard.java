package com.poping520.chip8emu;

/**
 * @author WangKZ
 * create on 2019/5/18 9:24
 */
public final class Keyboard {

    /* chip8 有 16 个按键 */
    private static final int CHIP8_KEY_NUM = 16;

    private boolean[] mKeys;

    Keyboard() {
        mKeys = new boolean[CHIP8_KEY_NUM];
    }

    boolean isKeyPressed(int key) {
        if (key > CHIP8_KEY_NUM) {
            return false;
        }
        return mKeys[key];
    }
}
