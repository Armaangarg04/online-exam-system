package com.examsystem.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;

import com.examsystem.db.DatabaseManager;
import com.examsystem.model.Question;
import com.examsystem.model.Quiz;

public class EditQuizFrame extends JFrame {

    private final Quiz quiz;
    private final TeacherDashboard dashboard;

    private JTextField titleField;
    private JSpinner timeSpinner, marksSpinner, attemptsSpinner;
    private JSpinner negMarksSpinner;
    private JCheckBox randomizeCheck;
    private DefaultListModel<String> qListModel;

    public EditQuizFrame(Quiz quiz, TeacherDashboard dashboard) {
        this.quiz      = quiz;
        this.dashboard = dashboard;
        setTitle("Edit Quiz — " + quiz.getExamId());
        setSize(620, 640);
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
        header.setPreferredSize(new Dimension(620, 72));
        JPanel hInner = new JPanel();
        hInner.setOpaque(false);
        hInner.setLayout(new BoxLayout(hInner, BoxLayout.Y_AXIS));
        JLabel t = new JLabel("Edit Quiz");
        t.setFont(new Font("Segoe UI", Font.BOLD, 17));
        t.setForeground(Color.WHITE);
        JLabel s = new JLabel("Exam ID: " + quiz.getExamId() + "  (cannot be changed)");
        s.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        s.setForeground(new Color(180, 210, 240));
        hInner.add(t); hInner.add(Box.createVerticalStrut(2)); hInner.add(s);
        header.add(hInner);

        // ── Split layout ──────────────────────────────────────────────────────
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        split.setDividerLocation(320);
        split.setDividerSize(1);
        split.setBorder(null);

        // ── Left: quiz settings ───────────────────────────────────────────────
        JPanel left = new JPanel();
        left.setBackground(Color.WHITE);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setBorder(new EmptyBorder(20, 24, 20, 16));

        JLabel settingsTitle = new JLabel("Quiz Settings");
        settingsTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        settingsTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        left.add(settingsTitle);
        left.add(Box.createVerticalStrut(14));

        left.add(makeLabel("Title"));
        left.add(Box.createVerticalStrut(5));
        titleField = makeTextField(quiz.getTitle());
        left.add(titleField);
        left.add(Box.createVerticalStrut(12));

        left.add(makeLabel("Time Limit (minutes)"));
        left.add(Box.createVerticalStrut(5));
        timeSpinner = new JSpinner(new SpinnerNumberModel(
            quiz.getTimeLimitMinutes(), 1, 300, 5));
        styleSpinner(timeSpinner);
        left.add(timeSpinner);
        left.add(Box.createVerticalStrut(12));

        left.add(makeLabel("Marks per Question"));
        left.add(Box.createVerticalStrut(5));
        marksSpinner = new JSpinner(new SpinnerNumberModel(
            quiz.getMarksPerQuestion(), 1, 100, 1));
        styleSpinner(marksSpinner);
        left.add(marksSpinner);
        left.add(Box.createVerticalStrut(12));

        left.add(makeLabel("Negative Marks per Wrong Answer"));
        left.add(Box.createVerticalStrut(5));
        negMarksSpinner = new JSpinner(new SpinnerNumberModel(
            quiz.getNegativeMarks(), 0.0, 50.0, 0.5));
        styleSpinner(negMarksSpinner);
        left.add(negMarksSpinner);
        left.add(Box.createVerticalStrut(12));

        left.add(makeLabel("Max Attempts per Student"));
        left.add(Box.createVerticalStrut(5));
        attemptsSpinner = new JSpinner(new SpinnerNumberModel(
            quiz.getMaxAttempts(), 1, 10, 1));
        styleSpinner(attemptsSpinner);
        left.add(attemptsSpinner);
        left.add(Box.createVerticalStrut(12));

        randomizeCheck = new JCheckBox("Randomize question order");
        randomizeCheck.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        randomizeCheck.setOpaque(false);
        randomizeCheck.setSelected(quiz.isRandomizeQuestions());
        randomizeCheck.setAlignmentX(Component.LEFT_ALIGNMENT);
        left.add(randomizeCheck);
        left.add(Box.createVerticalStrut(24));

        JButton saveBtn = makePrimaryButton("Save Changes");
        left.add(saveBtn);

        saveBtn.addActionListener(e -> handleSave());

        // ── Right: question list with edit/delete ─────────────────────────────
        JPanel right = new JPanel(new BorderLayout(0, 8));
        right.setBackground(new Color(248, 249, 252));
        right.setBorder(new EmptyBorder(20, 12, 20, 20));

        JLabel qTitle = new JLabel("Questions");
        qTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));

        qListModel = new DefaultListModel<>();
        JList<String> qList = new JList<>(qListModel);
        qList.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        qList.setBackground(new Color(248, 249, 252));
        qList.setCellRenderer(new QuestionListRenderer());
        refreshQuestionList();

        JScrollPane qScroll = new JScrollPane(qList);
        qScroll.setBorder(null);

        JPanel qBtnRow = new JPanel(new GridLayout(1, 2, 8, 0));
        qBtnRow.setOpaque(false);

        JButton editQBtn   = makeSmallBtn("✏ Edit Selected",   new Color(24, 95, 165));
        JButton deleteQBtn = makeSmallBtn("🗑 Delete Selected", new Color(180, 40, 40));

        qBtnRow.add(editQBtn);
        qBtnRow.add(deleteQBtn);

        right.add(qTitle,   BorderLayout.NORTH);
        right.add(qScroll,  BorderLayout.CENTER);
        right.add(qBtnRow,  BorderLayout.SOUTH);

        split.setLeftComponent(new JScrollPane(left) {{ setBorder(null); }});
        split.setRightComponent(right);

        root.add(header, BorderLayout.NORTH);
        root.add(split,  BorderLayout.CENTER);
        setContentPane(root);

        // ── Edit/Delete question actions ──────────────────────────────────────
        editQBtn.addActionListener(e -> {
            int idx = qList.getSelectedIndex();
            if (idx < 0) { showError("Select a question to edit."); return; }
            List<Question> qs = DatabaseManager.getInstance()
                .getQuestionsByQuizId(quiz.getId());
            if (idx < qs.size())
                new EditQuestionFrame(qs.get(idx), this).setVisible(true);
        });

        deleteQBtn.addActionListener(e -> {
            int idx = qList.getSelectedIndex();
            if (idx < 0) { showError("Select a question to delete."); return; }
            List<Question> qs = DatabaseManager.getInstance()
                .getQuestionsByQuizId(quiz.getId());
            if (idx >= qs.size()) return;
            int choice = JOptionPane.showConfirmDialog(this,
                "Delete this question?", "Confirm",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (choice == JOptionPane.YES_OPTION) {
                DatabaseManager.getInstance().deleteQuestion(qs.get(idx).getId());
                refreshQuestionList();
                dashboard.loadQuizzes();
            }
        });
    }

    private void handleSave() {
        String title   = titleField.getText().trim();
        int time       = (int) timeSpinner.getValue();
        int marks      = (int) marksSpinner.getValue();
        double negMark = ((Number) negMarksSpinner.getValue()).doubleValue();
        int attempts   = (int) attemptsSpinner.getValue();
        boolean random = randomizeCheck.isSelected();

        if (title.isEmpty()) { showError("Title cannot be empty."); return; }

        boolean ok = DatabaseManager.getInstance()
            .updateQuiz(quiz.getId(), title, time, marks, negMark, attempts, random, "", "");

        if (ok) {
            JOptionPane.showMessageDialog(this, "Quiz updated successfully!",
                "Saved", JOptionPane.INFORMATION_MESSAGE);
            dashboard.loadQuizzes();
            dispose();
        } else {
            showError("Failed to save changes.");
        }
    }

    public void refreshQuestionList() {
        qListModel.clear();
        List<Question> qs = DatabaseManager.getInstance().getQuestionsByQuizId(quiz.getId());
        for (int i = 0; i < qs.size(); i++) {
            Question q = qs.get(i);
            String type = switch (q.getQuestionType()) {
                case "truefalse" -> "[T/F] ";
                case "short"     -> "[Short] ";
                default          -> "[MCQ] ";
            };
            qListModel.addElement((i + 1) + ". " + type + q.getQuestionText());
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private JLabel makeLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        l.setForeground(new Color(60, 60, 60));
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private JTextField makeTextField(String text) {
        JTextField f = new JTextField(text);
        f.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        f.setAlignmentX(Component.LEFT_ALIGNMENT);
        return f;
    }

    private void styleSpinner(JSpinner sp) {
        sp.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        sp.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        sp.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    private JButton makePrimaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(new Color(24, 95, 165));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        return btn;
    }

    private JButton makeSmallBtn(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    static class QuestionListRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value,
                int idx, boolean sel, boolean foc) {
            JLabel lbl = (JLabel) super.getListCellRendererComponent(
                list, value, idx, sel, foc);
            lbl.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
            lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            if (!sel) lbl.setBackground(idx % 2 == 0 ? Color.WHITE : new Color(248, 249, 252));
            return lbl;
        }
    }
}