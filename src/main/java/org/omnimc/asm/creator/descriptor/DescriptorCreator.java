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

package org.omnimc.asm.creator.descriptor;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author <b><a href=https://github.com/CadenCCC>Caden</a></b>
 * @since 2.2.3
 */
public final class DescriptorCreator {

    public static String fieldDescriptor(@NotNull Class<?> clazz) {
        return clazz.descriptorString();
    }

    @Contract("_,null -> !null; _,!null -> !null")
    public static String methodDescriptor(@NotNull Class<?> returnableClass, @Nullable Class<?>... descriptorInformation) {
        StringBuilder descriptorBuilder = new StringBuilder();

        descriptorBuilder.append("(");

        for (Class<?> aClass : descriptorInformation) {
            if (aClass == null) {
                continue;
            }

            descriptorBuilder.append(aClass.descriptorString());
        }

        descriptorBuilder.append(")").append(returnableClass.descriptorString());

        return descriptorBuilder.toString();
    }

    public static String emptyMethodDescriptor(@NotNull Class<?> returnableClass) {
        return methodDescriptor(returnableClass, (Class<?>) null);
    }

}