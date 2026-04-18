package com.examsystem.ui;

import com.examsystem.db.DatabaseManager;
import com.examsystem.model.Quiz;
import com.examsystem.model.User;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class TeacherDashboard extends JFrame {

    private final User teacher;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private List<Quiz> currentQuizzes;

    public TeacherDashboard(User teacher) {
        this.teacher = teacher;
        setTitle("Teacher Dashboard — " + teacher.getUsername());
        setSize(860, 580);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        buildUI();
        loadQuizzes();
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Color.WHITE);

        // ── Navbar ────────────────────────────────────────────────────────────
        JPanel navbar = new JPanel(new BorderLayout());
        navbar.setBackground(new Color(24, 95, 165));
        navbar.setPreferredSize(new Dimension(860, 64));
        navbar.setBorder(new EmptyBorder(0, 24, 0, 24));

        JPanel navLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        navLeft.setOpaque(false);
        JLabel appName = new JLabel("📋 Exam System");
        appName.setFont(new Font("Segoe UI", Font.BOLD, 16));
        appName.setForeground(Color.WHITE);
        JLabel sep = new JLabel("  |  ");
        sep.setForeground(new Color(150, 190, 230));
        JLabel roleL = new JLabel("Teacher Panel");
        roleL.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        roleL.setForeground(new Color(180, 210, 240));
        navLeft.add(appName); navLeft.add(sep); navLeft.add(roleL);

        JPanel navRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        navRight.setOpaque(false);
        JLabel userLabel = new JLabel("👤 " + teacher.getUsername());
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        userLabel.setForeground(new Color(200, 220, 240));

        JButton profileBtn = makeNavButton("Profile");
        JButton logoutBtn  = makeNavButton("Logout");

        profileBtn.addActionListener(e -> new ProfileFrame(teacher).setVisible(true));
        logoutBtn.addActionListener(e -> { new LoginFrame().setVisible(true); dispose(); });

        navRight.add(userLabel);
        navRight.add(profileBtn);
        navRight.add(logoutBtn);
        navbar.add(navLeft, BorderLayout.WEST);
        navbar.add(navRight, BorderLayout.EAST);

        // ── Content ───────────────────────────────────────────────────────────
        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(Color.WHITE);
        content.setBorder(new EmptyBorder(20, 24, 20, 24));

        // ── Top toolbar ───────────────────────────────────────────────────────
        JPanel toolbar = new JPanel(new BorderLayout(12, 0));
        toolbar.setOpaque(false);
        toolbar.setBorder(new EmptyBorder(0, 0, 16, 0));

        JPanel titleBlock = new JPanel();
        titleBlock.setOpaque(false);
        titleBlock.setLayout(new BoxLayout(titleBlock, BoxLayout.Y_AXIS));
        JLabel pageTitle = new JLabel("My Quizzes");
        pageTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        JLabel pageSub   = new JLabel("Manage your exams and view student results");
        pageSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        pageSub.setForeground(new Color(120, 120, 120));
        titleBlock.add(pageTitle);
        titleBlock.add(Box.createVerticalStrut(2));
        titleBlock.add(pageSub);

        // Search bar
        searchField = new JTextField();
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        searchField.setPreferredSize(new Dimension(220, 36));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        searchField.setToolTipText("Search by title or exam ID");

        JButton searchBtn = makeSmallButton("🔍 Search", new Color(83, 74, 183));
        JButton clearBtn  = makeSmallButton("✕ Clear",   new Color(150, 150, 160));

        JPanel searchRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        searchRow.setOpaque(false);
        searchRow.add(searchField);
        searchRow.add(searchBtn);
        searchRow.add(clearBtn);

        JButton createBtn = makePrimaryButton("+ Create New Quiz");
        createBtn.addActionListener(e -> openCreateQuiz());

        JPanel rightTools = new JPanel(new BorderLayout(8, 0));
        rightTools.setOpaque(false);
        rightTools.add(searchRow, BorderLayout.CENTER);
        rightTools.add(createBtn, BorderLayout.EAST);

        toolbar.add(titleBlock,  BorderLayout.WEST);
        toolbar.add(rightTools,  BorderLayout.EAST);

        // ── Table ─────────────────────────────────────────────────────────────
        String[] cols = {"Exam ID", "Title", "Time", "Marks/Q", "Neg", "Attempts", "Qs", "Actions"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return c == 7; }
        };

        JTable table = new JTable(tableModel);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(46);
        table.setShowVerticalLines(false);
        table.setGridColor(new Color(240, 240, 240));
        table.setSelectionBackground(new Color(232, 241, 251));
        table.setFocusable(false);

        JTableHeader hdr = table.getTableHeader();
        hdr.setFont(new Font("Segoe UI", Font.BOLD, 12));
        hdr.setBackground(new Color(248, 249, 252));
        hdr.setForeground(new Color(80, 80, 80));
        hdr.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)));
        hdr.setReorderingAllowed(false);

        table.getColumnModel().getColumn(0).setPreferredWidth(80);
        table.getColumnModel().getColumn(1).setPreferredWidth(180);
        table.getColumnModel().getColumn(2).setPreferredWidth(60);
        table.getColumnModel().getColumn(3).setPreferredWidth(65);
        table.getColumnModel().getColumn(4).setPreferredWidth(45);
        table.getColumnModel().getColumn(5).setPreferredWidth(65);
        table.getColumnModel().getColumn(6).setPreferredWidth(40);
        table.getColumnModel().getColumn(7).setPreferredWidth(200);

        table.getColumnModel().getColumn(7).setCellRenderer(new ActionRenderer());
        table.getColumnModel().getColumn(7).setCellEditor(new ActionEditor(table));

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        scroll.getViewport().setBackground(Color.WHITE);

        content.add(toolbar, BorderLayout.NORTH);
        content.add(scroll,  BorderLayout.CENTER);

        root.add(navbar,  BorderLayout.NORTH);
        root.add(content, BorderLayout.CENTER);
        setContentPane(root);

        // ── Search actions ────────────────────────────────────────────────────
        searchBtn.addActionListener(e -> {
            String kw = searchField.getText().trim();
            if (!kw.isEmpty()) loadQuizzes(kw);
        });
        clearBtn.addActionListener(e -> { searchField.setText(""); loadQuizzes(); });
        searchField.addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    String kw = searchField.getText().trim();
                    if (kw.isEmpty()) loadQuizzes(); else loadQuizzes(kw);
                }
            }
        });
    }

    public void loadQuizzes() { loadQuizzes(null); }

    public void loadQuizzes(String keyword) {
        tableModel.setRowCount(0);
        currentQuizzes = keyword == null
            ? DatabaseManager.getInstance().getQuizzesByTeacher(teacher.getId())
            : DatabaseManager.getInstance().searchQuizzesByTeacher(teacher.getId(), keyword);

        for (Quiz q : currentQuizzes) {
            int qCount = DatabaseManager.getInstance().getQuestionsByQuizId(q.getId()).size();
            tableModel.addRow(new Object[]{
                q.getExamId(), q.getTitle(),
                q.getTimeLimitMinutes() + "m",
                q.getMarksPerQuestion() + "pt",
                q.getNegativeMarks() > 0 ? "-" + q.getNegativeMarks() : "—",
                q.getMaxAttempts() + "x",
                qCount, "actions"
            });
        }
    }

    private void openCreateQuiz() {
        new CreateQuizFrame(teacher, this).setVisible(true);
    }

    private void viewResults(int row) {
        if (row < currentQuizzes.size())
            new StudentRecordsFrame(currentQuizzes.get(row), teacher).setVisible(true);
    }

    private void addEditQuestions(int row) {
        if (row < currentQuizzes.size())
            new CreateQuizFrame(teacher, this, currentQuizzes.get(row)).setVisible(true);
    }

    private void editQuiz(int row) {
        if (row < currentQuizzes.size())
            new EditQuizFrame(currentQuizzes.get(row), this).setVisible(true);
    }

    private void previewQuiz(int row) {
        if (row < currentQuizzes.size())
            new QuizPreviewFrame(currentQuizzes.get(row)).setVisible(true);
    }

    private void deleteQuiz(int row) {
        if (row >= currentQuizzes.size()) return;
        Quiz q = currentQuizzes.get(row);
        int choice = JOptionPane.showConfirmDialog(this,
            "Delete quiz \"" + q.getTitle() + "\"?\nThis will also delete all questions and results.",
            "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (choice == JOptionPane.YES_OPTION) {
            DatabaseManager.getInstance().deleteQuiz(q.getId());
            loadQuizzes();
        }
    }

    // ── Button helpers ────────────────────────────────────────────────────────

    private JButton makePrimaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(new Color(24, 95, 165));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(10, 16, 10, 16));
        return btn;
    }

    private JButton makeNavButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255, 80)));
        btn.setOpaque(false);
        btn.setBackground(new Color(0, 0, 0, 0));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JButton makeSmallButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(6, 12, 6, 12));
        return btn;
    }

    // ── Action column ─────────────────────────────────────────────────────────

    private JPanel makeActionPanel() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 6));
        p.setOpaque(true);
        p.add(makeTinyBtn("✏ Edit",     new Color(24, 95, 165)));
        p.add(makeTinyBtn("+ Qs",       new Color(83, 74, 183)));
        p.add(makeTinyBtn("👁 Preview", new Color(100, 100, 120)));
        p.add(makeTinyBtn("📊 Results", new Color(59, 109, 17)));
        p.add(makeTinyBtn("🗑 Delete",  new Color(180, 40, 40)));
        return p;
    }

    private JButton makeTinyBtn(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(3, 7, 3, 7));
        return btn;
    }

    class ActionRenderer implements TableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable t, Object v,
                boolean sel, boolean foc, int row, int col) {
            JPanel p = makeActionPanel();
            p.setBackground(sel ? new Color(232, 241, 251) : Color.WHITE);
            return p;
        }
    }

    class ActionEditor extends AbstractCellEditor implements TableCellEditor {
        private final JPanel panel;
        private int currentRow;

        ActionEditor(JTable table) {
            panel = makeActionPanel();
            panel.setBackground(Color.WHITE);
            JButton[] btns = getButtons(panel);
            btns[0].addActionListener(e -> { fireEditingStopped(); editQuiz(currentRow); });
            btns[1].addActionListener(e -> { fireEditingStopped(); addEditQuestions(currentRow); });
            btns[2].addActionListener(e -> { fireEditingStopped(); previewQuiz(currentRow); });
            btns[3].addActionListener(e -> { fireEditingStopped(); viewResults(currentRow); });
            btns[4].addActionListener(e -> { fireEditingStopped(); deleteQuiz(currentRow); });
        }

        private JButton[] getButtons(JPanel p) {
            JButton[] arr = new JButton[5];
            int i = 0;
            for (Component c : p.getComponents()) {
                if (c instanceof JButton) arr[i++] = (JButton) c;
            }
            return arr;
        }

        @Override
        public Component getTableCellEditorComponent(JTable t, Object v,
                boolean sel, int row, int col) {
            currentRow = row;
            return panel;
        }

        @Override public Object getCellEditorValue() { return "actions"; }
    }
}