package net.lenni0451.imgui.swing;

import imgui.ImDrawData;
import imgui.ImVec2;
import imgui.ImVec4;
import net.lenni0451.imgui.swing.util.WrappedBuffer;
import net.raphimc.softwarerenderer.SoftwareRenderer;
import net.raphimc.softwarerenderer.data.ClipRect;
import net.raphimc.softwarerenderer.data.ImageBuffer;
import net.raphimc.softwarerenderer.enums.CullFace;
import net.raphimc.softwarerenderer.primitives.Triangle;
import net.raphimc.softwarerenderer.vertex.Vertex;

import java.util.ArrayList;
import java.util.List;

public class ImGuiSoftwareRenderer extends SoftwareRenderer {

    public ImGuiSoftwareRenderer(final int width, final int height) {
        super(width, height);
        this.setDepthEnabled(false);
        this.setCullFace(CullFace.NONE);
    }

    public void drawImDrawData(final ImDrawData drawData) {
        final int width = this.getImage().getWidth();
        final int height = this.getImage().getHeight();
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
                final float clipMinX = Math.max(0, (clipRect.x - clipOffX) * clipScaleX);
                final float clipMinY = Math.max(0, (clipRect.y - clipOffY) * clipScaleY);
                final float clipMaxX = Math.min(width, (clipRect.z - clipOffX) * clipScaleX);
                final float clipMaxY = Math.min(height, (clipRect.w - clipOffY) * clipScaleY);
                if (clipMaxX <= clipMinX || clipMaxY <= clipMinY) continue;
                final ClipRect clip = new ClipRect((int) clipMinX, (int) clipMinY, (int) clipMaxX, (int) clipMaxY);
                final ImageBuffer texture = new ImageBuffer(TextureManager.get(textureId));

                final List<Triangle> triangles = new ArrayList<>();
                for (int i = 0, x = 0; i < elementCount / 3; i++, x += 6) {
                    final Vertex left = vertexBuffer.getVertex((indexBuffer.getUnsignedShort(indices + x) + vertexBufferOffset) * ImDrawData.SIZEOF_IM_DRAW_VERT);
                    final Vertex middle = vertexBuffer.getVertex((indexBuffer.getUnsignedShort(indices + x + ImDrawData.SIZEOF_IM_DRAW_IDX) + vertexBufferOffset) * ImDrawData.SIZEOF_IM_DRAW_VERT);
                    final Vertex right = vertexBuffer.getVertex((indexBuffer.getUnsignedShort(indices + x + ImDrawData.SIZEOF_IM_DRAW_IDX * 2) + vertexBufferOffset) * ImDrawData.SIZEOF_IM_DRAW_VERT);
                    triangles.add(new Triangle(left, middle, right, texture));
                }

                this.setClipRect(clip);
                this.draw2DPrimitives(triangles);
            }
        }
    }

}
