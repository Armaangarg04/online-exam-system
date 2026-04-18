package com.examsystem.ui;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import com.examsystem.db.DatabaseManager;
import com.examsystem.model.Question;
import com.examsystem.model.Quiz;
import com.examsystem.model.Result;
import com.examsystem.model.User;

public class StudentDashboard extends JFrame {

    private final User student;
    private DefaultTableModel historyModel;

    public StudentDashboard(User student) {
        this.student = student;
        setTitle("Student Dashboard — " + student.getUsername());
        setSize(780, 580);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        buildUI();
        loadHistory();
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Color.WHITE);

        // ── Navbar ────────────────────────────────────────────────────────────
        JPanel navbar = new JPanel(new BorderLayout());
        navbar.setBackground(new Color(83, 74, 183));
        navbar.setPreferredSize(new Dimension(780, 64));
        navbar.setBorder(new EmptyBorder(0, 24, 0, 24));

        JPanel navLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        navLeft.setOpaque(false);

        JLabel appName = new JLabel("📋 Exam System");
        appName.setFont(new Font("Segoe UI", Font.BOLD, 16));
        appName.setForeground(Color.WHITE);

        JLabel sep = new JLabel("  |  ");
        sep.setForeground(new Color(180, 170, 230));

        JLabel roleLabel = new JLabel("Student Portal");
        roleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        roleLabel.setForeground(new Color(200, 195, 240));

        navLeft.add(appName);
        navLeft.add(sep);
        navLeft.add(roleLabel);

        JPanel navRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        navRight.setOpaque(false);

        JLabel userLabel = new JLabel("👤 " + student.getUsername());
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        userLabel.setForeground(new Color(200, 195, 240));

