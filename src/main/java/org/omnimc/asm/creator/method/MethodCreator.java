package org.omnimc.asm.creator.method;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

/**
 * @author <b><a href=https://github.com/CadenCCC>Caden</a></b>
 * @since 2.2.3
 */
@ApiStatus.NonExtendable
public class MethodCreator {
    private final int access;
    private final String name;
    private final String descriptor;
    private final String signature;
    private final String[] exceptions;

    private final MethodVisitor visitor;

    public MethodCreator(int access, String name, String descriptor, String signature, String[] exceptions, @NotNull ClassWriter classWriter) {
        this.access = access;
        this.name = name;
        this.descriptor = descriptor;
        this.signature = signature;
        this.exceptions = exceptions;

        this.visitor = classWriter.visitMethod(access, name, descriptor, signature, exceptions);
    }

    public MethodCreator end() {
        visitor.visitEnd();
        return this;
    }
}
