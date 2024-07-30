/*
 * MIT License
 *
 * Copyright (c) 2024 OmniMC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.omnimc.asm.file;

import org.omnimc.asm.changes.IResourceChange;
import org.omnimc.asm.manager.IClassManager;

import java.util.Map;

/**
 * <h6>Represents a resource file entry for managing binary data in a key-value format.
 * <p>Used within {@linkplain IClassManager}, at {@linkplain IClassManager#applyChanges(IResourceChange...)} to
 * encapsulate modified resources.
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