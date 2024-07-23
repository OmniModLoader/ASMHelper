package org.omnimc.asm.creator.options;

import static org.objectweb.asm.Opcodes.*;

/**
 * @author <b><a href=https://github.com/CadenCCC>Caden</a></b>
 * @since 2.2.3
 */
public class EnumOption extends ClassOption {
    public EnumOption(int javaVersion, int access, String name) {
        super(javaVersion, access + ACC_ENUM, name, null, "java/lang/Enum", new String[0]);
    }
}
