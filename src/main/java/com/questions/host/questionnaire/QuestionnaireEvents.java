package com.questions.host.questionnaire;

import java.util.List;

public interface QuestionnaireEvents {
    void winner(String peer, List<Answer> answers);
}
