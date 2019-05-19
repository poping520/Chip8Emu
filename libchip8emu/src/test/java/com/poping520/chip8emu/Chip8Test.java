package com.poping520.chip8emu;

import java.io.File;

/**
 * @author poping520
 * create on 19/05/17 23:24
 */
public class Chip8Test {

    public static void main(String[] args) throws Exception {

        String classPath = Chip8Test.class.getClassLoader().getResource(".").getPath();
        String romPath = classPath.replace("build/classes/java/test/", "rom/Rush_Hour.ch8");

        Chip8Emulator emu = new Chip8Emulator();
        emu.loadRom(new File(romPath));
        emu.run();

    }
}
