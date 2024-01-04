package net.lenni0451.imgui;

import imgui.*;
import imgui.callback.ImStrConsumer;
import imgui.callback.ImStrSupplier;
import imgui.flag.ImGuiBackendFlags;
import imgui.flag.ImGuiKey;
import imgui.type.ImInt;
import net.lenni0451.imgui.swing.TextureManager;
import net.lenni0451.imgui.swing.Triangle;
import net.lenni0451.imgui.swing.Vertex;
import net.lenni0451.imgui.swing.WrappedBuffer;

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
        Graphics2D g2d = frame.createGraphics();
        for (int cmdListI = 0; cmdListI < data.getCmdListsCount(); cmdListI++) {
            WrappedBuffer vertexBuffer = new WrappedBuffer(data.getCmdListVtxBufferData(cmdListI));
            WrappedBuffer indexBuffer = new WrappedBuffer(data.getCmdListIdxBufferData(cmdListI));
            for (int cmdBufferI = 0; cmdBufferI < data.getCmdListCmdBufferSize(cmdListI); cmdBufferI++) {
                int textureId = data.getCmdListCmdBufferTextureId(cmdListI, cmdBufferI);
                int elementCount = data.getCmdListCmdBufferElemCount(cmdListI, cmdBufferI);
                int indexBufferOffset = data.getCmdListCmdBufferIdxOffset(cmdListI, cmdBufferI);
                int vertexBufferOffset = data.getCmdListCmdBufferVtxOffset(cmdListI, cmdBufferI);
                int indices = indexBufferOffset * ImDrawData.SIZEOF_IM_DRAW_IDX;
                if (vertexBufferOffset != 0) throw new IllegalStateException("vertex buffer offset is not 0");

                for (int i = 0, x = 0; i < elementCount / 3; i++, x += 6) {
                    Vertex left = vertexBuffer.getVertex(indexBuffer.getUnsignedShort(indices + x) * ImDrawData.SIZEOF_IM_DRAW_VERT);
                    Vertex middle = vertexBuffer.getVertex(indexBuffer.getUnsignedShort(indices + x + 2) * ImDrawData.SIZEOF_IM_DRAW_VERT);
                    Vertex right = vertexBuffer.getVertex(indexBuffer.getUnsignedShort(indices + x + 4) * ImDrawData.SIZEOF_IM_DRAW_VERT);
                    draw(frame, g2d, new Triangle(left, middle, right, textureId));
                }
            }
        }
        g2d.dispose();
        System.out.println("Took " + (System.nanoTime() - start) / 1000000F + "ms");
        ImageIO.write(frame, "png", new File("test.png"));
    }

    private static void draw(final BufferedImage image, final Graphics2D g2d, final Triangle triangle) {
        BufferedImage texture = TextureManager.get(triangle.getTextureId());
        Vertex p1 = triangle.getP1();
        Vertex p2 = triangle.getP2();
        Vertex p3 = triangle.getP3();
        Rectangle bounds = triangle.getBounds();
        for (int x = bounds.x; x <= bounds.width; x++) {
            for (int y = bounds.y; y <= bounds.height; y++) {
                if (triangle.isInTriangle(x, y)) {
                    double w = ((p2.getY() - p3.getY()) * (x - p3.getX()) + (p3.getX() - p2.getX()) * (y - p3.getY())) /
                            ((p2.getY() - p3.getY()) * (p1.getX() - p3.getX()) + (p3.getX() - p2.getX()) * (p1.getY() - p3.getY()));
                    double u = ((p3.getY() - p1.getY()) * (x - p3.getX()) + (p1.getX() - p3.getX()) * (y - p3.getY())) /
                            ((p2.getY() - p3.getY()) * (p1.getX() - p3.getX()) + (p3.getX() - p2.getX()) * (p1.getY() - p3.getY()));
                    double v = 1 - u - w;
                    int b = (int) (w * (p1.getColor() >> 16 & 0xFF) + u * (p2.getColor() >> 16 & 0xFF) + v * (p3.getColor() >> 16 & 0xFF));
                    int g = (int) (w * (p1.getColor() >> 8 & 0xFF) + u * (p2.getColor() >> 8 & 0xFF) + v * (p3.getColor() >> 8 & 0xFF));
                    int r = (int) (w * (p1.getColor() & 0xFF) + u * (p2.getColor() & 0xFF) + v * (p3.getColor() & 0xFF));
                    int a = (int) (w * (p1.getColor() >> 24 & 0xFF) + u * (p2.getColor() >> 24 & 0xFF) + v * (p3.getColor() >> 24 & 0xFF));
                    int vertexColor = (a << 24) | (r << 16) | (g << 8) | b;

                    int textureX = (int) Math.round(w * p1.getU() * texture.getWidth() + u * p2.getU() * texture.getWidth() + v * p3.getU() * texture.getWidth());
                    int textureY = (int) Math.round(w * p1.getV() * texture.getHeight() + u * p2.getV() * texture.getHeight() + v * p3.getV() * texture.getHeight());
                    int textureColor = texture.getRGB(textureX, textureY);

                    int mixedColor = mix(vertexColor, textureColor);
                    int mixedAlpha = mixedColor >> 24 & 0xFF;
                    if (mixedAlpha == 255) {
                        image.setRGB(x, y, mixedColor);
                    } else if (mixedAlpha != 0) {
                        image.setRGB(x, y, blendColors(image.getRGB(x, y), mixedColor));
                    }
                }
            }
        }
    }

    private static int mix(final int color1, final int color2) {
        int a1 = color1 >> 24 & 0xFF;
        int r1 = color1 >> 16 & 0xFF;
        int g1 = color1 >> 8 & 0xFF;
        int b1 = color1 & 0xFF;
        int a2 = color2 >> 24 & 0xFF;
        int r2 = color2 >> 16 & 0xFF;
        int g2 = color2 >> 8 & 0xFF;
        int b2 = color2 & 0xFF;
        int a = (int) (a1 * a2 / 255F);
        int r = (int) (r1 * r2 / 255F);
        int g = (int) (g1 * g2 / 255F);
        int b = (int) (b1 * b2 / 255F);
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    public static int blendColors(final int color1, final int color2) {
        int a1 = color1 >> 24 & 0xFF;
        int r1 = color1 >> 16 & 0xFF;
        int g1 = color1 >> 8 & 0xFF;
        int b1 = color1 & 0xFF;
        int a2 = color2 >> 24 & 0xFF;
        int r2 = color2 >> 16 & 0xFF;
        int g2 = color2 >> 8 & 0xFF;
        int b2 = color2 & 0xFF;

        float alpha = a2 / 255.0f;

        int blendedRed = (int) ((1 - alpha) * r1 + alpha * r2);
        int blendedGreen = (int) ((1 - alpha) * g1 + alpha * g2);
        int blendedBlue = (int) ((1 - alpha) * b1 + alpha * b2);

        return (255 << 24) | (blendedRed << 16) | (blendedGreen << 8) | blendedBlue;
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
