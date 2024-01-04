package net.lenni0451.imgui;

import net.lenni0451.imgui.swing.ImGuiPanel;

import javax.swing.*;

public class Main2 {

    public static void main(String[] args) {
        JFrame frame = new JFrame("Test");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setContentPane(new ImGuiPanel());
        frame.setVisible(true);
    }

}
