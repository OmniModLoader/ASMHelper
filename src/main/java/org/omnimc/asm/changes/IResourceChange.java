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

import org.omnimc.asm.file.ResourceFile;

/**
 * <h6>Functional interface for applying changes to a resource identified by its name and data.
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
     * @return A {@linkplain ResourceFile} containing the updated name and byte array data after applying changes.
     */
    ResourceFile applyChange(String name, byte[] data);
}