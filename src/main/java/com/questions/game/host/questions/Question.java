package com.questions.game.host.questions;

public class Question {

    public final int id;
    public final String title;
    public final String type;
    public final String correctAnswer;

    public Question(int id, String title, String type, String correctAnswer) {
        this.id = id;
        this.title = title;
        this.type = type;
        this.correctAnswer = correctAnswer;
    }

    public boolean isCorrect(String answer) {
        return this.correctAnswer.equals(answer);
    }
}
