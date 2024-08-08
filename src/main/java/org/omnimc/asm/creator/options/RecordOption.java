package org.omnimc.asm.creator.options;

import org.objectweb.asm.Opcodes;

/**
 * @author <b><a href=https://github.com/CadenCCC>Caden</a></b>
 * @since 2.2.3
 */
public class RecordOption extends ClassOption {

    public RecordOption(int javaVersion, String name, String signature, String[] interfaces) {
        super(javaVersion, Opcodes.ACC_PUBLIC | Opcodes.ACC_FINAL, name, signature, "java/lang/Record", interfaces);
    }

}
