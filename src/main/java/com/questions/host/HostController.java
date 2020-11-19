package com.questions.host;

import com.questions.utils.Console;
import com.questions.host.questionnaire.Answer;
import com.questions.host.peer.Peer;

import java.util.List;

public class HostController implements EventsHost {

    public EventChangeRoundHost eventHostRound;
    public HostConfig config = new HostConfig();

    private Host round;

    public void start(HostConfig config) {
        this.config = config;
        this.startHost();
    }

    private void startHost() {
        this.round = new Host(this.config, this);

        this.round.start();
        Console.writer.println("waiting the peers\npress enter to start questionnaire\n");

        Console.input("");
        this.round.startRound();
    }

    @Override
    public void newAnswer(Answer answer) {
        Console.writer.println("new answer from '" + answer.getPeer() + "' in question with id: " + answer.getQuestion().id);
    }

    @Override
    public void peerConnection(Peer peer) {
        Console.writer.println("new peer connected: " + peer.username);
    }

    @Override
    public void winner(Peer peer, List<Answer> answers) {
        Console.writer.println("have a winner: " + peer);

    }

    @Override
    public void newHostRound(String host, int port) {
        this.eventHostRound.newHost(host, port);
    }

    public void waitFinish() {
        try { this.round.join(); }
        catch (InterruptedException e) { e.printStackTrace(); }
    }

    public void stop() {
        try {
            this.round.close();
        } catch (HostError hostError) {
            Console.writer.println("error on stop host: " + hostError.getMessage());
        }
    }
}
