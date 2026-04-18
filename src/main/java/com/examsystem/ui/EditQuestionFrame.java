package com.examsystem.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import com.examsystem.db.DatabaseManager;
import com.examsystem.model.Question;

public class EditQuestionFrame extends JFrame {

    private final Question question;
    private final EditQuizFrame parent;

    private JTextField questionField;
    private JTextField optAField, optBField, optCField, optDField;
    private JComboBox<String> correctCombo;

    public EditQuestionFrame(Question question, EditQuizFrame parent) {
        this.question = question;
        this.parent   = parent;
        setTitle("Edit Question");
        setSize(500, 480);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        buildUI();
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Color.WHITE);

        JPanel header = new JPanel(new GridBagLayout());
        header.setBackground(new Color(24, 95, 165));
        header.setPreferredSize(new Dimension(500, 60));
        JLabel t = new JLabel("Edit Question");
        t.setFont(new Font("Segoe UI", Font.BOLD, 16));
        t.setForeground(Color.WHITE);
        header.add(t);

        JPanel form = new JPanel();
        form.setBackground(Color.WHITE);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBorder(new EmptyBorder(24, 32, 24, 32));

        form.add(makeLabel("Question Text"));
        form.add(Box.createVerticalStrut(5));
        questionField = makeTextField(question.getQuestionText());
        form.add(questionField);
        form.add(Box.createVerticalStrut(12));

        if (!question.isShortAnswer()) {
            if (question.isTrueFalse()) {
                form.add(makeLabel("Option A (True)"));
                form.add(Box.createVerticalStrut(5));
                optAField = makeTextField("True");
                optAField.setEditable(false);
                optAField.setBackground(new Color(245, 245, 245));
                form.add(optAField);
                form.add(Box.createVerticalStrut(10));

                form.add(makeLabel("Option B (False)"));
                form.add(Box.createVerticalStrut(5));
                optBField = makeTextField("False");
                optBField.setEditable(false);
                optBField.setBackground(new Color(245, 245, 245));
                form.add(optBField);
                form.add(Box.createVerticalStrut(10));

                optCField = makeTextField("");
                optDField = makeTextField("");

                form.add(makeLabel("Correct Answer"));
                form.add(Box.createVerticalStrut(5));
                correctCombo = new JComboBox<>(new String[]{"A (True)", "B (False)"});
                correctCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                correctCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
                correctCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
                if ("B".equals(question.getCorrectOption())) correctCombo.setSelectedIndex(1);
                form.add(correctCombo);
            } else {
                String[] labels = {"Option A", "Option B", "Option C", "Option D"};
                String[] vals   = {question.getOptionA(), question.getOptionB(),
                                   question.getOptionC(), question.getOptionD()};
                optAField = makeTextField(vals[0]);
                optBField = makeTextField(vals[1]);
                optCField = makeTextField(vals[2]);
                optDField = makeTextField(vals[3]);
                JTextField[] fields = {optAField, optBField, optCField, optDField};

                for (int i = 0; i < 4; i++) {
                    form.add(makeLabel(labels[i]));
                    form.add(Box.createVerticalStrut(4));
                    form.add(fields[i]);
                    form.add(Box.createVerticalStrut(10));
                }

                form.add(makeLabel("Correct Answer"));
                form.add(Box.createVerticalStrut(5));
                correctCombo = new JComboBox<>(new String[]{"A", "B", "C", "D"});
                correctCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                correctCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
                correctCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
                String co = question.getCorrectOption();
                if ("B".equals(co)) correctCombo.setSelectedIndex(1);
                else if ("C".equals(co)) correctCombo.setSelectedIndex(2);
                else if ("D".equals(co)) correctCombo.setSelectedIndex(3);
                form.add(correctCombo);
            }
        } else {
            JLabel info = new JLabel("Short answer — no options to edit.");
            info.setFont(new Font("Segoe UI", Font.ITALIC, 12));
            info.setForeground(new Color(140, 140, 140));
            info.setAlignmentX(Component.LEFT_ALIGNMENT);
            form.add(info);
            optAField = optBField = optCField = optDField = new JTextField();
        }

        form.add(Box.createVerticalStrut(20));

        JButton saveBtn = new JButton("Save Question");
        saveBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        saveBtn.setBackground(new Color(24, 95, 165));
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setFocusPainted(false);
        saveBtn.setBorderPainted(false);
        saveBtn.setOpaque(true);
        saveBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        saveBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        saveBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        form.add(saveBtn);

        root.add(header, BorderLayout.NORTH);
        root.add(new JScrollPane(form) {{ setBorder(null); }}, BorderLayout.CENTER);
        setContentPane(root);

        saveBtn.addActionListener(e -> handleSave());
    }

    private void handleSave() {
        String qText = questionField.getText().trim();
        if (qText.isEmpty()) {
            showError("Question text cannot be empty."); return;
        }

        String optA, optB, optC = "", optD = "", correct = null;

        if (question.isShortAnswer()) {
            optA = optB = "";
        } else if (question.isTrueFalse()) {
            optA = "True"; optB = "False";
            correct = correctCombo.getSelectedIndex() == 0 ? "A" : "B";
        } else {
            optA = optAField.getText().trim();
            optB = optBField.getText().trim();
            optC = optCField.getText().trim();
            optD = optDField.getText().trim();
            if (optA.isEmpty() || optB.isEmpty() || optC.isEmpty() || optD.isEmpty()) {
                showError("All options must be filled."); return;
            }
            correct = (String) correctCombo.getSelectedItem();
        }

        DatabaseManager.getInstance().updateQuestion(
            question.getId(), qText, optA, optB, optC, optD, correct);

        JOptionPane.showMessageDialog(this, "Question updated!",
            "Saved", JOptionPane.INFORMATION_MESSAGE);
        parent.refreshQuestionList();
        dispose();
    }

    private JLabel makeLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        l.setForeground(new Color(60, 60, 60));
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private JTextField makeTextField(String text) {
        JTextField f = new JTextField(text != null ? text : "");
        f.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        f.setAlignmentX(Component.LEFT_ALIGNMENT);
        return f;
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
}