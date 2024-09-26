package net.lenni0451.imgui.swing.util;

import net.raphimc.softwarerenderer.vertex.FloatVertex;

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

    public FloatVertex getVertex(final int index) {
        final int abgr = this.getUnsignedInt(index + 16);
        final int a = (abgr >> 24) & 0xFF;
        final int b = (abgr >> 16) & 0xFF;
        final int g = (abgr >> 8) & 0xFF;
        final int r = abgr & 0xFF;
        final int argb = (a << 24) | (r << 16) | (g << 8) | b;
        return new FloatVertex(this.getFloat(index), this.getFloat(index + 4), 0F, argb, this.getFloat(index + 8), this.getFloat(index + 12));
    }

}
