package com.questions.game.client.events;

public interface EventNewHostRound {
    void event(String host, Integer port, boolean me);
}
