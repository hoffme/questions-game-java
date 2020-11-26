package com.questions.peer;

import com.questions.CommandOuterClass.*;
import com.questions.red.Neighbour;
import com.questions.utils.Console;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Client extends PeerListener {

    public Host hostUI;

    private final Peer peer;
    private final Set<String> roundRequests;
    private Neighbour actualHost;
    private boolean waiting;

    public Client(Peer peer) {
        this.peer = peer;
        this.roundRequests = new HashSet<>();
    }

    public void start() {
        Console.println("mode client started");
        this.peer.listener = this;

        String[] options = new String[]{"exit", "connect", "connected", "waitRound"};
        int selected = -1;

        while (selected != 0) {
            selected = Console.select("actions: ", options);
            switch (selected) {
                case 1 -> peer.actionConnect();
                case 2 -> peer.actionShowConnected();
                case 3 -> this.waitRound();
            }
        }
    }

    public synchronized void finishRound() {
        this.actualHost = null;
        this.waiting = false;
        this.notifyAll();
    }

    private synchronized void waitRound() {
        this.waiting = true;
        while (this.waiting) {
            try { this.wait(); } catch (InterruptedException ignore) { }
        }
    }

    @Override
    public void cmdRoundRequest(Neighbour neighbour, RoundRequest cmd) {
        if (!this.waiting || this.roundRequests.contains(cmd.getId())) return;
        this.roundRequests.add(cmd.getId());

        this.peer.sendRoundRequest(cmd);

        Console.println("new round request, id: " + cmd.getId());

        if (Console.select("initialize? ", new String[]{"yes", "no"}) == 1) return;

        if (!this.peer.neighbours.containsKey(cmd.getHost().getAlias())) {
            Console.print("connecting to " + cmd.getHost().getAlias() + " -> ");
            try {
                this.peer.connect(cmd.getHost().getHost(), cmd.getHost().getPort());
                Console.println("successfully");
            } catch (IOException e) {
                Console.println("error: " + e.getMessage());
                return;
            }
        }

        Console.println("connected to host");
        this.actualHost = this.peer.neighbours.get(cmd.getHost().getAlias());

        this.peer.sendRoundResponse(this.actualHost, true);

        Console.println("wait the question ...");
    }

    @Override
    public void cmdQuestion(Neighbour neighbour, Question cmd) {
        Console.println("new question: " + cmd.getTitle());
        String answer = Console.input("> ");

        switch (cmd.getType()) {
            case com.questions.quesionnaire.Question.TypeSimple -> {
                this.peer.sendAnswer(this.actualHost, cmd.getId(), answer);
            }
            case com.questions.quesionnaire.Question.TypeMultiple -> {
                this.peer.sendAnswer(this.actualHost, cmd.getId(), Arrays.asList(answer.split(",")));
            }
        }
    }

    @Override
    public void cmdAnswerResult(Neighbour neighbour, AnswerResult cmd) {
        Console.println("results:");
        for (PeerResult result: cmd.getPeersList()) {
            Console.println(result.getAlias() + " -> " + result.getPoints());
        }
        Console.println("correct answer from: " + cmd.getAliasCorrect());
    }

    @Override
    public void cmdRoundResult(Neighbour neighbour, RoundResult cmd) {
        Console.println("the winner is: " + cmd.getAliasWinner());
    }
}
