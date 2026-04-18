package com.examsystem.ui;

import com.examsystem.model.Question;
import com.examsystem.model.Quiz;
import com.examsystem.model.User;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.List;

public class ResultFrame extends JFrame {
    private final User student;
    private final Quiz quiz;
    private final List<Question> questions;
    private final String[] answers;
    private final int score;
    private final int totalMarks;

    public ResultFrame(User student, Quiz quiz, List<Question> questions,
                       String[] answers, int score, int totalMarks) {
        this.student = student;
        this.quiz = quiz;
        this.questions = questions;
        this.answers = answers;
        this.score = score;
        this.totalMarks = totalMarks;
        setTitle("Result — " + quiz.getTitle());
        setSize(680, 640);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        buildUI();
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Color.WHITE);
        int pct = (int) Math.round(score * 100.0 / totalMarks);
        boolean passed = pct >= 50;
        Color headerBg = passed ? new Color(59, 109, 17) : new Color(160, 40, 40);
        JPanel header = new JPanel(new GridBagLayout());
        header.setBackground(headerBg);
        header.setPreferredSize(new Dimension(680, 160));
        JPanel headerInner = new JPanel();
        headerInner.setOpaque(false);
        headerInner.setLayout(new BoxLayout(headerInner, BoxLayout.Y_AXIS));
        JLabel resultLabel = new JLabel(passed ? "Exam Passed!" : "Better luck next time");
        resultLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        resultLabel.setForeground(Color.WHITE);
        resultLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel scoreLabel = new JLabel(score + " / " + totalMarks + " marks  (" + pct + "%)");
        scoreLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        scoreLabel.setForeground(new Color(210, 240, 210));
        scoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel quizLabel = new JLabel(quiz.getTitle() + "  ·  " + quiz.getExamId());
        quizLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        quizLabel.setForeground(new Color(180, 220, 180));
        quizLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        headerInner.add(resultLabel);
        headerInner.add(Box.createVerticalStrut(6));
        headerInner.add(scoreLabel);
        headerInner.add(Box.createVerticalStrut(2));
        headerInner.add(quizLabel);
        header.add(headerInner);
        int correct = 0, incorrect = 0, skipped = 0;
        for (int i = 0; i < questions.size(); i++) {
            if (answers[i] == null) skipped++;
            else if (questions.get(i).getCorrectOption().equals(answers[i])) correct++;
            else incorrect++;
        }
        JPanel statsStrip = new JPanel(new GridLayout(1, 3));
        statsStrip.setBackground(new Color(248, 249, 252));
        statsStrip.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 225)));
        statsStrip.setPreferredSize(new Dimension(680, 56));
        statsStrip.add(makeStatCell("Correct",   String.valueOf(correct),   new Color(59, 109, 17)));
        statsStrip.add(makeStatCell("Incorrect", String.valueOf(incorrect), new Color(160, 40, 40)));
        statsStrip.add(makeStatCell("Skipped",   String.valueOf(skipped),   new Color(100, 100, 120)));
        JPanel reviewPanel = new JPanel();
        reviewPanel.setBackground(Color.WHITE);
        reviewPanel.setLayout(new BoxLayout(reviewPanel, BoxLayout.Y_AXIS));
        reviewPanel.setBorder(new EmptyBorder(16, 24, 16, 24));
        JLabel reviewTitle = new JLabel("Answer Review");
        reviewTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        reviewTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        reviewPanel.add(reviewTitle);
        reviewPanel.add(Box.createVerticalStrut(12));
        for (int i = 0; i < questions.size(); i++) {
            Question q = questions.get(i);
            String answer = answers[i];
            boolean isCorrect = q.getCorrectOption().equals(answer);
            boolean isSkipped = answer == null;
            JPanel card = buildReviewCard(i + 1, q, answer, isCorrect, isSkipped);
            card.setAlignmentX(Component.LEFT_ALIGNMENT);
            reviewPanel.add(card);
            reviewPanel.add(Box.createVerticalStrut(10));
        }
        JScrollPane scroll = new JScrollPane(reviewPanel);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottom.setBackground(new Color(248, 249, 252));
        bottom.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 220, 225)));
        JButton closeBtn = new JButton("Back to Dashboard");
        closeBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        closeBtn.setBackground(new Color(83, 74, 183));
        closeBtn.setForeground(Color.WHITE);
        closeBtn.setFocusPainted(false);
        closeBtn.setBorderPainted(false);
        closeBtn.setOpaque(true);
        closeBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        closeBtn.setBorder(new EmptyBorder(10, 28, 10, 28));
        closeBtn.addActionListener(e -> dispose());
        bottom.add(closeBtn);
        JPanel northBlock = new JPanel(new BorderLayout());
        northBlock.add(header, BorderLayout.NORTH);
        northBlock.add(statsStrip, BorderLayout.SOUTH);
        root.add(northBlock, BorderLayout.NORTH);
        root.add(scroll, BorderLayout.CENTER);
        root.add(bottom, BorderLayout.SOUTH);
        setContentPane(root);
    }

    private JPanel buildReviewCard(int num, Question q, String answer,
                                   boolean correct, boolean skipped) {
        Color borderColor = skipped ? new Color(200, 200, 215)
                          : correct ? new Color(100, 180, 80) : new Color(220, 100, 100);
        Color bgColor = skipped ? Color.WHITE
                      : correct ? new Color(243, 250, 237) : new Color(253, 237, 237);
        JPanel card = new JPanel(new BorderLayout(0, 8));
        card.setBackground(bgColor);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(borderColor),
            BorderFactory.createEmptyBorder(12, 16, 12, 16)));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        JPanel qRow = new JPanel(new BorderLayout(8, 0));
        qRow.setOpaque(false);
        JLabel numLabel = new JLabel("Q" + num);
        numLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        numLabel.setForeground(new Color(120, 120, 140));
        numLabel.setPreferredSize(new Dimension(28, 20));
        JLabel qText = new JLabel("<html><body style='width:460px'>" + q.getQuestionText() + "</body></html>");
        qText.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        JLabel statusIcon = new JLabel(skipped ? "—" : correct ? "✓" : "✗");
        statusIcon.setFont(new Font("Segoe UI", Font.BOLD, 16));
        statusIcon.setForeground(skipped ? new Color(150, 150, 170)
                               : correct ? new Color(59, 109, 17) : new Color(180, 40, 40));
        qRow.add(numLabel, BorderLayout.WEST);
        qRow.add(qText, BorderLayout.CENTER);
        qRow.add(statusIcon, BorderLayout.EAST);
        JPanel ansRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        ansRow.setOpaque(false);
        String corrAns = q.getCorrectOption() + ". " + getOptionText(q, q.getCorrectOption());
        if (!correct) {
            String yourAns = answer == null ? "Not answered" : answer + ". " + getOptionText(q, answer);
            JLabel yourLabel = new JLabel("Your answer: " + yourAns);
            yourLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            yourLabel.setForeground(new Color(180, 60, 60));
            JLabel sep = new JLabel("  |  ");
            sep.setForeground(new Color(200, 200, 210));
            JLabel corrLabel = new JLabel("Correct: " + corrAns);
            corrLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
            corrLabel.setForeground(new Color(59, 109, 17));
            ansRow.add(yourLabel);
            ansRow.add(sep);
            ansRow.add(corrLabel);
        } else {
            JLabel okLabel = new JLabel("Your answer: " + corrAns);
            okLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            okLabel.setForeground(new Color(59, 109, 17));
            ansRow.add(okLabel);
        }
        card.add(qRow, BorderLayout.NORTH);
        card.add(ansRow, BorderLayout.CENTER);
        return card;
    }

    private String getOptionText(Question q, String letter) {
        switch (letter) {
            case "A": return q.getOptionA();
            case "B": return q.getOptionB();
            case "C": return q.getOptionC();
            case "D": return q.getOptionD();
            default:  return "";
        }
    }

    private JPanel makeStatCell(String label, String value, Color color) {
        JPanel cell = new JPanel(new GridBagLayout());
        cell.setOpaque(false);
        cell.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(220, 220, 225)));
        JPanel inner = new JPanel();
        inner.setOpaque(false);
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));
        JLabel val = new JLabel(value);
        val.setFont(new Font("Segoe UI", Font.BOLD, 22));
        val.setForeground(color);
        val.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lbl.setForeground(new Color(120, 120, 130));
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        inner.add(val);
        inner.add(lbl);
        cell.add(inner);
        return cell;
    }
}