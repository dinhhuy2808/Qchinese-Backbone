package com.elearning.model;

public class QuizFinishedResponse {
    private int lesson;
    private boolean isFinished;

    public int getLesson() {
        return this.lesson;
    }

    public void setLesson(int lesson) {
        this.lesson = lesson;
    }

    public boolean isIsFinished() {
        return this.isFinished;
    }

    public boolean getIsFinished() {
        return this.isFinished;
    }

    public void setIsFinished(boolean isFinished) {
        this.isFinished = isFinished;
    }
}
