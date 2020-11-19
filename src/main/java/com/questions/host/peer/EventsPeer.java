package com.questions.host.peer;

import com.questions.game.Commands.*;

public interface EventsPeer {
    void newAnswer(Peer peer, Answer answer);
    void hostChangeRound(Peer peer, ChangeHostRound changeRound);
}
