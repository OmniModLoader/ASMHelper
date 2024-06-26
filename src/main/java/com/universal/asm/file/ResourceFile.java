package com.universal.asm.file;

import java.util.Map;

/**
 * @author <b><a href=https://github.com/CadenCCC>Caden</a></b>
 * @since 1.0.
 */
public class ResourceFile implements Map.Entry<String, byte[]> {
    private final String key;
    private byte[] value;

    public ResourceFile(String key, byte[] value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public byte[] getValue() {
        return value;
    }

    @Override
    public byte[] setValue(byte[] value) {
        this.value = value;
        return value;
    }
}
