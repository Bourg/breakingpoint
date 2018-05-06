package me.bourg.breakingpoint.core;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

public class Transformer implements ClassFileTransformer {
    public byte[] transform(ClassLoader loader,
                            String className,
                            Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain,
                            byte[] classfileBuffer) throws IllegalClassFormatException {

        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);

        new ClassReader(classfileBuffer).accept(new ClassInstrumentor(cw), 0);

        return cw.toByteArray();
    }
}
