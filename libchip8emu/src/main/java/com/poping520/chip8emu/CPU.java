package com.poping520.chip8emu;

/**
 * @author poping520
 * create on 2019/5/16 14:39
 */
public final class CPU extends Thread {

    private Memory mMemory;

    private Display mDisplay;

    private Keyboard mKeyboard;

    /* Java 中的基本数字类型都是有符号类型 所以 以下变量所用的类型都比其所占的空间要大 */

    /* 数据寄存器数组 (Data Registers) 16 * 8-bit */
    private int[] v;

    /* 地址寄存器 - 保存内存地址索引 (Address Register) 16-bit*/
    private int index;

    /* 程序计数器 - 程序指针 (Program Counter Register) 16-bit */
    private int pc;

    private int delayTimer;

    private int soundTimer;

    /* chip8 程序从内存地址 0x200 开始 */
    static final int CHIP8_PROGRAM_COUNTER_START = 0x200;

    CPU(Memory memory, Display display, Keyboard keyboard) {
        this.mMemory = memory;
        this.mDisplay = display;
        this.mKeyboard = keyboard;
        reset();
    }

    @Override
    public void run() {
        while (isAlive()) {
            cycle();
        }
    }

    public void reset() {
        /* pc 指针置位 0x200 */
        pc = CHIP8_PROGRAM_COUNTER_START;

        /* chip-8 有 16 个数据寄存器 V0 ~ VF */
        v = new int[16];
    }

    /* cpu 循环 */
    private void cycle() {
        /* 读取一个指令 16-bit */
        short opcode = mMemory.readOpcode(pc);
        executeInstruction(opcode);
    }

