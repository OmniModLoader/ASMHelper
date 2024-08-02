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

package org.omnimc.asm.access;

import org.objectweb.asm.Opcodes;

/**
 * <h6>Access Flag Combiner
 * <p>
 * This utility class provides a method to combine Java access flags while ensuring
 * that invalid combinations of flags are not included in the result.
 * </p>
 * @author <b><a href=https://github.com/CadenCCC>Caden</a></b>
 * @since 2.2.6
 */
public final class AccessFlagCombiner {
    /**
     * <h6>A list of invalid combinations of access flags that cannot be combined.
     * <p>
     * For example:
     * <ul>
     *     <li>{@linkplain Opcodes#ACC_PRIVATE} and {@linkplain Opcodes#ACC_PUBLIC}</li>
     *     <li>{@linkplain Opcodes#ACC_PROTECTED} and {@linkplain Opcodes#ACC_PUBLIC}</li>
     *     <li>{@linkplain Opcodes#ACC_PROTECTED} and {@linkplain Opcodes#ACC_PRIVATE}</li>
     * </ul>
     * </p>
     */
    private static final int[] INVALID_COMBINATIONS = {
            Opcodes.ACC_PRIVATE | Opcodes.ACC_PUBLIC,
            Opcodes.ACC_PROTECTED | Opcodes.ACC_PUBLIC,
            Opcodes.ACC_PROTECTED | Opcodes.ACC_PRIVATE
    };


    /**
     * <h6>Combines an array of access flags, ensuring that invalid combinations are not included.
     * <p>
     * This method iterates through the provided array of access flags and combines them into
     * a single value. If an invalid combination of flags is detected, it is excluded
     * from the final result.
     * </p>
     *
     * @param accesses An array of access flags to be combined.
     * @return The combined access flags, with invalid combinations excluded.
     * @throws IllegalArgumentException If the input array is null or empty.
     */
    public static int combineAccessFlags(int... accesses) {
        int finalAccess = 0;
        for (int access : accesses) {
            for (int invalidFlag : INVALID_COMBINATIONS) {
                if ((invalidFlag & access) != 0 && (invalidFlag & finalAccess) != 0) {
                    access &= ~access;
                }
            }
            finalAccess |= access;
        }
        return finalAccess;
    }
}