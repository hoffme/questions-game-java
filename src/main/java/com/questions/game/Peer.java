package com.questions.game;

import com.questions.game.client.ClientConnection;
import com.questions.game.client.ClientQuestionnaire;
import com.questions.game.host.HostConnection;
import com.questions.game.host.HostQuestionnaire;
import com.questions.red.Node;

import java.io.IOException;

public class Peer {

    private final PeerConfig config;
    private Node node;
    private PeerState state;

    public Peer(PeerConfig config) {
        this.config = config;

        try {
            this.node = new Node(this.config.username, this.config.host, this.config.port);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        if (this.config.modeClient) this.initClientState();
        else this.initHostState();
    }

    private void initClientState() {
        this.state = new ClientConnection();
        this.state.next = new ClientQuestionnaire();
    }

    private void initHostState() {
        this.state = new HostConnection();
        this.state.next = new HostQuestionnaire();
    }

    public void waitToExit() { this.state.start(this.node); }
}
