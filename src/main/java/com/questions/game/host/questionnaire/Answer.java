package com.questions.game.host.questionnaire;

import com.questions.game.host.peer.Peer;
import com.questions.game.host.questions.Question;

public class Answer {

    private final Question question;
    private final String answer;
    private final Peer peer;

    public Answer(Question question, String answer, Peer peer) {
        this.answer = answer;
        this.question = question;
        this.peer = peer;
    }

    public Question getQuestion() {
        return question;
    }

    public String getAnswer() {
        return answer;
    }

    public Peer getPeer() {
        return peer;
    }

    public boolean correct() {
        return this.question.isCorrect(this.answer);
    }
}
