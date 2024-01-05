package net.lenni0451.imgui.swing.renderer;

import imgui.ImDrawData;
import imgui.ImVec2;
import imgui.ImVec4;
import net.lenni0451.imgui.swing.TextureManager;
import net.lenni0451.imgui.swing.primitive.Triangle;
import net.lenni0451.imgui.swing.primitive.Vertex;
import net.lenni0451.imgui.swing.util.WrappedBuffer;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ImageDrawer {

    private static final Color CLEAR_COLOR = new Color(0, 0, 0, 0);

    private final BufferedImage target;
    private final Graphics2D graphics2D;

    public ImageDrawer(final BufferedImage target) {
        if (target.getType() != BufferedImage.TYPE_INT_ARGB) {
            throw new IllegalArgumentException("Target image must be of type TYPE_INT_ARGB");
        }

        this.target = target;
        this.graphics2D = target.createGraphics();
    }

    public void clear() {
        this.graphics2D.setBackground(CLEAR_COLOR);
        this.graphics2D.clearRect(0, 0, this.target.getWidth(), this.target.getHeight());
    }

    public void draw(final ImDrawData drawData) {
        final ImVec2 displayPos = drawData.getDisplayPos();
        final ImVec2 framebufferScale = drawData.getFramebufferScale();
        final float clipOffX = displayPos.x;
        final float clipOffY = displayPos.y;
        final float clipScaleX = framebufferScale.x;
        final float clipScaleY = framebufferScale.y;
        for (int cmdListI = 0; cmdListI < drawData.getCmdListsCount(); cmdListI++) {
            final WrappedBuffer vertexBuffer = new WrappedBuffer(drawData.getCmdListVtxBufferData(cmdListI));
            final WrappedBuffer indexBuffer = new WrappedBuffer(drawData.getCmdListIdxBufferData(cmdListI));
            for (int cmdBufferI = 0; cmdBufferI < drawData.getCmdListCmdBufferSize(cmdListI); cmdBufferI++) {
                final int textureId = drawData.getCmdListCmdBufferTextureId(cmdListI, cmdBufferI);
                final int elementCount = drawData.getCmdListCmdBufferElemCount(cmdListI, cmdBufferI);
                final int indexBufferOffset = drawData.getCmdListCmdBufferIdxOffset(cmdListI, cmdBufferI);
                final int vertexBufferOffset = drawData.getCmdListCmdBufferVtxOffset(cmdListI, cmdBufferI);
                final int indices = indexBufferOffset * ImDrawData.SIZEOF_IM_DRAW_IDX;

                final ImVec4 clipRect = drawData.getCmdListCmdBufferClipRect(cmdListI, cmdBufferI);
                final float clipMinX = (clipRect.x - clipOffX) * clipScaleX;
                final float clipMinY = (clipRect.y - clipOffY) * clipScaleY;
                final float clipMaxX = (clipRect.z - clipOffX) * clipScaleX;
                final float clipMaxY = (clipRect.w - clipOffY) * clipScaleY;
                if (clipMaxX <= clipMinX || clipMaxY <= clipMinY) continue;
                final Rectangle clip = new Rectangle((int) clipMinX, (int) clipMinY, (int) (clipMaxX - clipMinX), (int) (clipMaxY - clipMinY));
                final BufferedImage texture = TextureManager.get(textureId);

                for (int i = 0, x = 0; i < elementCount / 3; i++, x += 6) {
                    final Vertex left = vertexBuffer.getVertex((indexBuffer.getUnsignedShort(indices + x) + vertexBufferOffset) * ImDrawData.SIZEOF_IM_DRAW_VERT);
                    final Vertex middle = vertexBuffer.getVertex((indexBuffer.getUnsignedShort(indices + x + ImDrawData.SIZEOF_IM_DRAW_IDX) + vertexBufferOffset) * ImDrawData.SIZEOF_IM_DRAW_VERT);
                    final Vertex right = vertexBuffer.getVertex((indexBuffer.getUnsignedShort(indices + x + ImDrawData.SIZEOF_IM_DRAW_IDX * 2) + vertexBufferOffset) * ImDrawData.SIZEOF_IM_DRAW_VERT);
                    final Triangle triangle = new Triangle(left, middle, right, texture);
                    triangle.draw(this.target, clip);
                }
            }
        }
    }

    public BufferedImage getTarget() {
        return this.target;
    }

}
