package com.examsystem.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import com.examsystem.db.DatabaseManager;

public class RegisterFrame extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField, confirmField;
    private JRadioButton studentBtn, teacherBtn;

    public RegisterFrame() {
        setTitle("Exam System — Register");
        setSize(460, 620);
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
        banner.setPreferredSize(new Dimension(460, 110));
        banner.setLayout(new BoxLayout(banner, BoxLayout.Y_AXIS));
        banner.setBorder(new EmptyBorder(24, 0, 24, 0));

        JLabel title = new JLabel("Create Account");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Join the Exam System platform");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitle.setForeground(new Color(180, 210, 240));
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        banner.add(title);
        banner.add(Box.createVerticalStrut(4));
        banner.add(subtitle);

        // ── Form wrapper (centered) ───────────────────────────────────────────
        JPanel formWrapper = new JPanel(new GridBagLayout());
        formWrapper.setBackground(Color.WHITE);

        JPanel form = new JPanel();
        form.setBackground(Color.WHITE);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setPreferredSize(new Dimension(340, 460));

        // Username
        form.add(fieldLabel("Username"));
        form.add(Box.createVerticalStrut(6));
        usernameField = plainField();
        form.add(usernameField);
        form.add(Box.createVerticalStrut(14));

        // Password with toggle
        form.add(fieldLabel("Password"));
        form.add(Box.createVerticalStrut(6));
        passwordField = new JPasswordField();
        stylePasswordField(passwordField);
        JPanel pwRow = buildPasswordRow(passwordField);
        form.add(pwRow);
        form.add(Box.createVerticalStrut(14));

        // Confirm password
        form.add(fieldLabel("Confirm Password"));
        form.add(Box.createVerticalStrut(6));
        confirmField = new JPasswordField();
        stylePasswordField(confirmField);
        confirmField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        confirmField.setAlignmentX(Component.LEFT_ALIGNMENT);
        confirmField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        form.add(confirmField);
        form.add(Box.createVerticalStrut(20));

        // Role label
        form.add(fieldLabel("Register as"));
        form.add(Box.createVerticalStrut(10));

        // Role cards
        studentBtn = new JRadioButton();
        teacherBtn = new JRadioButton();
        ButtonGroup grp = new ButtonGroup();
        grp.add(studentBtn); grp.add(teacherBtn);
        studentBtn.setSelected(true);

        JPanel roleRow = new JPanel(new GridLayout(1, 2, 12, 0));
        roleRow.setOpaque(false);
        roleRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        roleRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        roleRow.add(buildRoleCard(studentBtn, "Student",
            "Take quizzes", "👨‍🎓", new Color(238, 237, 254)));
        roleRow.add(buildRoleCard(teacherBtn, "Teacher",
            "Create quizzes", "👨‍🏫", new Color(234, 243, 222)));
        form.add(roleRow);
        form.add(Box.createVerticalStrut(24));

        // Create account button
        JButton createBtn = primaryBtn("Create Account");
        createBtn.addActionListener(e -> handleRegister());
        form.add(createBtn);
        form.add(Box.createVerticalStrut(10));

        // Back to login
        JButton backBtn = outlineBtn("← Back to Login");
        backBtn.addActionListener(e -> {
            new LoginFrame().setVisible(true); dispose();
        });
        form.add(backBtn);

        formWrapper.add(form);
        root.add(banner,      BorderLayout.NORTH);
        root.add(formWrapper, BorderLayout.CENTER);
        setContentPane(root);
    }

    // ── Role card builder ─────────────────────────────────────────────────────

    private JPanel buildRoleCard(JRadioButton rb, String title,
                                  String sub, String emoji, Color bg) {
        JPanel card = new JPanel(new BorderLayout(10, 0));
        card.setBackground(bg);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(210, 210, 220)),
            BorderFactory.createEmptyBorder(12, 12, 12, 12)));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Left: radio
        rb.setOpaque(false);
        rb.setFocusPainted(false);

        // Right: text block
        JPanel textBlock = new JPanel();
        textBlock.setOpaque(false);
        textBlock.setLayout(new BoxLayout(textBlock, BoxLayout.Y_AXIS));

        JLabel emojiLabel = new JLabel(emoji + "  " + title);
        emojiLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        emojiLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subLabel = new JLabel(sub);
        subLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        subLabel.setForeground(new Color(100, 100, 110));
        subLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        textBlock.add(emojiLabel);
        textBlock.add(Box.createVerticalStrut(2));
        textBlock.add(subLabel);

        card.add(rb,        BorderLayout.WEST);
        card.add(textBlock, BorderLayout.CENTER);

        card.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { rb.setSelected(true); }
        });

        return card;
    }

    // ── Password row with eye toggle ──────────────────────────────────────────

    private JPanel buildPasswordRow(JPasswordField field) {
        JPanel row = new JPanel(new BorderLayout(0, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton eye = new JButton("👁");
        eye.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
        eye.setPreferredSize(new Dimension(44, 40));
        eye.setFocusPainted(false);
        eye.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        eye.setBackground(new Color(245, 247, 250));
        eye.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 1,
            new Color(200, 200, 200)));

        final boolean[] v = {false};
        eye.addActionListener(e -> {
            v[0] = !v[0];
            field.setEchoChar(v[0] ? '\0' : '•');
            eye.setText(v[0] ? "🙈" : "👁");
        });

        row.add(field, BorderLayout.CENTER);
        row.add(eye,   BorderLayout.EAST);
        return row;
    }

    // ── Register handler ──────────────────────────────────────────────────────

    private void handleRegister() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirm  = new String(confirmField.getPassword());
        String role     = studentBtn.isSelected() ? "student" : "teacher";

        if (username.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
            err("Please fill in all fields."); return;
        }
        if (username.length() < 3) {
            err("Username must be at least 3 characters."); return;
        }
        if (password.length() < 6) {
            err("Password must be at least 6 characters."); return;
        }
        if (!password.equals(confirm)) {
            err("Passwords do not match."); confirmField.setText(""); return;
        }

        boolean ok = DatabaseManager.getInstance()
            .registerUser(username, password, role);
        if (ok) {
            JOptionPane.showMessageDialog(this,
                "Account created! You can now log in.",
                "Success", JOptionPane.INFORMATION_MESSAGE);
            new LoginFrame().setVisible(true); dispose();
        } else {
            err("Username already exists. Choose another.");
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private JLabel fieldLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        l.setForeground(new Color(60, 60, 60));
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private JTextField plainField() {
        JTextField f = new JTextField();
        f.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        f.setAlignmentX(Component.LEFT_ALIGNMENT);
        return f;
    }

    private void stylePasswordField(JPasswordField f) {
        f.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 1, 1, 0, new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)));
    }

    private JButton primaryBtn(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.BOLD, 14));
        b.setBackground(new Color(24, 95, 165));
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setOpaque(true);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        b.setAlignmentX(Component.LEFT_ALIGNMENT);
        b.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                b.setBackground(new Color(18, 78, 138));
            }
            @Override public void mouseExited(MouseEvent e) {
                b.setBackground(new Color(24, 95, 165));
            }
        });
        return b;
    }

    private JButton outlineBtn(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        b.setBackground(Color.WHITE);
        b.setForeground(new Color(24, 95, 165));
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createLineBorder(new Color(24, 95, 165)));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        b.setAlignmentX(Component.LEFT_ALIGNMENT);
        return b;
    }

    private void err(String msg) {
        JOptionPane.showMessageDialog(this, msg,
            "Registration Failed", JOptionPane.ERROR_MESSAGE);
    }
}