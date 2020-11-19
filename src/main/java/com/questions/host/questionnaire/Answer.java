package com.questions.host.questionnaire;

public class Answer {

    private final Question question;
    private final String answer;
    private final String peerUsername;

    public Answer(Question question, String answer, String peerUsername) {
        this.answer = answer;
        this.question = question;
        this.peerUsername = peerUsername;
    }

    public Question getQuestion() {
        return question;
    }

    public String getAnswer() {
        return answer;
    }

    public String getPeer() {
        return peerUsername;
    }

    public boolean isCorrect() {
        return this.question.correct(this.answer);
    }
}
