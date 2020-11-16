package com.questions.game.host;

import com.questions.console.Console;
import com.questions.game.EventChangeMode;
import com.questions.game.host.questionnaire.Answer;
import com.questions.game.host.peer.Peer;

import java.util.List;

public class HostController {

    public EventChangeMode changeMode;
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
        Host round;
        try { round = new Host(this.config, this::winner); }
        catch (HostError e) {
            Console.writer.println(e);
            return;
        }

        Console.writer.println("round started");

        round.start();
        Console.writer.println("waiting the peers");

        Console.input("press enter to start questionnaire");
        round.startRound();

        try { round.join(); }
        catch (InterruptedException e) { e.printStackTrace(); }
    }

    private void winner(Peer peer, List<Answer> answers) {

    }
}