    /*
     * 执行指令
     * chip-8 有 35 个指令, 每个指令 16-bit
     * https://en.wikipedia.org/wiki/CHIP-8#Opcode_table
     *
     * NNN: address
     * NN : 8-bit constant
     * N  : 4-bit constant
     * X/Y: 4-bit register id
     * PC : program counter
     * I  : 16-bit address register (void pointer)
     */
    private void executeInstruction(short opcode) {
        System.out.printf("%x\n", opcode);
        final int NNN = opcode & 0x0FFF;
        final short NN = (short) (opcode & 0x00FF);
        final byte X = (byte) ((opcode & 0X0F00) >> 8);
        final byte Y = (byte) ((opcode & 0X00F0) >> 4);

        /* pc 向后移两个字节 */
        pc += 2;

        /* 取出指令的前 4-bit */
        switch ((opcode & 0xF000) >> 12) {
            case 0x0:
                switch (opcode & 0x00FF) {
                    case 0xE0:
                        /* 00E0: Clears the screen. */
                        mDisplay.clear();
                        break;

                    case 0xEE:
                        /* 00EE: Returns from a subroutine. */
                        break;
                }
                break;

            case 0x1:
                /* 1NNN: Jumps to address NNN. */
                pc = NNN;
                break;

            case 0x2:
                /* 2NNN: Calls subroutine at NNN. */

                break;

            case 0x3:
                /* 3XNN: Skips the next instruction if VX equals NN. */
                /* 如果 VX的值 等于 NN 则跳过下一个指令 */
                if (v[X] == NN) pc += 2;
                break;

            case 0x4:
                /* 4XNN: Skips the next instruction if VX doesn't equal NN. */
                /* 如果 VX的值 不等于 NN 则跳过下一个指令 */
                if (v[X] != NN) pc += 2;
                break;

            case 0x5:
                /* 5XY0: Skips the next instruction if VX equals VY. */
                /* 如果 VX的值 等于 VY寄存器的值 则跳过下一个指令 */
                if (v[X] == v[Y]) pc += 2;
                break;

            case 0x6:
                /* 6XNN: Sets VX to NN. */
                /* 将 NN 保存到 VX */
                v[X] = NN;
                break;

            case 0x7:
                /* 7XNN: Adds NN to VX. */
                /* 将 VX的值 加上 NN 保存到 VX */
                v[X] += NN;
                break;

            case 0x8:
                switch (opcode & 0x000F) {
                    default:
                        break;
                    case 0x0:
                        /* 8XY0: Sets VX to the value of VY. */
                        v[X] = v[Y];
                        break;

                    case 0x1:
                        /* 8XY1: Sets VX to VX or VY. */
                        v[X] |= v[Y];
                        break;

                    case 0x2:
                        /* 8XY2: Sets VX to VX and VY. */
                        v[X] &= v[Y];
                        break;

                    case 0x3:
                        /* 8XY3: Sets VX to VX xor VY. */
                        v[X] ^= v[Y];
                        break;

                    case 0x4:
                        /* 8XY4: Adds VY to VX. VF is set to 1 when there's a carry, and to 0 when there isn't.
                         * 如果 (VX + VF) 溢出 (即大于 寄存器能储存的最大值 0xFF)
                         */
                        int sum = v[X] + v[Y];
                        v[0xF] = (sum > 0xFF) ? 1 : 0;
                        v[X] = sum & 0xFF;
                        break;

                    case 0x5:
                        /* 8XY5: VY is subtracted from VX. VF is set to 0 when there's a borrow, and 1 when there isn't.
                         */
                        int diff = v[X] - v[Y];
                        v[0xF] = (diff < 0) ? 0 : 1;
                        v[X] = diff & 0xFF;
                        break;

                    case 0x6:
                        /* 8XY6: Stores the least significant bit of VX in VF and then shifts VX to the right by 1.
                         */
                        v[X] = v[Y] >> 1;
                        break;

                    case 0x7:

                        break;

                    case 0xE:

                        break;

                }
                break;

            case 9:
                /* 9XY0: Skip next instruction if Vx != Vy */
                if (v[X] != v[Y]) pc += 2;
                break;

            case 0xA:
                /* ANNN: Sets I to the address NNN. */
                index = NNN;
                break;

            case 0xB:
                /* BNNN: Jumps to the address NNN plus V0. */
                pc = NNN + v[0];
                break;

            case 0xC:
                /* CXNN: Sets VX to the result of a bitwise and operation on a random number (Typically: 0 to 255) and NN.
                 * v[X] = 随机数(0 ~ 255) & NN
                 */
                int random = (int) Math.floor(Math.random() * 0xFF);
                v[X] = random & NN;
                break;

            case 0xD:
                /* DXYN: Draws a sprite at coordinate (VX, VY) that has a width of 8 pixels and a height of N pixels.
                 * Each row of 8 pixels is read as bit-coded starting from memory location I;
                 * I value doesn’t change after the execution of this instruction.
                 * As described above, VF is set to 1 if any screen pixels are flipped from set to unset when the sprite is drawn,
                 * and to 0 if that doesn’t happen
                 */
                int x = v[X];
                int y = v[Y];
                int len = opcode & 0X000F;
                System.out.printf("(%d, %d)", x, y);
                break;

            case 0xE:
                switch (opcode & 0x00FF) {
                    case 0x9E:
                        /* EX9E: Skips the next instruction if the key stored in VX is pressed. */
                        if (mKeyboard.isKeyPressed(v[X])) {
                            pc += 2;
                        }
                        break;

                    case 0xA1:
                        /* EXA1: Skips the next instruction if the key stored in VX isn't pressed. */
                        if (!mKeyboard.isKeyPressed(v[X])) {
                            pc += 2;
                        }
                        break;
                }

                break;

            case 0xF:
                switch (opcode & 0x00FF) {
                    case 0x07:
                        /* FX07: Sets VX to the value of the delay timer. */
                        v[X] = delayTimer;
                        break;

                    case 0x0A:
                        /* FX0A: A key press is awaited, and then stored in VX. */

                        break;

                    case 0x15:
                        /* FX15: Sets the delay timer to VX. */
                        delayTimer = v[X];
                        break;

                    case 0x18:
                        /* FX18: Sets the sound timer to VX. */
                        soundTimer = v[X];
                        break;

                    case 0x1E:
                        /* FX1E: Adds VX to I. */
                        index = (index + v[X]) & 0x0FFF;
                        break;

                    case 0x29:
                        /* FX29: Sets I to the location of the sprite for the character in VX.
                         * Characters 0-F (in hexadecimal) are represented by a 4x5 font.
                         */

                        break;

                    case 0x33:
                        break;

                    case 0x55:
                        /* FX55: Stores V0 to VX (including VX) in memory starting at address I.
                         * The offset from I is increased by 1 for each value written, but I itself is left unmodified.
                         * 将 V0 ~ VX 的值从当前内存地址索引处, 按每次偏移量加 1 依次写入到内存中
                         * 索引自身不改变
                         */
                        for (int i = 0; i <= X; i++) { /* 包括 VX */
                            mMemory.write((byte) v[i], index + i);
                        }
                        break;

                    case 0x65:
                        /* FX65: Fills V0 to VX (including VX) with values from memory starting at address I.
                         * The offset from I is increased by 1 for each value written, but I itself is left unmodified.
                         * 从当前内存地址索引处, 按每次偏移量加 1 依次将内存的读取至 V0 ~ VX 中
                         * 索引自身不改变
                         */
                        for (int i = 0; i <= X; i++) { /* 包括 VX */
                            v[i] = mMemory.read(index + i);
                        }
                        break;
                }
                break;

        }
    }
}
