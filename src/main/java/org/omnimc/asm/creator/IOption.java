package org.omnimc.asm.creator;

import org.objectweb.asm.ClassWriter;

/**
 * @author <b><a href=https://github.com/CadenCCC>Caden</a></b>
 * @since 2.2.3
 */
public interface IOption {

   void runOption(ClassWriter classWriter);

}
