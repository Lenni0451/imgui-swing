package net.lenni0451.imgui.swing;

import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.ImGuiMouseButton;
import imgui.flag.ImGuiMouseCursor;
import net.raphimc.softwarerenderer.swing.SoftwareRendererCanvas;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class ImGuiCanvas extends SoftwareRendererCanvas<ImGuiSoftwareRenderer> {

    private static final Cursor[] MOUSE_CURSORS = new Cursor[ImGuiMouseCursor.COUNT];
    private static final Cursor HIDDEN_CURSOR;

    static {
        MOUSE_CURSORS[ImGuiMouseCursor.Arrow] = new Cursor(Cursor.DEFAULT_CURSOR);
        MOUSE_CURSORS[ImGuiMouseCursor.TextInput] = new Cursor(Cursor.TEXT_CURSOR);
        MOUSE_CURSORS[ImGuiMouseCursor.ResizeAll] = new Cursor(Cursor.MOVE_CURSOR);
        MOUSE_CURSORS[ImGuiMouseCursor.ResizeNS] = new Cursor(Cursor.N_RESIZE_CURSOR);
        MOUSE_CURSORS[ImGuiMouseCursor.ResizeEW] = new Cursor(Cursor.E_RESIZE_CURSOR);
        MOUSE_CURSORS[ImGuiMouseCursor.ResizeNESW] = new Cursor(Cursor.NE_RESIZE_CURSOR);
        MOUSE_CURSORS[ImGuiMouseCursor.ResizeNWSE] = new Cursor(Cursor.NW_RESIZE_CURSOR);
        MOUSE_CURSORS[ImGuiMouseCursor.Hand] = new Cursor(Cursor.HAND_CURSOR);
        MOUSE_CURSORS[ImGuiMouseCursor.NotAllowed] = new Cursor(Cursor.DEFAULT_CURSOR);
        BufferedImage cursorImg = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        HIDDEN_CURSOR = Toolkit.getDefaultToolkit().createCustomCursor(cursorImg, new Point(0, 0), "hidden");
    }

    private long lastFrame = 0;

    public ImGuiCanvas() {
        super(ImGuiSoftwareRenderer::new);
        ImGuiContext.init(this::init);

        this.setFocusable(true);
        this.setFocusTraversalKeysEnabled(false);
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
                        break;
                    case MouseEvent.BUTTON2:
                        io.setMouseDown(ImGuiMouseButton.Middle, down);
                        break;
                    case MouseEvent.BUTTON3:
                        io.setMouseDown(ImGuiMouseButton.Right, down);
                        break;
                }
            }
        });
        this.addMouseWheelListener(e -> {
            ImGuiIO io = ImGui.getIO();
            io.setMouseWheel(io.getMouseWheel() - e.getWheelRotation());
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

    /**
     * <b>Called before the implementation constructor!</b>
     */
    protected void init() {
    }

    @Override
    protected void render(final ImGuiSoftwareRenderer renderer) {
        ImGuiIO io = ImGui.getIO();
        int width = this.getWidth();
        int height = this.getHeight();
        io.setDisplaySize(width, height);
        try {
            Point mousePos = this.getMousePosition();
            if (mousePos != null) io.setMousePos(mousePos.x, mousePos.y);
        } catch (Throwable ignored) {
        }
        if (ImGui.getMouseCursor() == ImGuiMouseCursor.None || io.getMouseDrawCursor()) this.setCursor(HIDDEN_CURSOR);
        else this.setCursor(MOUSE_CURSORS[ImGui.getMouseCursor()]);
        if (this.lastFrame == 0) io.setDeltaTime(1F / 60F);
        else io.setDeltaTime((System.currentTimeMillis() - this.lastFrame) / 1000F);
        this.lastFrame = System.currentTimeMillis();

        ImGui.newFrame();
        this.render();
        ImGui.render();

        renderer.drawImDrawData(ImGui.getDrawData());
    }

    protected void render() {
        ImGui.showDemoWindow();
        //ImPlot.showDemoWindow(new ImBoolean());
    }

    @Override
    protected Color getClearColor() {
        return Color.WHITE;
    }

}
