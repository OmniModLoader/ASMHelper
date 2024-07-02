package com.universal.asm.file;

import java.util.Map;

/**
 * <h6>{@linkplain ClassFile} represents a file entry for a class, encapsulating its name and byte data.
 * <p>This class implements {@linkplain Map.Entry} interface, allowing it to be used in collections that require key-value pairs.</p>
 *
 * @author <b><a href=https://github.com/CadenCCC>Caden</a></b>
 * @since 2.1.3
 */
public class ClassFile implements Map.Entry<String, byte[]> {
    /**
     * The key representing the name or identifier of the class.
     */
    private final String name;
    /**
     * The byte array containing the binary data of the class.
     */
    private byte[] data;

    /**
     * Constructs a new {@linkplain ClassFile} with the specified name and byte data.
     *
     * @param name The name of the class file.
     * @param data The byte data representing the contents of the class file.
     */
    public ClassFile(String name, byte[] data) {
        this.name = name;
        this.data = data;
    }

    /**
     * Retrieves the key (name) of this {@linkplain ClassFile}.
     *
     * @return The name of the class file.
     */
    @Override
    public String getKey() {
        return name;
    }

    /**
     * Retrieves the value (byte data) of this {@linkplain ClassFile}.
     *
     * @return The byte data representing the contents of the class file.
     */
    @Override
    public byte[] getValue() {
        return data;
    }

    /**
     * Sets the byte data of this {@linkplain ClassFile} to the specified value.
     *
     * @param value The new byte data to be set.
     * @return The previous byte data before the update.
     */
    @Override
    public byte[] setValue(byte[] value) {
        this.data = value;
        return data;
    }
}
