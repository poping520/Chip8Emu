package com.poping520.chip8emu;


/**
 * @author WangKZ
 * create on 2019/5/17 18:03
 */
public final class Display {


    private static final int WIDTH_X = 64;

    private static final int HEIGHT_Y = 32;

    /* chip8 屏幕的像素点
     *  __ __ __ __ __ __ __ __ __ __
     * |                             |
     * |                             |
     * |                             | Y
     * |                             |
     * |__ __ __ __ __ __ __ __ __ __|
     *               X
     */
    private boolean[][] mPixels;

    Display() {
        mPixels = new boolean[WIDTH_X][HEIGHT_Y];
    }

    void clear() {
        for (boolean[] ws : mPixels) {
            for (int i = 0; i < HEIGHT_Y; i++) {
                ws[i] = false;
            }
        }
    }

    void setPixel(int x, int y, boolean on) {
        mPixels[x][y] = on;
    }
}
