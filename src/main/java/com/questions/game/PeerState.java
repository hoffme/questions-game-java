package com.questions.game;

import com.questions.red.Node;

public abstract class PeerState {

    public PeerState next;

    protected void next(Node node) {
        this.next.start(node);
    }

    public abstract void start(Node node);
}
