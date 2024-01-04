package net.lenni0451.imgui.swing;

import imgui.ImFontAtlas;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.ImGuiPlatformIO;
import imgui.callback.ImStrConsumer;
import imgui.callback.ImStrSupplier;
import imgui.flag.ImGuiBackendFlags;
import imgui.flag.ImGuiKey;
import imgui.type.ImInt;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicBoolean;

public class ImGuiContext {

    private static final AtomicBoolean initialized = new AtomicBoolean(false);

    public static void init() {
        if (initialized.getAndSet(true)) return;
        ImGui.createContext();

        ImGuiIO io = ImGui.getIO();
        io.addBackendFlags(ImGuiBackendFlags.None);
        io.setBackendPlatformName("imgui_java_impl_swing");
        io.setBackendRendererName("imgui_java_impl_buffered_image");

        io.setKeyMap(getKeyMap());
        setClipboardFunctions(io);
        initMonitors(io);
        initFonts(io);
        ImGui.getMainViewport().setPlatformHandle(0);
        io.setDisplayFramebufferScale(1, 1);
    }

    private static int[] getKeyMap() {
        int[] keyMap = new int[ImGuiKey.COUNT];
        keyMap[ImGuiKey.Tab] = KeyEvent.VK_TAB;
        keyMap[ImGuiKey.LeftArrow] = KeyEvent.VK_LEFT;
        keyMap[ImGuiKey.RightArrow] = KeyEvent.VK_RIGHT;
        keyMap[ImGuiKey.UpArrow] = KeyEvent.VK_UP;
        keyMap[ImGuiKey.DownArrow] = KeyEvent.VK_DOWN;
        keyMap[ImGuiKey.PageUp] = KeyEvent.VK_PAGE_UP;
        keyMap[ImGuiKey.PageDown] = KeyEvent.VK_PAGE_DOWN;
        keyMap[ImGuiKey.Home] = KeyEvent.VK_HOME;
        keyMap[ImGuiKey.End] = KeyEvent.VK_END;
        keyMap[ImGuiKey.Insert] = KeyEvent.VK_INSERT;
        keyMap[ImGuiKey.Delete] = KeyEvent.VK_DELETE;
        keyMap[ImGuiKey.Backspace] = KeyEvent.VK_BACK_SPACE;
        keyMap[ImGuiKey.Space] = KeyEvent.VK_SPACE;
        keyMap[ImGuiKey.Enter] = KeyEvent.VK_ENTER;
        keyMap[ImGuiKey.Escape] = KeyEvent.VK_ESCAPE;
        keyMap[ImGuiKey.KeyPadEnter] = KeyEvent.VK_ENTER; // KeyEvent doesn't distinguish between Enter and KeyPadEnter
        keyMap[ImGuiKey.A] = KeyEvent.VK_A;
        keyMap[ImGuiKey.C] = KeyEvent.VK_C;
        keyMap[ImGuiKey.V] = KeyEvent.VK_V;
        keyMap[ImGuiKey.X] = KeyEvent.VK_X;
        keyMap[ImGuiKey.Y] = KeyEvent.VK_Y;
        keyMap[ImGuiKey.Z] = KeyEvent.VK_Z;
        return keyMap;
    }

    private static void setClipboardFunctions(final ImGuiIO io) {
        Clipboard systemClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        io.setSetClipboardTextFn(new ImStrConsumer() {
            @Override
            public void accept(String str) {
                StringSelection content = new StringSelection(str);
                systemClipboard.setContents(content, content);
            }
        });
        io.setGetClipboardTextFn(new ImStrSupplier() {
            @Override
            public String get() {
                String content = "";
                if (systemClipboard.isDataFlavorAvailable(DataFlavor.stringFlavor)) {
                    try {
                        content = (String) systemClipboard.getData(DataFlavor.stringFlavor);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return content;
            }
        });
    }

    private static void initMonitors(final ImGuiIO io) {
        ImGuiPlatformIO platformIO = ImGui.getPlatformIO();
        platformIO.resizeMonitors(0);
        for (GraphicsDevice device : GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()) {
            Rectangle bounds = device.getDefaultConfiguration().getBounds();
            DisplayMode displayMode = device.getDisplayMode();
            float scale = (float) displayMode.getWidth() / (float) bounds.width;
            platformIO.pushMonitors(bounds.x, bounds.y, displayMode.getWidth(), displayMode.getHeight(), bounds.x, bounds.y, displayMode.getWidth(), displayMode.getHeight(), scale);
        }
    }

    private static void initFonts(final ImGuiIO io) {
        ImFontAtlas fontAtlas = io.getFonts();
        ImInt width = new ImInt();
        ImInt height = new ImInt();
        ByteBuffer buffer = fontAtlas.getTexDataAsRGBA32(width, height);
        int fontTextureId = TextureManager.createRGBA(buffer, width.get(), height.get());
        fontAtlas.setTexID(fontTextureId);
    }

}
