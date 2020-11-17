package com.questions.host.events;

import com.questions.host.peer.Peer;

public interface EventAnswer {
    void newAnswer(Peer peer, int questionId, String answer);
}
