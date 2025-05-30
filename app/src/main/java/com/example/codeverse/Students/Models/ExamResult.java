package com.example.codeverse.Students.Models;

public class ExamResult {
    private String subject;
    private String details;
    private String grade;
    private int animationRes;
    private int backgroundRes;
    private String textColor;

    public ExamResult(String subject, String details, String grade,
                      int animationRes, int backgroundRes, String textColor) {
        this.subject = subject;
        this.details = details;
        this.grade = grade;
        this.animationRes = animationRes;
        this.backgroundRes = backgroundRes;
        this.textColor = textColor;
    }

    public String getSubject() {
        return subject;
    }

    public String getDetails() {
        return details;
    }

    public String getGrade() {
        return grade;
    }

    public int getAnimationRes() {
        return animationRes;
    }

    public int getBackgroundRes() {
        return backgroundRes;
    }

    public String getTextColor() {
        return textColor;
    }
}