package game.host;

import console.Console;
import game.EventChangeMode;

import java.io.IOException;

public class HostController {

    public EventChangeMode changeMode;
    private HostConfig config = new HostConfig();

    public void start() {
        while (true) {
            try {
                this.config.configure();
                break;
            } catch (Exception err) {
                System.out.println("error in config, try again");
            }
        }

        this.startHost();
    }

    public void start(HostConfig config) {
        this.config = config;
        this.startHost();
    }

    private void startHost() {
        System.out.println("stating round");

        RoundHost round;
        try {
            round = new RoundHost(this.config, (peer, answers) -> {
                System.out.println("winner is " + peer.username);
            });
        } catch (IOException e) {
            System.out.println("error to start round");
            return;
        }

        round.start();
        System.out.println("round started wait the peers");

        Console.input("> start questionnaire? ");
        System.out.println("peers connected start questionnaire");

        round.activeRound();
        round.sendQuestion();


        Console.input("> finish? ");

        System.out.println("finish round");
    }
}
