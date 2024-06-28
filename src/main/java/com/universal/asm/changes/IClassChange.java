package com.universal.asm.changes;

import org.objectweb.asm.tree.ClassNode;

/**
 * <h6>Functional interface for applying changes to a {@linkplain ClassNode} instance.
 * <p>Implementations of this interface define logic to modify a given {@linkplain ClassNode}.
 *
 * @author <b><a href="https://github.com/CadenCCC">Caden</a></b>
 * @since 1.0.0
 */
@FunctionalInterface
public interface IClassChange {

    /**
     * Applies changes to the provided {@linkplain ClassNode} instance.
     *
     * @param classNode The {@linkplain ClassNode} instance to be modified.
     * @return The modified {@linkplain ClassNode} instance after applying changes.
     */
    ClassNode applyChanges(ClassNode classNode);

}
