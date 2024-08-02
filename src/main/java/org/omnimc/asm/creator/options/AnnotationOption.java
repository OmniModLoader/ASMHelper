package org.omnimc.asm.creator.options;

import static org.objectweb.asm.Opcodes.*;

/**
 * @author <b><a href=https://github.com/CadenCCC>Caden</a></b>
 * @since 2.2.3
 */
public class AnnotationOption extends ClassOption {

    public AnnotationOption(int javaVersion, int access, String name, String signature) {
        super(javaVersion, access + ACC_INTERFACE + ACC_ANNOTATION, name, signature, "java/lang/Object", new String[0]);
    }

}
