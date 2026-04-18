package com.examsystem.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.examsystem.model.Question;
import com.examsystem.model.Quiz;
import com.examsystem.model.Result;
import com.examsystem.model.User;
import com.examsystem.util.PasswordUtil;

public class DatabaseManager {

    private static final String DB_URL = "jdbc:sqlite:examsystem.db";
    private static DatabaseManager instance;
    private Connection connection;

    // ── Singleton ─────────────────────────────────────────────────────────────
    private DatabaseManager() {
        try {
            connection = DriverManager.getConnection(DB_URL);
            connection.createStatement().execute("PRAGMA foreign_keys = ON");
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public static DatabaseManager getInstance() {
        if (instance == null) instance = new DatabaseManager();
        return instance;
    }

    public Connection getConnection() { return connection; }

    // ── Schema ────────────────────────────────────────────────────────────────
    public void initializeDatabase() {
        try (Statement stmt = connection.createStatement()) {

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS users (
                    id       INTEGER PRIMARY KEY AUTOINCREMENT,
                    username TEXT UNIQUE NOT NULL,
                    password TEXT NOT NULL,
                    role     TEXT NOT NULL CHECK(role IN ('student','teacher','admin'))
                )
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS quizzes (
                    id                  INTEGER PRIMARY KEY AUTOINCREMENT,
                    exam_id             TEXT UNIQUE NOT NULL,
                    title               TEXT NOT NULL,
                    created_by          INTEGER NOT NULL,
                    time_limit_minutes  INTEGER NOT NULL,
                    marks_per_question  INTEGER NOT NULL,
                    negative_marks      REAL    NOT NULL DEFAULT 0,
                    max_attempts        INTEGER NOT NULL DEFAULT 1,
                    randomize_questions INTEGER NOT NULL DEFAULT 0,
                    FOREIGN KEY(created_by) REFERENCES users(id)
                )
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS questions (
                    id              INTEGER PRIMARY KEY AUTOINCREMENT,
                    quiz_id         INTEGER NOT NULL,
                    question_type   TEXT    NOT NULL DEFAULT 'mcq',
                    question_text   TEXT    NOT NULL,
                    option_a        TEXT,
                    option_b        TEXT,
                    option_c        TEXT,
                    option_d        TEXT,
                    correct_option  TEXT,
                    FOREIGN KEY(quiz_id) REFERENCES quizzes(id)
                )
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS results (
                    id           INTEGER PRIMARY KEY AUTOINCREMENT,
                    student_id   INTEGER NOT NULL,
                    quiz_id      INTEGER NOT NULL,
                    score        INTEGER NOT NULL,
                    total_marks  INTEGER NOT NULL,
                    attempted_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY(student_id) REFERENCES users(id),
                    FOREIGN KEY(quiz_id)    REFERENCES quizzes(id)
                )
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS short_answers (
                    id          INTEGER PRIMARY KEY AUTOINCREMENT,
                    result_id   INTEGER NOT NULL,
                    question_id INTEGER NOT NULL,
                    answer_text TEXT,
                    marks_given INTEGER DEFAULT 0,
                    graded      INTEGER DEFAULT 0,
                    FOREIGN KEY(result_id)   REFERENCES results(id),
                    FOREIGN KEY(question_id) REFERENCES questions(id)
                )
            """);

            // Migrate existing DB — add columns if they don't exist
            safeAddColumn("quizzes", "negative_marks",      "REAL    NOT NULL DEFAULT 0");
            safeAddColumn("quizzes", "max_attempts",        "INTEGER NOT NULL DEFAULT 1");
            safeAddColumn("quizzes", "randomize_questions", "INTEGER NOT NULL DEFAULT 0");
            safeAddColumn("questions", "question_type",     "TEXT NOT NULL DEFAULT 'mcq'");
            safeAddColumn("quizzes",   "start_time",  "TEXT");
            safeAddColumn("quizzes",   "end_time",    "TEXT");
            safeAddColumn("questions", "image_path",  "TEXT");

            System.out.println("Database initialized successfully!");

        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void safeAddColumn(String table, String column, String definition) {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("ALTER TABLE " + table + " ADD COLUMN " + column + " " + definition);
        } catch (SQLException ignored) {} // column already exists
    }

    // ── Default Admin ─────────────────────────────────────────────────────────
    public void ensureDefaultAdmin(String username, String password) {
    // Remove any existing admin accounts first
    try (Statement stmt = connection.createStatement()) {
        stmt.execute("DELETE FROM users WHERE role = 'admin'");
    } catch (SQLException e) { e.printStackTrace(); }

    // Create the admin with the specified credentials
    registerUser(username, password, "admin");
    System.out.println("Admin account ready: " + username);
}

    // ── User Operations ───────────────────────────────────────────────────────
    public boolean registerUser(String username, String plainPassword, String role) {
        String sql = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, PasswordUtil.hashPassword(plainPassword));
            ps.setString(3, role);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) { return false; }
    }

    public User loginUser(String username, String plainPassword) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next() && PasswordUtil.verifyPassword(plainPassword, rs.getString("password"))) {
                return new User(rs.getInt("id"), rs.getString("username"),
                    rs.getString("password"), rs.getString("role"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public boolean changePassword(int userId, String newPlainPassword) {
        String sql = "UPDATE users SET password = ? WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, PasswordUtil.hashPassword(newPlainPassword));
            ps.setInt(2, userId);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY role, username";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                users.add(new User(rs.getInt("id"), rs.getString("username"),
                    rs.getString("password"), rs.getString("role")));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return users;
    }

    public boolean deleteUser(int userId) {
        String sql = "DELETE FROM users WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean resetPassword(int userId, String newPlainPassword) {
        return changePassword(userId, newPlainPassword);
    }

    // ── Quiz Operations ───────────────────────────────────────────────────────
    public boolean createQuiz(String examId, String title, int createdBy,
                          int timeLimitMinutes, int marksPerQuestion,
                          double negativeMarks, int maxAttempts,
                          boolean randomize, String startTime, String endTime) {
    String sql = """
        INSERT INTO quizzes
        (exam_id,title,created_by,time_limit_minutes,marks_per_question,
         negative_marks,max_attempts,randomize_questions,start_time,end_time)
        VALUES (?,?,?,?,?,?,?,?,?,?)
    """;
    try (PreparedStatement ps = connection.prepareStatement(sql)) {
        ps.setString(1, examId);   ps.setString(2, title);
        ps.setInt(3, createdBy);   ps.setInt(4, timeLimitMinutes);
        ps.setInt(5, marksPerQuestion); ps.setDouble(6, negativeMarks);
        ps.setInt(7, maxAttempts); ps.setInt(8, randomize ? 1 : 0);
        ps.setString(9, startTime); ps.setString(10, endTime);
        ps.executeUpdate();
        return true;
    } catch (SQLException e) { return false; }
}

    public boolean updateQuiz(int quizId, String title, int timeLimitMinutes,
                          int marksPerQuestion, double negativeMarks,
                          int maxAttempts, boolean randomize,
                          String startTime, String endTime) {
    String sql = """
        UPDATE quizzes SET title=?,time_limit_minutes=?,marks_per_question=?,
        negative_marks=?,max_attempts=?,randomize_questions=?,
        start_time=?,end_time=? WHERE id=?
    """;
    try (PreparedStatement ps = connection.prepareStatement(sql)) {
        ps.setString(1, title);        ps.setInt(2, timeLimitMinutes);
        ps.setInt(3, marksPerQuestion); ps.setDouble(4, negativeMarks);
        ps.setInt(5, maxAttempts);     ps.setInt(6, randomize ? 1 : 0);
        ps.setString(7, startTime);    ps.setString(8, endTime);
        ps.setInt(9, quizId);
        ps.executeUpdate(); return true;
    } catch (SQLException e) { e.printStackTrace(); return false; }
}

    public boolean deleteQuiz(int quizId) {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("DELETE FROM questions WHERE quiz_id = " + quizId);
            stmt.execute("DELETE FROM results   WHERE quiz_id = " + quizId);
            stmt.execute("DELETE FROM quizzes   WHERE id = "      + quizId);
            return true;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public Quiz getQuizByExamId(String examId) {
        String sql = "SELECT * FROM quizzes WHERE exam_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, examId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapQuiz(rs);
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public Quiz getQuizById(int id) {
        String sql = "SELECT * FROM quizzes WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapQuiz(rs);
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public List<Quiz> getQuizzesByTeacher(int teacherId) {
        return getQuizzesWhere("created_by = " + teacherId);
    }

    public List<Quiz> getAllQuizzes() {
        return getQuizzesWhere(null);
    }

    public List<Quiz> searchQuizzesByTeacher(int teacherId, String keyword) {
        String kw = keyword.toLowerCase();
        List<Quiz> all = getQuizzesByTeacher(teacherId);
        List<Quiz> filtered = new ArrayList<>();
        for (Quiz q : all) {
            if (q.getTitle().toLowerCase().contains(kw) ||
                q.getExamId().toLowerCase().contains(kw)) {
                filtered.add(q);
            }
        }
        return filtered;
    }

    private List<Quiz> getQuizzesWhere(String where) {
        List<Quiz> quizzes = new ArrayList<>();
        String sql = "SELECT * FROM quizzes" + (where != null ? " WHERE " + where : "");
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) quizzes.add(mapQuiz(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return quizzes;
    }

    private Quiz mapQuiz(ResultSet rs) throws SQLException {
    Quiz q = new Quiz();
    q.setId(rs.getInt("id"));
    q.setExamId(rs.getString("exam_id"));
    q.setTitle(rs.getString("title"));
    q.setCreatedBy(rs.getInt("created_by"));
    q.setTimeLimitMinutes(rs.getInt("time_limit_minutes"));
    q.setMarksPerQuestion(rs.getInt("marks_per_question"));
    q.setNegativeMarks(rs.getDouble("negative_marks"));
    q.setMaxAttempts(rs.getInt("max_attempts"));
    q.setRandomizeQuestions(rs.getInt("randomize_questions") == 1);
    q.setStartTime(rs.getString("start_time"));
    q.setEndTime(rs.getString("end_time"));
    return q;
}

    // ── Question Operations ───────────────────────────────────────────────────
    public boolean addQuestion(int quizId, String type, String questionText,
                           String optA, String optB, String optC, String optD,
                           String correctOption, String imagePath) {
    String sql = """
        INSERT INTO questions
        (quiz_id,question_type,question_text,option_a,option_b,
         option_c,option_d,correct_option,image_path)
        VALUES (?,?,?,?,?,?,?,?,?)
    """;
    try (PreparedStatement ps = connection.prepareStatement(sql)) {
        ps.setInt(1, quizId);      ps.setString(2, type);
        ps.setString(3, questionText); ps.setString(4, optA);
        ps.setString(5, optB);     ps.setString(6, optC);
        ps.setString(7, optD);
        ps.setString(8, correctOption != null ? correctOption.toUpperCase() : null);
        ps.setString(9, imagePath);
        ps.executeUpdate(); return true;
    } catch (SQLException e) { e.printStackTrace(); return false; }
}

    public boolean updateQuestion(int questionId, String questionText,
                                  String optA, String optB, String optC, String optD,
                                  String correctOption) {
        String sql = """
            UPDATE questions SET question_text=?, option_a=?, option_b=?,
            option_c=?, option_d=?, correct_option=? WHERE id=?
        """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, questionText);
            ps.setString(2, optA);
            ps.setString(3, optB);
            ps.setString(4, optC);
            ps.setString(5, optD);
            ps.setString(6, correctOption != null ? correctOption.toUpperCase() : null);
            ps.setInt(7, questionId);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean deleteQuestion(int questionId) {
        String sql = "DELETE FROM questions WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, questionId);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public List<Question> getQuestionsByQuizId(int quizId) {
        List<Question> questions = new ArrayList<>();
        String sql = "SELECT * FROM questions WHERE quiz_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, quizId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) questions.add(mapQuestion(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return questions;
    }

    private Question mapQuestion(ResultSet rs) throws SQLException {
    Question q = new Question();
    q.setId(rs.getInt("id"));
    q.setQuizId(rs.getInt("quiz_id"));
    q.setQuestionType(rs.getString("question_type"));
    q.setQuestionText(rs.getString("question_text"));
    q.setOptionA(rs.getString("option_a"));
    q.setOptionB(rs.getString("option_b"));
    q.setOptionC(rs.getString("option_c"));
    q.setOptionD(rs.getString("option_d"));
    q.setCorrectOption(rs.getString("correct_option"));
    q.setImagePath(rs.getString("image_path"));
    return q;
}

    // ── Result Operations ─────────────────────────────────────────────────────
    public boolean saveResult(int studentId, int quizId, int score, int totalMarks) {
        String sql = "INSERT INTO results (student_id,quiz_id,score,total_marks) VALUES (?,?,?,?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, studentId); ps.setInt(2, quizId);
            ps.setInt(3, score);     ps.setInt(4, totalMarks);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public int getAttemptCount(int studentId, int quizId) {
        String sql = "SELECT COUNT(*) FROM results WHERE student_id=? AND quiz_id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, studentId); ps.setInt(2, quizId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    public List<Result> getResultsByStudent(int studentId) {
        List<Result> results = new ArrayList<>();
        String sql = """
            SELECT r.*, q.title as quiz_title
            FROM results r JOIN quizzes q ON r.quiz_id = q.id
            WHERE r.student_id = ? ORDER BY r.attempted_at DESC
        """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Result result = mapResult(rs);
                result.setQuizTitle(rs.getString("quiz_title"));
                results.add(result);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return results;
    }

    public List<Result> getResultsByQuiz(int quizId) {
        List<Result> results = new ArrayList<>();
        String sql = """
            SELECT r.*, u.username as student_name
            FROM results r JOIN users u ON r.student_id = u.id
            WHERE r.quiz_id = ? ORDER BY r.score DESC
        """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, quizId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Result result = mapResult(rs);
                result.setStudentName(rs.getString("student_name"));
                results.add(result);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return results;
    }

    public List<Result> getAllResults() {
        List<Result> results = new ArrayList<>();
        String sql = """
            SELECT r.*, u.username as student_name, q.title as quiz_title
            FROM results r
            JOIN users u ON r.student_id = u.id
            JOIN quizzes q ON r.quiz_id = q.id
            ORDER BY r.attempted_at DESC
        """;
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Result result = mapResult(rs);
                result.setStudentName(rs.getString("student_name"));
                result.setQuizTitle(rs.getString("quiz_title"));
                results.add(result);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return results;
    }

    private Result mapResult(ResultSet rs) throws SQLException {
        return new Result(rs.getInt("id"), rs.getInt("student_id"),
            rs.getInt("quiz_id"), rs.getInt("score"),
            rs.getInt("total_marks"), rs.getString("attempted_at"));
    }
}