package com.examsystem.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import com.examsystem.db.DatabaseManager;
import com.examsystem.model.User;

public class LoginFrame extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginFrame() {
        setTitle("Exam System — Login");
        setSize(440, 560);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        buildUI();
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Color.WHITE);

        // ── Banner ────────────────────────────────────────────────────────────
        JPanel banner = new JPanel();
        banner.setBackground(new Color(24, 95, 165));
        banner.setPreferredSize(new Dimension(440, 155));
        banner.setLayout(new BoxLayout(banner, BoxLayout.Y_AXIS));
        banner.setBorder(new EmptyBorder(26, 0, 26, 0));

        JLabel icon = new JLabel("📋");
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel title = new JLabel("Exam System");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Online Quiz Platform");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitle.setForeground(new Color(180, 210, 240));
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        banner.add(icon);
        banner.add(Box.createVerticalStrut(6));
        banner.add(title);
        banner.add(Box.createVerticalStrut(2));
        banner.add(subtitle);

        // ── Form (GridBagLayout for perfect alignment) ─────────────────────────
        JPanel formOuter = new JPanel(new GridBagLayout());
        formOuter.setBackground(Color.WHITE);

        JPanel form = new JPanel();
        form.setBackground(Color.WHITE);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setPreferredSize(new Dimension(320, 340));

        // Heading — centered
        JLabel heading = new JLabel("Sign in to your account");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 17));
        heading.setHorizontalAlignment(SwingConstants.CENTER);
        heading.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel headingSub = new JLabel("Enter your credentials to continue");
        headingSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        headingSub.setForeground(new Color(130, 130, 130));
        headingSub.setHorizontalAlignment(SwingConstants.CENTER);
        headingSub.setAlignmentX(Component.CENTER_ALIGNMENT);

        form.add(heading);
        form.add(Box.createVerticalStrut(4));
        form.add(headingSub);
        form.add(Box.createVerticalStrut(24));

        // Username label — left aligned within fixed width
        JLabel userLabel = fieldLabel("Username");
        form.add(userLabel);
        form.add(Box.createVerticalStrut(6));

        // Username field
        usernameField = new JTextField();
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        usernameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        usernameField.setPreferredSize(new Dimension(320, 42));
        usernameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        usernameField.setAlignmentX(Component.CENTER_ALIGNMENT);
        stylePlaceholder(usernameField, "Enter your username");
        form.add(usernameField);
        form.add(Box.createVerticalStrut(16));

        // Password label
        JLabel passLabel = fieldLabel("Password");
        form.add(passLabel);
        form.add(Box.createVerticalStrut(6));

        // Password row
        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 1, 1, 0, new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)));

        JButton eyeBtn = new JButton("👁");
        eyeBtn.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
        eyeBtn.setPreferredSize(new Dimension(44, 42));
        eyeBtn.setMinimumSize(new Dimension(44, 42));
        eyeBtn.setMaximumSize(new Dimension(44, 42));
        eyeBtn.setFocusPainted(false);
        eyeBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        eyeBtn.setBackground(new Color(245, 247, 250));
        eyeBtn.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 1,
            new Color(200, 200, 200)));

        final boolean[] vis = {false};
        eyeBtn.addActionListener(e -> {
            vis[0] = !vis[0];
            passwordField.setEchoChar(vis[0] ? '\0' : '•');
            eyeBtn.setText(vis[0] ? "🙈" : "👁");
        });

        JPanel pwRow = new JPanel(new BorderLayout());
        pwRow.setOpaque(false);
        pwRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        pwRow.setPreferredSize(new Dimension(320, 42));
        pwRow.setAlignmentX(Component.CENTER_ALIGNMENT);
        pwRow.add(passwordField, BorderLayout.CENTER);
        pwRow.add(eyeBtn, BorderLayout.EAST);

        form.add(pwRow);
        form.add(Box.createVerticalStrut(28));

        // Sign In button — full width
        JButton loginBtn = new JButton("Sign In");
        loginBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        loginBtn.setBackground(new Color(24, 95, 165));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFocusPainted(false);
        loginBtn.setBorderPainted(false);
        loginBtn.setOpaque(true);
        loginBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        loginBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        loginBtn.setPreferredSize(new Dimension(320, 44));
        loginBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginBtn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                loginBtn.setBackground(new Color(18, 78, 138));
            }
            @Override public void mouseExited(MouseEvent e) {
                loginBtn.setBackground(new Color(24, 95, 165));
            }
        });
        loginBtn.addActionListener(e -> handleLogin());
        form.add(loginBtn);
        form.add(Box.createVerticalStrut(18));

        // Divider — centered
        JLabel divider = new JLabel("Don't have an account?");
        divider.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        divider.setForeground(new Color(160, 160, 160));
        divider.setHorizontalAlignment(SwingConstants.CENTER);
        divider.setAlignmentX(Component.CENTER_ALIGNMENT);
        form.add(divider);
        form.add(Box.createVerticalStrut(10));

        // Create Account button — full width
        JButton registerBtn = new JButton("Create Account");
        registerBtn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        registerBtn.setBackground(Color.WHITE);
        registerBtn.setForeground(new Color(24, 95, 165));
        registerBtn.setFocusPainted(false);
        registerBtn.setBorder(BorderFactory.createLineBorder(
            new Color(24, 95, 165)));
        registerBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        registerBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        registerBtn.setPreferredSize(new Dimension(320, 42));
        registerBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        registerBtn.addActionListener(e -> {
            new RegisterFrame().setVisible(true);
            dispose();
        });
        form.add(registerBtn);

        formOuter.add(form);

        root.add(banner,     BorderLayout.NORTH);
        root.add(formOuter,  BorderLayout.CENTER);
        setContentPane(root);

        // Enter key
        KeyAdapter enter = new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) handleLogin();
            }
        };
        usernameField.addKeyListener(enter);
        passwordField.addKeyListener(enter);
    }

    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty()
                || username.equals("Enter your username")
                || password.isEmpty()) {
            err("Please fill in all fields."); return;
        }

        User user = DatabaseManager.getInstance().loginUser(username, password);
        if (user == null) {
            err("Invalid username or password.");
            passwordField.setText(""); return;
        }

        switch (user.getRole()) {
            case "teacher" -> { new TeacherDashboard(user).setVisible(true); dispose(); }
            case "admin"   -> { new AdminPanel(user).setVisible(true);        dispose(); }
            default        -> { new StudentDashboard(user).setVisible(true);  dispose(); }
        }
    }

    private JLabel fieldLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        l.setForeground(new Color(70, 70, 70));
        l.setAlignmentX(Component.CENTER_ALIGNMENT);
        return l;
    }

    private void stylePlaceholder(JTextField f, String ph) {
        f.setForeground(new Color(170, 170, 170));
        f.setText(ph);
        f.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                if (f.getText().equals(ph)) {
                    f.setText("");
                    f.setForeground(Color.BLACK);
                }
            }
            @Override public void focusLost(FocusEvent e) {
                if (f.getText().isEmpty()) {
                    f.setText(ph);
                    f.setForeground(new Color(170, 170, 170));
                }
            }
        });
    }

    private void err(String msg) {
        JOptionPane.showMessageDialog(this, msg,
            "Login Failed", JOptionPane.ERROR_MESSAGE);
    }
}