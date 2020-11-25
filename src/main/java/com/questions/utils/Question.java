package com.questions.utils;

import java.util.ArrayList;
import java.util.List;

public class Question {

    public final static String TypeSimple = "SIM";
    public final static String TypeMultiple = "MUL";

    public final int id;
    public final String title;
    public final String type;
    public final List<String> answer;

    public static Question Simple(int id, String title, String answer) {
        List<String> answers = new ArrayList<>();
        answers.add(answer);
        return new Question(id, title, Question.TypeSimple, answers);
    }

    public static Question Multiple(int id, String title, List<String> answers) {
        return new Question(id, title, Question.TypeMultiple, answers);
    }

    private Question(int id, String title, String type, List<String> answer) {
        this.id = id;
        this.title = title;
        this.type = type;
        this.answer = answer;
    }

    public boolean isCorrect(String answer) {
        return this.answer.get(0).equals(answer);
    }

    public boolean isCorrect(List<String> answers) {
        for (String answer: answers) if (!this.answer.contains(answer)) return false;
        return this.answer.size() == answers.size();
    }
}
