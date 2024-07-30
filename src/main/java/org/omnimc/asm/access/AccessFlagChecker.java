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
 * <h6>Utility class for checking the presence of specific access flags in a given access value.
 * <p>
 * This class provides methods to determine if various Java access flags (defined in {@linkplain Opcodes})
 * are present in an value representing access flags. These access flags are typically found in
 * the access modifiers of Java class files, methods, fields, etc.
 * </p>
 *
 * @author <b><a href=https://github.com/CadenCCC>Caden</a></b>
 * @since 2.2.6
 */
public final class AccessFlagChecker {

    /**
     * <h6>Checks if the {@linkplain Opcodes#ACC_PRIVATE} flag is present.
     * <p>
     * Where the flag is present:
     * <pre>{@code
     *      // What can be private is a class, field, or method.
     *      private class Monkey {}
     *
     *      private int field;
     *
     *      private void method() {}
     *
     * }</pre>
     * </p>
     * @param access The access flags to check.
     * @return {@code true} if the private flag is present, {@code false} otherwise.
     */
    public static boolean isPrivatePresent(int access) {
        return isFlagFound(access, Opcodes.ACC_PRIVATE);
    }

    /**
     * <h6>Checks if the {@linkplain Opcodes#ACC_PUBLIC} flag is present.
     * <p>
     * Where the flag is present:
     * <pre>{@code
     *      // What can be public is a class, field, or method.
     *      public class Monkey {}
     *
     *      public int field;
     *
     *      public void method() {}
     *
     * }</pre>
     * </p>
     * @param access The access flags to check.
     * @return {@code true} if the public flag is present, {@code false} otherwise.
     */
    public static boolean isPublicPresent(int access) {
        return isFlagFound(access, Opcodes.ACC_PUBLIC);
    }

    /**
     * <h6>Checks if the {@linkplain Opcodes#ACC_PROTECTED} flag is present.
     * <p>
     * Where the flag is present:
     * <pre>{@code
     *      // What can be protected is a class, field, or method.
     *      protected class Monkey {}
     *
     *      protected int field;
     *
     *      protected void method() {}
     *
     * }</pre>
     * </p>
     * @param access The access flags to check.
     * @return {@code true} if the protected flag is present, {@code false} otherwise.
     */
    public static boolean isProtectedPresent(int access) {
        return isFlagFound(access, Opcodes.ACC_PROTECTED);
    }

    /**
     * <h6>Checks if the {@linkplain Opcodes#ACC_FINAL} flag is present.
     * <p>
     * Where the flag is present:
     * <pre>{@code
     *      // What can be final is a class, field, method, or a parameter.
     *      public final class Monkey {}
     *
     *      public final int field = 12;
     *
     *      public final int method(final int param) {return param;}
     *
     * }</pre>
     * </p>
     * @param access The access flags to check.
     * @return {@code true} if the final flag is present, {@code false} otherwise.
     */
    public static boolean isFinalPresent(int access) {
        return isFlagFound(access, Opcodes.ACC_FINAL);
    }

    /**
     * <h6>Checks if the {@linkplain Opcodes#ACC_STATIC} flag is present.
     * <p>
     * Where the flag is present:
     * <pre>{@code
     *      // What can be static is a field, or method.
     *      public static int field;
     *
     *      private static void method() {}
     *
     * }</pre>
     * </p>
     * @param access The access flags to check.
     * @return {@code true} if the static flag is present, {@code false} otherwise.
     */
    public static boolean isStaticPresent(int access) {
        return isFlagFound(access, Opcodes.ACC_STATIC);
    }

    /**
     * <h6>Checks if the {@linkplain Opcodes#ACC_VOLATILE} flag is present.
     * <p>
     * Where the flag is present:
     * <pre>{@code
     *      // What can be volatile is a field.
     *      public volatile int field;
     * }</pre>
     * </p>
     * @param access The access flags to check.
     * @return {@code true} if the volatile flag is present, {@code false} otherwise.
     */
    public static boolean isVolatilePresent(int access) {
        return isFlagFound(access, Opcodes.ACC_VOLATILE);
    }

    /**
     * <h6>Checks if the {@linkplain Opcodes#ACC_DEPRECATED} flag is present.
     * <p>
     * The {@linkplain Opcodes#ACC_DEPRECATED} flag is set by the compiler for classes, methods, or fields
     * that are annotated with {@linkplain Deprecated} in the source code.
     * </p>
     *
     * <p>
     * Where the flag is present:
     * <pre>{@code
     *      // What can be deprecated is a class, field, or method.
     *      @Deprecated
     *      protected class Monkey {}
     *
     *      @Deprecated
     *      protected int field;
     *
     *      @Deprecated
     *      protected void method() {}
     *
     * }</pre>
     * </p>
     * @param access The access flags to check.
     * @return {@code true} if the deprecated flag is present, {@code false} otherwise.
     */
    public static boolean isDeprecatedPresent(int access) {
        return isFlagFound(access, Opcodes.ACC_DEPRECATED);
    }

