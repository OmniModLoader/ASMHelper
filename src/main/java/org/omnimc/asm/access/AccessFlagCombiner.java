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
