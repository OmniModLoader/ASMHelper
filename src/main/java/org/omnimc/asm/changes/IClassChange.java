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