package org.omnimc.asm.common;

import org.jetbrains.annotations.ApiStatus;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author <b><a href=https://github.com/CadenCCC>Caden</a></b>
 * @since 2.2.3
 */
@ApiStatus.Internal
public final class ByteUtil {

    public static byte[] toByteArray(InputStream inputStream, ByteArrayOutputStream byteArrayOutputStream) throws IOException {
        byte[] buffer = new byte[8192];
        int bytesRead;

        while ((bytesRead = inputStream.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, bytesRead);
        }

        return byteArrayOutputStream.toByteArray();
    }
}