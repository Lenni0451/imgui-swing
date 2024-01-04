package net.lenni0451.imgui;

import imgui.*;
import imgui.callback.ImStrConsumer;
import imgui.callback.ImStrSupplier;
import imgui.flag.ImGuiBackendFlags;
import imgui.flag.ImGuiKey;
import imgui.type.ImInt;
import net.lenni0451.imgui.swing.TextureManager;
import net.lenni0451.imgui.swing.renderer.ImageDrawer;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;

public class Main {

    public static void main(String[] args) throws Throwable {
        ImGui.createContext();

        ImGuiIO io = ImGui.getIO();
        io.addBackendFlags(ImGuiBackendFlags.None);
        io.setBackendPlatformName("imgui_java_impl_swing");
        io.setBackendRendererName("imgui_java_impl_swing");
        io.setKeyMap(getKeyMap());

        io.setGetClipboardTextFn(new ImStrSupplier() {
            @Override
            public String get() {
                String clipboard = "";
                Clipboard systemClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                if (systemClipboard.isDataFlavorAvailable(DataFlavor.stringFlavor)) {
                    try {
                        clipboard = (String) systemClipboard.getData(DataFlavor.stringFlavor);
                    } catch (Throwable ignored) {
                    }
                }
                return clipboard;
            }
        });
        io.setSetClipboardTextFn(new ImStrConsumer() {
            @Override
            public void accept(String str) {
                StringSelection stringSelection = new StringSelection(str);
                Clipboard systemClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                systemClipboard.setContents(stringSelection, stringSelection);
            }
        });

        ImGuiPlatformIO platformIO = ImGui.getPlatformIO();
        platformIO.resizeMonitors(0);
        //TODO: Push monitors
        {
//            platformIO.pushMonitors(0, 0, 1280, 720, 0, 0, 1280, 730, 750);
        }

        ImGuiViewport mainViewport = ImGui.getMainViewport();
        mainViewport.setPlatformHandle(0);

        io.setDisplaySize(1280, 720);
        io.setDisplayFramebufferScale(1, 1);
        io.setDeltaTime(1 / 60F);
        ImFontAtlas fonts = io.getFonts();
        ImInt width = new ImInt();
        ImInt height = new ImInt();
        ByteBuffer buffer = fonts.getTexDataAsRGBA32(width, height);
        int fontTextureId = TextureManager.createRGBA(buffer, width.get(), height.get());
        fonts.setTexID(fontTextureId);

        ImGui.newFrame();
        ImGui.showDemoWindow();
//        ImGui.begin("Hello World", ImGuiWindowFlags.NoCollapse);
//        ImGui.text("Hello World");
//        ImGui.end();
        ImGui.render();

        long start = System.nanoTime();
        ImDrawData data = ImGui.getDrawData();
        BufferedImage frame = new BufferedImage((int) data.getDisplaySizeX(), (int) data.getDisplaySizeY(), BufferedImage.TYPE_INT_ARGB);
        final ImageDrawer imageDrawer = new ImageDrawer(frame);
        imageDrawer.clear();
        imageDrawer.draw(data);
        System.out.println("Took " + (System.nanoTime() - start) / 1000000F + "ms");
        ImageIO.write(frame, "png", new File("test.png"));
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

}
