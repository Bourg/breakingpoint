package me.bourg.breakingpoint.util;

import org.objectweb.asm.Opcodes;

import java.util.Arrays;
import java.util.stream.Stream;

public class Util {
    public static String externalizeClassName(String internalName) {
        return internalName.replace('/', '.');
    }

    private static String[] internals = new String[]{
            "jdk", "sun", "java"
    };

    public static boolean shouldInstrumentInside(String internalName) {
        return Arrays.stream(internals)
                .flatMap(p -> Stream.of(p + '.', p + '/'))
                .noneMatch(internalName::startsWith);
    }

    private static int[] returnOps = new int[]{
            Opcodes.RETURN,
            Opcodes.ARETURN,
            Opcodes.IRETURN,
            Opcodes.LRETURN,
            Opcodes.FRETURN,
            Opcodes.DRETURN
    };

    public static boolean isReturnOpcode(int opcode) {
         return Arrays.stream(returnOps).anyMatch(o -> o == opcode);
    }
}
