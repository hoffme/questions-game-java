package com.questions.client;

import com.questions.game.Commands;

public interface EventsClient {
    void finish(Commands.Finish finish);
    void changeHostRound(Commands.ChangeHostRound change);
    void question(Commands.Question question);
    void result(Commands.Result result);
}
