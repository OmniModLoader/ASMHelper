package com.universal.asm.file;

import com.universal.asm.changes.IResourceChange;
import com.universal.asm.manager.IClassManager;

import java.util.Map;

/**
 * <h6>Represents a resource file entry for managing binary data in a key-value format.
 * <p>Used within {@linkplain IClassManager}, at {@linkplain IClassManager#applyChanges(IResourceChange...)} to encapsulate modified resources.
 * <p>Implements {@linkplain java.util.Map.Entry} where the key is a {@code String} identifier
 * and the value is a {@code byte[]} array.
 *
 * @author <b><a href="https://github.com/CadenCCC">Caden</a></b>
 * @since 1.0.0
 */
public class ResourceFile implements Map.Entry<String, byte[]> {
    /**
     * The key representing the name or identifier of the resource.
     */
    private final String key;
    /**
     * The byte array containing the binary data of the resource.
     */
    private byte[] value;

    /**
     * Constructs a new {@linkplain ResourceFile} with the specified key and value.
     *
     * @param key   The key representing the name or identifier of the resource.
     * @param value The byte array containing the binary data of the resource.
     */
    public ResourceFile(String key, byte[] value) {
        this.key = key;
        this.value = value;
    }

    /**
     * Retrieves the key associated with this resource file entry.
     *
     * @return The key of this resource file.
     */
    @Override
    public String getKey() {
        return key;
    }

    /**
     * Retrieves the value (byte array) associated with this resource file entry.
     *
     * @return The byte array value of this resource file.
     */
    @Override
    public byte[] getValue() {
        return value;
    }

    /**
     * Sets a new value (byte array) for this resource file entry.
     *
     * @param value The new byte array value to set.
     * @return The value inputted as the replacement for the previous value.
     */
    @Override
    public byte[] setValue(byte[] value) {
        this.value = value;
        return value;
    }
}
