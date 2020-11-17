package com.questions.host.events;

import com.questions.host.questionnaire.Answer;

import java.util.List;

public interface EventWinner {
    void win(String peer, List<Answer> answers);
}
