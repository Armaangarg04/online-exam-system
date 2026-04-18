package com.examsystem.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.List;

import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import com.examsystem.db.DatabaseManager;
import com.examsystem.model.Quiz;
import com.examsystem.model.Result;
import com.examsystem.model.User;

public class AdminPanel extends JFrame {

    private final User admin;
    private JTabbedPane tabs;
    private DefaultTableModel userModel, quizModel, resultModel;

    public AdminPanel(User admin) {
        this.admin = admin;
        setTitle("Admin Panel — " + admin.getUsername());
        setSize(900, 620);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        buildUI();
        loadAll();
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Color.WHITE);

        // ── Navbar ────────────────────────────────────────────────────────────
        JPanel navbar = new JPanel(new BorderLayout());
        navbar.setBackground(new Color(140, 30, 30));
        navbar.setPreferredSize(new Dimension(900, 64));
        navbar.setBorder(new EmptyBorder(0, 24, 0, 24));

        JPanel navLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        navLeft.setOpaque(false);
        JLabel appName = new JLabel("📋 Exam System");
        appName.setFont(new Font("Segoe UI", Font.BOLD, 16));
        appName.setForeground(Color.WHITE);
        JLabel sep = new JLabel("  |  ");
        sep.setForeground(new Color(220, 160, 160));
        JLabel roleL = new JLabel("Admin Super Panel");
        roleL.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        roleL.setForeground(new Color(240, 200, 200));
        navLeft.add(appName); navLeft.add(sep); navLeft.add(roleL);

        JPanel navRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        navRight.setOpaque(false);
        JLabel userLabel = new JLabel("🔑 " + admin.getUsername());
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        userLabel.setForeground(new Color(240, 200, 200));

        JButton profileBtn = makeNavBtn("Profile");
        JButton logoutBtn  = makeNavBtn("Logout");
        profileBtn.addActionListener(e -> new ProfileFrame(admin).setVisible(true));
        logoutBtn.addActionListener(e -> { new LoginFrame().setVisible(true); dispose(); });

        navRight.add(userLabel); navRight.add(profileBtn); navRight.add(logoutBtn);
        navbar.add(navLeft, BorderLayout.WEST);
        navbar.add(navRight, BorderLayout.EAST);

        // ── Tabs ──────────────────────────────────────────────────────────────
        tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabs.setBackground(Color.WHITE);

        tabs.addTab("👥  Users",   buildUsersTab());
        tabs.addTab("📝  Quizzes", buildQuizzesTab());
        tabs.addTab("📊  Results", buildResultsTab());

