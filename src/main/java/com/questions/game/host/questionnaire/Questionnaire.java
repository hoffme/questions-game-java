package com.questions.game.host.questionnaire;

import com.questions.game.host.events.EventWinner;
import java.util.*;

public class Questionnaire {

    private final List<Question> questions;
    private final Map<String, List<Answer>> answers;
    private final Set<String> peersResponse;

    private final int pointsToWin;

    private int indexQuestion;
    private boolean open;

    public EventWinner eventWinner;

    public Questionnaire(List<Question> questions) {
        this.questions = questions;
        this.answers = new HashMap<>();
        this.peersResponse = new HashSet<>();

        this.pointsToWin = 5;
        this.indexQuestion = -1;
        this.open = false;
    }

    public Question getQuestion() { return this.questions.get(this.indexQuestion); }

    public void open() { this.open = true; }

    public void close() { this.open = false; }

    public boolean next() {
        if (this.indexQuestion >= this.questions.size()) {
            return false;
        }
        this.indexQuestion++;
        this.peersResponse.clear();
        return true;
    }

    public Answer answer(int questionID, String peer, String answerString) {
        Answer answer = new Answer(this.getQuestion(questionID), answerString, peer);
        this.peersResponse.add(peer);

        if (!this.answers.containsKey(peer)) this.answers.put(peer, new ArrayList<>());
        this.answers.get(peer).add(answer);

        if (this.answers.get(peer).size() >= this.pointsToWin) {
            this.eventWinner.win(peer, this.answers.get(peer));
        }

        return answer;
    }

    public Set<String> getPeersResponse() { return this.peersResponse; }

    public Question getQuestion(int questionID) {
        for (Question question: this.questions) {
            if (question.id == questionID) return question;
        }
        return null;
    }

    public boolean isOpen() { return this.open; }

    public Question actualQuestion() {
        return this.questions.get(this.indexQuestion);
    }
}
