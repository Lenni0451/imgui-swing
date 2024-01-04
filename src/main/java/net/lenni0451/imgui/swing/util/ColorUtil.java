package net.lenni0451.imgui.swing.util;

public class ColorUtil {

    public static int mix(final int color1, final int color2) {
        final int a1 = color1 >> 24 & 0xFF;
        final int r1 = color1 >> 16 & 0xFF;
        final int g1 = color1 >> 8 & 0xFF;
        final int b1 = color1 & 0xFF;
        final int a2 = color2 >> 24 & 0xFF;
        final int r2 = color2 >> 16 & 0xFF;
        final int g2 = color2 >> 8 & 0xFF;
        final int b2 = color2 & 0xFF;
        final int a = (int) (a1 * a2 / 255F);
        final int r = (int) (r1 * r2 / 255F);
        final int g = (int) (g1 * g2 / 255F);
        final int b = (int) (b1 * b2 / 255F);

        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    public static int blendColors(final int color1, final int color2) {
        final int a1 = color1 >> 24 & 0xFF;
        final int r1 = color1 >> 16 & 0xFF;
        final int g1 = color1 >> 8 & 0xFF;
        final int b1 = color1 & 0xFF;
        final int a2 = color2 >> 24 & 0xFF;
        final int r2 = color2 >> 16 & 0xFF;
        final int g2 = color2 >> 8 & 0xFF;
        final int b2 = color2 & 0xFF;

        final float alpha = a2 / 255.0f;

        final int blendedRed = (int) ((1 - alpha) * r1 + alpha * r2);
        final int blendedGreen = (int) ((1 - alpha) * g1 + alpha * g2);
        final int blendedBlue = (int) ((1 - alpha) * b1 + alpha * b2);

        return (255 << 24) | (blendedRed << 16) | (blendedGreen << 8) | blendedBlue;
    }

}
