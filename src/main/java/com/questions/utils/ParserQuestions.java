package com.questions.utils;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.questions.host.questionnaire.Question;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ParserQuestions {
    public static List<Question> load(String path) throws IOException {
        List<Question> result = new ArrayList<>();

        JsonReader reader = new JsonReader(new FileReader(new File(path)));
        JsonElement element = new JsonParser().parse(reader);

        JsonArray questionsArray = element.getAsJsonArray();
        for (JsonElement questionElement: questionsArray) {
            JsonObject questObj = questionElement.getAsJsonObject();

            Question question = new Question(
                    questObj.get("id").getAsInt(),
                    questObj.get("question").getAsString(),
                    questObj.get("type").getAsString(),
                    questObj.get("answer").getAsString()
            );

            result.add(question);
        }

        return result;
    }
}
