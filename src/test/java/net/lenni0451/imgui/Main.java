package net.lenni0451.imgui;

import net.lenni0451.imgui.swing.ImGuiCanvas;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        JFrame frame = new JFrame("Test");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.add(new ImGuiCanvas());
        frame.setVisible(true);
    }

}
