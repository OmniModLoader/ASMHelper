package org.omnimc.asm.creator.options;

import org.omnimc.asm.creator.IOption;
import org.objectweb.asm.ClassWriter;

/**
 * @author <b><a href=https://github.com/CadenCCC>Caden</a></b>
 * @since 2.2.3
 */
public class ClassOption implements IOption {
    protected final int javaVersion;
    protected final int access;
    protected final String name;
    protected final String signature;
    protected final String superClass;
    protected final String[] interfaces;

    public ClassOption(int javaVersion, int access, String name, String signature, String superClass, String[] interfaces) {
        this.javaVersion = javaVersion;
        this.access = access;
        this.name = name;
        this.signature = signature;
        this.superClass = superClass;
        this.interfaces = interfaces;
    }

    @Override
    public void runOption(ClassWriter classWriter) {
        classWriter.visit(javaVersion, access, name, signature, superClass, interfaces);
    }
}
