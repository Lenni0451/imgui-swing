package net.lenni0451.imgui.swing;

import java.awt.*;

public class Triangle {

    private final Vertex p1;
    private final Vertex p2;
    private final Vertex p3;
    private final int textureId;

    public Triangle(final Vertex p1, final Vertex p2, final Vertex p3, final int textureId) {
        this.p1 = p1;
        this.p2 = p2;
        this.p3 = p3;
        this.textureId = textureId;
    }

    public Vertex getP1() {
        return this.p1;
    }

    public Vertex getP2() {
        return this.p2;
    }

    public Vertex getP3() {
        return this.p3;
    }

    public int getTextureId() {
        return this.textureId;
    }

    public Rectangle getBounds() {
        return new Rectangle(
                (int) Math.min(Math.min(this.p1.getX(), this.p2.getX()), this.p3.getX()),
                (int) Math.min(Math.min(this.p1.getY(), this.p2.getY()), this.p3.getY()),
                (int) Math.max(Math.max(this.p1.getX(), this.p2.getX()), this.p3.getX()),
                (int) Math.max(Math.max(this.p1.getY(), this.p2.getY()), this.p3.getY())
        );
    }

    public boolean isInTriangle(final float x, final float y) {
        double s = this.p1.getY() * this.p3.getX() - this.p1.getX() * this.p3.getY() + (this.p3.getY() - this.p1.getY()) * x + (this.p1.getX() - this.p3.getX()) * y;
        double t = this.p1.getX() * this.p2.getY() - this.p1.getY() * this.p2.getX() + (this.p1.getY() - this.p2.getY()) * x + (this.p2.getX() - this.p1.getX()) * y;

        if ((s < 0) != (t < 0)) return false;

        double A = -this.p2.getY() * this.p3.getX() + this.p1.getY() * (this.p3.getX() - this.p2.getX()) + this.p1.getX() * (this.p2.getY() - this.p3.getY()) + this.p2.getX() * this.p3.getY();

        return A < 0 ? (s <= 0 && s + t >= A) : (s >= 0 && s + t <= A);
    }

}
