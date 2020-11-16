package com.questions.game.host.events;

import com.questions.game.host.questionnaire.Answer;

public interface EventNewAnswer {
    void event(Answer answer);
}
