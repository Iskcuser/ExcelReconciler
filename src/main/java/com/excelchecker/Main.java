package com.excelchecker;

import gui.ExcelCheckerGUI;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        SwingUtilities.invokeLater(ExcelCheckerGUI::new);
    }
}