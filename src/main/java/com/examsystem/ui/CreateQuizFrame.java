package com.examsystem.ui;

import com.examsystem.db.DatabaseManager;
import com.examsystem.model.Question;
import com.examsystem.model.Quiz;
import com.examsystem.model.User;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.List;

public class CreateQuizFrame extends JFrame {

    private final User teacher;
    private final TeacherDashboard dashboard;
    private Quiz existingQuiz;

    private JTextField examIdField, titleField;
    private JTextField startTimeField, endTimeField, imagePathField;
    private JSpinner timeSpinner, marksSpinner, attemptsSpinner;
    private JSpinner negMarksSpinner;
    private JCheckBox randomizeCheck;

    private JTextField questionField;
    private JTextField optAField, optBField, optCField, optDField;
    private JComboBox<String> correctCombo;
    private JComboBox<String> typeCombo;
    private JPanel optionsPanel;
    private DefaultListModel<String> questionListModel;

    public CreateQuizFrame(User teacher, TeacherDashboard dashboard) {
        this.teacher = teacher; this.dashboard = dashboard; this.existingQuiz = null;
        init("Create New Quiz"); showQuizPanel();
    }

    public CreateQuizFrame(User teacher, TeacherDashboard dashboard, Quiz quiz) {
        this.teacher = teacher; this.dashboard = dashboard; this.existingQuiz = quiz;
        init("Add Questions — " + quiz.getTitle()); showQuestionsPanel();
    }

    private void init(String title) {
        setTitle(title);
        setSize(660, 680);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
    }

    // ── Panel 1: Create Quiz ──────────────────────────────────────────────────

    private void showQuizPanel() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Color.WHITE);
        root.add(makeHeader("Create New Quiz", "Set up the exam details"), BorderLayout.NORTH);

        JPanel form = new JPanel();
        form.setBackground(Color.WHITE);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBorder(new EmptyBorder(24, 36, 24, 36));

        form.add(makeLabel("Exam ID  (students use this to join)"));
        form.add(Box.createVerticalStrut(5));
        examIdField = makeTextField("e.g. MATH101");
        form.add(examIdField);
        form.add(Box.createVerticalStrut(14));

        form.add(makeLabel("Quiz Title"));
        form.add(Box.createVerticalStrut(5));
        titleField = makeTextField("e.g. Mathematics Mid-term");
        form.add(titleField);
        form.add(Box.createVerticalStrut(14));

        // 2-column grid for settings
        JPanel grid = new JPanel(new GridLayout(2, 2, 16, 12));
        grid.setOpaque(false);
        grid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));
        grid.setAlignmentX(Component.LEFT_ALIGNMENT);

        timeSpinner    = makeSpinner(30, 1, 300, 5);
        marksSpinner   = makeSpinner(5,  1, 100, 1);
        negMarksSpinner= new JSpinner(new SpinnerNumberModel(0.0, 0.0, 50.0, 0.5));
        negMarksSpinner.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        attemptsSpinner= makeSpinner(1,  1,  10, 1);

        grid.add(labeledSpinner("Time Limit (min)", timeSpinner));
        grid.add(labeledSpinner("Marks / Question", marksSpinner));
        grid.add(labeledSpinner("Negative Marks",   negMarksSpinner));
        grid.add(labeledSpinner("Max Attempts",      attemptsSpinner));

        form.add(grid);
        form.add(Box.createVerticalStrut(12));

        randomizeCheck = new JCheckBox("Randomize question order for each student");
        randomizeCheck.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        randomizeCheck.setOpaque(false);
        randomizeCheck.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(randomizeCheck);
        form.add(Box.createVerticalStrut(16));

// ── Scheduling ────────────────────────────────────────────────────────────
JPanel schedBox = new JPanel();
schedBox.setBackground(new Color(248, 249, 252));
schedBox.setLayout(new BoxLayout(schedBox, BoxLayout.Y_AXIS));
schedBox.setBorder(BorderFactory.createCompoundBorder(
    BorderFactory.createLineBorder(new Color(210, 210, 225)),
    BorderFactory.createEmptyBorder(12, 14, 14, 14)));
schedBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));
schedBox.setAlignmentX(Component.LEFT_ALIGNMENT);

JLabel schedTitle = new JLabel("📅  Schedule  (optional — leave blank = always open)");
schedTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
schedTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

JPanel schedRow = new JPanel(new GridLayout(1, 2, 14, 0));
schedRow.setOpaque(false);
schedRow.setAlignmentX(Component.LEFT_ALIGNMENT);

// Start block
JPanel startBlock = new JPanel();
startBlock.setOpaque(false);
startBlock.setLayout(new BoxLayout(startBlock, BoxLayout.Y_AXIS));
JLabel startLbl = new JLabel("Start  (YYYY-MM-DDTHH:MM)");
startLbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
startLbl.setForeground(new Color(120, 120, 140));
startLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
startTimeField = new JTextField(16);
startTimeField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
startTimeField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
startTimeField.setAlignmentX(Component.LEFT_ALIGNMENT);
startTimeField.setBorder(BorderFactory.createCompoundBorder(
    BorderFactory.createLineBorder(new Color(200, 200, 215)),
    BorderFactory.createEmptyBorder(5, 8, 5, 8)));
startBlock.add(startLbl);
startBlock.add(Box.createVerticalStrut(4));
startBlock.add(startTimeField);

// End block
JPanel endBlock = new JPanel();
endBlock.setOpaque(false);
endBlock.setLayout(new BoxLayout(endBlock, BoxLayout.Y_AXIS));
JLabel endLbl = new JLabel("End  (YYYY-MM-DDTHH:MM)");
endLbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
endLbl.setForeground(new Color(120, 120, 140));
endLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
endTimeField = new JTextField(16);
endTimeField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
endTimeField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
endTimeField.setAlignmentX(Component.LEFT_ALIGNMENT);
endTimeField.setBorder(BorderFactory.createCompoundBorder(
    BorderFactory.createLineBorder(new Color(200, 200, 215)),
    BorderFactory.createEmptyBorder(5, 8, 5, 8)));
endBlock.add(endLbl);
endBlock.add(Box.createVerticalStrut(4));
endBlock.add(endTimeField);

