package com.questions.quesionnaire;

import java.util.*;

public class Questionnaire<T> {

    public final long id;

    private final List<Question> questions;
    private final Set<T> participants;
    private final HashMap<Question, Round<T>> rounds;

    public Questionnaire(List<Question> questions, Set<T> participants) {
        this.id = System.currentTimeMillis() / 1000L;

        this.questions = questions;
        Collections.shuffle(this.questions);

        this.participants = participants;
        this.rounds = new HashMap<>();
    }
    
    public Round<T> newRound() {
        if (this.questions.size() <= this.rounds.size()) {
            return null;
        }
        Question question = this.questions.get(this.rounds.size());

        Round<T> round = new Round<T>(question, this.participants);
        this.rounds.put(question, round);

        return round;
    }

    public T getWinner() {
        Map<T, Integer> points = new HashMap<>();

        this.participants.forEach(participant -> points.put(participant, 0));
        this.rounds.values().forEach(round -> {
            points.put(round.getWinner(), points.get(round.getWinner()) + 1);
        });

        T winner = null;
        for (T participant: points.keySet()) {
            if (winner == null || points.get(winner) < points.get(participant)) {
                winner = participant;
            }
        }

        return winner;
    }

    public boolean hasWinner() {
        return this.getWinner() != null;
    }

    public Round<T> getRound(Question question) {
        return this.rounds.get(question);
    }

    public List<Round<T>> getRounds() {
        return new ArrayList<>(this.rounds.values());
    }

    public void forceWinner() {

    }
}
