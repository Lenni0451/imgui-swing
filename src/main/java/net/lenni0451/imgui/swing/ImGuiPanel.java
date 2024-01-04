package net.lenni0451.imgui.swing;

import imgui.ImDrawData;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.extension.implot.ImPlot;
import imgui.flag.ImGuiMouseButton;
import imgui.type.ImBoolean;
import net.lenni0451.imgui.swing.renderer.ImageDrawer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class ImGuiPanel extends JPanel {

    private long lastFrame = 0;

    public ImGuiPanel() {
        ImGuiContext.init();

        this.setFocusable(true);
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                this.update(e.getButton(), true);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                this.update(e.getButton(), false);
            }

            private void update(final int mouseButton, final boolean down) {
                ImGuiIO io = ImGui.getIO();
                switch (mouseButton) {
                    case MouseEvent.BUTTON1:
                        io.setMouseDown(ImGuiMouseButton.Left, down);
                    case MouseEvent.BUTTON2:
                        io.setMouseDown(ImGuiMouseButton.Middle, down);
                    case MouseEvent.BUTTON3:
                        io.setMouseDown(ImGuiMouseButton.Right, down);
                }
            }
        });
        this.addMouseWheelListener(e -> {
            ImGuiIO io = ImGui.getIO();
            io.setMouseWheel(-e.getWheelRotation());
        });
        this.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                ImGuiIO io = ImGui.getIO();
                if (e.getKeyChar() == KeyEvent.CHAR_UNDEFINED) return;
                if (e.getKeyChar() == '\n') return;
                io.addInputCharacter(e.getKeyChar());
            }

            @Override
            public void keyPressed(KeyEvent e) {
                this.update(e, true);
            }

            @Override
            public void keyReleased(KeyEvent e) {
                this.update(e, false);
            }

            private void update(final KeyEvent e, final boolean pressed) {
                ImGuiIO io = ImGui.getIO();
                io.setKeysDown(e.getKeyCode(), pressed);
                io.setKeyCtrl(e.isControlDown());
                io.setKeyShift(e.isShiftDown());
                io.setKeyAlt(e.isAltDown());
                io.setKeySuper(e.isMetaDown());
                e.consume();
            }
        });
    }

    @Override
    public void paint(Graphics g) {
        ImGuiIO io = ImGui.getIO();
        try {
            Point mousePos = this.getMousePosition();
            if (mousePos != null) io.setMousePos(mousePos.x, mousePos.y);
        } catch (Throwable ignored) {
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
//        ImGui.showDemoWindow();
        ImPlot.showDemoWindow(new ImBoolean());
    }

}
