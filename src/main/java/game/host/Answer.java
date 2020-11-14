package game.host;

import game.questions.Question;

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
}
