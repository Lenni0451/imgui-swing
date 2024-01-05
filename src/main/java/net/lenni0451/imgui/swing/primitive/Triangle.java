package net.lenni0451.imgui.swing.primitive;

import net.lenni0451.imgui.swing.util.ColorUtil;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

public class Triangle {

    private final Vertex p1;
    private final Vertex p2;
    private final Vertex p3;
    private final BufferedImage texture;

    public Triangle(final Vertex p1, final Vertex p2, final Vertex p3, final BufferedImage texture) {
        this.p1 = p1;
        this.p2 = p2;
        this.p3 = p3;
        this.texture = texture;
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

    public BufferedImage getTexture() {
        return this.texture;
    }

    public void draw(final BufferedImage target, final Rectangle clipRect) {
        final int[] targetBuffer = ((DataBufferInt) target.getRaster().getDataBuffer()).getData();
        final int[] textureBuffer = ((DataBufferInt) this.texture.getRaster().getDataBuffer()).getData();

        final int minX = (int) Math.min(Math.min(this.p1.x, this.p2.x), this.p3.x);
        final int minY = (int) Math.min(Math.min(this.p1.y, this.p2.y), this.p3.y);
        final int maxX = (int) Math.max(Math.max(this.p1.x, this.p2.x), this.p3.x);
        final int maxY = (int) Math.max(Math.max(this.p1.y, this.p2.y), this.p3.y);
        final int frameWidth = target.getWidth();
        final int textureWidth = this.texture.getWidth();
        final int textureHeight = this.texture.getHeight();
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                if (clipRect.contains(x, y) && this.isInTriangle(x, y)) {
                    final double w = ((this.p2.y - this.p3.y) * (x - this.p3.x) + (this.p3.x - this.p2.x) * (y - this.p3.y)) / ((this.p2.y - this.p3.y) * (this.p1.x - this.p3.x) + (this.p3.x - this.p2.x) * (this.p1.y - this.p3.y));
                    final double u = ((this.p3.y - this.p1.y) * (x - this.p3.x) + (this.p1.x - this.p3.x) * (y - this.p3.y)) / ((this.p2.y - this.p3.y) * (this.p1.x - this.p3.x) + (this.p3.x - this.p2.x) * (this.p1.y - this.p3.y));
                    final double v = 1 - u - w;

                    final int a = (int) (w * (this.p1.color >> 24 & 0xFF) + u * (this.p2.color >> 24 & 0xFF) + v * (this.p3.color >> 24 & 0xFF));
                    if (a == 0) continue;

                    final int b = (int) (w * (this.p1.color >> 16 & 0xFF) + u * (this.p2.color >> 16 & 0xFF) + v * (this.p3.color >> 16 & 0xFF));
                    final int g = (int) (w * (this.p1.color >> 8 & 0xFF) + u * (this.p2.color >> 8 & 0xFF) + v * (this.p3.color >> 8 & 0xFF));
                    final int r = (int) (w * (this.p1.color & 0xFF) + u * (this.p2.color & 0xFF) + v * (this.p3.color & 0xFF));
                    final int vertexColor = (a << 24) | (r << 16) | (g << 8) | b;

                    final int textureX = (int) Math.round(w * this.p1.u * textureWidth + u * this.p2.u * textureWidth + v * this.p3.u * textureWidth);
                    final int textureY = (int) Math.round(w * this.p1.v * textureHeight + u * this.p2.v * textureHeight + v * this.p3.v * textureHeight);
                    if (textureX < 0 || textureY < 0 || textureX >= textureWidth || textureY >= textureHeight) continue;
                    final int textureColor = textureBuffer[textureX + textureY * textureWidth];
                    if ((textureColor >> 24 & 0xFF) == 0) continue;

                    final int mixedColor = ColorUtil.mix(vertexColor, textureColor);
                    final int mixedAlpha = mixedColor >> 24 & 0xFF;
                    final int bufferIndex = x + y * frameWidth;
                    if (mixedAlpha == 255) {
                        targetBuffer[bufferIndex] = mixedColor;
                    } else if (mixedAlpha != 0) {
                        targetBuffer[bufferIndex] = ColorUtil.blendColors(targetBuffer[bufferIndex], mixedColor);
                    }
                }
            }
        }
    }

    public boolean isInTriangle(final float x, final float y) {
        double s = this.p1.y * this.p3.x - this.p1.x * this.p3.y + (this.p3.y - this.p1.y) * x + (this.p1.x - this.p3.x) * y;
        double t = this.p1.x * this.p2.y - this.p1.y * this.p2.x + (this.p1.y - this.p2.y) * x + (this.p2.x - this.p1.x) * y;
        if ((s < 0) != (t < 0)) return false;

        double a = -this.p2.y * this.p3.x + this.p1.y * (this.p3.x - this.p2.x) + this.p1.x * (this.p2.y - this.p3.y) + this.p2.x * this.p3.y;
        return a < 0 ? (s <= 0 && s + t >= a) : (s >= 0 && s + t <= a);
    }

}
