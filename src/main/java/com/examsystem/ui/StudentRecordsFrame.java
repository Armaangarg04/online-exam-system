package com.examsystem.ui;

import com.examsystem.db.DatabaseManager;
import com.examsystem.model.Quiz;
import com.examsystem.model.Result;
import com.examsystem.model.User;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

public class StudentRecordsFrame extends JFrame {
    private final Quiz quiz;
    private final User teacher;

    public StudentRecordsFrame(Quiz quiz, User teacher) {
        this.quiz = quiz;
        this.teacher = teacher;
        setTitle("Student Results — " + quiz.getTitle());
        setSize(700, 520);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        buildUI();
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Color.WHITE);
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(24, 95, 165));
        header.setPreferredSize(new Dimension(700, 80));
        header.setBorder(new EmptyBorder(0, 24, 0, 24));
        JPanel headerLeft = new JPanel();
        headerLeft.setOpaque(false);
        headerLeft.setLayout(new BoxLayout(headerLeft, BoxLayout.Y_AXIS));
        JLabel titleLabel = new JLabel("Student Results");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        JLabel subLabel = new JLabel(quiz.getTitle() + "  ·  " + quiz.getExamId() +
            "  ·  " + quiz.getTimeLimitMinutes() + " min  ·  " + quiz.getMarksPerQuestion() + " pts/Q");
        subLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subLabel.setForeground(new Color(180, 210, 240));
        headerLeft.add(titleLabel);
        headerLeft.add(Box.createVerticalStrut(4));
        headerLeft.add(subLabel);
        header.add(headerLeft, BorderLayout.WEST);
        List<Result> results = DatabaseManager.getInstance().getResultsByQuiz(quiz.getId());
        int count   = results.size();
        double avg  = results.stream().mapToInt(Result::getScore).average().orElse(0);
        int highest = results.stream().mapToInt(Result::getScore).max().orElse(0);
        int lowest  = results.stream().mapToInt(Result::getScore).min().orElse(0);
        int totalM  = results.isEmpty() ? 1 : results.get(0).getTotalMarks();
        JPanel summaryRow = new JPanel(new GridLayout(1, 4, 12, 0));
        summaryRow.setBackground(new Color(248, 249, 252));
        summaryRow.setBorder(new EmptyBorder(14, 24, 14, 24));
        summaryRow.add(makeStat("Attempts", String.valueOf(count),                     new Color(232, 241, 251)));
        summaryRow.add(makeStat("Average",  String.format("%.1f", avg) + "/" + totalM, new Color(238, 237, 254)));
        summaryRow.add(makeStat("Highest",  highest + "/" + totalM,                    new Color(234, 243, 222)));
        summaryRow.add(makeStat("Lowest",   lowest  + "/" + totalM,                    new Color(252, 235, 235)));
        String[] cols = {"Rank", "Student", "Score", "Total", "Percentage", "Date"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        for (int i = 0; i < results.size(); i++) {
            Result r = results.get(i);
            int pct  = (int) Math.round(r.getScore() * 100.0 / r.getTotalMarks());
            model.addRow(new Object[]{i + 1, r.getStudentName(), r.getScore(),
                r.getTotalMarks(), pct + "%", r.getAttemptedAt()});
        }
        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(42);
        table.setShowVerticalLines(false);
        table.setGridColor(new Color(240, 240, 245));
        table.setSelectionBackground(new Color(232, 241, 251));
        table.setFocusable(false);
        JTableHeader hdr = table.getTableHeader();
        hdr.setFont(new Font("Segoe UI", Font.BOLD, 12));
        hdr.setBackground(new Color(248, 248, 252));
        hdr.setForeground(new Color(80, 80, 100));
        hdr.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 230)));
        hdr.setReorderingAllowed(false);
        table.getColumnModel().getColumn(0).setPreferredWidth(60);
        table.getColumnModel().getColumn(1).setPreferredWidth(160);
        table.getColumnModel().getColumn(2).setPreferredWidth(70);
        table.getColumnModel().getColumn(3).setPreferredWidth(70);
        table.getColumnModel().getColumn(4).setPreferredWidth(100);
        table.getColumnModel().getColumn(5).setPreferredWidth(180);
        table.getColumnModel().getColumn(4).setCellRenderer(new PercentRenderer());
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(center);
        table.getColumnModel().getColumn(2).setCellRenderer(center);
        table.getColumnModel().getColumn(3).setCellRenderer(center);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 220, 230)));
        scroll.getViewport().setBackground(Color.WHITE);
        if (results.isEmpty()) {
            JPanel empty = new JPanel(new GridBagLayout());
            empty.setBackground(Color.WHITE);
            JLabel msg = new JLabel("No students have attempted this exam yet.");
            msg.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            msg.setForeground(new Color(160, 160, 170));
            empty.add(msg);
            root.add(header, BorderLayout.NORTH);
            root.add(empty,  BorderLayout.CENTER);
            setContentPane(root);
            return;
        }
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.setBackground(new Color(248, 249, 252));
        bottom.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 220, 225)));
        JButton closeBtn = new JButton("Close");
        closeBtn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        closeBtn.setBackground(Color.WHITE);
        closeBtn.setForeground(new Color(24, 95, 165));
        closeBtn.setFocusPainted(false);
        closeBtn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(24, 95, 165)),
            BorderFactory.createEmptyBorder(8, 20, 8, 20)));
        closeBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        closeBtn.addActionListener(e -> dispose());
        bottom.add(closeBtn);
        JPanel northBlock = new JPanel(new BorderLayout());
        northBlock.add(header,     BorderLayout.NORTH);
        northBlock.add(summaryRow, BorderLayout.SOUTH);
        root.add(northBlock, BorderLayout.NORTH);
        root.add(scroll,     BorderLayout.CENTER);
        root.add(bottom,     BorderLayout.SOUTH);
        setContentPane(root);
    }

    private JPanel makeStat(String label, String value, Color bg) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(bg);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(215, 215, 225)),
            BorderFactory.createEmptyBorder(10, 14, 10, 14)));
        JLabel val = new JLabel(value);
        val.setFont(new Font("Segoe UI", Font.BOLD, 18));
        val.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lbl.setForeground(new Color(110, 110, 130));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(val);
        card.add(Box.createVerticalStrut(2));
        card.add(lbl);
        return card;
    }

    static class PercentRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean selected, boolean focused, int row, int col) {
            super.getTableCellRendererComponent(table, value, selected, focused, row, col);
            setHorizontalAlignment(SwingConstants.CENTER);
            if (value != null && !selected) {
                int pct = Integer.parseInt(value.toString().replace("%", ""));
                if (pct >= 75)      { setForeground(new Color(40, 110, 20));  setBackground(new Color(234, 243, 222)); }
                else if (pct >= 50) { setForeground(new Color(140, 90, 0));   setBackground(new Color(255, 243, 205)); }
                else                { setForeground(new Color(160, 30, 30));  setBackground(new Color(252, 235, 235)); }
            }
            return this;
        }
    }
}