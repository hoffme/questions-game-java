package com.questions.host.questionnaire;

import java.util.List;

public interface EventsQuestionnaire {
    void newWinner(String peer, List<Answer> answers);
}
