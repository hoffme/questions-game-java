package com.questions.quesionnaire;

import java.util.*;

public class Round<T> {

    public final Question question;
    private final Set<T> participants;
    private final HashMap<T, List<String>> answers;
    private T winner;

    public Round(Question question, Set<T> participants) {
        this.question = question;

        this.answers = new HashMap<>();
        this.participants = participants;

        this.winner = null;
    }

    public Set<T> participantsMissing() {
        Set<T> missing = new HashSet<>(this.participants);
        missing.removeAll(this.answers.keySet());
        return missing;
    }

    public Set<T> participantsReady() {
        return this.answers.keySet();
    }

    public void response(T participant, String answer) {
        if (this.answers.containsKey(participant)) return;

        if (this.question.isCorrect(answer)) {
            this.winner = participant;
        }

        this.answers.put(participant, Collections.singletonList(answer));
    }

    public void response(T participant, List<String> answer) {
        if (this.answers.containsKey(participant)) return;

        if (this.question.isCorrect(answer)) {
            this.winner = participant;
        }

        this.answers.put(participant, answer);
    }

    public T getWinner() { return this.winner; }

    public Map<T, List<String>> getAnswers() { return this.answers; }

    public boolean hasWinner() {
        return this.getWinner() == null;
    }
}
