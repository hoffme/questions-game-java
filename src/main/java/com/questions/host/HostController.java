package com.questions.host;

import com.questions.utils.Console;
import com.questions.host.questionnaire.Answer;
import com.questions.host.peer.Peer;

import java.util.List;

public class HostController implements EventsHost {

    private HostConfig config = new HostConfig();

    public void start() {
        while (true) {
            try {
                this.config.configure();
                break;
            }
            catch (HostError err) { Console.writer.println(err); }
        }

        this.startHost();
    }

    public void start(HostConfig config) {
        this.config = config;
        this.startHost();
    }

    private void startHost() {
        Host round = new Host(this.config, this);

        round.start();
        Console.writer.println("waiting the peers\npress enter to start questionnaire\n");

        Console.input("");
        round.startRound();

        try { round.join(); }
        catch (InterruptedException e) { e.printStackTrace(); }
    }

    @Override
    public void answer(Answer answer) {
        Console.writer.println("new answer:\n");
        Console.writer.println("\tquestionId: " + answer.getQuestion().id);
        Console.writer.println("\tfrom: " + answer.getPeer());
        Console.writer.println("\tanswer: " + answer.getAnswer());
    }

    @Override
    public void peerConnection(Peer peer) {
        Console.writer.println("new peer connected: " + peer.username);
    }

    @Override
    public void winner(Peer peer, List<Answer> answers) {
        Console.writer.println("have a winner: " + peer);
    }
}
