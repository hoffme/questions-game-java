package com.questions.peer;

import com.questions.CommandOuterClass.*;
import com.questions.red.Neighbour;
import com.questions.utils.Console;

import java.io.IOException;
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
        if (this.roundRequests.contains(cmd.getId())) return;
        this.roundRequests.add(cmd.getId());

        this.peer.sendRoundRequest(cmd);

        Console.println("new round request, id: " + cmd.getId());

        if (Console.select("initialize? ", new String[]{"yes", "no"}) == 0) {
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
        }
    }

    @Override
    public void cmdQuestion(Neighbour neighbour, Question cmd) {

    }

    @Override
    public void cmdAnswerResult(Neighbour neighbour, AnswerResult cmd) {

    }

    @Override
    public void cmdRoundResult(Neighbour neighbour, RoundResult cmd) {

    }
}
