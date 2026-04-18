package com.examsystem.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import com.examsystem.db.DatabaseManager;
import com.examsystem.model.Question;
import com.examsystem.model.Quiz;
import com.examsystem.model.User;

public class QuizFrame extends JFrame {

    private final User student;
    private final Quiz quiz;
    private final List<Question> questions;
    private final StudentDashboard dashboard;

    private int currentIndex = 0;
    private final String[] answers;
    private final Map<Integer, String> shortAnswers = new HashMap<>();

    private javax.swing.Timer countdownTimer;
    private javax.swing.Timer pulseTimer;
    private AnimatedTimerPanel timerPanel;

    private JLabel questionNumLabel, questionText;
    private JPanel answerArea, navDotsPanel, imagePanel;
    private JButton prevBtn, nextBtn, submitBtn;
    private ButtonGroup optGroup;
    private JRadioButton optA, optB, optC, optD;
    private JTextArea shortAnswerArea;

    public QuizFrame(User student, Quiz quiz,
                     List<Question> questions, StudentDashboard dashboard) {
        this.student   = student;
        this.quiz      = quiz;
        this.dashboard = dashboard;

        List<Question> qList = new ArrayList<>(questions);
        if (quiz.isRandomizeQuestions()) Collections.shuffle(qList);
        this.questions = qList;
        this.answers   = new String[qList.size()];

        setTitle(quiz.getTitle() + "  [" + quiz.getExamId() + "]");
        setSize(720, 620);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) { confirmExit(); }
        });

        buildUI();
        loadQuestion(0);
        startTimer();
    }

    // ── UI ────────────────────────────────────────────────────────────────────

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Color.WHITE);

        // ── Top bar ───────────────────────────────────────────────────────────
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(83, 74, 183));
        topBar.setPreferredSize(new Dimension(720, 90));
        topBar.setBorder(new EmptyBorder(12, 24, 12, 20));

        JPanel topLeft = new JPanel();
        topLeft.setOpaque(false);
        topLeft.setLayout(new BoxLayout(topLeft, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel(quiz.getTitle());
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        titleLabel.setForeground(Color.WHITE);

        String negStr = quiz.getNegativeMarks() > 0
            ? "  ·  -" + quiz.getNegativeMarks() + " neg" : "";
        JLabel subLabel = new JLabel("ID: " + quiz.getExamId()
            + "  ·  " + questions.size() + " questions"
            + "  ·  " + quiz.getMarksPerQuestion() + " pts" + negStr);
        subLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        subLabel.setForeground(new Color(200, 195, 240));

        topLeft.add(titleLabel);
        topLeft.add(Box.createVerticalStrut(4));
        topLeft.add(subLabel);

        // Animated timer
        timerPanel = new AnimatedTimerPanel(quiz.getTimeLimitMinutes() * 60);

        topBar.add(topLeft,    BorderLayout.CENTER);
        topBar.add(timerPanel, BorderLayout.EAST);

        // ── Scroll content ────────────────────────────────────────────────────
        JPanel scrollContent = new JPanel();
        scrollContent.setBackground(Color.WHITE);
        scrollContent.setLayout(new BoxLayout(scrollContent, BoxLayout.Y_AXIS));
        scrollContent.setBorder(new EmptyBorder(20, 36, 16, 36));

        // Question number label
        questionNumLabel = new JLabel("Question 1");
        questionNumLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        questionNumLabel.setForeground(new Color(130, 120, 190));
        questionNumLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Question text
        questionText = new JLabel();
        questionText.setFont(new Font("Segoe UI", Font.BOLD, 15));
        questionText.setVerticalAlignment(SwingConstants.TOP);
        questionText.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Image panel
        imagePanel = new JPanel();
        imagePanel.setOpaque(false);
        imagePanel.setLayout(new BoxLayout(imagePanel, BoxLayout.Y_AXIS));
        imagePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        imagePanel.setVisible(false);

        // Answer area
        answerArea = new JPanel();
        answerArea.setOpaque(false);
        answerArea.setLayout(new BoxLayout(answerArea, BoxLayout.Y_AXIS));
        answerArea.setAlignmentX(Component.LEFT_ALIGNMENT);

        scrollContent.add(questionNumLabel);
        scrollContent.add(Box.createVerticalStrut(8));
        scrollContent.add(questionText);
        scrollContent.add(Box.createVerticalStrut(12));
        scrollContent.add(imagePanel);
        scrollContent.add(Box.createVerticalStrut(12));
        scrollContent.add(answerArea);

        JScrollPane scrollPane = new JScrollPane(scrollContent);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(14);
        scrollPane.setHorizontalScrollBarPolicy(
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        // ── Bottom bar ────────────────────────────────────────────────────────
        navDotsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        navDotsPanel.setOpaque(false);

        JPanel bottomBar = new JPanel(new BorderLayout());
        bottomBar.setBackground(new Color(248, 248, 252));
        bottomBar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 220, 230)),
            BorderFactory.createEmptyBorder(12, 24, 12, 24)));

        prevBtn   = makeNavBtn("← Previous");
        nextBtn   = makeNavBtn("Next →");

        submitBtn = new JButton("Submit Exam");
        submitBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        submitBtn.setBackground(new Color(59, 109, 17));
        submitBtn.setForeground(Color.WHITE);
        submitBtn.setFocusPainted(false);
        submitBtn.setBorderPainted(false);
        submitBtn.setOpaque(true);
        submitBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        submitBtn.setBorder(new EmptyBorder(10, 22, 10, 22));

        JPanel leftBtns  = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        leftBtns.setOpaque(false);
        leftBtns.add(prevBtn);

        JPanel rightBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        rightBtns.setOpaque(false);
        rightBtns.add(nextBtn);
        rightBtns.add(submitBtn);

        bottomBar.add(leftBtns,     BorderLayout.WEST);
        bottomBar.add(navDotsPanel, BorderLayout.CENTER);
        bottomBar.add(rightBtns,    BorderLayout.EAST);

        root.add(topBar,    BorderLayout.NORTH);
        root.add(scrollPane, BorderLayout.CENTER);
        root.add(bottomBar, BorderLayout.SOUTH);
        setContentPane(root);

        // Actions
        prevBtn.addActionListener(e -> {
            saveCurrentAnswer();
            if (currentIndex > 0) loadQuestion(currentIndex - 1);
        });
        nextBtn.addActionListener(e -> {
            saveCurrentAnswer();
            if (currentIndex < questions.size() - 1) loadQuestion(currentIndex + 1);
        });
        submitBtn.addActionListener(e -> { saveCurrentAnswer(); confirmSubmit(); });
    }

    // ── Load question ─────────────────────────────────────────────────────────

    private void loadQuestion(int index) {
        currentIndex = index;
        Question q   = questions.get(index);

        questionNumLabel.setText(
            "Question " + (index + 1) + " of " + questions.size());
        questionText.setText(
            "<html><body style='width:580px'>" + q.getQuestionText() + "</body></html>");

        // ── Image ─────────────────────────────────────────────────────────────
        imagePanel.removeAll();
        if (q.hasImage()) {
            File f = new File(q.getImagePath());
            if (f.exists()) {
                try {
                    ImageIcon raw    = new ImageIcon(q.getImagePath());
                    int origW        = raw.getIconWidth();
                    int origH        = raw.getIconHeight();
                    int targetW      = Math.min(origW, 560);
                    int targetH      = (int)((double) origH / origW * targetW);
                    Image scaled     = raw.getImage()
                        .getScaledInstance(targetW, targetH, Image.SCALE_SMOOTH);
                    JLabel imgLbl    = new JLabel(new ImageIcon(scaled));
                    imgLbl.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(210, 210, 225)),
                        BorderFactory.createEmptyBorder(4, 4, 4, 4)));
                    imgLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
                    imagePanel.add(imgLbl);
                    imagePanel.add(Box.createVerticalStrut(4));
                    imagePanel.setVisible(true);
                } catch (Exception ex) {
                    imagePanel.setVisible(false);
                }
            } else {
                JLabel warn = new JLabel("⚠  Image file not found");
                warn.setFont(new Font("Segoe UI", Font.ITALIC, 11));
                warn.setForeground(new Color(180, 100, 30));
                warn.setAlignmentX(Component.LEFT_ALIGNMENT);
                imagePanel.add(warn);
                imagePanel.setVisible(true);
            }
        } else {
            imagePanel.setVisible(false);
        }

        // ── Answers ───────────────────────────────────────────────────────────
        answerArea.removeAll();
        optGroup = new ButtonGroup();

        if (q.isShortAnswer()) {
            JLabel hint = new JLabel("Type your answer:");
            hint.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            hint.setForeground(new Color(120, 120, 140));
            hint.setAlignmentX(Component.LEFT_ALIGNMENT);

            shortAnswerArea = new JTextArea(4, 40);
            shortAnswerArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            shortAnswerArea.setLineWrap(true);
            shortAnswerArea.setWrapStyleWord(true);
            shortAnswerArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 215)),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
            if (shortAnswers.containsKey(index))
                shortAnswerArea.setText(shortAnswers.get(index));

            JScrollPane sp = new JScrollPane(shortAnswerArea);
            sp.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));
            sp.setAlignmentX(Component.LEFT_ALIGNMENT);
            sp.setBorder(null);

            answerArea.add(hint);
            answerArea.add(Box.createVerticalStrut(8));
            answerArea.add(sp);

        } else if (q.isTrueFalse()) {
            optA = makeOptBtn("True");
            optB = makeOptBtn("False");
            optC = new JRadioButton(); optD = new JRadioButton();
            optGroup.add(optA); optGroup.add(optB);
            answerArea.add(wrapOpt(optA, "A", new Color(234, 243, 222),
                new Color(59, 109, 17)));
            answerArea.add(Box.createVerticalStrut(10));
            answerArea.add(wrapOpt(optB, "B", new Color(252, 235, 235),
                new Color(180, 40, 40)));
            restoreSelection(index);

        } else {
            String[] labels = {"A", "B", "C", "D"};
            String[] texts  = {q.getOptionA(), q.getOptionB(),
                               q.getOptionC(), q.getOptionD()};
            JRadioButton[] opts = new JRadioButton[4];
            for (int i = 0; i < 4; i++) {
                opts[i] = makeOptBtn(texts[i]);
                optGroup.add(opts[i]);
                answerArea.add(wrapOpt(opts[i], labels[i],
                    new Color(250, 250, 253), new Color(83, 74, 183)));
                answerArea.add(Box.createVerticalStrut(10));
            }
            optA = opts[0]; optB = opts[1];
            optC = opts[2]; optD = opts[3];
            restoreSelection(index);
        }

        prevBtn.setEnabled(index > 0);
        nextBtn.setEnabled(index < questions.size() - 1);
        submitBtn.setVisible(index == questions.size() - 1);

        updateNavDots();
        answerArea.revalidate(); answerArea.repaint();
        imagePanel.revalidate(); imagePanel.repaint();
    }

    private void restoreSelection(int index) {
        String s = answers[index];
        if ("A".equals(s) && optA != null) optA.setSelected(true);
        else if ("B".equals(s) && optB != null) optB.setSelected(true);
        else if ("C".equals(s) && optC != null) optC.setSelected(true);
        else if ("D".equals(s) && optD != null) optD.setSelected(true);
    }

    private void saveCurrentAnswer() {
        Question q = questions.get(currentIndex);
        if (q.isShortAnswer()) {
            if (shortAnswerArea != null)
                shortAnswers.put(currentIndex, shortAnswerArea.getText().trim());
            answers[currentIndex] = "SHORT";
        } else {
            if (optA != null && optA.isSelected())      answers[currentIndex] = "A";
            else if (optB != null && optB.isSelected()) answers[currentIndex] = "B";
            else if (optC != null && optC.isSelected()) answers[currentIndex] = "C";
            else if (optD != null && optD.isSelected()) answers[currentIndex] = "D";
            else                                         answers[currentIndex] = null;
        }
        updateNavDots();
    }

    private void updateNavDots() {
        navDotsPanel.removeAll();
        for (int i = 0; i < questions.size(); i++) {
            JLabel dot = new JLabel();
            dot.setPreferredSize(new Dimension(11, 11));
            dot.setOpaque(true);
            dot.setBackground(
                i == currentIndex  ? new Color(83, 74, 183) :
                answers[i] != null ? new Color(59, 109, 17) :
                                     new Color(210, 210, 220));
            dot.setToolTipText("Q" + (i + 1));
            final int idx = i;
            dot.addMouseListener(new MouseAdapter() {
                @Override public void mouseClicked(MouseEvent e) {
                    saveCurrentAnswer(); loadQuestion(idx);
                }
                @Override public void mouseEntered(MouseEvent e) {
                    dot.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                }
            });
            navDotsPanel.add(dot);
        }
        navDotsPanel.revalidate();
        navDotsPanel.repaint();
    }

    // ── Timer ─────────────────────────────────────────────────────────────────

    private void startTimer() {
        countdownTimer = new javax.swing.Timer(1000, e -> {
            int left = timerPanel.getSecondsLeft() - 1;
            timerPanel.setSecondsLeft(left);
            if (left <= 0) {
                countdownTimer.stop();
                if (pulseTimer != null) pulseTimer.stop();
                JOptionPane.showMessageDialog(this,
                    "Time's up! Your exam is being submitted.",
                    "Time Up", JOptionPane.WARNING_MESSAGE);
                submitExam();
            }
        });
        countdownTimer.start();

        // Smooth pulse repaint in last 10% of time
        pulseTimer = new javax.swing.Timer(80, e -> {
            int total = quiz.getTimeLimitMinutes() * 60;
            if (timerPanel.getSecondsLeft() <= total * 0.10)
                timerPanel.repaint();
        });
        pulseTimer.start();
    }

    // ── Submit ────────────────────────────────────────────────────────────────

    private void confirmSubmit() {
        long answered   = Arrays.stream(answers).filter(Objects::nonNull).count();
        long unanswered = questions.size() - answered;
        String msg = "Answered: " + answered + " of " + questions.size();
        if (unanswered > 0) msg += "\n" + unanswered + " unanswered.";
        msg += "\n\nSubmit the exam?";
        if (JOptionPane.showConfirmDialog(this, msg, "Submit",
            JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) submitExam();
    }

    private void confirmExit() {
        if (JOptionPane.showConfirmDialog(this,
            "Leaving will submit your exam. Proceed?", "Exit",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) submitExam();
    }

    private void submitExam() {
        if (countdownTimer != null) countdownTimer.stop();
        if (pulseTimer     != null) pulseTimer.stop();

        int score     = 0;
        int marksPerQ = quiz.getMarksPerQuestion();
        double neg    = quiz.getNegativeMarks();

        for (int i = 0; i < questions.size(); i++) {
            Question q = questions.get(i);
            if (q.isShortAnswer()) continue;
            String ans = answers[i];
            if (ans == null) continue;
            if (q.getCorrectOption().equals(ans)) score += marksPerQ;
            else score = (int) Math.max(0, score - neg);
        }

        int totalMarks = (int) questions.stream()
            .filter(q -> !q.isShortAnswer()).count() * marksPerQ;

        DatabaseManager.getInstance()
            .saveResult(student.getId(), quiz.getId(), score, totalMarks);

        dashboard.loadHistory();
        dashboard.setVisible(true);
        new ResultFrame(student, quiz, questions, answers, score, totalMarks)
            .setVisible(true);
        dispose();
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private JRadioButton makeOptBtn(String text) {
        JRadioButton rb = new JRadioButton(text);
        rb.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        rb.setOpaque(false);
        rb.setFocusPainted(false);
        rb.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return rb;
    }

    private JPanel wrapOpt(JRadioButton rb, String letter,
                           Color baseBg, Color accent) {
        JPanel w = new JPanel(new BorderLayout(12, 0));
        w.setBackground(baseBg);
        w.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));
        w.setAlignmentX(Component.LEFT_ALIGNMENT);
        w.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 218, 240)),
            BorderFactory.createEmptyBorder(10, 14, 10, 14)));

        JLabel lbl = new JLabel(letter + ".");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(new Color(100, 96, 160));
        lbl.setPreferredSize(new Dimension(22, 20));

        w.add(lbl, BorderLayout.WEST);
        w.add(rb,  BorderLayout.CENTER);

        w.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { rb.setSelected(true); }
            @Override public void mouseEntered(MouseEvent e) {
                w.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(accent),
                    BorderFactory.createEmptyBorder(10, 14, 10, 14)));
                w.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }
            @Override public void mouseExited(MouseEvent e) {
                w.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(220, 218, 240)),
                    BorderFactory.createEmptyBorder(10, 14, 10, 14)));
            }
        });
        return w;
    }

    private JButton makeNavBtn(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setBackground(Color.WHITE);
        btn.setForeground(new Color(83, 74, 183));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 198, 230)),
            BorderFactory.createEmptyBorder(8, 16, 8, 16)));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }
}