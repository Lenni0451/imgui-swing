package net.lenni0451.imgui.swing;

import java.nio.ByteBuffer;

public class WrappedBuffer {

    private final byte[] buffer;

    public WrappedBuffer(final ByteBuffer buffer) {
        this.buffer = new byte[buffer.remaining()];
        buffer.get(this.buffer);
    }

    private byte get(final int index) {
        return this.buffer[index];
    }

    public int getUnsignedShort(final int index) {
        return (this.get(index) & 0xFF) | ((this.get(index + 1) & 0xFF) << 8);
    }

    public float getFloat(final int index) {
        return Float.intBitsToFloat(this.getUnsignedInt(index));
    }

    public int getUnsignedInt(final int index) {
        return (this.get(index) & 0xFF) | ((this.get(index + 1) & 0xFF) << 8) | ((this.get(index + 2) & 0xFF) << 16) | ((this.get(index + 3) & 0xFF) << 24);
    }

    public Vertex getVertex(final int index) {
        return new Vertex(this.getFloat(index), this.getFloat(index + 4), this.getFloat(index + 8), this.getFloat(index + 12), this.getUnsignedInt(index + 16));
    }

}
