package com.questions.peer;

import com.questions.CommandOuterClass;
import com.questions.red.Neighbour;
import com.questions.utils.Console;
import com.questions.utils.Question;

import java.util.*;

public class Host extends PeerListener {

    public Client clientUI;

    private final Peer peer;
    private final List<Question> questions;

    private long idRound;

    private final Set<Peer> peers;
    private final Map<Question, Map<Neighbour, List<String>>> questionnaire;

    public Host(Peer peer, List<Question> questions) {
        this.peer = peer;
        this.questionnaire = new HashMap<>();

        this.questions = questions;
        Collections.shuffle(this.questions);
        peers = new HashSet<>();
    }

    public void start() {
        Console.println("mode host started");

        this.peer.listener = this;
        this.idRound = 0;
        this.questionnaire.clear();
        this.peers.clear();

        String[] options = new String[]{"exit", "connect", "connected", "startRound"};
        int selected = -1;

        while (selected != 0) {
            selected = Console.select("actions: ", options);
            switch (selected) {
                case 1 -> peer.actionConnect();
                case 2 -> peer.actionShowConnected();
                case 3 -> this.startRound();
            }
        }
    }

    private void startRound() {
        this.idRound = (System.currentTimeMillis() / 1000L);
        peer.sendRoundRequest(idRound);

        String[] options = new String[]{
                "abort",
                "resendRoundRequest",
                "sendQuestion",
                "showAnswers",
                "sendResults",
                "finishRound"
        };

        while (this.idRound != 0) {
            switch (Console.select("actions: ", options)) {
                case 0 -> this.idRound = 0;
                case 1 -> this.peer.sendRoundRequest(this.idRound);
                case 2 -> this.sendQuestion();
                case 3 -> this.showAnswers();
                case 4 -> this.sendResults();
                case 5 -> this.finishRound();
            }
        }
    }

    private void finishRound() {
    }

    private void sendResults() {

    }

    private void showAnswers() {
        List<Question> questionsObject = new ArrayList<>();
        List<String> questionsString = new ArrayList<>();
        this.questionnaire.keySet().forEach(question -> {
            questionsString.add(question.title);
            questionsObject.add(question);
        });

        String[] options = questionsString.toArray(new String[questionsString.size() + 1]);
        options[questionsString.size()] = "exit";

        int selected = -1;
        while (selected != questionsString.size()) {
            selected = Console.selectNumber("select the question:", options);

            Question questionSelected = questionsObject.get(selected);

            Console.println("question selected[" + questionSelected.id + "]: " + questionSelected.title);
            this.questionnaire.get(questionSelected).forEach((neighbour, answer) -> {
                Console.println(" - " + neighbour.getAlias() + " -> " + String.join(",", answer));
            });
        }
    }

    private void sendQuestion() {
        if (this.questionnaire.size() >= this.questions.size()) this.peer.sendRoundResult(null);

        Question question = this.questions.get(this.questionnaire.size());
        this.questionnaire.put(question, new HashMap<>());

        this.peer.sendQuestion(question);
    }

    @Override
    public void cmdAnswer(Neighbour neighbour, CommandOuterClass.Answer cmd) {
        this.questionnaire.get(this.questions.get(this.questionnaire.size() - 1)).put(neighbour, cmd.getDataList());
    }

    @Override
    void cmdRoundResponse(Neighbour neighbour, CommandOuterClass.RoundResponse cmd) {

    }
}
