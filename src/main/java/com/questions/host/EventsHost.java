package com.questions.host;

import com.questions.host.peer.Peer;
import com.questions.host.questionnaire.Answer;

import java.util.List;

public interface EventsHost {
    void answer(Answer answer);
    void peerConnection(Peer peer);
    void winner(Peer peer, List<Answer> answers);
}
