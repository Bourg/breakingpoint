package me.bourg.breakingpoint.visitors;

import me.bourg.breakingpoint.sink.InstrumentationSink;
import me.bourg.breakingpoint.util.Util;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import static org.objectweb.asm.Opcodes.*;

public class MethodInstrumentor extends MethodVisitor {
    private final String qualifiedClassName, methodName;

    private static final Type STRING_TYPE = Type.getObjectType("java/lang/String");

    public MethodInstrumentor(String internalClassName, String methodName, MethodVisitor mv) {
        super(ASM6, mv);

        this.qualifiedClassName = Util.externalizeClassName(internalClassName);
        this.methodName = methodName;
    }

    ///////////////////////////////////////////////////////////////////////////
    // CALL / RETURN HOOKS AND HELPERS                                       //
    ///////////////////////////////////////////////////////////////////////////

    /*@Override
    public void visitInsn(int opc) {
        if (Util.shouldInstrumentInside(qualifiedClassName) &&
                Util.isReturnOpcode(opc)) {
            injectLogReturn(getQualifiedName());
        }

        mv.visitInsn(opc);
    }*/

    @Override
    public void visitMethodInsn(int opc, String owner, String name, String desc, boolean isInterface) {
        String calleeExternalName = Util.externalizeClassName(owner) + "." + name;

        if (Util.shouldInstrumentInside(qualifiedClassName)) {
            injectLogCall(calleeExternalName);
        }

        mv.visitMethodInsn(opc, owner, name, desc, isInterface);

        if (Util.shouldInstrumentInside(qualifiedClassName)) {
            injectLogReturn(calleeExternalName);
        }
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

    ///////////////////////////////////////////////////////////////////////////
    // BRANCHING HOOKS AND HELPERS                                           //
    ///////////////////////////////////////////////////////////////////////////

    private short branchNumber = 1;

    @Override
    public void visitJumpInsn(int opc, Label lbl) {
        if (Util.shouldInstrumentInside(qualifiedClassName)) {
            injectLogPreBranching(branchNumber++);
        }

        mv.visitJumpInsn(opc, lbl);

        if (Util.shouldInstrumentInside(qualifiedClassName)) {
            injectLogDidNotBranch();
        }
    }

    private void injectLogPreBranching(int branchNumber) {
        mv.visitLdcInsn(getQualifiedName());
        mv.visitIntInsn(SIPUSH, branchNumber);

        mv.visitMethodInsn(INVOKESTATIC,
                "me/bourg/breakingpoint/sink/InstrumentationSink",
                "logPreBranching",
                Type.getMethodDescriptor(Type.VOID_TYPE, STRING_TYPE, Type.INT_TYPE),
                false);
    }

    private void injectLogDidNotBranch() {
        mv.visitMethodInsn(INVOKESTATIC,
                "me/bourg/breakingpoint/sink/InstrumentationSink",
                "logDidNotBranch",
                Type.getMethodDescriptor(Type.VOID_TYPE),
                false);
    }

    ///////////////////////////////////////////////////////////////////////////
    // GENERAL UTILTIY                                                       //
    ///////////////////////////////////////////////////////////////////////////

    private String getQualifiedName() {
        return qualifiedClassName + "." + methodName;
    }
}