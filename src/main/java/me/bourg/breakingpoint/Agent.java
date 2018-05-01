package me.bourg.breakingpoint;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

public class Agent {
    public static void premain(String args, Instrumentation instrumentation) {
        System.out.println("PREMAIN");

        instrumentation.addTransformer(new ClassFileTransformer() {
            public byte[] transform(ClassLoader loader,
                                    String className,
                                    Class<?> classBeingRedefined,
                                    ProtectionDomain protectionDomain,
                                    byte[] classfileBuffer) throws IllegalClassFormatException {
                System.out.println("AGENT INTERCEPTED " + className);
                return classfileBuffer;
            }
        });
    }
}
