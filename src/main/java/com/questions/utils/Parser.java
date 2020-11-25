package com.questions.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Parser {
    public static List<Question> load(String path) throws IOException {
        List<Question> result = new ArrayList<>();

        JsonReader reader = new JsonReader(new FileReader(new File(path)));
        JsonElement element = new JsonParser().parse(reader);

        JsonArray questionsArray = element.getAsJsonArray();
        for (JsonElement questionElement: questionsArray) {
            JsonObject questObj = questionElement.getAsJsonObject();

            Question question = null;
            switch (questObj.get("type").getAsString()) {
                case Question.TypeMultiple -> question = Parser.createQuestionMultiple(questObj);
                case Question.TypeSimple -> question = Parser.createQuestionSimple(questObj);
            }

            if (question != null) result.add(question);
        }

        return result;
    }

    private static Question createQuestionSimple(JsonObject questObj) {
        return Question.Simple(
                questObj.get("id").getAsInt(),
                questObj.get("question").getAsString(),
                questObj.get("answer").getAsString()
        );
    }

    private static Question createQuestionMultiple(JsonObject questObj) {
        ArrayList<String> answers = new ArrayList<>();
        for (JsonElement answer: questObj.get("answer").getAsJsonArray()) {
            answers.add(answer.getAsString());
        }

        return Question.Multiple(
                questObj.get("id").getAsInt(),
                questObj.get("question").getAsString(),
                answers
        );
    }
}
