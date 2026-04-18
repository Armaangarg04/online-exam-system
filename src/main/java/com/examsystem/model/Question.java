package com.examsystem.model;

public class Question {
    private int id, quizId;
    private String questionType, questionText;
    private String optionA, optionB, optionC, optionD;
    private String correctOption, imagePath;

    public Question() {}

    public int getId()                    { return id; }
    public void setId(int v)              { this.id = v; }
    public int getQuizId()                { return quizId; }
    public void setQuizId(int v)          { this.quizId = v; }
    public String getQuestionType()       { return questionType != null ? questionType : "mcq"; }
    public void setQuestionType(String v) { this.questionType = v; }
    public String getQuestionText()       { return questionText; }
    public void setQuestionText(String v) { this.questionText = v; }
    public String getOptionA()            { return optionA; }
    public void setOptionA(String v)      { this.optionA = v; }
    public String getOptionB()            { return optionB; }
    public void setOptionB(String v)      { this.optionB = v; }
    public String getOptionC()            { return optionC; }
    public void setOptionC(String v)      { this.optionC = v; }
    public String getOptionD()            { return optionD; }
    public void setOptionD(String v)      { this.optionD = v; }
    public String getCorrectOption()      { return correctOption; }
    public void setCorrectOption(String v){ this.correctOption = v; }
    public String getImagePath()          { return imagePath; }
    public void setImagePath(String v)    { this.imagePath = v; }

    public boolean hasImage()      { return imagePath != null && !imagePath.isBlank(); }
    public boolean isMcq()         { return "mcq".equals(getQuestionType()); }
    public boolean isTrueFalse()   { return "truefalse".equals(getQuestionType()); }
    public boolean isShortAnswer() { return "short".equals(getQuestionType()); }
}