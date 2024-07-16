package org.omnimc.asm.changes;

import org.omnimc.asm.file.ClassFile;

/**
 * <h6>Functional interface for applying changes to a class file represented as byte data.
 * <p>Implementations of this interface define logic to modify a given class file.
 * <p>Implementors should provide logic to transform a class file represented as
 * byte data into a {@link ClassFile} object, encapsulating the modified class file.
 *
 * @author <b><a href="https://github.com/CadenCCC">Caden</a></b>
 * @since 1.0.0
 */
@FunctionalInterface
public interface IClassChange {

    /**
     * Applies changes to the provided class file data.
     *
     * @param name       The name or identifier of the class.
     * @param classBytes The byte data representing the class file to be modified.
     * @return The {@link ClassFile} object representing the modified class file.
     */
    ClassFile applyChange(String name, byte[] classBytes);

}