    /**
     * <h6>Checks if the {@linkplain Opcodes#ACC_NATIVE} flag is present.
     * <p>
     * Where the flag is present:
     * <pre>{@code
     *      // What can be native is a method.
     *      public native void method();
     *
     * }</pre>
     * </p>
     * @param access The access flags to check.
     * @return {@code true} if the native flag is present, {@code false} otherwise.
     */
    public static boolean isNativePresent(int access) {
        return isFlagFound(access, Opcodes.ACC_NATIVE);
    }

    /**
     * <h6>Checks if the {@linkplain Opcodes#ACC_SYNCHRONIZED} flag is present.
     * <p>
     * Where the flag is present:
     * <pre>{@code
     *      // What can be synchronized is a method.
     *      public synchronized void method() {}
     *
     * }</pre>
     * </p>
     * @param access The access flags to check.
     * @return {@code true} if the synchronized flag is present, {@code false} otherwise.
     */
    public static boolean isSynchronizedPresent(int access) {
        return isFlagFound(access, Opcodes.ACC_SYNCHRONIZED);
    }

    /**
     * <h6>Checks if the {@linkplain Opcodes#ACC_TRANSIENT} flag is present.
     * <p>
     * Where the flag is present:
     * <pre>{@code
     *      // What can be transient is a field.
     *      private transient int field;
     *
     * }</pre>
     * </p>
     * @param access The access flags to check.
     * @return {@code true} if the transient flag is present, {@code false} otherwise.
     */
    public static boolean isTransientPresent(int access) {
        return isFlagFound(access, Opcodes.ACC_TRANSIENT);
    }

    /**
     * <h6>Checks if the {@linkplain Opcodes#ACC_VARARGS} flag is present.
     * <p>
     * Where the flag is present:
     * <pre>{@code
     *      // What can have varargs is a method.
     *      public void method(String... args) {}
     *
     * }</pre>
     * </p>
     * @param access The access flags to check.
     * @return {@code true} if the varargs flag is present, {@code false} otherwise.
     */
    public static boolean isVarargsPresent(int access) {
        return isFlagFound(access, Opcodes.ACC_VARARGS);
    }

    /**
     * <h6>Checks if the {@linkplain Opcodes#ACC_TRANSITIVE} flag is present.
     * <p>
     * Where the flag is present:
     * <pre>{@code
     *      // What can be transitive is a module requires statement.
     *      requires transitive some.module;
     *
     * }</pre>
     * </p>
     * @param access The access flags to check.
     * @return {@code true} if the transitive flag is present, {@code false} otherwise.
     */
    public static boolean isTransitivePresent(int access) {
        return isFlagFound(access, Opcodes.ACC_TRANSITIVE);
    }

    /**
     * <h6>Checks if the {@linkplain Opcodes#ACC_SYNTHETIC} flag is present.
     * <p>
     * Where the flag is present:
     * <pre>{@code
     *      // Synthetic elements are generated by the compiler.
     * }</pre>
     * </p>
     * @param access The access flags to check.
     * @return {@code true} if the synthetic flag is present, {@code false} otherwise.
     */
    public static boolean isSyntheticPresent(int access) {
        return isFlagFound(access, Opcodes.ACC_SYNTHETIC);
    }

    /**
     * <h6>Checks if the {@linkplain Opcodes#ACC_SUPER} flag is present.
     * <p>
     * Where the flag is present:
     * <pre>{@code
     *      // The super flag is set for classes using invokespecial to access methods in superclasses.
     * }</pre>
     * </p>
     * @param access The access flags to check.
     * @return {@code true} if the super flag is present, {@code false} otherwise.
     */
    public static boolean isSuperPresent(int access) {
        return isFlagFound(access, Opcodes.ACC_SUPER);
    }

    /**
     * <h6>Checks if the {@linkplain Opcodes#ACC_STRICT} flag is present.
     * <p>
     * Where the flag is present:
     * <pre>{@code
     *      // What can be strictfp is a class or method.
     *      strictfp class Monkey {}
     *
     *      public strictfp void method() {}
     *
     * }</pre>
     * </p>
     * @param access The access flags to check.
     * @return {@code true} if the strict flag is present, {@code false} otherwise.
     */
    public static boolean isStrictPresent(int access) {
        return isFlagFound(access, Opcodes.ACC_STRICT);
    }

