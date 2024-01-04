package net.lenni0451.imgui.swing;

import imgui.ImDrawData;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.ImGuiMouseButton;
import net.lenni0451.imgui.swing.renderer.ImageDrawer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class ImGuiPanel extends JPanel {

    private long lastFrame = 0;

    public ImGuiPanel() {
        ImGuiContext.init();

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                ImGuiIO io = ImGui.getIO();
                int button = this.mapButton(e.getButton());
                if (button != -1) io.setMouseDown(button, true);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                ImGuiIO io = ImGui.getIO();
                int button = this.mapButton(e.getButton());
                if (button != -1) io.setMouseDown(button, false);
            }

            private int mapButton(final int button) {
                switch (button) {
                    case MouseEvent.BUTTON1:
                        return ImGuiMouseButton.Left;
                    case MouseEvent.BUTTON2:
                        return ImGuiMouseButton.Middle;
                    case MouseEvent.BUTTON3:
                        return ImGuiMouseButton.Right;
                    default:
                        return -1;
                }
            }
        });
        this.addMouseWheelListener(e -> {
            ImGuiIO io = ImGui.getIO();
            io.setMouseWheelH(io.getMouseWheelH() + e.getWheelRotation());
            io.setMouseWheel(-(io.getMouseWheel() + e.getWheelRotation()));
        });
    }

    @Override
    public void paint(Graphics g) {
        ImGuiIO io = ImGui.getIO();
        try {
            Point mousePos = this.getMousePosition();
            if (mousePos != null) io.setMousePos(mousePos.x, mousePos.y);
            else io.setMousePos(-1, -1);
        } catch (Throwable ignored) {
            io.setMousePos(-1, -1);
        }
        io.setDisplaySize(this.getWidth(), this.getHeight());
        if (this.lastFrame == 0) io.setDeltaTime(1F / 60F);
        else io.setDeltaTime((System.currentTimeMillis() - this.lastFrame) / 1000F);
        this.lastFrame = System.currentTimeMillis();
//        System.out.println("FPS: " + (1 / io.getDeltaTime()) + " (" + io.getDeltaTime() + ")");

        ImGui.newFrame();
        this.render();
        ImGui.render();

        ImDrawData data = ImGui.getDrawData();
        BufferedImage frame = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_ARGB);
        ImageDrawer imageDrawer = new ImageDrawer(frame);
        imageDrawer.clear();
        imageDrawer.draw(data);
        g.clearRect(0, 0, this.getWidth(), this.getHeight());
        g.drawImage(frame, 0, 0, null);
        this.repaint();
    }

    protected void render() {
        ImGui.showDemoWindow();
        //ImPlot.showDemoWindow(new ImBoolean());
    }

}
