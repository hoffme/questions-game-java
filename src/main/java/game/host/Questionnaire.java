package game.host;

import game.questions.Question;

import java.util.*;

public class Questionnaire {

    private final List<Question> questions;
    private final Map<Peer, List<Answer>> answers;
    private final EventWinner event;
    private final int pointsToWin;
    private int indexQuestion;
    private boolean open;

    public Questionnaire(List<Question> questions, EventWinner eventWinner) {
        this.questions = questions;
        this.answers = new HashMap<>();
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

    public void next() { this.indexQuestion++; }

    public boolean answer(Peer peer, String answerString) throws Exception {
        if (!this.open) {
            throw new Exception("Questionnaire is closed");
        }

        boolean correct = this.questions.get(this.indexQuestion).answer().equals(answerString);
        if (correct) {
            this.close();

            Answer answer = new Answer(this.questions.get(this.indexQuestion), answerString, peer);

            if (!this.answers.containsKey(peer)) this.answers.put(peer, new ArrayList<>());
            this.answers.get(peer).add(answer);

            if (this.answers.get(peer).size() >= this.pointsToWin) {
                event.win(peer, this.answers.get(peer));
            }
        }

        return correct;
    }

    public Map<Peer, List<Answer>> results() {
        return this.answers;
    }
}
