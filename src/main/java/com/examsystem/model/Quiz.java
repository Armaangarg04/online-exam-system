package com.examsystem.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Quiz {
    private int id;
    private String examId, title;
    private int createdBy, timeLimitMinutes, marksPerQuestion, maxAttempts;
    private double negativeMarks;
    private boolean randomizeQuestions;
    private String startTime, endTime;

    public Quiz() {}

    public int getId()                           { return id; }
    public void setId(int v)                     { this.id = v; }
    public String getExamId()                    { return examId; }
    public void setExamId(String v)              { this.examId = v; }
    public String getTitle()                     { return title; }
    public void setTitle(String v)               { this.title = v; }
    public int getCreatedBy()                    { return createdBy; }
    public void setCreatedBy(int v)              { this.createdBy = v; }
    public int getTimeLimitMinutes()             { return timeLimitMinutes; }
    public void setTimeLimitMinutes(int v)       { this.timeLimitMinutes = v; }
    public int getMarksPerQuestion()             { return marksPerQuestion; }
    public void setMarksPerQuestion(int v)       { this.marksPerQuestion = v; }
    public double getNegativeMarks()             { return negativeMarks; }
    public void setNegativeMarks(double v)       { this.negativeMarks = v; }
    public int getMaxAttempts()                  { return maxAttempts; }
    public void setMaxAttempts(int v)            { this.maxAttempts = v; }
    public boolean isRandomizeQuestions()        { return randomizeQuestions; }
    public void setRandomizeQuestions(boolean v) { this.randomizeQuestions = v; }
    public String getStartTime()                 { return startTime; }
    public void setStartTime(String v)           { this.startTime = v; }
    public String getEndTime()                   { return endTime; }
    public void setEndTime(String v)             { this.endTime = v; }

    public boolean isScheduled() {
        return startTime != null && !startTime.isBlank()
            && endTime != null && !endTime.isBlank();
    }

    public boolean isOpenNow() {
        if (!isScheduled()) return true;
        try {
            LocalDateTime now   = LocalDateTime.now();
            LocalDateTime start = LocalDateTime.parse(startTime);
            LocalDateTime end   = LocalDateTime.parse(endTime);
            return !now.isBefore(start) && !now.isAfter(end);
        } catch (Exception e) { return true; }
    }

    public String getScheduleDisplay() {
        if (!isScheduled()) return "Always open";
        try {
            DateTimeFormatter fmt =
                DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");
            return LocalDateTime.parse(startTime).format(fmt)
                + "  →  " + LocalDateTime.parse(endTime).format(fmt);
        } catch (Exception e) { return startTime + " → " + endTime; }
    }
}