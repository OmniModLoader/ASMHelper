package com.universal.asm.changes;

import com.universal.asm.file.ResourceFile;

/**
 * Functional interface for applying changes to a resource identified by its name and data.
 * <p>Implementations of this interface define logic to modify resources.
 *
 * @author <b><a href="https://github.com/CadenCCC">Caden</a></b>
 * @since 1.0.0
 */
@FunctionalInterface
public interface IResourceChange {

    /**
     * Applies changes to the resource identified by the specified name and data.
     *
     * @param name The name or identifier of the resource.
     * @param data The byte array representing the current data of the resource.
     * @return A {@code ResourceFile} containing the updated name and byte array data after applying changes.
     */
    ResourceFile applyResourceChange(String name, byte[] data);

}