schedRow.add(startBlock);
schedRow.add(endBlock);
schedBox.add(schedTitle);
schedBox.add(Box.createVerticalStrut(10));
schedBox.add(schedRow);
form.add(schedBox);
        form.add(Box.createVerticalStrut(20));

        JPanel info = new JPanel(new BorderLayout());
        info.setBackground(new Color(232, 241, 251));
        info.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 210, 240)),
            BorderFactory.createEmptyBorder(10, 14, 10, 14)));
        info.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));
        info.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel infoText = new JLabel("<html><b>Next:</b> After creating the quiz, add MCQ, True/False, or Short Answer questions.</html>");
        infoText.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        infoText.setForeground(new Color(24, 95, 165));
        info.add(infoText);
        form.add(info);
        form.add(Box.createVerticalStrut(20));

        JButton createBtn = makePrimaryButton("Create Quiz & Add Questions");
        JButton cancelBtn = makeSecondaryButton("Cancel");
        form.add(createBtn);
        form.add(Box.createVerticalStrut(10));
        form.add(cancelBtn);

        root.add(form, BorderLayout.CENTER);
        setContentPane(root); revalidate();

        createBtn.addActionListener(e -> handleCreateQuiz());
        cancelBtn.addActionListener(e -> dispose());
    }

    private void handleCreateQuiz() {
    String examId  = examIdField.getText().trim().toUpperCase();
    String title   = titleField.getText().trim();
    int time       = (int) timeSpinner.getValue();
    int marks      = (int) marksSpinner.getValue();
    double negMark = ((Number) negMarksSpinner.getValue()).doubleValue();
    int attempts   = (int) attemptsSpinner.getValue();
    boolean random = randomizeCheck.isSelected();

    String startT = startTimeField != null
        ? startTimeField.getText().trim() : null;
    String endT = endTimeField != null
        ? endTimeField.getText().trim() : null;
    if (startT != null && startT.isEmpty()) startT = null;
    if (endT   != null && endT.isEmpty())   endT   = null;

    if (examId.isEmpty() || title.isEmpty()) {
        showError("Please fill in Exam ID and Title."); return;
    }

    boolean ok = DatabaseManager.getInstance()
        .createQuiz(examId, title, teacher.getId(), time, marks,
                    negMark, attempts, random, startT, endT);
    if (!ok) { showError("Exam ID \"" + examId + "\" already exists."); return; }

    existingQuiz = DatabaseManager.getInstance().getQuizByExamId(examId);
    setTitle("Add Questions — " + title);
    showQuestionsPanel();
    dashboard.loadQuizzes();
}

    // ── Panel 2: Add Questions ────────────────────────────────────────────────

    private void showQuestionsPanel() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Color.WHITE);
        root.add(makeHeader("Add Questions",
            "ID: " + existingQuiz.getExamId() + "  ·  " +
            existingQuiz.getTimeLimitMinutes() + " min  ·  " +
            existingQuiz.getMarksPerQuestion() + " pts/Q  ·  " +
            (existingQuiz.getNegativeMarks() > 0 ? "-" + existingQuiz.getNegativeMarks() + " neg" : "no neg marks")),
            BorderLayout.NORTH);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        split.setDividerLocation(380);
        split.setDividerSize(1);
        split.setBorder(null);

        // ── Left: Question form ───────────────────────────────────────────────
        JPanel qForm = new JPanel();
        qForm.setBackground(Color.WHITE);
        qForm.setLayout(new BoxLayout(qForm, BoxLayout.Y_AXIS));
        qForm.setBorder(new EmptyBorder(16, 20, 16, 12));

        JLabel qFormTitle = new JLabel("Add Question");
        qFormTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        qFormTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        qForm.add(qFormTitle);
        qForm.add(Box.createVerticalStrut(12));

        // Question type selector
        qForm.add(makeLabel("Question Type"));
        qForm.add(Box.createVerticalStrut(4));
        typeCombo = new JComboBox<>(new String[]{
            "MCQ (4 options)", "True / False", "Short Answer"});
        typeCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        typeCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        typeCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
        qForm.add(typeCombo);
        qForm.add(Box.createVerticalStrut(12));

        qForm.add(makeLabel("Question Text"));
        qForm.add(Box.createVerticalStrut(4));
        questionField = makeTextField("Enter the question...");
        qForm.add(questionField);
        qForm.add(Box.createVerticalStrut(12));

        // Dynamic options area
        optionsPanel = new JPanel();
        optionsPanel.setOpaque(false);
        optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));
        optionsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        qForm.add(optionsPanel);

        buildMcqOptions(); // default

        typeCombo.addActionListener(e -> {
            int idx = typeCombo.getSelectedIndex();
            optionsPanel.removeAll();
            if (idx == 0) buildMcqOptions();
            else if (idx == 1) buildTrueFalseOptions();
            else buildShortAnswerOptions();
            optionsPanel.revalidate();
            optionsPanel.repaint();
        });

        qForm.add(Box.createVerticalStrut(16));
        // ── Image picker ──────────────────────────────────────────────────────────
qForm.add(Box.createVerticalStrut(10));
qForm.add(makeLabel("Question Image  (optional)"));
qForm.add(Box.createVerticalStrut(4));

JPanel imgRow = new JPanel(new BorderLayout(6, 0));
imgRow.setOpaque(false);
imgRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
imgRow.setAlignmentX(Component.LEFT_ALIGNMENT);

imagePathField = new JTextField();
imagePathField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
imagePathField.setBorder(BorderFactory.createCompoundBorder(
    BorderFactory.createLineBorder(new Color(200, 200, 200)),
    BorderFactory.createEmptyBorder(6, 8, 6, 8)));

JButton browseBtn = new JButton("Browse");
browseBtn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
browseBtn.setBackground(new Color(83, 74, 183));
browseBtn.setForeground(Color.WHITE);
browseBtn.setFocusPainted(false);
browseBtn.setBorderPainted(false);
browseBtn.setOpaque(true);
browseBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
browseBtn.setPreferredSize(new Dimension(72, 36));
browseBtn.addActionListener(e -> {
    JFileChooser fc = new JFileChooser();
    fc.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
        "Images", "jpg", "jpeg", "png", "gif", "bmp"));
    if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
        imagePathField.setText(fc.getSelectedFile().getAbsolutePath());
});

