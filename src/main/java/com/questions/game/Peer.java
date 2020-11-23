package com.questions.game;

import com.questions.CommandOuterClass.*;
import com.questions.red.Neighbour;
import com.questions.red.Node;
import com.questions.utils.Console;

import java.io.IOException;

public class Peer extends Node {

    private final PeerConfig config;

    public Peer(PeerConfig config) throws IOException {
        super(config.username, config.host, config.port);

        this.config = config;
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
        if (command.hasRoundRequest()) {}
        else if (command.hasQuestion()) {}
        else if (command.hasAnswer()) {}
        else if (command.hasAnswerResult()) {}
        else if (command.hasRoundResult()) {}
    }


}
