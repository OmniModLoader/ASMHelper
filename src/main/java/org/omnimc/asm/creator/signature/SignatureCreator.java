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

package org.omnimc.asm.creator.signature;

import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * <h6>A utility class for creating JVM-readable type signatures, including support for generics.
 * <p>
 * This class provides functionality to generate correct JVM signatures for classes, including their generic type
 * parameters if specified. It handles the conversion of class types and their generics into the appropriate JVM
 * descriptor format, which is useful for tasks such as reflection and bytecode manipulation.
 * </p>
 * <p>
 * <b>Example usage:</b>
 * <pre>{@code
 *      String signature = SignatureCreator.createSignature(HashMap.class, String.class, Integer.class);
 *      // 'signature' will be something like "Ljava/util/HashMap<Ljava/lang/String;Ljava/lang/Integer;>;"
 * }</pre>
 * </p>
 *
 * @author <b><a href="https://github.com/CadenCCC">Caden</a></b>
 * @since 2.2.3
 */
public final class SignatureCreator {

    /**
     * <h6>Creates a JVM-readable signature for a given class type with optional generic type parameters.
     * <p>
     * This method generates a String representing the signature of a class, including it's generic type parameters. For
     * example, if you pass {@code HashMap.class} as the {@code type1} parameter and specify generic types such as
     * {@code String.class} and {@code Integer.class}, the resulting signature String will represent
     * {@code HashMap<String, Integer>}.
     * </p>
     *
     * @param type1        The base class for which the signature is created. This class may contain generic type
     *                     parameters. For example, {@code HashMap.class} will be represented as
     *                     {@code Ljava/util/HashMap<Ljava/lang/Object;Ljava/lang/Object;>;}.
     * @param genericTypes An optional varargs parameter representing the generic types of {@code type1}. If there are
     *                     fewer generic types provided than required, the remaining types will default to
     *                     {@code java.lang.Object}. If {@code genericTypes} is {@code null}, the method will assume all
     *                     types as {@code java.lang.Object}.
     * @return A String representing the combined and correct JVM signature for the base class, including its generic
     * type parameters, if any. For instance, {@code HashMap<String, Integer>} will be converted into a JVM signature
     * like {@code Ljava/util/HashMap<Ljava/lang/String;Ljava/lang/Integer;>;}.
     */
    @NotNull
    public static String createSignature(@NotNull Class<?> type1, Class<?>... genericTypes) {
        Objects.requireNonNull(type1);
        String mainClassDescriptor = type1.descriptorString();

        if (type1.getTypeParameters().length == 0) {
            return mainClassDescriptor;
        }

        return mainClassDescriptor.replace(";", "") + genericLookUp(type1, genericTypes);
    }

    /**
     * <h6>Recursively builds the signature for generic types.
     * <p>
     * This method constructs the signature for each generic type parameter, and handles nested generic types if
     * present. It ensures that each generic type is represented correctly according to its descriptor String.
     * </p>
     *
     * @param type1        The base class whose generic types are being processed.
     * @param genericTypes The array of generic types to be included in the signature.
     * @return A String representing the signature of the generic types.
     */
    private static String genericLookUp(Class<?> type1, Class<?>... genericTypes) {
        StringBuilder signatureBuilder = new StringBuilder("<");
        List<Class<?>> typesList = genericTypes == null ? Collections.emptyList() : new ArrayList<>(Arrays.asList(genericTypes));
        int numberOfGenericParams = type1.getTypeParameters().length;

        for (int i = 0; i < numberOfGenericParams; i++) {
            if (genericTypes != null && genericTypes.length > i) {
                Class<?> currentClass = genericTypes[i];
                int nestedTypeParams = currentClass.getTypeParameters().length;
                String classDescriptor = currentClass.descriptorString();

                if (!typesList.contains(currentClass)) {
                    currentClass = typesList.get(Math.max(i - 1, 0));
                    classDescriptor = currentClass.descriptorString();
                    nestedTypeParams = currentClass.getTypeParameters().length;
                }

                signatureBuilder.append(nestedTypeParams > 0 ? classDescriptor.replace(";", "") : classDescriptor);

                typesList.remove(currentClass);
                if (nestedTypeParams > 0) {
                    signatureBuilder.append(genericLookUp(currentClass, typesList.toArray(new Class[0])));
                    if (typesList.size() > nestedTypeParams - 1) {
                        typesList.subList(0, nestedTypeParams).clear();
                    }
                }
            } else {
                signatureBuilder.append(Object.class.descriptorString());
            }
        }
        return signatureBuilder.append(">;").toString();
    }

/*    public static void main(String[] args) {
        System.out.println(createSignature(Class.class, HashMap.class, Boolean.class, Class.class, HashMap.class, String.class, Boolean.class, Object.class));
    }*/
}