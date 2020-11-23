package com.questions.game.host;

import com.questions.CommandOuterClass.*;
import com.questions.game.PeerState;
import com.questions.red.Neighbour;
import com.questions.red.Node;
import com.questions.red.NodeReceiver;
import com.questions.utils.Console;

import java.io.IOException;

public class HostConnection extends PeerState implements NodeReceiver {

    private Node node;

    @Override
    public void start(Node node) {
        this.node = node;
        this.node.receiver = this;
    }

    @Override
    public void command(Neighbour neighbour, Command cmd) { }
}
