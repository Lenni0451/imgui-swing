package net.lenni0451.imgui.swing.primitive;

public class Vertex {

    final float x;
    final float y;
    final float u;
    final float v;
    final int color;

    public Vertex(final float x, final float y, final float u, final float v, final int color) {
        this.x = x;
        this.y = y;
        this.u = u;
        this.v = v;
        this.color = color;
    }

    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }

    public float getU() {
        return this.u;
    }

    public float getV() {
        return this.v;
    }

    public int getColor() {
        return this.color;
    }

    @Override
    public String toString() {
        return "Vertex{" +
                "x=" + this.x +
                ", y=" + this.y +
                ", u=" + this.u +
                ", v=" + this.v +
                ", color=" + Integer.toHexString(this.color) +
                '}';
    }

}