        root.add(navbar, BorderLayout.NORTH);
        root.add(tabs,   BorderLayout.CENTER);
        setContentPane(root);
    }

    // ── Users Tab ─────────────────────────────────────────────────────────────

    private JPanel buildUsersTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(16, 20, 16, 20));

        String[] cols = {"ID", "Username", "Role", "Actions"};
        userModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return c == 3; }
        };

        JTable table = makeTable(userModel);
        table.getColumnModel().getColumn(0).setPreferredWidth(40);
        table.getColumnModel().getColumn(1).setPreferredWidth(200);
        table.getColumnModel().getColumn(2).setPreferredWidth(80);
        table.getColumnModel().getColumn(3).setPreferredWidth(200);

        table.getColumnModel().getColumn(2).setCellRenderer(new RoleBadgeRenderer());
        table.getColumnModel().getColumn(3).setCellRenderer(new UserActionRenderer());
        table.getColumnModel().getColumn(3).setCellEditor(new UserActionEditor(table));

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));

        JPanel toolbar = new JPanel(new BorderLayout());
        toolbar.setOpaque(false);
        JLabel title = new JLabel("All Users");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        JButton refreshBtn = makeSmallBtn("↻ Refresh", new Color(80, 80, 100));
        refreshBtn.addActionListener(e -> loadUsers());
        toolbar.add(title,      BorderLayout.WEST);
        toolbar.add(refreshBtn, BorderLayout.EAST);

        panel.add(toolbar, BorderLayout.NORTH);
        panel.add(scroll,  BorderLayout.CENTER);
        return panel;
    }

    // ── Quizzes Tab ───────────────────────────────────────────────────────────

    private JPanel buildQuizzesTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(16, 20, 16, 20));

        String[] cols = {"Exam ID", "Title", "Created By", "Time", "Marks/Q", "Qs", "Actions"};
        quizModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return c == 6; }
        };

        JTable table = makeTable(quizModel);
        table.getColumnModel().getColumn(0).setPreferredWidth(80);
        table.getColumnModel().getColumn(1).setPreferredWidth(200);
        table.getColumnModel().getColumn(2).setPreferredWidth(100);
        table.getColumnModel().getColumn(3).setPreferredWidth(60);
        table.getColumnModel().getColumn(4).setPreferredWidth(60);
        table.getColumnModel().getColumn(5).setPreferredWidth(40);
        table.getColumnModel().getColumn(6).setPreferredWidth(100);

        table.getColumnModel().getColumn(6).setCellRenderer(new QuizActionRenderer());
        table.getColumnModel().getColumn(6).setCellEditor(new QuizActionEditor(table));

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));

        JPanel toolbar = new JPanel(new BorderLayout());
        toolbar.setOpaque(false);
        JLabel title = new JLabel("All Quizzes");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        JButton refreshBtn = makeSmallBtn("↻ Refresh", new Color(80, 80, 100));
        refreshBtn.addActionListener(e -> loadQuizzes());
        toolbar.add(title,      BorderLayout.WEST);
        toolbar.add(refreshBtn, BorderLayout.EAST);

        panel.add(toolbar, BorderLayout.NORTH);
        panel.add(scroll,  BorderLayout.CENTER);
        return panel;
    }

    // ── Results Tab ───────────────────────────────────────────────────────────

    private JPanel buildResultsTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(16, 20, 16, 20));

        String[] cols = {"Student", "Quiz", "Score", "Total", "%", "Date"};
        resultModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable table = makeTable(resultModel);
        table.getColumnModel().getColumn(0).setPreferredWidth(140);
        table.getColumnModel().getColumn(1).setPreferredWidth(200);
        table.getColumnModel().getColumn(2).setPreferredWidth(60);
        table.getColumnModel().getColumn(3).setPreferredWidth(60);
        table.getColumnModel().getColumn(4).setPreferredWidth(60);
        table.getColumnModel().getColumn(5).setPreferredWidth(160);

        table.getColumnModel().getColumn(4).setCellRenderer(new StudentRecordsFrame.PercentRenderer());

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));

        JPanel toolbar = new JPanel(new BorderLayout());
        toolbar.setOpaque(false);
        JLabel title = new JLabel("All Results");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        JButton refreshBtn = makeSmallBtn("↻ Refresh", new Color(80, 80, 100));
        refreshBtn.addActionListener(e -> loadResults());
        toolbar.add(title,      BorderLayout.WEST);
        toolbar.add(refreshBtn, BorderLayout.EAST);

        panel.add(toolbar, BorderLayout.NORTH);
        panel.add(scroll,  BorderLayout.CENTER);
        return panel;
    }

    // ── Load data ─────────────────────────────────────────────────────────────

    private void loadAll() { loadUsers(); loadQuizzes(); loadResults(); }

    private void loadUsers() {
        userModel.setRowCount(0);
        for (User u : DatabaseManager.getInstance().getAllUsers()) {
            userModel.addRow(new Object[]{u.getId(), u.getUsername(), u.getRole(), "actions"});
        }
    }

    private void loadQuizzes() {
        quizModel.setRowCount(0);
        List<Quiz> quizzes = DatabaseManager.getInstance().getAllQuizzes();
        List<User> users   = DatabaseManager.getInstance().getAllUsers();
        for (Quiz q : quizzes) {
            String creatorName = users.stream()
                .filter(u -> u.getId() == q.getCreatedBy())
                .map(User::getUsername).findFirst().orElse("Unknown");
            int qCount = DatabaseManager.getInstance().getQuestionsByQuizId(q.getId()).size();
            quizModel.addRow(new Object[]{
                q.getExamId(), q.getTitle(), creatorName,
                q.getTimeLimitMinutes() + "m", q.getMarksPerQuestion() + "pt",
                qCount, "actions"
            });
        }
    }

    private void loadResults() {
        resultModel.setRowCount(0);
        for (Result r : DatabaseManager.getInstance().getAllResults()) {
            int pct = (int) Math.round(r.getScore() * 100.0 / r.getTotalMarks());
            resultModel.addRow(new Object[]{
                r.getStudentName(), r.getQuizTitle(),
                r.getScore(), r.getTotalMarks(), pct + "%", r.getAttemptedAt()
            });
        }
    }

    // ── User actions ──────────────────────────────────────────────────────────

    private void deleteUser(int row) {
        List<User> users = DatabaseManager.getInstance().getAllUsers();
        if (row >= users.size()) return;
        User u = users.get(row);
        if (u.getId() == admin.getId()) {
            JOptionPane.showMessageDialog(this, "Cannot delete your own account.",
                "Error", JOptionPane.ERROR_MESSAGE); return;
        }
        int choice = JOptionPane.showConfirmDialog(this,
            "Delete user \"" + u.getUsername() + "\"?",
            "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (choice == JOptionPane.YES_OPTION) {
            DatabaseManager.getInstance().deleteUser(u.getId());
            loadUsers();
        }
    }

    private void resetPassword(int row) {
        List<User> users = DatabaseManager.getInstance().getAllUsers();
        if (row >= users.size()) return;
        User u = users.get(row);
        String newPass = JOptionPane.showInputDialog(this,
            "Enter new password for \"" + u.getUsername() + "\":",
            "Reset Password", JOptionPane.QUESTION_MESSAGE);
        if (newPass == null || newPass.trim().isEmpty()) return;
        if (newPass.length() < 6) {
            JOptionPane.showMessageDialog(this, "Password must be at least 6 characters.",
                "Error", JOptionPane.ERROR_MESSAGE); return;
        }
        DatabaseManager.getInstance().resetPassword(u.getId(), newPass);
        JOptionPane.showMessageDialog(this, "Password reset successfully!",
            "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void deleteQuiz(int row) {
        List<Quiz> quizzes = DatabaseManager.getInstance().getAllQuizzes();
        if (row >= quizzes.size()) return;
        Quiz q = quizzes.get(row);
        int choice = JOptionPane.showConfirmDialog(this,
            "Delete quiz \"" + q.getTitle() + "\" and all its data?",
            "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (choice == JOptionPane.YES_OPTION) {
            DatabaseManager.getInstance().deleteQuiz(q.getId());
            loadQuizzes();
        }
    }

    // ── Table factory ─────────────────────────────────────────────────────────

    private JTable makeTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(44);
        table.setShowVerticalLines(false);
        table.setGridColor(new Color(240, 240, 245));
        table.setSelectionBackground(new Color(252, 235, 235));
        table.setFocusable(false);
        JTableHeader hdr = table.getTableHeader();
        hdr.setFont(new Font("Segoe UI", Font.BOLD, 12));
        hdr.setBackground(new Color(248, 248, 252));
        hdr.setForeground(new Color(80, 80, 80));
        hdr.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)));
        hdr.setReorderingAllowed(false);
        return table;
    }

    // ── Renderers & Editors ───────────────────────────────────────────────────

    class RoleBadgeRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable t, Object v,
                boolean sel, boolean foc, int row, int col) {
            JLabel l = new JLabel(v != null ? v.toString().toUpperCase() : "");
            l.setFont(new Font("Segoe UI", Font.BOLD, 11));
            l.setOpaque(true);
            l.setHorizontalAlignment(SwingConstants.CENTER);
            l.setBorder(new EmptyBorder(4, 0, 4, 0));
            String role = v != null ? v.toString() : "";
            switch (role) {
                case "admin"   -> { l.setBackground(new Color(252,235,235)); l.setForeground(new Color(160,40,40)); }
                case "teacher" -> { l.setBackground(new Color(234,243,222)); l.setForeground(new Color(59,109,17)); }
                default        -> { l.setBackground(new Color(238,237,254)); l.setForeground(new Color(83,74,183)); }
            }
            return l;
        }
    }

    // User action renderer
    class UserActionRenderer implements TableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable t, Object v,
                boolean sel, boolean foc, int row, int col) {
            JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 8));
            p.setBackground(sel ? new Color(252, 235, 235) : Color.WHITE);
            p.add(makeTinyBtn("🔑 Reset Password", new Color(83, 74, 183)));
            p.add(makeTinyBtn("🗑 Delete",          new Color(180, 40, 40)));
            return p;
        }
    }

    class UserActionEditor extends AbstractCellEditor implements TableCellEditor {
        private final JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 8));
        private int currentRow;

        UserActionEditor(JTable table) {
            panel.setBackground(Color.WHITE);
            JButton resetBtn  = makeTinyBtn("🔑 Reset Password", new Color(83, 74, 183));
            JButton deleteBtn = makeTinyBtn("🗑 Delete",          new Color(180, 40, 40));
            panel.add(resetBtn); panel.add(deleteBtn);
            resetBtn.addActionListener(e  -> { fireEditingStopped(); resetPassword(currentRow); });
            deleteBtn.addActionListener(e -> { fireEditingStopped(); deleteUser(currentRow); });
        }

        @Override
        public Component getTableCellEditorComponent(JTable t, Object v,
                boolean sel, int row, int col) {
            currentRow = row; return panel;
        }

        @Override public Object getCellEditorValue() { return "actions"; }
    }

    // Quiz action renderer/editor
    class QuizActionRenderer implements TableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable t, Object v,
                boolean sel, boolean foc, int row, int col) {
            JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 8));
            p.setBackground(sel ? new Color(252, 235, 235) : Color.WHITE);
            p.add(makeTinyBtn("🗑 Delete", new Color(180, 40, 40)));
            return p;
        }
    }

    class QuizActionEditor extends AbstractCellEditor implements TableCellEditor {
        private final JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 8));
        private int currentRow;

        QuizActionEditor(JTable table) {
            panel.setBackground(Color.WHITE);
            JButton deleteBtn = makeTinyBtn("🗑 Delete", new Color(180, 40, 40));
            panel.add(deleteBtn);
            deleteBtn.addActionListener(e -> { fireEditingStopped(); deleteQuiz(currentRow); });
        }

        @Override
        public Component getTableCellEditorComponent(JTable t, Object v,
                boolean sel, int row, int col) {
            currentRow = row; return panel;
        }

        @Override public Object getCellEditorValue() { return "actions"; }
    }

    // ── Button helpers ────────────────────────────────────────────────────────

    private JButton makeNavBtn(String text) {
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

    private JButton makeSmallBtn(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(6, 14, 6, 14));
        return btn;
    }

    private JButton makeTinyBtn(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(4, 10, 4, 10));
        return btn;
    }
}