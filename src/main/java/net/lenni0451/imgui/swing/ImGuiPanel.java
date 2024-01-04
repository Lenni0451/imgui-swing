package net.lenni0451.imgui.swing;

import imgui.ImDrawData;
import imgui.ImGui;
import imgui.ImGuiIO;
import net.lenni0451.imgui.swing.renderer.ImageDrawer;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class ImGuiPanel extends JPanel {

    private long lastFrame = 0;

    public ImGuiPanel() {
        ImGuiContext.init();
    }

    @Override
    public void paint(Graphics g) {
        ImGuiIO io = ImGui.getIO();
        io.setDisplaySize(this.getWidth(), this.getHeight());
        if (this.lastFrame == 0) io.setDeltaTime(1F / 60F);
        else io.setDeltaTime((System.currentTimeMillis() - this.lastFrame) / 1000F);
        this.lastFrame = System.currentTimeMillis();

        ImGui.newFrame();
        this.render();
        ImGui.render();

        ImDrawData data = ImGui.getDrawData();
        BufferedImage frame = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_ARGB);
        final ImageDrawer imageDrawer = new ImageDrawer(frame);
        imageDrawer.clear();
        imageDrawer.draw(data);
        g.drawImage(frame, 0, 0, null);
        this.repaint();
    }

    protected void render() {
        ImGui.showDemoWindow();
    }

}
