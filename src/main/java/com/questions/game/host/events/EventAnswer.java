package com.questions.game.host.events;

import com.questions.game.host.peer.Peer;

public interface EventAnswer {
    void newAnswer(Peer peer, int questionId, String answer);
}
