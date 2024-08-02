package org.omnimc.asm.creator.options;

/**
 * @author <b><a href=https://github.com/CadenCCC>Caden</a></b>
 * @since 2.2.3
 */
public class InterfaceOption extends ClassOption {

    public InterfaceOption(int javaVersion, int access, String name, String signature, String superClass, String[] interfaces) {
        super(javaVersion, access, name, signature, superClass, interfaces);
    }

}
