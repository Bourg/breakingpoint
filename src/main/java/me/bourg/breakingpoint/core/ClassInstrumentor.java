package me.bourg.breakingpoint.core;


import me.bourg.breakingpoint.util.Util;
import me.bourg.breakingpoint.method.MethodInstrumentor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.ASM6;

public class ClassInstrumentor extends ClassVisitor {
    private String qualifiedClassName;

    public ClassInstrumentor(ClassVisitor w) {
        super(ASM6, w);
    }

    @Override
    public void visit(int version, int access, String name,
                             String signature, String superName, String[] interfaces) {
        cv.visit(version, access, name, signature, superName, interfaces);

        this.qualifiedClassName = Util.externalizeClassName(name);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc,
                                     String signature, String[] exceptions) {
        MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);

        if (mv != null) {
            return new MethodInstrumentor(qualifiedClassName, name, mv);
        } else {
            return mv;
        }
    }
}
