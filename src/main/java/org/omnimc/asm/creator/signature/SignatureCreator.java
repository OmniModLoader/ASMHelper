package org.omnimc.asm.creator.signature;

/**
 * @author <b><a href=https://github.com/CadenCCC>Caden</a></b>
 * @since 2.2.3
 */
public final class SignatureCreator {

    public static String createSignature(Class<?> type1, Class<?>... genericTypes) {
        return type1.descriptorString().replace(";", "") + genericLookUp(type1, genericTypes);
    }

    private static String genericLookUp(Class<?> type1, Class<?>... genericTypes) {
        StringBuilder genericBuilder = new StringBuilder("<");
        int genericTypePossible = type1.getTypeParameters().length;

        for (int i = 0; i < genericTypePossible; i++) {
            if (genericTypes.length > i) { // todo add a check so I can make sure the genericTypes dont have genericTypes
                genericBuilder.append(genericTypes[i].descriptorString());
            } else {
                genericBuilder.append(Object.class.descriptorString());
            }
        }

        return genericBuilder.append(">;").toString();
    }

    public static void main(String[] args) {
        System.out.println(createSignature(Class.class));
    }
}
