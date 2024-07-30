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

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

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

    @Override
    public String toString() {
        return "ClassFile{" +
                "name='" + name + '\'' +
                ", data=" + Arrays.toString(data) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClassFile classFile = (ClassFile) o;
        return Objects.equals(name, classFile.getKey()) && Objects.deepEquals(data, classFile.getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, Arrays.hashCode(data));
    }
}