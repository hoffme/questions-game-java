package com.questions.host.peer;

import com.questions.game.Commands.*;

public interface EventsPeer {
    void answer(Peer peer, Answer answer);
    void changeRound(Peer peer, ChangeHostRound changeRound);
}
