package com.examsystem.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import com.examsystem.db.DatabaseManager;
import com.examsystem.model.Result;
import com.examsystem.model.User;

public class ProfileFrame extends JFrame {

    private final User user;

    public ProfileFrame(User user) {
        this.user = user;
        setTitle("Profile — " + user.getUsername());
        setSize(460, 520);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        buildUI();
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Color.WHITE);

        // ── Header ────────────────────────────────────────────────────────────
        JPanel header = new JPanel(new GridBagLayout());
        header.setBackground(new Color(24, 95, 165));
        header.setPreferredSize(new Dimension(460, 130));

        JPanel headerInner = new JPanel();
        headerInner.setOpaque(false);
        headerInner.setLayout(new BoxLayout(headerInner, BoxLayout.Y_AXIS));

        // Avatar circle
        JLabel avatar = new JLabel(String.valueOf(
            user.getUsername().toUpperCase().charAt(0))) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 255, 255, 60));
                g2.fillOval(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
            }
        };
        avatar.setFont(new Font("Segoe UI", Font.BOLD, 28));
        avatar.setForeground(Color.WHITE);
        avatar.setHorizontalAlignment(SwingConstants.CENTER);
        avatar.setPreferredSize(new Dimension(60, 60));
        avatar.setOpaque(false);
        avatar.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel nameLabel = new JLabel(user.getUsername());
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        Color roleBg = switch (user.getRole()) {
            case "teacher" -> new Color(59, 109, 17);
            case "admin"   -> new Color(160, 40, 40);
            default        -> new Color(83, 74, 183);
        };
        JLabel roleLabel = new JLabel(" " + user.getRole().toUpperCase() + " ");
        roleLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        roleLabel.setForeground(Color.WHITE);
        roleLabel.setOpaque(true);
        roleLabel.setBackground(roleBg);
        roleLabel.setBorder(new EmptyBorder(3, 8, 3, 8));
        roleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        headerInner.add(avatar);
        headerInner.add(Box.createVerticalStrut(6));
        headerInner.add(nameLabel);
        headerInner.add(Box.createVerticalStrut(4));
        headerInner.add(roleLabel);
        header.add(headerInner);

        // ── Stats (for students) ──────────────────────────────────────────────
        JPanel statsRow = new JPanel(new GridLayout(1, 3, 0, 0));
        statsRow.setBackground(new Color(248, 249, 252));
        statsRow.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 230)));
        statsRow.setPreferredSize(new Dimension(460, 70));

        if ("student".equals(user.getRole())) {
            List<Result> results = DatabaseManager.getInstance().getResultsByStudent(user.getId());
            int attempts  = results.size();
            int totalScore = results.stream().mapToInt(Result::getScore).sum();
            int totalMax   = results.stream().mapToInt(Result::getTotalMarks).sum();
            String avg = totalMax > 0
                ? String.format("%.0f%%", totalScore * 100.0 / totalMax) : "—";
            int best = results.stream()
                .mapToInt(r -> r.getTotalMarks() > 0
                    ? (int)(r.getScore() * 100.0 / r.getTotalMarks()) : 0)
                .max().orElse(0);

            statsRow.add(makeStatCell("Attempts",  String.valueOf(attempts)));
            statsRow.add(makeStatCell("Average",   avg));
            statsRow.add(makeStatCell("Best",      best > 0 ? best + "%" : "—"));
        } else {
            statsRow.add(makeStatCell("Role",      user.getRole().toUpperCase()));
            statsRow.add(makeStatCell("User ID",   "#" + user.getId()));
            statsRow.add(makeStatCell("Status",    "Active"));
        }

        // ── Change password section ───────────────────────────────────────────
        JPanel pwSection = new JPanel();
        pwSection.setBackground(Color.WHITE);
        pwSection.setLayout(new BoxLayout(pwSection, BoxLayout.Y_AXIS));
        pwSection.setBorder(new EmptyBorder(24, 32, 24, 32));

        JLabel pwTitle = new JLabel("Change Password");
        pwTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        pwTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel pwSub = new JLabel("Leave blank to keep your current password");
        pwSub.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        pwSub.setForeground(new Color(140, 140, 140));
        pwSub.setAlignmentX(Component.LEFT_ALIGNMENT);

        pwSection.add(pwTitle);
        pwSection.add(Box.createVerticalStrut(2));
        pwSection.add(pwSub);
        pwSection.add(Box.createVerticalStrut(16));

        pwSection.add(makeLabel("Current Password"));
        pwSection.add(Box.createVerticalStrut(5));
        JPasswordField currentPw = makePwField();
        pwSection.add(currentPw);
        pwSection.add(Box.createVerticalStrut(12));

        pwSection.add(makeLabel("New Password"));
        pwSection.add(Box.createVerticalStrut(5));
        JPasswordField newPw = makePwFieldWithToggle(pwSection);
        pwSection.add(Box.createVerticalStrut(12));

        pwSection.add(makeLabel("Confirm New Password"));
        pwSection.add(Box.createVerticalStrut(5));
        JPasswordField confirmPw = makePwField();
        pwSection.add(confirmPw);
        pwSection.add(Box.createVerticalStrut(24));

        JButton saveBtn = new JButton("Save Changes");
        saveBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        saveBtn.setBackground(new Color(24, 95, 165));
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setFocusPainted(false);
        saveBtn.setBorderPainted(false);
        saveBtn.setOpaque(true);
        saveBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        saveBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        saveBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        pwSection.add(saveBtn);

        JPanel northBlock = new JPanel(new BorderLayout());
        northBlock.add(header,   BorderLayout.NORTH);
        northBlock.add(statsRow, BorderLayout.SOUTH);

        root.add(northBlock,  BorderLayout.NORTH);
        root.add(pwSection,   BorderLayout.CENTER);
        setContentPane(root);

        // ── Save action ───────────────────────────────────────────────────────
        saveBtn.addActionListener(e -> {
            String curr    = new String(currentPw.getPassword());
            String newPass = new String(newPw.getPassword());
            String conf    = new String(confirmPw.getPassword());

            if (curr.isEmpty() && newPass.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No changes made.", "Info",
                    JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            if (curr.isEmpty()) {
                showError("Enter your current password."); return;
            }
            User check = DatabaseManager.getInstance()
                .loginUser(user.getUsername(), curr);
            if (check == null) {
                showError("Current password is incorrect."); return;
            }
            if (newPass.length() < 6) {
                showError("New password must be at least 6 characters."); return;
            }
            if (!newPass.equals(conf)) {
                showError("New passwords do not match."); return;
            }
            DatabaseManager.getInstance().changePassword(user.getId(), newPass);
            JOptionPane.showMessageDialog(this, "Password changed successfully!",
                "Success", JOptionPane.INFORMATION_MESSAGE);
            currentPw.setText(""); newPw.setText(""); confirmPw.setText("");
        });
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private JPasswordField makePwField() {
        JPasswordField f = new JPasswordField();
        f.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(7, 10, 7, 10)));
        f.setAlignmentX(Component.LEFT_ALIGNMENT);
        return f;
    }

    private JPasswordField makePwFieldWithToggle(JPanel parent) {
        JPasswordField field = makePwField();
        JPanel row = new JPanel(new BorderLayout(6, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        JButton eye = new JButton("👁");
        eye.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 13));
        eye.setPreferredSize(new Dimension(38, 38));
        eye.setFocusPainted(false);
        eye.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        eye.setBackground(new Color(245, 247, 250));
        eye.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        final boolean[] v = {false};
        eye.addActionListener(e -> {
            v[0] = !v[0];
            field.setEchoChar(v[0] ? '\0' : '•');
            eye.setText(v[0] ? "🙈" : "👁");
        });
        row.add(field, BorderLayout.CENTER);
        row.add(eye,   BorderLayout.EAST);
        parent.add(row);
        return field;
    }

    private JLabel makeLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lbl.setForeground(new Color(80, 80, 80));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private JPanel makeStatCell(String label, String value) {
        JPanel cell = new JPanel(new GridBagLayout());
        cell.setOpaque(false);
        cell.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(220, 220, 230)));
        JPanel inner = new JPanel();
        inner.setOpaque(false);
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));
        JLabel val = new JLabel(value);
        val.setFont(new Font("Segoe UI", Font.BOLD, 20));
        val.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lbl.setForeground(new Color(130, 130, 140));
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        inner.add(val); inner.add(lbl);
        cell.add(inner);
        return cell;
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
}