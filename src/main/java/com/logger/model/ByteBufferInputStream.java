package com.logger.model;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Objects;

public class ByteBufferInputStream extends InputStream {

    private final ByteBuffer byteBuffer;

    public ByteBufferInputStream(ByteBuffer byteBuffer) {
        this.byteBuffer = byteBuffer;
    }

    @Override
    public int read() throws IOException {
        if(!byteBuffer.hasRemaining()) {
            return -1;
        }
        return byteBuffer.get() & 255;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        Objects.checkFromIndexSize(off, len, b.length);
        if(len == 0) {
            return 0;
        }
        if(!byteBuffer.hasRemaining()) {
            return -1;
        }
        int bytesCanRead = Math.min(byteBuffer.remaining(), len);
        byteBuffer.get(b, off, bytesCanRead);
        return bytesCanRead;
    }
    
}
