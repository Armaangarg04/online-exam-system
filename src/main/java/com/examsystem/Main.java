package com.examsystem;

import com.examsystem.db.DatabaseManager;
import com.examsystem.ui.LoginFrame;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        DatabaseManager db = DatabaseManager.getInstance();
        db.initializeDatabase();
        db.ensureDefaultAdmin("Armaangarg04", "Armaan@2026");
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}