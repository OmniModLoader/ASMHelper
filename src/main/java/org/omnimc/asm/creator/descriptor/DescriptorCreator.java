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
