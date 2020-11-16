package com.questions.game.host.events;

import com.questions.game.host.peer.Peer;
import com.questions.game.host.questionnaire.Answer;

import java.util.List;

public interface EventWinner {
    void win(Peer peer, List<Answer> answers);
}
