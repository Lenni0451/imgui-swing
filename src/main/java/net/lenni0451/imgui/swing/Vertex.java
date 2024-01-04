package net.lenni0451.imgui.swing;

public class Vertex {

    private final float x;
    private final float y;
    private final float u;
    private final float v;
    private final int color;

    public Vertex(final float x, final float y, final float u, final float v, final int color) {
        this.x = x;
        this.y = y;
        this.u = u;
        this.v = v;
        this.color = color;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getU() {
        return this.u;
    }

    public double getV() {
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
