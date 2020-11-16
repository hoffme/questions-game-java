package com.questions.game.host.questionnaire;

import com.questions.game.host.events.EventWinner;
import com.questions.game.host.peer.Peer;
import com.questions.game.host.questions.Question;

import java.util.*;

public class Questionnaire {

    private final List<Question> questions;
    private final Map<Peer, List<Answer>> answersCorrect;
    private final Set<Peer> peersResponse;
    private final EventWinner event;
    private final int pointsToWin;

    private int indexQuestion;
    private boolean open;

    public Questionnaire(List<Question> questions, EventWinner eventWinner) {
        this.questions = questions;
        this.answersCorrect = new HashMap<>();
        this.peersResponse = new HashSet<>();
        this.event = eventWinner;
        this.pointsToWin = 5;
        this.indexQuestion = -1;
        this.open = false;
    }

    public Question getQuestion() {
        return this.questions.get(this.indexQuestion);
    }

    public void open() { this.open = true; }

    public void close() { this.open = false; }

    public void next() {
        this.indexQuestion++;
        this.peersResponse.clear();
    }

    public Answer answer(int questionID, Peer peer, String answerString) {
        Answer answer = new Answer(this.getQuestion(questionID), answerString, peer);
        this.peersResponse.add(peer);

        if (!this.answersCorrect.containsKey(peer)) this.answersCorrect.put(peer, new ArrayList<>());
        this.answersCorrect.get(peer).add(answer);

        if (this.answersCorrect.get(peer).size() >= this.pointsToWin) {
            this.event.win(peer, this.answersCorrect.get(peer));
        }

        return answer;
    }

    public Set<Peer> getPeersResponse() { return this.peersResponse; }

    public Question getQuestion(int questionID) {
        for (Question question: this.questions) {
            if (question.id == questionID) return question;
        }
        return null;
    }

    public boolean isOpen() { return this.open; }

    public Question actualQuestion() { return this.questions.get(this.indexQuestion); }
}
