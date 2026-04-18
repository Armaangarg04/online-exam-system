package com.examsystem.model;

public class Result {
    private int id;
    private int studentId;
    private int quizId;
    private int score;
    private int totalMarks;
    private String attemptedAt;
    private String studentName; // for teacher's view
    private String quizTitle;   // for student's history

    public Result() {}

    public Result(int id, int studentId, int quizId,
                  int score, int totalMarks, String attemptedAt) {
        this.id = id;
        this.studentId = studentId;
        this.quizId = quizId;
        this.score = score;
        this.totalMarks = totalMarks;
        this.attemptedAt = attemptedAt;
    }

    public int getId()                          { return id; }
    public void setId(int id)                   { this.id = id; }

    public int getStudentId()                   { return studentId; }
    public void setStudentId(int studentId)     { this.studentId = studentId; }

    public int getQuizId()                      { return quizId; }
    public void setQuizId(int quizId)           { this.quizId = quizId; }

    public int getScore()                       { return score; }
    public void setScore(int score)             { this.score = score; }

    public int getTotalMarks()                          { return totalMarks; }
    public void setTotalMarks(int totalMarks)           { this.totalMarks = totalMarks; }

    public String getAttemptedAt()                      { return attemptedAt; }
    public void setAttemptedAt(String attemptedAt)      { this.attemptedAt = attemptedAt; }

    public String getStudentName()                      { return studentName; }
    public void setStudentName(String studentName)      { this.studentName = studentName; }

    public String getQuizTitle()                        { return quizTitle; }
    public void setQuizTitle(String quizTitle)          { this.quizTitle = quizTitle; }

    @Override
    public String toString() {
        return "Result{studentId=" + studentId + ", quizId=" + quizId +
               ", score=" + score + "/" + totalMarks + "}";
    }
}