package net.lenni0451.imgui.swing.renderer;

import imgui.ImDrawData;
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
        this.target = target;
        this.graphics2D = target.createGraphics();
    }

    public void clear() {
        this.graphics2D.setBackground(CLEAR_COLOR);
        this.graphics2D.clearRect(0, 0, this.target.getWidth(), this.target.getHeight());
    }

    public void draw(final ImDrawData drawData) {
        for (int cmdListI = 0; cmdListI < drawData.getCmdListsCount(); cmdListI++) {
            final WrappedBuffer vertexBuffer = new WrappedBuffer(drawData.getCmdListVtxBufferData(cmdListI));
            final WrappedBuffer indexBuffer = new WrappedBuffer(drawData.getCmdListIdxBufferData(cmdListI));
            for (int cmdBufferI = 0; cmdBufferI < drawData.getCmdListCmdBufferSize(cmdListI); cmdBufferI++) {
                final int textureId = drawData.getCmdListCmdBufferTextureId(cmdListI, cmdBufferI);
                final int elementCount = drawData.getCmdListCmdBufferElemCount(cmdListI, cmdBufferI);
                final int indexBufferOffset = drawData.getCmdListCmdBufferIdxOffset(cmdListI, cmdBufferI);
                final int vertexBufferOffset = drawData.getCmdListCmdBufferVtxOffset(cmdListI, cmdBufferI);
                final int indices = indexBufferOffset * ImDrawData.SIZEOF_IM_DRAW_IDX;

                for (int i = 0, x = 0; i < elementCount / 3; i++, x += 6) {
                    final Vertex left = vertexBuffer.getVertex((indexBuffer.getUnsignedShort(indices + x) + vertexBufferOffset) * ImDrawData.SIZEOF_IM_DRAW_VERT);
                    final Vertex middle = vertexBuffer.getVertex((indexBuffer.getUnsignedShort(indices + x + ImDrawData.SIZEOF_IM_DRAW_IDX) + vertexBufferOffset) * ImDrawData.SIZEOF_IM_DRAW_VERT);
                    final Vertex right = vertexBuffer.getVertex((indexBuffer.getUnsignedShort(indices + x + ImDrawData.SIZEOF_IM_DRAW_IDX * 2) + vertexBufferOffset) * ImDrawData.SIZEOF_IM_DRAW_VERT);
                    final Triangle triangle = new Triangle(left, middle, right, textureId);
                    triangle.draw(this.target);
                }
            }
        }
    }

    public BufferedImage getTarget() {
        return this.target;
    }

}
