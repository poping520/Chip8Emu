package com.poping520.chip8emu;

/**
 * @author poping520
 * create on 2019/5/16 15:48
 */
public final class Memory {

    /* 4KB 内存 */
    private static final int CHIP8_MEMORY_SIZE = 4096;

    private byte[] mMemory;

    public Memory() {
        mMemory = new byte[CHIP8_MEMORY_SIZE];
    }
}
