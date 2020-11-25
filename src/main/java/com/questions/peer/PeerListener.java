package com.questions.peer;

import com.questions.CommandOuterClass.*;
import com.questions.red.Neighbour;

public abstract class PeerListener {
    void cmdRoundRequest(Neighbour neighbour, RoundRequest cmd) {}
    void cmdQuestion(Neighbour neighbour, Question cmd) {}
    void cmdAnswer(Neighbour neighbour, Answer cmd) {}
    void cmdAnswerResult(Neighbour neighbour, AnswerResult cmd) {}
    void cmdRoundResult(Neighbour neighbour, RoundResult cmd) {}
    void cmdRoundResponse(Neighbour neighbour, RoundResponse cmd) {}
}