    /**
     * <h6>Checks if the {@linkplain Opcodes#ACC_STATIC_PHASE} flag is present.
     * <p>
     * Where the flag is present:
     * <pre>{@code
     *      // The static phase flag is used in module declarations.
     * }</pre>
     * </p>
     * @param access The access flags to check.
     * @return {@code true} if the static phase flag is present, {@code false} otherwise.
     */
    public static boolean isStaticPhasePresent(int access) {
        return isFlagFound(access, Opcodes.ACC_STATIC_PHASE);
    }

    /**
     * <h6>Checks if the {@linkplain Opcodes#ACC_MANDATED} flag is present.
     * <p>
     * Where the flag is present:
     * <pre>{@code
     *      // The mandated flag is used for elements that are mandated by the JVM.
     * }</pre>
     * </p>
     * @param access The access flags to check.
     * @return {@code true} if the mandated flag is present, {@code false} otherwise.
     */
    public static boolean isMandatedPresent(int access) {
        return isFlagFound(access, Opcodes.ACC_MANDATED);
    }

    /**
     * <h6>Checks if the {@linkplain Opcodes#ACC_OPEN} flag is present.
     * <p>
     * Where the flag is present:
     * <pre>{@code
     *      // The open flag is used in module declarations.
     * }</pre>
     * </p>
     * @param access The access flags to check.
     * @return {@code true} if the open flag is present, {@code false} otherwise.
     */
    public static boolean isOpenPresent(int access) {
        return isFlagFound(access, Opcodes.ACC_OPEN);
    }

    /**
     * <h6>Checks if the {@linkplain Opcodes#ACC_RECORD} flag is present.
     * <p>
     * Where the flag is present:
     * <pre>{@code
     *      // The record flag is used for record classes.
     *      public record Monkey(String name) {}
     * }</pre>
     * </p>
     * @param access The access flags to check.
     * @return {@code true} if the record flag is present, {@code false} otherwise.
     */
    public static boolean isRecordPresent(int access) {
        return isFlagFound(access, Opcodes.ACC_RECORD);
    }

    /**
     * <h6>Checks if the {@linkplain Opcodes#ACC_ENUM} flag is present.
     * <p>
     * Where the flag is present:
     * <pre>{@code
     *      // The enum flag is used for enum types.
     *      public enum Monkey { A, B, C }
     * }</pre>
     * </p>
     * @param access The access flags to check.
     * @return {@code true} if the enum flag is present, {@code false} otherwise.
     */
    public static boolean isEnumPresent(int access) {
        return isFlagFound(access, Opcodes.ACC_ENUM);
    }

    /**
     * <h6>Checks if the {@linkplain Opcodes#ACC_BRIDGE} flag is present.
     * <p>
     * Where the flag is present:
     * <pre>{@code
     *      // The bridge flag is used for bridge methods generated by the compiler.
     * }</pre>
     * </p>
     * @param access The access flags to check.
     * @return {@code true} if the bridge flag is present, {@code false} otherwise.
     */
    public static boolean isBridgePresent(int access) {
        return isFlagFound(access, Opcodes.ACC_BRIDGE);
    }

    /**
     * <h6>Checks if the {@linkplain Opcodes#ACC_ABSTRACT} flag is present.
     * <p>
     * Where the flag is present:
     * <pre>{@code
     *      // What can be abstract is a class or method.
     *      public abstract class Monkey {}
     *
     *      public abstract void method();
     *
     * }</pre>
     * </p>
     * @param access The access flags to check.
     * @return {@code true} if the abstract flag is present, {@code false} otherwise.
     */
    public static boolean isAbstractPresent(int access) {
        return isFlagFound(access, Opcodes.ACC_ABSTRACT);
    }

    /**
     * <h6>Checks if the {@linkplain Opcodes#ACC_INTERFACE} flag is present.
     * <p>
     * Where the flag is present:
     * <pre>{@code
     *      // The interface flag is used for interfaces.
     *      public interface Monkey {}
     * }</pre>
     * </p>
     * @param access The access flags to check.
     * @return {@code true} if the interface flag is present, {@code false} otherwise.
     */
    public static boolean isInterfacePresent(int access) {
        return isFlagFound(access, Opcodes.ACC_INTERFACE);
    }

    /**
     * <h6>Checks if the {@linkplain Opcodes#ACC_ANNOTATION} flag is present.
     * <p>
     * Where the flag is present:
     * <pre>{@code
     *      // The annotation flag is used for annotation types.
     *      public @interface Monkey {}
     * }</pre>
     * </p>
     * @param access The access flags to check.
     * @return {@code true} if the annotation flag is present, {@code false} otherwise.
     */
    public static boolean isAnnotationPresent(int access) {
        return isFlagFound(access, Opcodes.ACC_ANNOTATION);
    }

    private static boolean isFlagFound(int access, int flag) {
        return (access & flag) != 0;
    }
}