package net.lenni0451.imgui.swing;

import net.raphimc.softwarerenderer.util.ImageUtil;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

public class TextureManager {

    private static final Map<Integer, BufferedImage> textures = new HashMap<>();
    private static int nextId = 1;

    public static int createRGBA(final ByteBuffer buffer, final int width, final int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        int[] pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
        for (int i = 0; i < pixels.length; i++) {
            int r = buffer.get() & 0xFF;
            int g = buffer.get() & 0xFF;
            int b = buffer.get() & 0xFF;
            int a = buffer.get() & 0xFF;
            pixels[i] = (a << 24) | (r << 16) | (g << 8) | b;
        }
        return create(image);
    }

    public static int create(BufferedImage texture) {
        int id = nextId++;
        textures.put(id, ImageUtil.ensureArgb(texture));
        return id;
    }

    public static void update(final int id, final BufferedImage texture) {
        textures.put(id, texture);
    }

    public static void destroy(final int id) {
        textures.remove(id);
    }

    public static BufferedImage get(final int id) {
        return textures.get(id);
    }

}
