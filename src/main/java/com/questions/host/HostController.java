package com.questions.host;

import com.questions.utils.Console;
import com.questions.host.questionnaire.Answer;
import com.questions.host.peer.Peer;

import java.util.List;

public class HostController {

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
        Host round = new Host(this.config);

        round.eventWinner = this::winner;
        round.eventNewAnswer = this::newAnswer;
        round.eventNewPeer = this::newPeer;

        round.start();
        Console.writer.println("waiting the peers");

        Console.input("press enter to start questionnaire");
        round.startRound();

        try { round.join(); }
        catch (InterruptedException e) { e.printStackTrace(); }
    }

    private void newPeer(Peer peer) {
        Console.writer.println("new peer connected: " + peer.username);
    }

    private void newAnswer(Answer answer) {
        Console.writer.println("new answer:\n");
        Console.writer.println("\tquestionId: " + answer.getQuestion().id);
        Console.writer.println("\tfrom: " + answer.getPeer());
        Console.writer.println("\tanswer: " + answer.getAnswer());
    }

    private void winner(String peer, List<Answer> answers) {
        Console.writer.println("have a winner: " + peer);
    }
}
