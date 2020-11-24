package com.questions.game;

import com.questions.CommandOuterClass.*;
import com.questions.red.Neighbour;
import com.questions.red.Node;
import com.questions.utils.Console;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class Peer extends Node {

    private final PeerConfig config;
    private final Set<String> roundRequests;

    public Peer(PeerConfig config) throws IOException {
        super(config.username, config.host, config.port);

        this.config = config;
        roundRequests = new HashSet<>();
    }

    public void waitToExit() {
        this.mainMenu();
        System.exit(0);
    }

    private void mainMenu() {
        int option = -1;
        String[] options = {"exit", "connect", "connected", "questionnaire", "send new round"};

        while (option != 0) {
            option = Console.select("actions:", options);
            switch (option) {
                case 1 -> this.connectAction();
                case 2 -> this.connectedAction();
                case 3 -> this.questionnaireAction();
                case 4 -> this.sendNewRoundAction();
            }
        }
    }

    private void sendNewRoundAction() {
        Command cmd = Command
                .newBuilder()
                .setRoundRequest(RoundRequest
                        .newBuilder()
                        .setHost(Address
                                .newBuilder()
                                .setHost(this.host)
                                .setPort(this.port)
                                .setAlias(this.alias)
                                .build()
                        ).setTime(System.currentTimeMillis() / 1000L)
                        .build()
                ).build();

        for (Neighbour neighbour: this.neighbours.values()) {
            Console.println("sending round request to " + neighbour.getAlias());
            try {
                neighbour.send(cmd);
            } catch (IOException e) {
                Console.println("error to send round request: " + e.getMessage());
            }
        }

        Console.println("round request sent");
    }

    private void questionnaireAction() {

    }

    private void connectedAction() {
        for (Neighbour neighbour: this.neighbours.values()) {
            Console.println("neighbour: " + neighbour.getAlias());
        }
    }

    private void connectAction() {
        Console.println("write peers (host:port, ...):");
        String[] peers = Console.input("> ").replaceAll("\\s+","").split(",");

        for (String peer: peers) {
            Console.print("connecting to " + peer + " ... ");
            try {
                this.connect(peer.split(":")[0], Integer.parseInt(peer.split(":")[1]));
                Console.println("successfully");
            } catch (Exception e) {
                Console.println("error to connect to '"+peer+"': "+ e.getMessage());
            }
        }

        Console.input("press enter to continue");
    }

    @Override
    protected void receive(Neighbour neighbour, Command command) {
        if (command.hasRoundRequest()) this.receiveRoundRequest(neighbour, command.getRoundRequest());
        else if (command.hasQuestion()) {}
        else if (command.hasAnswer()) {}
        else if (command.hasAnswerResult()) {}
        else if (command.hasRoundResult()) {}
        Console.println("received");
    }

    private void receiveRoundRequest(Neighbour neighbourSent, RoundRequest cmd) {
        String roundRequestId = cmd.getHost().getAlias() + cmd.getTime();

        if (!this.roundRequests.contains(roundRequestId)) {
            this.roundRequests.add(roundRequestId);

            Console.println("round request receive from " + cmd.getHost().getAlias());

            for (Neighbour neighbour: this.neighbours.values()) {
                if (neighbourSent == neighbour) continue;

                try {
                    neighbour.send(Command.newBuilder().setRoundRequest(cmd).build());
                } catch (IOException ignore) {}
            }
        }

        if (!this.neighbours.containsKey(cmd.getHost().getAlias())) {
            Console.print("connecting to host ... ");
            try {
                this.connect(cmd.getHost().getHost(), cmd.getHost().getPort());
                Console.println("successfully");
            } catch (IOException e) {
                Console.println("error to connect: " + e.getMessage());
            }
        }
    }
}
