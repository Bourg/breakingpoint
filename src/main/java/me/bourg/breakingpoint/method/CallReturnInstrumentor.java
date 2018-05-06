package me.bourg.breakingpoint.method;

import me.bourg.breakingpoint.Util;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import static org.objectweb.asm.Opcodes.ASM6;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;

public class CallReturnInstrumentor extends MethodVisitor {
    private final String qualifiedClassName, methodName;

    private static final Type STRING_TYPE = Type.getObjectType("java/lang/String");

    public CallReturnInstrumentor(String qualifiedClassName, String methodName, MethodVisitor mv) {
        super(ASM6, mv);

        this.qualifiedClassName = qualifiedClassName;
        this.methodName = methodName;
    }

    public void visitMethodInsn(int opc, String owner, String name, String desc, boolean isInterface) {
        String calleeExternalName = Util.externalizeClassName(owner) + "." + name;

        if (Util.shouldInstrumentInside(qualifiedClassName)) {
            injectLogCall(calleeExternalName);
        }

        mv.visitMethodInsn(opc, owner, name, desc, isInterface);

        if (Util.shouldInstrumentInside(qualifiedClassName) &&
                !Util.shouldInstrumentInside(owner)) {
            injectLogReturn(calleeExternalName);
        }
    }

    public void visitInsn(int opc) {
        if (Util.shouldInstrumentInside(qualifiedClassName) &&
                Util.isReturnOpcode(opc)) {
            injectLogReturn(getQualifiedName());
        }

        mv.visitInsn(opc);
    }

    private void injectLogCall(String calleeExternalName) {
        mv.visitLdcInsn(getQualifiedName());
        mv.visitLdcInsn(calleeExternalName);
        mv.visitMethodInsn(INVOKESTATIC,
                "me/bourg/breakingpoint/sink/InstrumentationSink",
                "logCall",
                Type.getMethodDescriptor(Type.VOID_TYPE, STRING_TYPE, STRING_TYPE),
                false);
    }

    private void injectLogReturn(String returnerExternalName) {
        mv.visitLdcInsn(returnerExternalName);
        mv.visitMethodInsn(INVOKESTATIC,
                "me/bourg/breakingpoint/sink/InstrumentationSink",
                "logReturn",
                Type.getMethodDescriptor(Type.VOID_TYPE, STRING_TYPE),
                false);
    }

    private String getQualifiedName() {
        return qualifiedClassName + "." + methodName;
    }
}
