package com.poping520.chip8emu;

/**
 * @author poping520
 * create on 2019/5/16 15:48
 */
public final class Memory {

    /* 4KB 内存 */
    private static final int CHIP8_MEMORY_SIZE = 4096;

    /* chip 的内存 */
    private short[] mMemArr;

    /* chip 程序指针起始地址 */
    private final int mPCStart;

    Memory(int pcStart) {
        this.mPCStart = pcStart;
        mMemArr = new short[CHIP8_MEMORY_SIZE];
    }

    /* 加载 ROM 数据 到 内存数组中 */
    void loadRomIntoMemory(byte[] romBytes) throws Chip8Exception {
        int offset = mPCStart;

        if ((romBytes.length + offset) > CHIP8_MEMORY_SIZE)
            throw new Chip8Exception("ERROR ROM");

        for (byte romByte : romBytes) {
            write(romByte, offset);
            ++offset;
        }
    }

    /* 向内存 addr 地址处写入 b */
    void write(byte b, int addr) {
        mMemArr[addr] = (short) (b & 0xFF);
    }

    /* 从内存 addr 地址处读出数据 */
    short read(int addr) {
        return mMemArr[addr];
    }
}