imgRow.add(imagePathField, BorderLayout.CENTER);
imgRow.add(browseBtn,      BorderLayout.EAST);
qForm.add(imgRow);
qForm.add(Box.createVerticalStrut(16));
        JButton addQBtn = makePrimaryButton("+ Add Question");
        qForm.add(addQBtn);

        // ── Right: Question list ──────────────────────────────────────────────
        JPanel listPanel = new JPanel(new BorderLayout());
        listPanel.setBackground(new Color(248, 249, 252));
        listPanel.setBorder(new EmptyBorder(16, 10, 16, 16));

        JLabel listTitle = new JLabel("Questions Added");
        listTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        listTitle.setBorder(new EmptyBorder(0, 0, 8, 0));

        questionListModel = new DefaultListModel<>();
        JList<String> questionList = new JList<>(questionListModel);
        questionList.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        questionList.setBackground(new Color(248, 249, 252));
        questionList.setCellRenderer(new EditQuizFrame.QuestionListRenderer());
        refreshQuestionList();

        JScrollPane listScroll = new JScrollPane(questionList);
        listScroll.setBorder(null);

        JButton doneBtn = makePrimaryButton("Done ✓");
        doneBtn.setBackground(new Color(59, 109, 17));

        listPanel.add(listTitle,  BorderLayout.NORTH);
        listPanel.add(listScroll, BorderLayout.CENTER);
        listPanel.add(doneBtn,    BorderLayout.SOUTH);

        split.setLeftComponent(new JScrollPane(qForm) {{ setBorder(null); }});
        split.setRightComponent(listPanel);

        root.add(split, BorderLayout.CENTER);
        setContentPane(root); revalidate();

        addQBtn.addActionListener(e -> handleAddQuestion());
        doneBtn.addActionListener(e -> { dashboard.loadQuizzes(); dispose(); });
    }

    private void buildMcqOptions() {
        optAField = addOptionField("Option A");
        optBField = addOptionField("Option B");
        optCField = addOptionField("Option C");
        optDField = addOptionField("Option D");
        optionsPanel.add(makeLabel("Correct Answer"));
        optionsPanel.add(Box.createVerticalStrut(4));
        correctCombo = new JComboBox<>(new String[]{"A", "B", "C", "D"});
        correctCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        correctCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        correctCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
        optionsPanel.add(correctCombo);
    }

    private void buildTrueFalseOptions() {
        JLabel info = new JLabel("Options: True / False (auto-set)");
        info.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        info.setForeground(new Color(100, 100, 120));
        info.setAlignmentX(Component.LEFT_ALIGNMENT);
        optionsPanel.add(info);
        optionsPanel.add(Box.createVerticalStrut(10));
        optionsPanel.add(makeLabel("Correct Answer"));
        optionsPanel.add(Box.createVerticalStrut(4));
        correctCombo = new JComboBox<>(new String[]{"A (True)", "B (False)"});
        correctCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        correctCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        correctCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
        optionsPanel.add(correctCombo);
        optAField = new JTextField("True");
        optBField = new JTextField("False");
        optCField = new JTextField("");
        optDField = new JTextField("");
    }

    private void buildShortAnswerOptions() {
        JPanel infoBox = new JPanel(new BorderLayout());
        infoBox.setBackground(new Color(255, 243, 205));
        infoBox.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(240, 200, 100)),
            BorderFactory.createEmptyBorder(10, 12, 10, 12)));
        infoBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        infoBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel infoText = new JLabel("<html>Students type a text answer.<br>You will grade these manually in the results view.</html>");
        infoText.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        infoText.setForeground(new Color(120, 80, 0));
        infoBox.add(infoText);
        optionsPanel.add(infoBox);
        optAField = optBField = optCField = optDField = new JTextField();
        correctCombo = new JComboBox<>(new String[]{"N/A"});
    }

    private JTextField addOptionField(String label) {
        optionsPanel.add(makeLabel(label));
        optionsPanel.add(Box.createVerticalStrut(4));
        JTextField f = makeTextField(label);
        optionsPanel.add(f);
        optionsPanel.add(Box.createVerticalStrut(10));
        return f;
    }

    private void handleAddQuestion() {
    String qText = questionField.getText().trim();
    if (qText.isEmpty()) { showError("Enter the question text."); return; }

    int typeIdx = typeCombo.getSelectedIndex();
    String type, optA, optB, optC = "", optD = "", correct = null;
    String imgPath = (imagePathField != null
        && !imagePathField.getText().isBlank())
        ? imagePathField.getText().trim() : null;

    if (typeIdx == 0) {
        type  = "mcq";
        optA  = optAField.getText().trim();
        optB  = optBField.getText().trim();
        optC  = optCField.getText().trim();
        optD  = optDField.getText().trim();
        if (optA.isEmpty() || optB.isEmpty() || optC.isEmpty() || optD.isEmpty()) {
            showError("Fill in all four options."); return;
        }
        correct = (String) correctCombo.getSelectedItem();
    } else if (typeIdx == 1) {
        type = "truefalse"; optA = "True"; optB = "False";
        correct = correctCombo.getSelectedIndex() == 0 ? "A" : "B";
    } else {
        type = "short"; optA = optB = "";
    }

    boolean ok = DatabaseManager.getInstance().addQuestion(
        existingQuiz.getId(), type, qText,
        optA, optB, optC, optD, correct, imgPath);

    if (ok) {
        refreshQuestionList();
        questionField.setText("");
        if (typeIdx == 0) {
            optAField.setText(""); optBField.setText("");
            optCField.setText(""); optDField.setText("");
            correctCombo.setSelectedIndex(0);
        }
        if (imagePathField != null) imagePathField.setText("");
        questionField.requestFocus();
    } else {
        showError("Failed to save question.");
    }
}

    private void refreshQuestionList() {
        if (existingQuiz == null || questionListModel == null) return;
        questionListModel.clear();
        List<Question> qs = DatabaseManager.getInstance()
            .getQuestionsByQuizId(existingQuiz.getId());
        for (int i = 0; i < qs.size(); i++) {
            Question q = qs.get(i);
            String tag = switch (q.getQuestionType()) {
                case "truefalse" -> "[T/F] ";
                case "short"     -> "[Short] ";
                default          -> "[MCQ] ";
            };
            questionListModel.addElement((i + 1) + ". " + tag + q.getQuestionText());
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private JPanel makeHeader(String title, String sub) {
        JPanel h = new JPanel(new GridBagLayout());
        h.setBackground(new Color(24, 95, 165));
        h.setPreferredSize(new Dimension(660, 72));
        JPanel inner = new JPanel();
        inner.setOpaque(false);
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));
        JLabel t = new JLabel(title);
        t.setFont(new Font("Segoe UI", Font.BOLD, 17));
        t.setForeground(Color.WHITE);
        JLabel s = new JLabel(sub);
        s.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        s.setForeground(new Color(180, 210, 240));
        inner.add(t); inner.add(Box.createVerticalStrut(2)); inner.add(s);
        h.add(inner);
        return h;
    }

    private JLabel makeLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        l.setForeground(new Color(60, 60, 60));
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private JTextField makeTextField(String hint) {
        JTextField f = new JTextField();
        f.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        f.setAlignmentX(Component.LEFT_ALIGNMENT);
        return f;
    }

    private JSpinner makeSpinner(int val, int min, int max, int step) {
        JSpinner sp = new JSpinner(new SpinnerNumberModel(val, min, max, step));
        sp.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        return sp;
    }

    private JPanel labeledSpinner(String label, JSpinner sp) {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        JLabel l = new JLabel(label);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        l.setForeground(new Color(60, 60, 60));
        p.add(l);
        p.add(Box.createVerticalStrut(4));
        p.add(sp);
        return p;
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

    private JButton makeSecondaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setBackground(new Color(245, 247, 250));
        btn.setForeground(new Color(24, 95, 165));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        return btn;
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
}