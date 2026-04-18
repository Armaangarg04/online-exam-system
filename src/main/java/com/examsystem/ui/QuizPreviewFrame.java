package com.examsystem.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import com.examsystem.db.DatabaseManager;
import com.examsystem.model.Question;
import com.examsystem.model.Quiz;

public class QuizPreviewFrame extends JFrame {

    private final Quiz quiz;
    private final List<Question> questions;
    private int currentIndex = 0;

    private JLabel questionNumLabel, questionText;
    private JPanel optionsPanel;
    private JButton prevBtn, nextBtn;
    private JLabel typeTag;

    public QuizPreviewFrame(Quiz quiz) {
        this.quiz      = quiz;
        this.questions = DatabaseManager.getInstance().getQuestionsByQuizId(quiz.getId());
        setTitle("Preview — " + quiz.getTitle());
        setSize(640, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        buildUI();
        if (!questions.isEmpty()) loadQuestion(0);
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Color.WHITE);

        // ── Top bar ───────────────────────────────────────────────────────────
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(100, 100, 120));
        topBar.setPreferredSize(new Dimension(640, 64));
        topBar.setBorder(new EmptyBorder(0, 24, 0, 24));

        JPanel topLeft = new JPanel();
        topLeft.setOpaque(false);
        topLeft.setLayout(new BoxLayout(topLeft, BoxLayout.Y_AXIS));

        JLabel previewBadge = new JLabel("👁  PREVIEW MODE — Read Only");
        previewBadge.setFont(new Font("Segoe UI", Font.BOLD, 13));
        previewBadge.setForeground(Color.WHITE);

        JLabel subLabel = new JLabel(quiz.getTitle() + "  ·  " + quiz.getExamId() +
            "  ·  " + questions.size() + " questions");
        subLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        subLabel.setForeground(new Color(200, 200, 210));

        topLeft.add(previewBadge);
        topLeft.add(Box.createVerticalStrut(2));
        topLeft.add(subLabel);
        topBar.add(topLeft, BorderLayout.WEST);

        // ── Question area ─────────────────────────────────────────────────────
        JPanel qPanel = new JPanel(new BorderLayout());
        qPanel.setBackground(Color.WHITE);
        qPanel.setBorder(new EmptyBorder(24, 36, 16, 36));

        JPanel qHeader = new JPanel(new BorderLayout(8, 0));
        qHeader.setOpaque(false);

        questionNumLabel = new JLabel();
        questionNumLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        questionNumLabel.setForeground(new Color(130, 120, 190));

        typeTag = new JLabel();
        typeTag.setFont(new Font("Segoe UI", Font.BOLD, 11));
        typeTag.setOpaque(true);
        typeTag.setBorder(new EmptyBorder(2, 8, 2, 8));

        JPanel numRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        numRow.setOpaque(false);
        numRow.add(questionNumLabel);
        numRow.add(typeTag);

        questionText = new JLabel();
        questionText.setFont(new Font("Segoe UI", Font.BOLD, 15));
        questionText.setVerticalAlignment(SwingConstants.TOP);

        JPanel qTop = new JPanel();
        qTop.setOpaque(false);
        qTop.setLayout(new BoxLayout(qTop, BoxLayout.Y_AXIS));
        qTop.add(numRow);
        qTop.add(Box.createVerticalStrut(8));
        qTop.add(questionText);

