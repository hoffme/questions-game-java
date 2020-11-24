package com.questions.game;

import com.questions.CommandOuterClass.*;
import com.questions.red.Neighbour;
import com.questions.red.Node;
import com.questions.utils.Console;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class Peer extends Node {

    public Peer(PeerConfig config) throws IOException {
        super(config.username, config.host, config.port);
    }

    public void waitToExit() {
        System.exit(0);
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
