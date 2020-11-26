package com.questions.peer;

import com.questions.CommandOuterClass;
import com.questions.quesionnaire.Questionnaire;
import com.questions.quesionnaire.Round;
import com.questions.red.Neighbour;
import com.questions.utils.Console;
import com.questions.quesionnaire.Question;

import java.util.*;

public class Host extends PeerListener {

    public Client clientUI;

    private final Peer peer;
    private final List<Question> questions;
    private final Set<Neighbour> participants;

    private Questionnaire<Neighbour> questionnaire;
    private Round<Neighbour> actualRound;

    public Host(Peer peer, List<Question> questions) {
        this.peer = peer;
        this.questions = questions;
        this.participants = new HashSet<>();
    }

    public void start() {
        Console.println("mode host started");

        this.peer.listener = this;

        String[] options = new String[]{"exit", "connect", "connected", "startQuestionnaire"};
        int selected = -1;

        while (selected != 0) {
            selected = Console.select("actions: ", options);
            switch (selected) {
                case 1 -> peer.actionConnect();
                case 2 -> peer.actionShowConnected();
                case 3 -> this.startQuestionnaire();
            }
        }
    }

    private void startQuestionnaire() {
        this.questionnaire = new Questionnaire<>(this.questions, this.participants);
        this.peer.sendRoundRequest(this.questionnaire.id);

        String[] options = new String[]{
                "abort",
                "resendRoundRequest",
                "sendQuestion",
                "showAnswers",
                "sendResults",
                "finishRound"
        };

        while (this.questionnaire != null) {
            switch (Console.select("actions: ", options)) {
                case 0 -> this.questionnaire = null;
                case 1 -> this.peer.sendRoundRequest(this.questionnaire.id);
                case 2 -> this.sendQuestion();
                case 3 -> this.showAnswers();
                case 4 -> this.sendResults();
                case 5 -> this.finishRound();
            }
        }
    }

    private void finishRound() {
        this.peer.sendRoundResult(this.questionnaire.getWinner());
    }

    private void sendResults() {
        this.peer.sendAnswerResult(this.actualRound);
    }

    private void showAnswers() {
        List<String> options = new ArrayList<>();
        this.questions.forEach(question -> options.add(question.title));

        options.add("exit");

        int selected = -1;
        while (selected != options.size() - 1) {
            selected = Console.selectNumber("select the question:", options);
            if (selected == options.size() - 1) break;

            Question questionSelected = this.questions.get(selected);

            Console.println("question selected[" + questionSelected.id + "]: " + questionSelected.title);

            this.questionnaire.getRound(questionSelected).getAnswers().forEach((neighbour, answers) -> {
                Console.println(" - " + neighbour.getAlias() + " -> " + String.join(",", answers));
            });
        }
    }

    private void sendQuestion() {
        if (this.questionnaire.hasWinner()) {
            Console.println("finish round, winner: " + this.questionnaire.getWinner().getAlias());
            return;
        };

        this.actualRound = this.questionnaire.newRound();

        this.peer.sendQuestion(this.actualRound.question);
    }

    @Override
    public void cmdAnswer(Neighbour neighbour, CommandOuterClass.Answer cmd) {
        this.actualRound.response(neighbour, cmd.getDataList());
    }

    @Override
    void cmdRoundResponse(Neighbour neighbour, CommandOuterClass.RoundResponse cmd) {
        if (cmd.getOk()) this.participants.add(neighbour);
    }
}
