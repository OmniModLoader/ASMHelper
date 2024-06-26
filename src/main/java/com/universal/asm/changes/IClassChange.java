package com.universal.asm.changes;

import org.objectweb.asm.tree.ClassNode;

/**
 * Functional interface for applying changes to a {@link ClassNode} instance.
 * Implementations of this interface define logic to modify a given {@code ClassNode}.
 *
 * @author <b><a href="https://github.com/CadenCCC">Caden</a></b>
 * @since 1.0.0
 */
@FunctionalInterface
public interface IClassChange {

    /**
     * Applies changes to the provided {@code ClassNode} instance.
     *
     * @param accessor The {@code ClassNode} instance to be modified.
     * @return The modified {@code ClassNode} instance after applying changes.
     */
    ClassNode applyChanges(ClassNode classNode);

}
