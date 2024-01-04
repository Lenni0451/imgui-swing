package net.lenni0451.imgui.swing.primitive;

import net.lenni0451.imgui.swing.TextureManager;
import net.lenni0451.imgui.swing.util.ColorUtil;

import java.awt.*;
import java.awt.image.BufferedImage;

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

    public void draw(final BufferedImage target) {
        final BufferedImage texture = TextureManager.get(this.textureId);
        final Rectangle bounds = this.getBounds();
        for (int x = bounds.x; x <= bounds.width; x++) {
            for (int y = bounds.y; y <= bounds.height; y++) {
                if (this.isInTriangle(x, y)) {
                    final double w = ((p2.y - p3.y) * (x - p3.x) + (p3.x - p2.x) * (y - p3.y)) / ((p2.y - p3.y) * (p1.x - p3.x) + (p3.x - p2.x) * (p1.y - p3.y));
                    final double u = ((p3.y - p1.y) * (x - p3.x) + (p1.x - p3.x) * (y - p3.y)) / ((p2.y - p3.y) * (p1.x - p3.x) + (p3.x - p2.x) * (p1.y - p3.y));
                    final double v = 1 - u - w;
                    final int b = (int) (w * (p1.color >> 16 & 0xFF) + u * (p2.color >> 16 & 0xFF) + v * (p3.color >> 16 & 0xFF));
                    final int g = (int) (w * (p1.color >> 8 & 0xFF) + u * (p2.color >> 8 & 0xFF) + v * (p3.color >> 8 & 0xFF));
                    final int r = (int) (w * (p1.color & 0xFF) + u * (p2.color & 0xFF) + v * (p3.color & 0xFF));
                    final int a = (int) (w * (p1.color >> 24 & 0xFF) + u * (p2.color >> 24 & 0xFF) + v * (p3.color >> 24 & 0xFF));
                    final int vertexColor = (a << 24) | (r << 16) | (g << 8) | b;

                    int textureX = (int) Math.round(w * p1.u * texture.getWidth() + u * p2.u * texture.getWidth() + v * p3.u * texture.getWidth());
                    int textureY = (int) Math.round(w * p1.v * texture.getHeight() + u * p2.v * texture.getHeight() + v * p3.v * texture.getHeight());
                    int textureColor = texture.getRGB(textureX, textureY);

                    int mixedColor = ColorUtil.mix(vertexColor, textureColor);
                    int mixedAlpha = mixedColor >> 24 & 0xFF;
                    if (mixedAlpha == 255) {
                        target.setRGB(x, y, mixedColor);
                    } else if (mixedAlpha != 0) {
                        target.setRGB(x, y, ColorUtil.blendColors(target.getRGB(x, y), mixedColor));
                    }
                }
            }
        }
    }

    public Rectangle getBounds() {
        return new Rectangle(
                (int) Math.min(Math.min(this.p1.x, this.p2.x), this.p3.x),
                (int) Math.min(Math.min(this.p1.y, this.p2.y), this.p3.y),
                (int) Math.max(Math.max(this.p1.x, this.p2.x), this.p3.x),
                (int) Math.max(Math.max(this.p1.y, this.p2.y), this.p3.y)
        );
    }

    public boolean isInTriangle(final float x, final float y) {
        double s = this.p1.y * this.p3.x - this.p1.x * this.p3.y + (this.p3.y - this.p1.y) * x + (this.p1.x - this.p3.x) * y;
        double t = this.p1.x * this.p2.y - this.p1.y * this.p2.x + (this.p1.y - this.p2.y) * x + (this.p2.x - this.p1.x) * y;

        if ((s < 0) != (t < 0)) return false;

        double A = -this.p2.y * this.p3.x + this.p1.y * (this.p3.x - this.p2.x) + this.p1.x * (this.p2.y - this.p3.y) + this.p2.x * this.p3.y;

        return A < 0 ? (s <= 0 && s + t >= A) : (s >= 0 && s + t <= A);
    }

}
