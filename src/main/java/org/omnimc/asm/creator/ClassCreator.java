package org.omnimc.asm.creator;

import org.omnimc.asm.creator.method.MethodCreator;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.ClassWriter;

/**
 * @author <b><a href=https://github.com/CadenCCC>Caden</a></b>
 * @since 2.2.3
 */
@SuppressWarnings("unused")
public class ClassCreator {
    private final ClassWriter classWriter;
    private Option currentOption;

    public ClassCreator(int flags) {
        this.classWriter = new ClassWriter(flags);
    }

    public ClassCreator createClass(@NotNull Option selectedOption, IOption option) {
        this.currentOption = selectedOption;

        switch (selectedOption) {
            case CLASS, RECORD, INTERFACE, ENUM, ANNOTATION -> option.runOption(classWriter);
        }
        return this;
    }

    public ClassCreator createField(int access, String name, String descriptor, String signature, Object value) {
        switch (currentOption) {

        }

        return this;
    }

    public MethodCreator createMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        return new MethodCreator(access, name, descriptor, signature, exceptions, classWriter);
    }


    public ClassCreator end() {
        classWriter.visitEnd();
        return this;
    }

    public enum Option {
        CLASS,
        INTERFACE,
        RECORD,
        ANNOTATION,
        ENUM;
    }
}