        JButton logoutBtn = makeNavButton("Logout");
        logoutBtn.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });

        navRight.add(userLabel);
        navRight.add(logoutBtn);

        navbar.add(navLeft, BorderLayout.WEST);
        navbar.add(navRight, BorderLayout.EAST);

        // ── Content ───────────────────────────────────────────────────────────
        JPanel content = new JPanel(new BorderLayout(0, 20));
        content.setBackground(new Color(248, 248, 252));
        content.setBorder(new EmptyBorder(24, 24, 24, 24));

        // ── Join exam card ────────────────────────────────────────────────────
        JPanel joinCard = new JPanel(new BorderLayout(20, 0));
        joinCard.setBackground(Color.WHITE);
        joinCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 235)),
            BorderFactory.createEmptyBorder(20, 24, 20, 24)
        ));

        JPanel joinLeft = new JPanel();
        joinLeft.setOpaque(false);
        joinLeft.setLayout(new BoxLayout(joinLeft, BoxLayout.Y_AXIS));

        JLabel joinTitle = new JLabel("Join an Exam");
        joinTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        joinTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel joinSub = new JLabel("Enter the exam ID provided by your teacher");
        joinSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        joinSub.setForeground(new Color(120, 120, 130));
        joinSub.setAlignmentX(Component.LEFT_ALIGNMENT);

        joinLeft.add(joinTitle);
        joinLeft.add(Box.createVerticalStrut(4));
        joinLeft.add(joinSub);

        JPanel joinRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        joinRight.setOpaque(false);

        JTextField examIdField = new JTextField(14);
        examIdField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        examIdField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 215)),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        examIdField.setToolTipText("e.g. MATH101");

        JLabel placeholder = new JLabel("e.g. MATH101");
        placeholder.setForeground(new Color(180, 180, 190));
        examIdField.setForeground(new Color(180, 180, 190));
        examIdField.setText("e.g. MATH101");

        examIdField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override public void focusGained(java.awt.event.FocusEvent e) {
                if (examIdField.getText().equals("e.g. MATH101")) {
                    examIdField.setText("");
                    examIdField.setForeground(Color.BLACK);
                }
            }
            @Override public void focusLost(java.awt.event.FocusEvent e) {
                if (examIdField.getText().isEmpty()) {
                    examIdField.setText("e.g. MATH101");
                    examIdField.setForeground(new Color(180, 180, 190));
                }
            }
        });

        JButton startBtn = new JButton("Start Exam →");
        startBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        startBtn.setBackground(new Color(83, 74, 183));
        startBtn.setForeground(Color.WHITE);
        startBtn.setFocusPainted(false);
        startBtn.setBorderPainted(false);
        startBtn.setOpaque(true);
        startBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        startBtn.setBorder(new EmptyBorder(10, 20, 10, 20));

        joinRight.add(examIdField);
        joinRight.add(startBtn);

        joinCard.add(joinLeft, BorderLayout.WEST);
        joinCard.add(joinRight, BorderLayout.EAST);

        // ── Stats row ─────────────────────────────────────────────────────────
        JPanel statsRow = new JPanel(new GridLayout(1, 3, 14, 0));
        statsRow.setOpaque(false);

        List<Result> results = DatabaseManager.getInstance().getResultsByStudent(student.getId());

        int totalAttempts = results.size();
        int totalScore    = results.stream().mapToInt(Result::getScore).sum();
        int totalMax      = results.stream().mapToInt(Result::getTotalMarks).sum();
        String avgPct     = totalMax > 0
            ? String.format("%.0f%%", (totalScore * 100.0 / totalMax))
            : "—";

        int best = results.stream()
            .mapToInt(r -> totalMax > 0
                ? (int) Math.round(r.getScore() * 100.0 / r.getTotalMarks()) : 0)
            .max().orElse(0);

        statsRow.add(makeStatCard("Exams Taken",    String.valueOf(totalAttempts), new Color(238, 237, 254)));
        statsRow.add(makeStatCard("Average Score",  avgPct,                        new Color(234, 243, 222)));
        statsRow.add(makeStatCard("Best Score",     best > 0 ? best + "%" : "—",  new Color(232, 241, 251)));

        // ── History table ─────────────────────────────────────────────────────
        JPanel histPanel = new JPanel(new BorderLayout(0, 10));
        histPanel.setOpaque(false);

        JLabel histTitle = new JLabel("Attempt History");
        histTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));

        String[] cols = {"Quiz Title", "Score", "Total", "Percentage", "Date"};
        historyModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable histTable = new JTable(historyModel);
        histTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        histTable.setRowHeight(40);
        histTable.setShowVerticalLines(false);
        histTable.setGridColor(new Color(240, 240, 245));
        histTable.setSelectionBackground(new Color(238, 237, 254));
        histTable.setSelectionForeground(Color.BLACK);
        histTable.setFocusable(false);

        JTableHeader hdr = histTable.getTableHeader();
        hdr.setFont(new Font("Segoe UI", Font.BOLD, 12));
        hdr.setBackground(new Color(248, 248, 252));
        hdr.setForeground(new Color(80, 80, 100));
        hdr.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 230)));
        hdr.setReorderingAllowed(false);

        histTable.getColumnModel().getColumn(0).setPreferredWidth(230);
        histTable.getColumnModel().getColumn(1).setPreferredWidth(70);
        histTable.getColumnModel().getColumn(2).setPreferredWidth(70);
        histTable.getColumnModel().getColumn(3).setPreferredWidth(110);
        histTable.getColumnModel().getColumn(4).setPreferredWidth(180);

        // Percentage column colored renderer
        histTable.getColumnModel().getColumn(3).setCellRenderer(new PercentRenderer());

        JScrollPane scroll = new JScrollPane(histTable);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 230)));
        scroll.getViewport().setBackground(Color.WHITE);

        histPanel.add(histTitle, BorderLayout.NORTH);
        histPanel.add(scroll, BorderLayout.CENTER);

        // ── Assemble ──────────────────────────────────────────────────────────
        JPanel topSection = new JPanel(new BorderLayout(0, 14));
        topSection.setOpaque(false);
        topSection.add(joinCard, BorderLayout.NORTH);
        topSection.add(statsRow, BorderLayout.CENTER);

        content.add(topSection, BorderLayout.NORTH);
        content.add(histPanel, BorderLayout.CENTER);

        root.add(navbar, BorderLayout.NORTH);
        root.add(content, BorderLayout.CENTER);
        setContentPane(root);

        // ── Start exam action ─────────────────────────────────────────────────
        startBtn.addActionListener(e -> {
    String examId = examIdField.getText().trim().toUpperCase();
    if (examId.isEmpty() || examId.equals("E.G. MATH101")) {
        JOptionPane.showMessageDialog(this, "Please enter an Exam ID.",
            "Missing ID", JOptionPane.WARNING_MESSAGE); return;
    }
    Quiz quiz = DatabaseManager.getInstance().getQuizByExamId(examId);
    if (quiz == null) {
        // Schedule check
if (!quiz.isOpenNow()) {
    JOptionPane.showMessageDialog(this,
        "This exam is not available right now.\n\n"
        + "Window: " + quiz.getScheduleDisplay(),
        "Not Available Yet", JOptionPane.WARNING_MESSAGE);
    return;
}
        JOptionPane.showMessageDialog(this, "No exam found with ID: " + examId,
            "Not Found", JOptionPane.ERROR_MESSAGE); return;
    }
    // ── Attempt limit check ───────────────────────────────────────────────
    int attempts = DatabaseManager.getInstance()
        .getAttemptCount(student.getId(), quiz.getId());
    if (attempts >= quiz.getMaxAttempts()) {
        JOptionPane.showMessageDialog(this,
            "You have used all " + quiz.getMaxAttempts() +
            " attempt(s) for this exam.", "Limit Reached",
            JOptionPane.WARNING_MESSAGE); return;
    }
    List<Question> questions = DatabaseManager.getInstance()
        .getQuestionsByQuizId(quiz.getId());
    if (questions.isEmpty()) {
        JOptionPane.showMessageDialog(this, "This exam has no questions yet.",
            "Empty Exam", JOptionPane.WARNING_MESSAGE); return;
    }
    new QuizFrame(student, quiz, questions, this).setVisible(true);
    setVisible(false);
});
    }

    public void loadHistory() {
        if (historyModel == null) return;
        historyModel.setRowCount(0);
        List<Result> results = DatabaseManager.getInstance().getResultsByStudent(student.getId());
        for (Result r : results) {
            int pct = (int) Math.round(r.getScore() * 100.0 / r.getTotalMarks());
            historyModel.addRow(new Object[]{
                r.getQuizTitle(),
                r.getScore(),
                r.getTotalMarks(),
                pct + "%",
                r.getAttemptedAt()
            });
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private JPanel makeStatCard(String label, String value, Color bg) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(bg);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 230)),
            BorderFactory.createEmptyBorder(16, 20, 16, 20)
        ));

        JLabel val = new JLabel(value);
        val.setFont(new Font("Segoe UI", Font.BOLD, 26));
        val.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lbl.setForeground(new Color(100, 100, 120));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(val);
        card.add(Box.createVerticalStrut(4));
        card.add(lbl);
        return card;
    }

    private JButton makeNavButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(0, 0, 0, 0));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255, 80)));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setOpaque(false);
        return btn;
    }

    // Percentage cell renderer — green / amber / red
    static class PercentRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean selected, boolean focused, int row, int col) {
            super.getTableCellRendererComponent(table, value, selected, focused, row, col);
            setHorizontalAlignment(SwingConstants.CENTER);
            if (value != null) {
                int pct = Integer.parseInt(value.toString().replace("%", ""));
                if (!selected) {
                    if (pct >= 75)      { setForeground(new Color(40, 110, 20)); setBackground(new Color(234, 243, 222)); }
                    else if (pct >= 50) { setForeground(new Color(140, 90, 0));  setBackground(new Color(255, 243, 205)); }
                    else                { setForeground(new Color(160, 30, 30)); setBackground(new Color(252, 235, 235)); }
                }
            }
            return this;
        }
    }
}