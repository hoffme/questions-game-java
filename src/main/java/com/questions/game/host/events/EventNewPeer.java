package com.questions.game.host.events;

import com.questions.game.host.peer.Peer;

public interface EventNewPeer {
    void event(Peer peer);
}