        optionsPanel = new JPanel();
        optionsPanel.setOpaque(false);
        optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));
        optionsPanel.setBorder(new EmptyBorder(16, 0, 0, 0));

        qPanel.add(qTop,          BorderLayout.NORTH);
        qPanel.add(optionsPanel,  BorderLayout.CENTER);

        // ── Bottom nav ────────────────────────────────────────────────────────
        JPanel bottomBar = new JPanel(new BorderLayout());
        bottomBar.setBackground(new Color(248, 248, 252));
        bottomBar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 220, 230)),
            BorderFactory.createEmptyBorder(12, 24, 12, 24)));

        prevBtn = makeNavBtn("← Previous");
        nextBtn = makeNavBtn("Next →");

        JLabel infoLabel = new JLabel("Preview — students cannot see correct answers");
        infoLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        infoLabel.setForeground(new Color(160, 160, 170));
        infoLabel.setHorizontalAlignment(SwingConstants.CENTER);

        bottomBar.add(prevBtn,   BorderLayout.WEST);
        bottomBar.add(infoLabel, BorderLayout.CENTER);
        bottomBar.add(nextBtn,   BorderLayout.EAST);

        root.add(topBar,    BorderLayout.NORTH);
        root.add(qPanel,    BorderLayout.CENTER);
        root.add(bottomBar, BorderLayout.SOUTH);

        if (questions.isEmpty()) {
            JPanel empty = new JPanel(new GridBagLayout());
            empty.setBackground(Color.WHITE);
            JLabel msg = new JLabel("No questions in this quiz yet.");
            msg.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            msg.setForeground(new Color(160, 160, 170));
            empty.add(msg);
            root.add(empty, BorderLayout.CENTER);
        }

        setContentPane(root);

        prevBtn.addActionListener(e -> { if (currentIndex > 0) loadQuestion(currentIndex - 1); });
        nextBtn.addActionListener(e -> { if (currentIndex < questions.size() - 1) loadQuestion(currentIndex + 1); });
    }

    private void loadQuestion(int index) {
        currentIndex = index;
        Question q   = questions.get(index);

        questionNumLabel.setText("Question " + (index + 1) + " of " + questions.size());
        questionText.setText("<html><body style='width:500px'>" + q.getQuestionText() + "</body></html>");

        // Type tag
        switch (q.getQuestionType()) {
            case "truefalse" -> {
                typeTag.setText("True / False");
                typeTag.setBackground(new Color(234, 243, 222));
                typeTag.setForeground(new Color(59, 109, 17));
            }
            case "short" -> {
                typeTag.setText("Short Answer");
                typeTag.setBackground(new Color(255, 243, 205));
                typeTag.setForeground(new Color(120, 80, 0));
            }
            default -> {
                typeTag.setText("MCQ");
                typeTag.setBackground(new Color(232, 241, 251));
                typeTag.setForeground(new Color(24, 95, 165));
            }
        }

        optionsPanel.removeAll();

        if (q.isShortAnswer()) {
            JPanel box = new JPanel(new BorderLayout());
            box.setBackground(new Color(252, 252, 248));
            box.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 210, 180)),
                BorderFactory.createEmptyBorder(12, 14, 12, 14)));
            box.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
            JLabel hint = new JLabel("Student will type their answer here...");
            hint.setFont(new Font("Segoe UI", Font.ITALIC, 13));
            hint.setForeground(new Color(180, 170, 140));
            box.add(hint);
            optionsPanel.add(box);
        } else {
            String[] labels = {"A", "B", "C", "D"};
            String[] texts;
            if (q.isTrueFalse()) {
                texts = new String[]{"True", "False", null, null};
            } else {
                texts = new String[]{q.getOptionA(), q.getOptionB(),
                                     q.getOptionC(), q.getOptionD()};
            }
            for (int i = 0; i < texts.length; i++) {
                if (texts[i] == null || texts[i].isEmpty()) continue;
                boolean isCorrect = labels[i].equals(q.getCorrectOption());
                JPanel opt = makePreviewOption(labels[i], texts[i], isCorrect);
                optionsPanel.add(opt);
                optionsPanel.add(Box.createVerticalStrut(8));
            }
        }

        prevBtn.setEnabled(index > 0);
        nextBtn.setEnabled(index < questions.size() - 1);
        optionsPanel.revalidate();
        optionsPanel.repaint();
    }

    private JPanel makePreviewOption(String letter, String text, boolean correct) {
        JPanel p = new JPanel(new BorderLayout(10, 0));
        p.setBackground(correct ? new Color(240, 250, 235) : new Color(250, 250, 253));
        p.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(correct
                ? new Color(100, 180, 80) : new Color(220, 218, 240)),
            BorderFactory.createEmptyBorder(10, 14, 10, 14)));
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));

        JLabel letterLabel = new JLabel(letter + ".");
        letterLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        letterLabel.setForeground(correct ? new Color(59, 109, 17) : new Color(80, 80, 100));
        letterLabel.setPreferredSize(new Dimension(24, 20));

        JLabel textLabel = new JLabel(text);
        textLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        textLabel.setForeground(correct ? new Color(40, 100, 20) : new Color(50, 50, 70));

        JLabel checkLabel = new JLabel(correct ? "✓ Correct" : "");
        checkLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        checkLabel.setForeground(new Color(59, 109, 17));

        p.add(letterLabel,  BorderLayout.WEST);
        p.add(textLabel,    BorderLayout.CENTER);
        p.add(checkLabel,   BorderLayout.EAST);
        return p;
    }

    private JButton makeNavBtn(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setBackground(Color.WHITE);
        btn.setForeground(new Color(80, 80, 100));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 215)),
            BorderFactory.createEmptyBorder(8, 16, 8, 16)));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }
}