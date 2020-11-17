package com.questions.host.events;

import com.questions.host.questionnaire.Answer;

public interface EventNewAnswer {
    void event(Answer answer);
}
