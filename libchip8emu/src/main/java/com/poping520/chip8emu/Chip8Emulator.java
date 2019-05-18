package com.poping520.chip8emu;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author poping520
 * create on 19/05/17 21:50
 */
public final class Chip8Emulator {

    /* chip8 内存 */
    private Memory mMemory;

    private Display mDisplay;

    private Keyboard mKeyboard;

    /* chip 处理器 */
    private CPU mCPU;

    public Chip8Emulator() {
        mMemory = new Memory(CPU.CHIP8_PROGRAM_COUNTER_START);
        mDisplay = new Display();
        mKeyboard = new Keyboard();
        mCPU = new CPU(mMemory, mDisplay, mKeyboard);
    }

    public void run() {
        mCPU.start();
    }

    /**
     * 载入 chip8 ROM 程序
     */
    public void loadRom(File file) throws IOException, Chip8Exception {
        InputStream is = new FileInputStream(file);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        while ((len = is.read(buffer)) != -1) {
            baos.write(buffer, 0, len);
        }
        baos.flush();
        mMemory.loadRomIntoMemory(baos.toByteArray());
        is.close();
        baos.close();
    }
}
