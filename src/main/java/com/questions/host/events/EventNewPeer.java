package com.questions.host.events;

import com.questions.host.peer.Peer;

public interface EventNewPeer {
    void event(Peer peer);
}
