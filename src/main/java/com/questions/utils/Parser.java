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
