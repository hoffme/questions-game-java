package com.questions.host.questionnaire;

import java.util.*;

public class Questionnaire {

    private final List<Question> questions;
    private final Map<String, List<Answer>> answers;
    private final Set<String> actualAnswered;

    private final EventsQuestionnaire events;

    private final int pointsToWin;
    private int indexQuestion;
    private boolean open;

    public Questionnaire(List<Question> questions, EventsQuestionnaire events) {
        this.questions = questions;
        this.answers = new HashMap<>();
        this.actualAnswered = new HashSet<>();
        this.events = events;

        this.pointsToWin = 5;
        this.indexQuestion = -1;
        this.open = false;
    }

    public Question getQuestion() { return this.questions.get(this.indexQuestion); }

    public Question getQuestion(int questionID) {
        for (Question question: this.questions) {
            if (question.id == questionID) return question;
        }
        return null;
    }

    public void open() { this.open = true; }

    public void close() { this.open = false; }

    public boolean isOpen() { return this.open; }

    public boolean next() {
        if (this.indexQuestion >= this.questions.size()) return false;

        this.indexQuestion++;
        this.actualAnswered.clear();

        return true;
    }

    public Answer answer(int questionID, String user, String answerString) {
        Answer answer = new Answer(this.getQuestion(questionID), answerString, user);

        if (this.questions.get(this.indexQuestion).id == questionID) {
            this.actualAnswered.add(user);
        }

        if (!this.answers.containsKey(user)) this.answers.put(user, new ArrayList<>());
        this.answers.get(user).add(answer);

        if (this.answers.get(user).size() >= this.pointsToWin) {
            this.events.newWinner(user, this.answers.get(user));
        }

        return answer;
    }

    public Question actualQuestion() {
        return this.questions.get(this.indexQuestion);
    }

    public Answer correctAnswer() {
        return new Answer(
                this.actualQuestion(),
                this.actualQuestion().correctAnswer,
                ""
        );
    }

    public int numberResponse() {
        return this.actualAnswered.size();
    }
}
